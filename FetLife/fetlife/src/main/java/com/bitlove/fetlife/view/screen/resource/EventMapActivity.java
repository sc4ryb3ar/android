package com.bitlove.fetlife.view.screen.resource;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.EventsByLocationRetrieveFailedEvent;
import com.bitlove.fetlife.event.EventsByLocationRetrievedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.json.Event;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.DateUtil;
import com.bitlove.fetlife.util.MapUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class EventMapActivity extends ResourceActivity implements OnMapReadyCallback, PlaceSelectionListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, ClusterManager.OnClusterClickListener<Event>, ClusterManager.OnClusterItemClickListener<Event>, ClusterManager.OnClusterInfoWindowClickListener<Event>, ClusterManager.OnClusterItemInfoWindowClickListener<Event>, GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    //Check 500 max range
    //Add progress
    //Solve initial flickering
    //Marker colors
    //(Cluster) Info Window
    //2+ Grouping

    private static final float MIN_ZOOM_LEVEL = 7.5f;
    private static final float DEFAULT_ZOOM_LEVEL = 10f;
    private static final int MARKER_SEARCH_LIMIT = 100;
    private static final double MAX_MARKER_VISIBLE_RANGE = 0;

    private static final boolean USE_CLUSTERING = true;
    private ClusterManager<Event> clusterManager;

    private GoogleMap map;

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, EventMapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final SupportMapFragment mf = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.map, mf)
                        .commit();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mf.getMapAsync(EventMapActivity.this);
                    }
                });
            }
        }).start();
        //TODO: init map without UI flickering
        PlaceAutocompleteFragment searchFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        searchFragment.setOnPlaceSelectedListener(this);
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_event_map);
    }

    @Override
    protected void onResourceStart() {
    }

    @Override
    protected void onCreateActivityComponents() {
    }

    //Map events

    private EventMapActivity.DelayedEventRetriever delayedEventRetriever;
    private Object delayedEventRetrieverLock = new Object();

    @Override
    public void onCameraMove() {
        synchronized (delayedEventRetrieverLock) {
            if (delayedEventRetriever != null) {
                delayedEventRetriever.runState.compareAndSet(EventMapActivity.DelayedEventRetriever.WAITING, EventMapActivity.DelayedEventRetriever.CANCELLED);
                delayedEventRetriever = null;
            }
        }
    }

    @Override
    public void onCameraIdle() {
        if (USE_CLUSTERING) {
            clusterManager.onCameraIdle();
        }
        synchronized (delayedEventRetrieverLock) {
            delayedEventRetriever = new EventMapActivity.DelayedEventRetriever();
            //TODO use thread executor
            new Thread(delayedEventRetriever).start();
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),DEFAULT_ZOOM_LEVEL));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            //TODO move to current location
        }
        map.setMinZoomPreference(MIN_ZOOM_LEVEL);
        map.setOnCameraMoveListener(this);
        map.setOnCameraIdleListener(this);

        getFetLifeApplication().getInMemoryStorage().getMapEvents().clear();
        getFetLifeApplication().getInMemoryStorage().getMapPositions().clear();

        if (USE_CLUSTERING) {
            clusterManager = new ClusterManager<>(this, map);
            map.setOnMarkerClickListener(clusterManager);
            map.setOnInfoWindowClickListener(clusterManager);
            map.setInfoWindowAdapter(this);
            clusterManager.setOnClusterClickListener(this);
            clusterManager.setOnClusterItemClickListener(this);
            clusterManager.setOnClusterInfoWindowClickListener(this);
            clusterManager.setOnClusterItemInfoWindowClickListener(this);
            clusterManager.setAnimation(false);
        } else {
            map.setOnMarkerClickListener(this);
            map.setOnInfoWindowClickListener(this);
            map.setInfoWindowAdapter(this);
        }

        //TODO set up clustering and normal version
    }

    @Override
    public void onError(Status status) {
        //TODO implement
    }

    //Search logic

    private LatLngBounds searchBounds;
    private Object searchBoundLock = new Object();

    private LatLngBounds getSearchBounds() {
        synchronized (searchBoundLock) {
            return searchBounds;
        }
    }

    private void validateAndClearSearchBounds(LatLngBounds searchBounds) {
        synchronized (searchBoundLock) {
            if (searchBounds.equals(this.searchBounds)) {
                this.searchBounds = null;
            }
        }
    }

    public void validateAndSearch() {
        synchronized (searchBoundLock) {
            LatLngBounds mapBounds = map.getProjection().getVisibleRegion().latLngBounds;
            if (searchBounds == null) {
                searchBounds = mapBounds;
                startEventSearch(searchBounds,1);
            } else if (!searchBounds.contains(mapBounds.southwest) || !searchBounds.contains(mapBounds.northeast)) {
                searchBounds = mapBounds;
                startEventSearch(searchBounds,1);
            }
        }
    }

    private void startEventSearch(LatLngBounds searchBounds,int page) {
        FetLifeApiIntentService.startClearApiCall(
                EventMapActivity.this,
                FetLifeApiIntentService.ACTION_APICALL_SEARCH_EVENT_BY_LOCATION,
                Double.toString(searchBounds.southwest.latitude),
                Double.toString(searchBounds.southwest.longitude),
                Double.toString(searchBounds.northeast.latitude),
                Double.toString(searchBounds.northeast.longitude),
                Integer.toString(MARKER_SEARCH_LIMIT),
                Integer.toString(page));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventsRetrievedEvent(EventsByLocationRetrievedEvent eventsByLocationRetrievedEvent) {
        if (eventsByLocationRetrievedEvent.getEvents().size() == 0 || !eventsByLocationRetrievedEvent.getSearchBounds().equals(getSearchBounds())) {
            return;
        }
        startEventSearch(getSearchBounds(),eventsByLocationRetrievedEvent.getPage()+1);
        addEvents(eventsByLocationRetrievedEvent.getEvents());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventsRetrieveFailedEvent(EventsByLocationRetrieveFailedEvent eventsByLocationRetrieveFailedEvent) {
        validateAndClearSearchBounds(eventsByLocationRetrieveFailedEvent.getSearchBounds());
    }

    private void addEvents(List<Event> events) {
        Set<Event> eventSet = getFetLifeApplication().getInMemoryStorage().getMapEvents();
        Set<LatLng> positionSet = getFetLifeApplication().getInMemoryStorage().getMapPositions();
        synchronized (eventSet) {
            //TODO implement both clustering and normal version
            for (Event event : events) {
                if (MAX_MARKER_VISIBLE_RANGE > 0 && MapUtil.getRange(event.getPosition(),map.getCameraPosition().target) > MAX_MARKER_VISIBLE_RANGE) {
                    continue;
                }
                if (!eventSet.add(event)) {
                    continue;
                }

                LatLng markerLatLng = new LatLng(event.getLatitude(),event.getLongitude());
                if (!positionSet.add(markerLatLng)) {
                    markerLatLng = getMarkerOffset(markerLatLng,positionSet);
                    positionSet.add(markerLatLng);
                }
                if (USE_CLUSTERING) {
                    event.setPosition(markerLatLng);
                    clusterManager.addItem(event);
                } else {
                    String time = event.getStartDateTime();
                    String snippet = time != null ? SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(time)) : "";
                    map.addMarker(new MarkerOptions().position(markerLatLng).title(event.getName()).icon(BitmapDescriptorFactory
                            .defaultMarker(getMarkerColorForEvent(event))).snippet(snippet));
                }
            }
            if (USE_CLUSTERING) {
                clusterManager.cluster();
            }
        }
    }

    private LatLng getMarkerOffset(LatLng markerLatLng, Set<LatLng> positionSet) {
        double offset = 0.00003d;
        double offsetAdd = offset/2;
        double baseDegrees = 45.0;
        double degrees = 0;
        LatLng newMarkerLatLng;
        do {
            if (degrees >= 360) {
                baseDegrees /= 2;
                degrees = baseDegrees;
                offset += offsetAdd;
            }
            double rad = Math.toRadians(degrees);
            double newLatitude = markerLatLng.latitude + offset * Math.sin(rad);
            double newLongitude = markerLatLng.longitude + offset * Math.cos(rad);
            degrees += baseDegrees;
            newMarkerLatLng = new LatLng(newLatitude,newLongitude);
        } while (!positionSet.add(newMarkerLatLng));
        return newMarkerLatLng;
    }

    private float getMarkerColorForEvent(Event event) {
        long now = System.currentTimeMillis();
        String eventDateTime = event.getStartDateTime();
        if (eventDateTime == null) {
            return BitmapDescriptorFactory.HUE_CYAN;
        }
        long eventTime = DateUtil.parseDate(eventDateTime);
        long timeDistance = eventTime - now;

        if (timeDistance < 24*60*60*1000) {
            return BitmapDescriptorFactory.HUE_VIOLET;
        } else if (timeDistance < 7*24*60*60*1000) {
            return BitmapDescriptorFactory.HUE_BLUE;
        } else {
            return BitmapDescriptorFactory.HUE_AZURE;
        }
    }

    //Info Window Providing methods

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    //OnClick events

    @Override
    public boolean onClusterClick(Cluster<Event> cluster) {
        //Clustering version
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Event> cluster) {
        //Clustering version
    }

    @Override
    public boolean onClusterItemClick(Event event) {
        //Clustering version
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Event event) {
        //Clustering version
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //Non-Clustering version
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Non-Clustering version
        return false;
    }

    private class DelayedEventRetriever implements Runnable {

        static final long WAIT_FOR_STOP_DELAY = 500l;

        static final int WAITING = 0;
        static final int CANCELLED = -1;
        static final int RUNNING = 1;

        AtomicInteger runState = new AtomicInteger(WAITING);

        DelayedEventRetriever() {
        }

        @Override
        public void run() {
            synchronized (this) {
                try {
                    wait(WAIT_FOR_STOP_DELAY);
                } catch (InterruptedException e) {
                    //skip
                }
            }
            if (runState.compareAndSet(WAITING,RUNNING)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        validateAndSearch();
                    }
                });
            }
        }
    }

}

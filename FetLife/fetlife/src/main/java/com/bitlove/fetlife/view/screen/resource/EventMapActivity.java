package com.bitlove.fetlife.view.screen.resource;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.EventsByLocationRetrieveFailedEvent;
import com.bitlove.fetlife.event.EventsByLocationRetrievedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.json.Event;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.DateUtil;
import com.bitlove.fetlife.util.MapUtil;
import com.bitlove.fetlife.util.ReflectionUtil;
import com.bitlove.fetlife.util.UrlUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class EventMapActivity extends ResourceActivity implements OnMapReadyCallback, PlaceSelectionListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, ClusterManager.OnClusterClickListener<Event>, ClusterManager.OnClusterItemClickListener<Event>, ClusterManager.OnClusterInfoWindowClickListener<Event>, ClusterManager.OnClusterItemInfoWindowClickListener<Event>, GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, LocationListener {

    //Check 500 max range
    //Add progress
    //Solve initial flickering
    //(Cluster) Info Window

    private static final float MIN_ZOOM_LEVEL = 1f;
    private static final float DEFAULT_ZOOM_LEVEL = 10f;
    private static final int MARKER_SEARCH_LIMIT = 100;
    private static final double MAX_MARKER_VISIBLE_RANGE = 0;

    private static final float MARKER_COLOR_NO_DUE = BitmapDescriptorFactory.HUE_CYAN;
    private static final float MARKER_COLOR_SHORT_DUE = BitmapDescriptorFactory.HUE_VIOLET;
    private static final float MARKER_COLOR_MID_DUE = BitmapDescriptorFactory.HUE_BLUE;
    private static final float MARKER_COLOR_LONG_DUE = BitmapDescriptorFactory.HUE_AZURE;
    private static final long MARKER_DUE_SHORT = 24 * 60 * 60 * 1000;
    private static final long MARKER_DUE_MID = 7 * MARKER_DUE_SHORT;

    private static final boolean USE_CLUSTERING = true;
    private static final double MAX_SEARCH_RANGE = 500d;

    private ClusterManager<Event> clusterManager;
    private CustomClusterRenderer customClusterRenderer;
    private LocationManager locationManager;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        showProgress();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                FrameLayout mapContainer = (FrameLayout) findViewById(R.id.map_container);
                LayoutInflater layoutInflater = LayoutInflater.from(EventMapActivity.this);
                View mapContent = layoutInflater.inflate(R.layout.content_eventmap,mapContainer,false);
                mapContainer.addView(mapContent);
                final SupportMapFragment mf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mf.getMapAsync(EventMapActivity.this);
                PlaceAutocompleteFragment searchFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                searchFragment.setOnPlaceSelectedListener(EventMapActivity.this);
            }
        }, 200);
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_eventmap);
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
        if (USE_CLUSTERING) {
            customClusterRenderer.setZoomLevel(map.getCameraPosition().zoom);
        }
        clearLocationUpdates();
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

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            if(location != null){
                onLocationChanged(location);
            } else {
                locationManager.requestLocationUpdates(provider, Long.MAX_VALUE, 0f, this);
            }
//            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//                @Override
//                public void onMyLocationChange(Location location) {
//                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM_LEVEL);
//                    map.moveCamera(cu);
//                    map.setOnMyLocationButtonClickListener(null);
//                }
//            });
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
            customClusterRenderer = new CustomClusterRenderer();
            clusterManager.setRenderer(customClusterRenderer);
            clusterManager.setAnimation(false);
        } else {
            map.setOnMarkerClickListener(this);
            map.setOnInfoWindowClickListener(this);
            map.setInfoWindowAdapter(this);
        }

        if (!FetLifeApiIntentService.ACTION_APICALL_SEARCH_EVENT_BY_LOCATION.equals(FetLifeApiIntentService.getActionInProgress())) {
            dismissProgress();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM_LEVEL));
        clearLocationUpdates();
    }

    private void clearLocationUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        //TODO implement
    }

    @Override
    public void onProviderDisabled(String provider) {
        //TODO implement
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
            if (MapUtil.getRange(mapBounds.getCenter(),mapBounds.southwest) > MAX_SEARCH_RANGE) {
                return;
            }
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
            dismissProgress();
            return;
        }
        startEventSearch(getSearchBounds(),eventsByLocationRetrievedEvent.getPage()+1);
        addEvents(eventsByLocationRetrievedEvent.getEvents());
        dismissProgress();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRetrievalStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_SEARCH_EVENT_BY_LOCATION)) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventsRetrieveFailedEvent(EventsByLocationRetrieveFailedEvent eventsByLocationRetrieveFailedEvent) {
        validateAndClearSearchBounds(eventsByLocationRetrieveFailedEvent.getSearchBounds());
        dismissProgress();
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
                    Marker marker = map.addMarker(new MarkerOptions().position(markerLatLng).title(event.getName()).icon(BitmapDescriptorFactory
                            .defaultMarker(getMarkerColorForEvent(event))).snippet(snippet));
                    marker.setTag(event);
                }
            }
            if (USE_CLUSTERING) {
                clusterManager.cluster();
            }
        }
    }

    private LatLng getMarkerOffset(LatLng markerLatLng, Set<LatLng> positionSet) {
        double offset = 0.00004d;
        double offsetAdd = offset;
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
            return MARKER_COLOR_NO_DUE;
        }
        long eventTime = event.getRoughtDate();
        long timeDistance = eventTime - now;

        if (timeDistance < MARKER_DUE_SHORT) {
            return MARKER_COLOR_SHORT_DUE;
        } else if (timeDistance < MARKER_DUE_MID) {
            return MARKER_COLOR_MID_DUE;
        } else {
            return MARKER_COLOR_LONG_DUE;
        }
    }

    private int getClusterColorForEvents(Collection<Event> items) {
        float markerHue = MARKER_COLOR_LONG_DUE;
        for (Event event : items) {
            float hue = getMarkerColorForEvent(event);
            if (hue == MARKER_COLOR_SHORT_DUE) {
                markerHue = hue;
                break;
            }
            if (hue == MARKER_COLOR_MID_DUE) {
                markerHue = hue;
                continue;
            }
        }
        return ColorUtils.HSLToColor(new float[] {markerHue,1f,0.5f});
    }

    private class CustomClusterRenderer extends DefaultClusterRenderer<Event> {
        private Cluster<Event> clusterToBeRendered;
        private float zoomLevel = 2f;

        private static final float MAX_ZOOM_LEVEL_TO_CLUSTER = 17f;


        public CustomClusterRenderer() {
            super(EventMapActivity.this, map, clusterManager);
        }

        public synchronized void setZoomLevel(float zoomLevel) {
            this.zoomLevel = zoomLevel;
        }

        @Override
        protected synchronized boolean shouldRenderAsCluster(Cluster<Event> cluster) {
            if (zoomLevel > MAX_ZOOM_LEVEL_TO_CLUSTER) {
                return false;
            }
            return cluster.getSize() > 1;
        }

        @Override
        protected void onBeforeClusterItemRendered(Event item,
                                                   MarkerOptions markerOptions) {
            BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(getMarkerColorForEvent(item));
            markerOptions.icon(markerDescriptor);
        }

        @Override
        protected void onClusterItemRendered(Event clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Event> cluster, MarkerOptions markerOptions) {
            clusterToBeRendered = cluster;
            SparseArray icons = ReflectionUtil.getValue("mIcons",this);
            if (icons != null) {
                icons.remove(getBucket(cluster));
            }
            super.onBeforeClusterRendered(cluster,markerOptions);
        }

        @Override
        protected int getColor(int clusterSize) {
            return getClusterColorForEvents(clusterToBeRendered.getItems());
        }

    }

    //OnClick events

    private static final int MAX_CLUSTER_CLICK_COUNT = 42;

    private Cluster<Event> clickedCluster;
    private List<Event> sortedCluster;
    private int eventInCluster = 0;
    private Event clickedEvent;

    @Override
    public boolean onMarkerClick(Marker marker) {
        clickedEvent = (Event) marker.getTag();
        clickedCluster = null;
        return false;
    }

    @Override
    public boolean onClusterItemClick(Event event) {
        clickedEvent = event;
        clickedCluster = null;
        return false;
    }

    @Override
    public boolean onClusterClick(Cluster<Event> cluster) {
        if (cluster.getSize() > MAX_CLUSTER_CLICK_COUNT) {
            return true;
        }
        if (cluster.equals(clickedCluster)) {
            eventInCluster = (eventInCluster+1)%cluster.getSize();
        } else {
            clickedEvent = null;
            clickedCluster = cluster;
            eventInCluster = 0;
            List<Event> eventList = new ArrayList<>(cluster.getItems());
            Collections.sort(eventList);
            sortedCluster = eventList;
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        UrlUtil.openUrl(EventMapActivity.this,clickedEvent.getUrl());
    }

    @Override
    public void onClusterItemInfoWindowClick(Event event) {
        UrlUtil.openUrl(EventMapActivity.this,clickedEvent.getUrl());
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Event> cluster) {
        UrlUtil.openUrl(EventMapActivity.this,sortedCluster.get(eventInCluster).getUrl());
    }

    //Info Window Providing methods

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater layoutInflater = LayoutInflater.from(EventMapActivity.this);
        View layout = layoutInflater.inflate(R.layout.content_eventmap_infowindow,null);
        TextView eventCountInfo = (TextView) layout.findViewById(R.id.event_count_info);
        TextView eventName = (TextView) layout.findViewById(R.id.event_name);
        TextView eventTagLine = (TextView) layout.findViewById(R.id.event_tagline);
        TextView eventDate = (TextView) layout.findViewById(R.id.event_date);

        Event event = clickedEvent != null ? clickedEvent : sortedCluster.get(eventInCluster);
        if (clickedEvent != null) {
            eventCountInfo.setVisibility(View.GONE);
        } else {
            //TODO add text resource
            eventCountInfo.setText("Event #" + (eventInCluster+1) + "/" + clickedCluster.getSize());
            eventCountInfo.setVisibility(View.VISIBLE);
        }

        eventName.setText(event.getName());
        String tagLine = event.getTagline();
        if (TextUtils.isEmpty(tagLine)) {
            eventTagLine.setVisibility(View.GONE);
        } else {
            eventTagLine.setText(tagLine);
            eventTagLine.setVisibility(View.VISIBLE);
        }
        String time = event.getStartDateTime();
        if (TextUtils.isEmpty(time)) {
            eventDate.setVisibility(View.GONE);
        } else {
            eventDate.setText(SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(time)));
            eventDate.setVisibility(View.VISIBLE);
        }
        return layout;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

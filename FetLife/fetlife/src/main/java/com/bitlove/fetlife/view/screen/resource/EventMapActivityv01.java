package com.bitlove.fetlife.view.screen.resource;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.EventsByLocationRetrievedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.json.Event;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.DateUtil;
import com.bitlove.fetlife.util.UrlUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
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
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

//TODO(eventmap) : make resource activity
public class EventMapActivityv01 extends FragmentActivity implements OnMapReadyCallback, PlaceSelectionListener, GoogleMap.OnCameraMoveListener, ClusterManager.OnClusterClickListener<Event>, ClusterManager.OnClusterItemClickListener<Event>, ClusterManager.OnClusterInfoWindowClickListener<Event>, ClusterManager.OnClusterItemInfoWindowClickListener<Event>, GoogleMap.InfoWindowAdapter {

    private static final float DEFAULT_ZOOM_LEVEL = 10f;
    private static final float MIN_ZOOM_LEVEL = 8f;
    private static final float MAX_DISTANCE = 500 * 1000;

    private static final boolean USE_CLUSTER = true;

    private Cluster<Event> clickedCluster;
    private Event clickedClusterItem;

    private DelayedEventRetriever currentDelayedEventRetriever;
    private Location lastLocation = new Location("lastLocation");
    private float lastRange = 1000f;

    private class DelayedEventRetriever implements Runnable {

        static final long WAIT_FOR_STOP_DELAY = 500l;

        static final int WAITING = 0;
        static final int CANCELLED = -1;
        static final int RUNNING = 1;

        private final float latitude;
        private final float longitude;
        private final float range;

        AtomicInteger runState = new AtomicInteger(WAITING);

        DelayedEventRetriever(float latitude,float longitude,float range) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.range = range;
        }

        @Override
        public void run() {
            try {
                synchronized (this) {
                    wait(WAIT_FOR_STOP_DELAY);
                }
            } catch (InterruptedException e) {
                //skip
            }
            if (runState.compareAndSet(WAITING,RUNNING)) {
                FetLifeApiIntentService.startClearApiCall(EventMapActivityv01.this,FetLifeApiIntentService.ACTION_APICALL_SEARCH_EVENT_BY_LOCATION,Float.toString(latitude),Float.toString(longitude),Float.toString(range));
            }
        }
    }

    private class CustomClusterRenderer extends DefaultClusterRenderer<Event> {
        private Cluster<Event> clusterToBeRendered;

        public CustomClusterRenderer() {
            super(EventMapActivityv01.this, mMap, mClusterManager);
        }

//        @Override
//        protected void onBeforeClusterItemRendered(Event item,
//                                                   MarkerOptions markerOptions) {
//            BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(getMarkerColorForEvent(item));
//            markerOptions.icon(markerDescriptor);
//        }
//
//        @Override
//        protected void onClusterItemRendered(Event clusterItem, Marker marker) {
//            super.onClusterItemRendered(clusterItem, marker);
//        }
//
//        @Override
//        protected void onBeforeClusterRendered(Cluster<Event> cluster, MarkerOptions markerOptions) {
//            clusterToBeRendered = cluster;
//            super.onBeforeClusterRendered(cluster,markerOptions);
//        }
//
//        @Override
//        protected int getColor(int clusterSize) {
//            return getClusterColorForEvents(clusterToBeRendered.getItems());
//        }
    }

    private GoogleMap mMap;
    private ClusterManager<Event> mClusterManager;

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, EventMapActivityv01.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    public void onCameraMove() {
        LatLngBounds mapBounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        Location mapLocation = new Location("mapLocation");
        mapLocation.setLatitude(mapBounds.getCenter().latitude);
        mapLocation.setLongitude(mapBounds.getCenter().longitude);

        Location edgeLocation = new Location("edge");
        edgeLocation.setLatitude(mapBounds.northeast.latitude);
        edgeLocation.setLongitude(mapBounds.northeast.longitude);

        float currentRange = Math.min(mapLocation.distanceTo(edgeLocation), MAX_DISTANCE);

//        Log.d("Map","" + lastLocation.distanceTo(mapLocation));
        if (lastLocation.distanceTo(mapLocation) < (lastRange/2)/* && currentRange <= lastRange*/) {
            return;
        }

        lastLocation.setLatitude(mapLocation.getLatitude());
        lastLocation.setLongitude(mapLocation.getLongitude());

        if (currentDelayedEventRetriever != null) {
            currentDelayedEventRetriever.runState.compareAndSet(DelayedEventRetriever.WAITING,DelayedEventRetriever.CANCELLED);
        };

        lastRange = currentRange;
        currentDelayedEventRetriever = new DelayedEventRetriever((float)mapLocation.getLatitude(),(float)mapLocation.getLongitude(),lastRange/1000);

        new Thread(currentDelayedEventRetriever).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(EventMapActivityv01.this);
            }
        }, 1000);
        PlaceAutocompleteFragment searchFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        searchFragment.setOnPlaceSelectedListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        FetLifeApplication.getInstance().getEventBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FetLifeApplication.getInstance().getEventBus().unregister(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setMinZoomPreference(MIN_ZOOM_LEVEL);
        mMap.setOnCameraMoveListener(this);

        getFetLifeApplication().getInMemoryStorage().getMapEvents().clear();

        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build());

        LatLng defaultLocation;

        if (currentLocation != null) {
            defaultLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            defaultLocation = new LatLng(49.2832436,-123.1066479);
        }

        lastLocation.setLatitude(0);
        lastLocation.setLongitude(0);

        if (USE_CLUSTER) {
            mClusterManager = new ClusterManager<>(this, mMap);

            mMap.setOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);

//            mClusterManager.setRenderer(new CustomClusterRenderer());

//            mMap.setOnInfoWindowClickListener(mClusterManager);
//            mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
//            mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(this);
//            mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(this);
              mClusterManager.setOnClusterClickListener(this);
//            mClusterManager.setOnClusterInfoWindowClickListener(this);
//            mClusterManager.setOnClusterItemClickListener(this);
//            mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,DEFAULT_ZOOM_LEVEL));
        onCameraMove();
    }

    private FetLifeApplication getFetLifeApplication() {
        return FetLifeApplication.getInstance();
    }

    @Override
    public void onPlaceSelected(Place place) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),DEFAULT_ZOOM_LEVEL));
        onCameraMove();
    }

    @Override
    public void onError(Status status) {
        //TODO(eventmap) : implement
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventsRetrievedEvent(EventsByLocationRetrievedEvent eventByLocationRetrievedEvent) {
        List<Event> events = eventByLocationRetrievedEvent.getEvents();
        for (Event event : events) {
            addEvent(event);
        }
        mClusterManager.cluster();
    }

    private void addEvent(Event event) {
//        Log.d("Map","Adding event " + event.getPosition());
//        Map<LatLng, Event> eventMap = getFetLifeApplication().getInMemoryStorage().getMapEvents();
//        Event storedEvent = eventMap.get(event.getPosition());
//        if (storedEvent == null) {
//            eventMap.put(event.getPosition(),event);
//            Log.d("Map","No event stored at location " + event.getPosition());
//        } else if (event.getTitle().equals(storedEvent.getTitle()) && event.getSnippet().equals(storedEvent.getSnippet())) {
//            Log.d("Map","Same event found " + event.getPosition());
//            return;
//        }
//
//        if (USE_CLUSTER) {
//            mClusterManager.addItem(event);
//        } else {
//            String time = event.getStartDateTime();
//            String snippet = time != null ? SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(time)) : "";
//            mMap.addMarker(new MarkerOptions().position(new LatLng(event.getLatitude(),event.getLongitude())).title(event.getName()).icon(BitmapDescriptorFactory
//                    .defaultMarker(getMarkerColorForEvent(event))).snippet(snippet));
//        }
//        Log.d("Map","Adding marker " + event.getDistance() + " " + event.getName());
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

    private int getClusterColorForEvents(Collection<Event> items) {
        int color = 0xff00ffff;
        for (Event event : items) {
            float markerColor = getMarkerColorForEvent(event);
            if (markerColor == BitmapDescriptorFactory.HUE_VIOLET) {
                return 0xffee82ee;
            } else if (markerColor == BitmapDescriptorFactory.HUE_BLUE) {
                color = Color.BLUE;
            } else {
                color = 0xff007fff;
            }
        }
        return color;
    }

    @Override
    public boolean onClusterClick(Cluster<Event> cluster) {
        clickedCluster = cluster;
        clickedClusterItem = null;
        return false;
    }

    @Override
    public boolean onClusterItemClick(Event event) {
        clickedClusterItem = event;
        clickedCluster = null;
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Event> cluster) {
        getFetLifeApplication().showToast(cluster.getSize() + " events clicked");
    }

    @Override
    public void onClusterItemInfoWindowClick(Event event) {
        UrlUtil.openUrl(EventMapActivityv01.this, event.getUrl());
//        LatLng location = null;
//        for (Event event : cluster.getItems()) {
//            if (location == null) {
//                location = event.getPosition();
//                continue;
//            }
//            if (!location.equals(event.getPosition())) {
//                return false;
//            }
//        }
//        getFetLifeApplication().showToast("Same Location");
//        return true;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (clickedClusterItem != null) {
            TextView textView = new TextView(EventMapActivityv01.this);
            textView.setText(clickedClusterItem.getName());
            return textView;
        } else {
            TextView textView = new TextView(EventMapActivityv01.this);
            textView.setText("Event count: " + clickedCluster.getSize());
            return textView;
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }



    //TODO(eventmap) : implement progress and failure

}

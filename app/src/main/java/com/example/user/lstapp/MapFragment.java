package com.example.user.lstapp;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class MapFragment extends Fragment implements MapEventsReceiver {

    private static View view;
    private mapFragListener mapListener;

    private MapView mMapView;
    protected ResourceProxy mResourceProxy;
    private MapController mapController;
    private ItemizedOverlay<OverlayItem> myLocationOverlay;
    ArrayList<OverlayItem> overlays; //I don't think we need this?

    ArrayList<Marker> mMarkers;
    private static GeoPoint defLoc = null;
    private static boolean markerMode; //if true allow insertion of new markers onto the map

    private OnMarkerClickListener mListener;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if(getArguments() != null){
            float[] arr = getArguments().getFloatArray("init_location");
            defLoc = new GeoPoint((double)arr[0], (double)arr[1]);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mapListener = (mapFragListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement mapFragListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMarkers = new ArrayList<Marker>();
        if (container == null)
            return null;
        mResourceProxy = new DefaultResourceProxyImpl(inflater.getContext().getApplicationContext());
        View V = inflater.inflate(R.layout.osm_main, container, false);

        mMapView = (MapView) V.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mMapView.setBuiltInZoomControls(true);
        mMapView.getController().setZoom(12);
        mMapView.getController().setCenter(defLoc);

        ToggleButton tb = (ToggleButton) V.findViewById(R.id.marker_mode_button);
        tb.setOnClickListener(new OnClickListener () {public void onClick(View v) {
            //start/stop allowing new markers to be added
            markerMode = !markerMode;
        }});

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), this);
        mMapView.getOverlays().add(0, mapEventsOverlay);

        overlays = new ArrayList<OverlayItem>();
        overlays.add(new OverlayItem("New Overlay", "Overlay Description", defLoc));
        this.myLocationOverlay = new ItemizedIconOverlay<OverlayItem>(overlays, null, mResourceProxy);

        this.mMapView.getOverlays().add(this.myLocationOverlay);
        mMapView.invalidate();//force redraw
        return V;
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {//related to mapeventsoverlay interface
        //Toast.makeText(getActivity(), "Tapped", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {//related to mapeventsoverlay interface
        //DO NOTHING FOR NOW:
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mapListener = null;
    }

    private static double[] testArr = {0.0, .1, .2, .3, .4, .5, .6, .7, .8, .9, 1.0};
    private static int testI = 0;
    public double testPok(){ //just a random array
        double ret = testArr[testI];
        testI = (testI + 1) % testArr.length;
        return ret;
    }

    public void testOverlay(Location l){
//        //overlays.add(new OverlayItem("New Overlay", "Overlay Description", new GeoPoint(l)));
        Marker startMarker = new Marker(mMapView);
        mMarkers.add(startMarker);
        startMarker.setPosition(new GeoPoint(l));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMarker);
        mMapView.invalidate();//force redraw
        Toast.makeText(getActivity(), "testing add overlay", //getResources().getString(R.string.location_updated_message),
                Toast.LENGTH_SHORT).show();
    }

    public interface mapFragListener {
        public void sendMapDefaultLocation(Location l);
    }
}
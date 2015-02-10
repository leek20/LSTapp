package com.example.user.lstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.ResourceProxyImpl;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;

import java.util.Date;
import java.util.HashMap;

public class MapFragment extends Fragment implements OnMarkerClickListener, OnMapClickListener {

    private static View view;
    public static boolean setup = false;
    private mapFragListener mapListener;

    private MapView         mMapView;
    protected ResourceProxy mResourceProxy;
    private MapController mapController;

    private static GeoPoint defLoc = null;
    private static boolean markerMode; //if true allow insertion of new markers onto the map
    private HashMap<Marker, Circle> mCircles = null;

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
        if (container == null)
            return null;
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
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

        return V;

//        if (mMap != null) {
//            try {
//                mMap.setOnMarkerClickListener((OnMarkerClickListener) this);
//            } catch (ClassCastException e) {
//                throw new ClassCastException("map must implement OnMarkerClickListener");
//            }
//
//            try{
//                mMap.setOnMapClickListener((OnMapClickListener) this);
//            }catch (ClassCastException e) {
//                throw new ClassCastException("map must implement OnMapClickListener");
//            }
//            setUpMap();
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mapListener = null;
    }

    public void drawRangeMarker(Marker marker){
        LatLng center = marker.getPosition();
        //int color = Color.argb(127, 255, 0, 255); //alpha, red, green, blue
//        Circle circle = mMap.addCircle(new CircleOptions()
//                .center(center)
//                .radius(Constants.map_radius) //find better way to do this than integers.xml -> have to get resources
//                .strokeColor(Color.argb(Constants.l0_s[0], Constants.l0_s[1], Constants.l0_s[2], Constants.l0_s[3]))
//                .fillColor(Color.argb(Constants.l0_f[0], Constants.l0_f[1], Constants.l0_f[2], Constants.l0_f[3])));
//        mCircles.put(marker, circle);
    }

    public boolean onMarkerClick(Marker marker){
        //TODO: call LST functions to show pok of region
        if(marker.isInfoWindowShown()){//TODO: this isn't perfect, only looks at isVisible() attribute - look at marker listeners maybe?
            marker.hideInfoWindow();
        } else{
            marker.showInfoWindow();

            //this is all for testing - fill in correct implementation later
            double pok = testPok();
            String update = "Pok: " + pok;
            marker.setSnippet(update);
            updateCircle(marker, pok);
        }
        return true;
    }

    private void updateCircle(Marker marker, double pok){
        Circle circle = mCircles.get(marker);
        int[] arr_f = null;
        int[] arr_s = null;
        if(pok <= Constants.l0_cuttoff){
            arr_f = Constants.l0_f;
            arr_s = Constants.l0_s;
        } else if(pok <= Constants.l1_cuttoff){
            arr_f = Constants.l1_f;
            arr_s = Constants.l1_s;
        } else{
            arr_f = Constants.l2_f;
            arr_s = Constants.l2_s;
        }
        circle.setFillColor(Color.argb(arr_f[0], arr_f[1], arr_f[2], arr_f[3]));
        circle.setStrokeColor(Color.argb(arr_s[0], arr_s[1], arr_s[2], arr_s[3]));
    }

    private static double[] testArr = {0.0, .1, .2, .3, .4, .5, .6, .7, .8, .9, 1.0};
    private static int testI = 0;
    public double testPok(){ //just a random array
        double ret = testArr[testI];
        testI = (testI + 1) % testArr.length;
        return ret;
    }

    public void onMapClick(LatLng point){
        if(markerMode) {
//            Marker pt = mMap.addMarker(new MarkerOptions().
//                    position(point).title(" ").snippet("Pok: " + testPok()));
//            drawRangeMarker(pt);
            //TODO: insertPointIntoFilter(point);
        } else{ //TODO: check if in one of the circles
            //TODO: insertPointIntoFilter(point);
        }
    }

    public interface mapFragListener {
        public void sendMapDefaultLocation(Location l);
    }
}
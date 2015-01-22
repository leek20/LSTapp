package com.example.user.lstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.ToggleButton;

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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMarkerClickListener, OnMapClickListener {

    private static View view;
    /**
     * Note that this may be null if the Google Play services APK is not
     * available.
     */

    private static GoogleMap mMap;
    private static Double latitude, longitude;
    private static boolean markerMode; //if true allow insertion of new markers onto the map
    private HashMap<Marker, Circle> mCircles = null;
    private LSTFilter filter = null;
    private Date cTime = null;

    private OnMarkerClickListener mListener;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        filter = new LSTFilter();
        cTime = new Date();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        view = inflater.inflate(R.layout.fragment_map, container, false);
        // Passing harcoded values for latitude & longitude. Please change as per your need. This is just used to drop a Marker on the Map
        latitude = 26.78;
        longitude = 72.56;

        FragmentManager fm = getChildFragmentManager();

        setUpMapIfNeeded(fm); // For setting up the MapFragment

        mCircles = new HashMap<Marker, Circle> ();

        ToggleButton tb = (ToggleButton) view.findViewById(R.id.marker_mode_button);
        tb.setOnClickListener(
                new OnClickListener () {
                    public void onClick(View v) {
                        //start/stop allowing new markers to be added
                        markerMode = !markerMode;
                    }
                }
        );
        if (mMap != null) {
            try {
                mMap.setOnMarkerClickListener((OnMarkerClickListener) this);
            } catch (ClassCastException e) {
                throw new ClassCastException("map must implement OnMarkerClickListener");
            }

            try{
                mMap.setOnMapClickListener((OnMapClickListener) this);
            }catch (ClassCastException e) {
                throw new ClassCastException("map must implement OnMapClickListener");
            }
            setUpMap();
        }

        return view;
    }

    /***** Sets up the map if it is possible to do so *****/
    public static void setUpMapIfNeeded(FragmentManager fm) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment smf = (SupportMapFragment) fm.findFragmentById(R.id.location_map);
            mMap = smf.getMap();
            //mMap = ((SupportMapFragment) fm.findFragmentById(R.id.location_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the
     * camera.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap}
     * is not null.
     */
    private static void setUpMap() {
        // For showing a move to my loction button
        mMap.setMyLocationEnabled(true);
        // For dropping a marker at a point on the Map
        //Marker pt = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,
        //        longitude)).snippet("Home Address"));
        // For zooming automatically to the Dropped PIN Location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                longitude), 12.0f));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mMap != null)
            setUpMap();

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.location_map)).getMap();
            // Check if we were successful in obtaining the map.
        }
    }

    /**** The mapfragment's id must be removed from the FragmentManager
     **** or else if the same it is passed on the next time then
     **** app will crash ****/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            //getActivity().getSupportFragmentManager().beginTransaction()
            //        .remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.location_map));//.commit();
            //mMap = null;
        }
    }

    public void drawRangeMarker(Marker marker){
        LatLng center = marker.getPosition();
        //int color = Color.argb(127, 255, 0, 255); //alpha, red, green, blue
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(center)
                .radius(Constants.map_radius) //find better way to do this than integers.xml -> have to get resources
                .strokeColor(Color.argb(Constants.l0_s[0], Constants.l0_s[1], Constants.l0_s[2], Constants.l0_s[3]))
                .fillColor(Color.argb(Constants.l0_f[0], Constants.l0_f[1], Constants.l0_f[2], Constants.l0_f[3])));
        mCircles.put(marker, circle);
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
        if(pok <= Constants.l0_cuttoff){
            circle.setFillColor(Color.argb(Constants.l0_f[0],
                    Constants.l0_f[1], Constants.l0_f[2], Constants.l0_f[3]));
            circle.setStrokeColor(Color.argb(Constants.l0_s[0],
                    Constants.l0_s[1], Constants.l0_s[2], Constants.l0_s[3]));
        } else if(pok <= Constants.l1_cuttoff){
            circle.setFillColor(Color.argb(Constants.l0_f[0],
                    Constants.l1_f[1], Constants.l1_f[2], Constants.l1_f[3]));
            circle.setStrokeColor(Color.argb(Constants.l0_s[0],
                    Constants.l1_s[1], Constants.l1_s[2], Constants.l1_s[3]));
        } else{
            circle.setFillColor(Color.argb(Constants.l0_f[0],
                    Constants.l2_f[1], Constants.l2_f[2], Constants.l2_f[3]));
            circle.setStrokeColor(Color.argb(Constants.l0_s[0],
                    Constants.l2_s[1], Constants.l2_s[2], Constants.l2_s[3]));
        }
    }

    private static double[] testArr = {0.0, .1, .2, .3, .4, .5, .6, .7, .8, .9, 1.0};
    private static int testI = 0;
    public double testPok(){ //just a random array
        double ret = testArr[testI];
        testI = (testI + 1) % testArr.length;
        return ret;
    }

    public void insertPointIntoFilter(LatLng point){
        if(filter != null){
            long t = cTime.getTime();
            float l = (float) point.longitude;
            float ll = (float) point.latitude;
            filter.insert(new STPoint((float) point.longitude, (float) point.latitude, cTime.getTime()));
        }
    }

    public void onMapClick(LatLng point){
        if(markerMode) {
            Marker pt = mMap.addMarker(new MarkerOptions().
                    position(point).title(" ").snippet("Pok: " + testPok()));
            drawRangeMarker(pt);
            //insertPointIntoFilter(point);
        } else{ //check if in one of the circles
            //insertPointIntoFilter(point); //for testing purposes
        }
    }

    public boolean drawTest(){ //EXAMPLE: of drawing on map, TODO
        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(0, 0), new LatLng(0, 5), new LatLng(3, 5), new LatLng(0, 0))
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));
        return true;
    }
}
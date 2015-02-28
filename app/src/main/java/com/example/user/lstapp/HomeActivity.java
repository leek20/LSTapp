package com.example.user.lstapp;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.nathanielwendt.lstrtree.SQLiteRTree;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends ActionBarActivity implements
        SettingsFragment.OnFragmentInteractionListener, ActionBar.TabListener,
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
        MapFragment.mapFragListener, PlacesFragment.PlacesFragmentInteractionListener,
        CreatePlaceFragment.CreatePlaceFragmentDoneListener, PlaceFragment.OnFragmentInteractionListener {

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private LSTFilter filter;

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static boolean mTracking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SQLiteRTree rtree = new SQLiteRTree(this, "RTreeMain");
        filter = new LSTFilter(rtree);

        // Set up the action bar to show tabs.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // for each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText(R.string.action_settings)
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.action_map)
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.action_places)
                .setTabListener(this));

        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(10000);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(5000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //TODO: PRIORITY_BALANCED_POWER_ACCURACY instead?
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current tab position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current tab position.
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Fragment mFragment = getSupportFragmentManager().findFragmentByTag(tab.getText().toString());
        if (mFragment == null) {
            String tag = tab.getText().toString();
            Bundle args = new Bundle();
            int position = getSupportActionBar().getSelectedNavigationIndex ();
            if (position == 0){
                mFragment = SettingsFragment.newInstance();
            } else if (position == 1){
                mFragment = new MapFragment();
                float[] location = {(float) mLastLocation.getLatitude(), (float) mLastLocation.getLongitude()};
                args.putFloatArray("init_location", location);
            } else{
                mFragment = (Fragment) PlaceFragment.newInstance("", "");
                //tag = "CREATEPLACE";
                //mFragment = (Fragment) PlacesFragment.newInstance("placeholderParam1", "placeholderParam2");
            }
            mFragment.setArguments(args);
            FragmentTransaction f = getSupportFragmentManager().beginTransaction();
            f.add(R.id.container, mFragment, tag);

            f.commit();
        } else {
            //FragmentTransaction f = getSupportFragmentManager().beginTransaction();
            ft.show(mFragment);
            //f.commit();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Fragment mFragment = getSupportFragmentManager().findFragmentByTag(tab.getText().toString());
        if (mFragment != null)
            ft.hide(mFragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        //Fragment mFragment = getSupportFragmentManager().findFragmentByTag("Map");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void notifyTracking(boolean nStatus){
        mTracking = nStatus;
        if(mTracking){
            startLocationUpdates();
        } else{
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null)
            Toast.makeText(this, "No location detected. Make sure location is enabled on the device.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("basic-location-sample", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("basic-location-sample", "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location l) {
        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        Fragment mFragment = getSupportFragmentManager().findFragmentByTag("Map");
//        if(mFragment != null)
//            ((MapFragment) mFragment).testOverlay(location);
        if(mTracking) {
            STPoint pt = new STPoint((float) l.getLongitude(),
                    (float) l.getLatitude(), System.currentTimeMillis());
            filter.insert(pt);
            double prob = filter.pointPoK(pt);
            String loc = "lat: " + l.getLatitude() + " long: " + l.getLongitude() + " PoK: " + prob;
            Toast.makeText(this, loc, Toast.LENGTH_SHORT).show();
        }
    }

    public void sendMapDefaultLocation(Location l){//TODO: eventually turn this into query function

    }

    @Override
    public void createPlace(String regionAsString) {
        Fragment oldFragment = getSupportFragmentManager().findFragmentByTag("Settings");

//        SharedPreferences sharedpreferences = getSharedPreferences("Places", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.clear().commit();

        Fragment mFragment = (Fragment) CreatePlaceFragment.newInstance(regionAsString);
        FragmentTransaction f = getSupportFragmentManager().beginTransaction();
        f.add(R.id.container, mFragment, "CREATEPLACE");
        if (mFragment != null)
            f.hide(oldFragment);
        f.commit();
    }

    @Override
    public void onFragmentDone() {
        Log.d("LST", "fragment is done from picture");

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("CREATEPLACE");
        if(fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        Fragment oldFragment = getSupportFragmentManager().findFragmentByTag("Settings");
        FragmentTransaction f = getSupportFragmentManager().beginTransaction();
        f.show(oldFragment);
        f.commit();
//        FragmentManager mgr = getSupportFragmentManager();
//        FragmentTransaction transaction = mgr.beginTransaction();
//
//        // Create new fragment and transaction
//        Bundle args = new Bundle();
//        Fragment mFragment = new MapFragment();
//        float[] location = {(float) mLastLocation.getLatitude(), (float) mLastLocation.getLongitude()};
//        args.putFloatArray("init_location", location);
//        mFragment.setArguments(args);
//
//        // Replace whatever is in the fragment_container view with this fragment,
//        // and add the transaction to the back stack
//        transaction.replace(R.id.container, mFragment);
//        transaction.addToBackStack(null);
//
//        // Commit the transaction
//        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    public void queryPins(View view) {
        // Do something in response to button
        Toast.makeText(this, "Query Timeeeeee~~", Toast.LENGTH_SHORT).show();
        View lGroup = this.findViewById(R.id.lower_group);
        View uGroup = this.findViewById(R.id.upper_group);
        if (lGroup != null && uGroup != null) {
            SeekBar lSeek = (SeekBar) lGroup.findViewById(R.id.seek_lower);
            SeekBar uSeek = (SeekBar) uGroup.findViewById(R.id.seek_upper);
            int lTimeBnd = lSeek.getProgress();
            int uTimeBnd = lSeek.getProgress();
            MapFragment mFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag("Map");
            ArrayList<Polygon> areasOfInterest = mFragment.rectangles;

            ArrayList<Double> poks = new ArrayList<Double>();
            for(Polygon poly : areasOfInterest){
                STRegion region = rectToRegion(poly.getPoints());
                double p = filter.windowPoK(region);
                poks.add(p);
            }
            mFragment.drawAllQueryResults(poks);
            //Toast.makeText(this, "Query Redraw Complete", Toast.LENGTH_SHORT).show();
        }
    }

    public void confirmPin(View view) {
        //Toast.makeText(this, "Drop the pin", Toast.LENGTH_SHORT).show();
        Fragment mFragment = getSupportFragmentManager().findFragmentByTag("Map");
        if(mFragment != null) {
            Marker pin = ((MapFragment) mFragment).confirmPinDropped();
            if(pin == null)
                return;
            int ind = ((MapFragment) mFragment).mMarkers.indexOf(pin); //markers and rectangles are indexed the same
            List<GeoPoint> bnds = ((MapFragment) mFragment).rectangles.get(ind).getPoints();
            String qRegion = rectToRegion(bnds).toString();
            createPlace(qRegion);
        }
    }

    public STRegion rectToRegion(List<GeoPoint> bnds){//not actually sure what to do with time bounds/how to integrate them
        GeoPoint min = bnds.get(0);
        GeoPoint max = bnds.get(0);
        for(GeoPoint pt : bnds){
            if(pt.getLongitude() > max.getLongitude() || pt.getLatitude() > max.getLatitude())
                max = pt;
            if(pt.getLongitude() < min.getLongitude() || pt.getLatitude() < min.getLatitude())
                min = pt;
        }

        STPoint b = new STPoint((float)min.getLongitude(), (float)min.getLatitude());
        STPoint e = new STPoint((float)max.getLongitude(), (float)max.getLatitude());
        return new STRegion(b, e);
    }

    public void undoSelection(View view) {
        //Toast.makeText(this, "Undo pin stub~~", Toast.LENGTH_SHORT).show();
        Fragment mFragment = getSupportFragmentManager().findFragmentByTag("Map");
        if(mFragment != null)
            ((MapFragment) mFragment).undoLastPin();
    }

    @Override
    public double windowPoK(STRegion region) {
        return Math.random();
        //return filter.windowPoK(region);
    }
}

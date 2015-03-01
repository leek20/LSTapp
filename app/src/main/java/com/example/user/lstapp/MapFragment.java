package com.example.user.lstapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Calendar;

public class MapFragment extends Fragment implements MapEventsReceiver{

    private static View view;
    private mapFragListener mapListener;

    private MapView mMapView;
    protected ResourceProxy mResourceProxy;
    private MapController mapController;
    private ItemizedOverlay<OverlayItem> myLocationOverlay;
    public ArrayList<Overlay> overlays; //I don't think we need this?

    private SeekBar minTime;
    private SeekBar maxTime;
    private TextView minTimeLabel;
    private TextView maxTimeLabel;
    private Button windowPoKBtn;
    private Button clearBtn;

    public ArrayList<Marker> mMarkers;
    private static GeoPoint defLoc = null;
    private static Marker nLocation = null; //associated w/nLocationDrop
    private boolean nLocationDrop = false; //ensures only one location drop
    private static boolean markerMode; //if true allow insertion of new markers onto the map

    private OnMarkerClickListener mListener;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        overlays = new ArrayList<Overlay>();
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

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), this);
        mMapView.getOverlays().add(0, mapEventsOverlay);

        windowPoKBtn = (Button) V.findViewById(R.id.window_pok_button);
        windowPoKBtn.setOnClickListener(windowPoKListener);

        clearBtn = (Button) V.findViewById(R.id.clear_button);
        clearBtn.setOnClickListener(clearBtnListener);

        minTime = (SeekBar) V.findViewById(R.id.seek_lower);
        maxTime = (SeekBar) V.findViewById(R.id.seek_upper);
        minTimeLabel = (TextView) V.findViewById(R.id.seek_lower_label);
        maxTimeLabel = (TextView) V.findViewById(R.id.seek_upper_label);

        minTime.setOnSeekBarChangeListener(minTimeListener);
        maxTime.setOnSeekBarChangeListener(maxTimeListener);
        return V;
    }

    private String progressToLabel(int progress){
        String result;
        progress += 1;
        if(progress <= 12){
            if(progress == 12){
                result = "12:00 pm";
            } else {
                result = String.valueOf(progress) + ":00 am";
            }
        } else {
            if(progress == 24){
                result = "12:00 am";
            } else {
                result = String.valueOf(progress - 12) + ":00 pm";
            }
        }
        return result;
    }

    private Button.OnClickListener windowPoKListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            clearMap();
            int minTimeProgress = minTime.getProgress() + 1; //0 is 1am
            int maxTimeProgress = maxTime.getProgress() + 1; //0 is 1am

            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            if(hour == 0){ hour = 24; } //wraparound to match sliders

            int minute = now.get(Calendar.MINUTE);
            int second = now.get(Calendar.SECOND);
            int millis = now.get(Calendar.MILLISECOND);

            double overshoot = ((minute * 60) + second) * 1000 + millis;
            double currMS = System.currentTimeMillis() - overshoot - (3600 * 1000 * 24); //normalized to the hour ref period

            double minMS, maxMS;

            minMS = currMS - ((hour - minTimeProgress) * 60 * 60 * 1000);
            maxMS = currMS - ((hour - maxTimeProgress) * 60 * 60 * 1000);
            IGeoPoint mapTopLeft = mMapView.getProjection().fromPixels(0, 0);
            float topLatitude = (float)(mapTopLeft.getLatitudeE6())/1000000;
            float leftLongitude = (float)(mapTopLeft.getLongitudeE6())/1000000;

            float latitudeSpan = (float)(mMapView.getLatitudeSpan())/1000000;
            float longitudeSpan = (float)(mMapView.getLongitudeSpan()/1000000);
            Log.d("LST", "span is " + latitudeSpan + " , " + longitudeSpan);

            Log.d("LST", String.valueOf(mMapView.getZoomLevel()));

            IGeoPoint mapBottomRight
                    = mMapView.getProjection().fromPixels(mMapView.getWidth(), mMapView.getHeight());
            float bottomLatitude = (float)(mapBottomRight.getLatitudeE6())/1000000;
            float rightLongitude = (float)(mapBottomRight.getLongitudeE6())/1000000;

            GeoPoint mapCenter = (GeoPoint) mMapView.getMapCenter();

            STPoint minPoint = new STPoint(leftLongitude, bottomLatitude, (float) minMS);
            STPoint maxPoint = new STPoint(rightLongitude, topLatitude, (float) maxMS);
            STRegion mapRegion = new STRegion(minPoint, maxPoint);
            Log.d("LST", mapRegion.toString());
            double pok = mapListener.windowPoK(mapRegion, false);
            Log.d("LST", String.valueOf(pok));

            double zoomLevel = mMapView.getZoomLevel();
            double radius = 1200 * pok;
            if(radius < 200){
                radius = 200;
            }
            Polygon rect = new Polygon(getActivity());
            rect.setPoints(Polygon.pointsAsCircle(mapCenter, radius));
            drawQueryResult(rect, pok, false);

            //Get the screen position:
            SimpleTextOverlay textOverlay = new SimpleTextOverlay(getActivity(), pok);
            mMapView.getOverlays().add(textOverlay);
            overlays.add(textOverlay);

            rect.setStrokeWidth(2);
            overlays.add(rect);
            mMapView.getOverlays().add(rect);
            mMapView.invalidate();//force redraw
        }
    };

    private Button.OnClickListener clearBtnListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            clearMap();
        }
    };

    public void clearMap(){
        for(Overlay overlay: overlays){
            mMapView.getOverlays().remove(overlay);
        }
        undoLastPin();
        mMapView.invalidate();//force redraw
    }

    private SeekBar.OnSeekBarChangeListener minTimeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            minTimeLabel.setText(progressToLabel(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private SeekBar.OnSeekBarChangeListener maxTimeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            maxTimeLabel.setText(progressToLabel(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    static int iii = 0;

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {//related to mapeventsoverlay interface
        //Toast.makeText(getActivity(), "Tapped", Toast.LENGTH_SHORT).show();
        if(nLocationDrop)
            return true;
        View view = getView();

        Polygon rect = new Polygon(getActivity());
        rect.setPoints(Polygon.pointsAsRect(p, 500.0, 500.0));
        drawQueryResult(rect, .15, false);
//        if(iii == 0) {
//            drawQueryResult(rect, 0.0, false);
//        } else if (iii == 1){
//            drawQueryResult(rect, .15, false);
//        } else if (iii == 2){
//            drawQueryResult(rect, .3, false);
//        } else if (iii == 3){
//            drawQueryResult(rect, .6, false);
//        } else{
//            drawQueryResult(rect, .9, false);
//        }
        iii++;
        rect.setStrokeWidth(2);
        overlays.add(rect);
        mMapView.getOverlays().add(rect);

        Marker startMarker = new Marker(mMapView);
        //TODO: this is where you would add a call to add the location to the places tab

        mMarkers.add(startMarker);
        startMarker.setPosition(p);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMarker);

        mMapView.invalidate();//force redraw
        nLocation = startMarker;
        nLocationDrop = true;
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

    public void undoLastPin(){
        if(!nLocationDrop)
            return;
        int len = mMarkers.size();
        if(len > 0){
            Marker last = mMarkers.remove(len - 1);
            last.remove(mMapView);
            Polygon lSquare = (Polygon) overlays.remove(len - 1);
            mMapView.getOverlays().remove(lSquare);
            mMapView.invalidate();
        }
        nLocationDrop = false;
    }

    public Marker confirmPinDropped(){
        if(!nLocationDrop)
            return null;
        nLocationDrop = false;
        return nLocation;
    }

//    public void drawAllQueryResults(ArrayList<Double> poks){
//        int i = 0;
//        for(Polygon r : overlays){
//            drawQueryResult(r, poks.get(i++), false);
//        }
//        mMapView.invalidate();//refresh the map
//    }

    public void drawQueryResult(Polygon area, Double pok, boolean invalidate){
        int fill = 0;
        int stroke = 0;
        if(pok < Constants.lowPoKThresh){
            fill = Color.TRANSPARENT;
            stroke = Color.TRANSPARENT;
        } else if(pok < Constants.stdPoKThresh){//blue
            stroke = transformColor(Constants.outlineC[0]);
            fill = transformColor(Constants.fillC[0]);
        } else if(pok < Constants.mediumPoKThresh){//green
            stroke = transformColor(Constants.outlineC[1]);
            fill = transformColor(Constants.fillC[1]);
        } else if(pok < Constants.highPoKThresh){//yellow
            stroke = transformColor(Constants.outlineC[2]);
            fill = transformColor(Constants.fillC[2]);
        } else{//red
            stroke = transformColor(Constants.outlineC[3]);
            fill = transformColor(Constants.fillC[3]);
        }

        area.setStrokeColor(stroke);
        area.setFillColor(fill);

        if(invalidate)
            mMapView.invalidate();
    }

    private int transformColor(int[] arr){
        //(alpha << 24) | (red << 16) | (green << 8) | blue
        int color = (arr[0] << 24) | (arr[1] << 16) | (arr[2] << 8) | arr[3];
        return color;
    }

    public interface mapFragListener {
        public double windowPoK(STRegion region, boolean snap);
    }
}
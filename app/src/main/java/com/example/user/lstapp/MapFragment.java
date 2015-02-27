package com.example.user.lstapp;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class MapFragment extends Fragment implements MapEventsReceiver{

    private static View view;
    private mapFragListener mapListener;

    private MapView mMapView;
    protected ResourceProxy mResourceProxy;
    private MapController mapController;
    private ItemizedOverlay<OverlayItem> myLocationOverlay;
    ArrayList<Polygon> rectangles; //I don't think we need this?

    ArrayList<Marker> mMarkers;
    private static GeoPoint defLoc = null;
    private static boolean markerMode; //if true allow insertion of new markers onto the map

    private OnMarkerClickListener mListener;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        rectangles = new ArrayList<Polygon>();
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

        //ToggleButton tb = (ToggleButton) V.findViewById(R.id.marker_mode_button);
        //tb.setOnClickListener(new OnClickListener () {public void onClick(View v) {

           // markerMode = !markerMode;
        //}});
        markerMode = true;

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), this);
        mMapView.getOverlays().add(0, mapEventsOverlay);

        //overlays = new ArrayList<OverlayItem>();
        //overlays.add(new OverlayItem("New Overlay", "Overlay Description", defLoc));
        //this.myLocationOverlay = new ItemizedIconOverlay<OverlayItem>(overlays, null, mResourceProxy);
        //this.mMapView.getOverlays().add(this.myLocationOverlay);
        //mMapView.invalidate();//force redraw
        return V;
    }


    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {//related to mapeventsoverlay interface
        //Toast.makeText(getActivity(), "Tapped", Toast.LENGTH_SHORT).show();
        if(markerMode){
            //final GeoPoint q = p;
            //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = getView();//inflater.inflate(R.layout.location_name_dialog, null);
            //final EditText mEdit = (EditText) view.findViewById(R.id.location_dialog);
            final SeekBar sbL = (SeekBar) view.findViewById(R.id.seek_lower);
            final SeekBar sbU = (SeekBar) view.findViewById(R.id.seek_upper);
            Polygon rect = new Polygon(getActivity());
            rect.setPoints(Polygon.pointsAsRect(p, 2000.0, 2000.0));
            rect.setFillColor(0x12121212);
            rect.setStrokeColor(Color.RED);
            rect.setStrokeWidth(2);
            rectangles.add(rect);//oh the irony
            mMapView.getOverlays().add(rect);

            Marker startMarker = new Marker(mMapView);
            int lProgress = sbL.getProgress();
            int uProgress = sbU.getProgress();
            //TODO: this is where you would add a call to add the location to the places tab
            mMarkers.add(startMarker);
            startMarker.setPosition(p);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//                            startMarker.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble, mMapView));
            mMapView.getOverlays().add(startMarker);

            mMapView.invalidate();//force redraw

        }
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
        int len = mMarkers.size();
        if(len > 0){
            Marker last = mMarkers.remove(len - 1);
            last.remove(mMapView);
            Polygon lSquare = rectangles.remove(len - 1);
            mMapView.getOverlays().remove(lSquare);
            mMapView.invalidate();
        }
    }

    public interface mapFragListener {
        public void sendMapDefaultLocation(Location l);
    }
}
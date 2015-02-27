package com.example.user.lstapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ut.mpc.utils.STRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PlaceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<PlaceData> places;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView placesList;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static PlaceFragment newInstance(String param1, String param2) {
        PlaceFragment fragment = new PlaceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlaceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

       // places = getPlaces();
        // TODO: Change Adapter to display your content

    }

    public List<PlaceData> getPlaces(){
        HomeActivity activity = (HomeActivity) getActivity();
        SharedPreferences sharedpreferences = activity.getSharedPreferences("Places", Context.MODE_PRIVATE);
//        Log.d("LST", "getting places");
        Map<String,?> keys = sharedpreferences.getAll();
        List<PlaceData> places = new ArrayList<PlaceData>();
        for(Map.Entry<String,?> entry : keys.entrySet()){
//            Log.d("map values", entry.getKey() + ": " +
//                    entry.getValue().toString());
            String placeName = entry.getKey();
            String[] data = entry.getValue().toString().split("\\*\\*");
            Log.d("LST", data[0]);
            Log.d("LST", data[1]);
            String uri = data[0];
            STRegion bounds = STRegion.fromString(data[1]);
            PlaceData nextPlace = new PlaceData(placeName, uri, bounds);
            places.add(nextPlace);
        }
        return places;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place, container, false);

        places = getPlaces();
        mAdapter = new PlaceDataAdapter(getActivity(), mListener, places);
        placesList = (ListView) view.findViewById(R.id.place_list_view);
        placesList.setAdapter(mAdapter);
        //populatePlaceCoverages();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            int prevSize = places.size();
            Log.d("LST", "places old size: " + prevSize);

            places = getPlaces();
            Log.d("LST", "places new size: " + places.size());
            if(places.size() > prevSize){
                //super hacky way to update list view...
                mAdapter = new PlaceDataAdapter(getActivity(), mListener, places);
                placesList.setAdapter(mAdapter);
            } else {
                populatePlaceCoverages();
            }
        }
    }

    private void populatePlaceCoverages(){
        for(PlaceData place : places){
            View v = placesList.getChildAt(places.indexOf(place));
            TextView tv = (TextView) v.findViewById(R.id.coverage);
            STRegion reg = place.getRegion();
            Object[] arr = new Object[]{reg, tv, place, mListener};
            new PoKTask().execute(arr);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        placesList.setAdapter(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        placesList.setAdapter(null);
    }


//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (null != mListener) {
//            // Notify the active callbacks interface (the activity, if the
//            // fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
//        }
//    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = placesList.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);

        public double windowPoK(STRegion region);
    }

}

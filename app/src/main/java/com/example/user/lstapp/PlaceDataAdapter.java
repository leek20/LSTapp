package com.example.user.lstapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ut.mpc.utils.STRegion;

import java.io.File;
import java.util.List;

public class PlaceDataAdapter extends ArrayAdapter<PlaceData> {
    private final Context context;
    private final List<PlaceData> places;
    private PlacesFragment.OnFragmentInteractionListener mListener;

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return places.size();
    }

    @Override
    public PlaceData getItem(int position) {
        // TODO Auto-generated method stub
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public PlaceDataAdapter(Context context, PlacesFragment.OnFragmentInteractionListener mListener, List<PlaceData> places) {
        super(context, R.layout.place_list_item, places);
        this.context = context;
        this.places = places;
        this.mListener = mListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d("LST", "inside get View");
        View rowView = inflater.inflate(R.layout.place_list_item, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView coverage = (TextView) rowView.findViewById(R.id.coverage);
        ImageView poster = (ImageView) rowView.findViewById(R.id.poster);

        PlaceData place = places.get(position);
        name.setText(place.getName());
        String coverageVal = place.getCoverage();
        if(coverageVal == null){
            //coverage.setText("calculating");
            STRegion reg = place.getRegion();
            Object[] arr = new Object[]{reg, coverage, place, mListener};
            new PoKTask().execute(arr);

        } else {
            coverage.setText(coverageVal);
        }

        File pictureFile = new File(place.getUri());
        if(pictureFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
            poster.setImageBitmap(myBitmap);
        }

        return rowView;
    }
}


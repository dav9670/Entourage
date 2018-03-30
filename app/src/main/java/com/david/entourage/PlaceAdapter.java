package com.david.entourage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by David on 3/21/2018.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private ArrayList<PlaceInfo> nearbyPlaces;

    public PlaceAdapter(ArrayList<PlaceInfo> nearbyPlaces) {
        this.nearbyPlaces = nearbyPlaces;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_layout, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        PlaceInfo placeInfo = nearbyPlaces.get(position);
        holder.setPlaceInfo(placeInfo);
    }

    @Override
    public int getItemCount() {
        return nearbyPlaces.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView_name;

        private PlaceInfo placeInfo;


        public PlaceViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView_name = itemView.findViewById(R.id.textView_name);
        }

        public void setPlaceInfo(PlaceInfo placeInfo){
            this.placeInfo = placeInfo;
            bind();
        }

        public void bind(){
            imageView.setImageBitmap(placeInfo.getPhotos().size() > 0 ? placeInfo.getPhotos().get(0) : null);
            textView_name.setText(placeInfo.getName());
        }
    }
}

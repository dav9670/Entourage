package com.david.entourage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.List;

/**
 * Created by David on 3/21/2018.
 */

public class PlaceAdapter extends  RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private Context mCtx;
    private List<Place> nearbyPlaces;
    private GeoDataClient mGeoDataClient;

    public PlaceAdapter(Context mCtx, List<Place> nearbyPlaces) {
        this.mCtx = mCtx;
        this.nearbyPlaces = nearbyPlaces;
        mGeoDataClient = Places.getGeoDataClient(mCtx, null);
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout, null);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place place = nearbyPlaces.get(position);
        PhotoSetter photoSetter = new PhotoSetter(holder.imageView, mGeoDataClient);
        photoSetter.setImage(place);
        holder.textView_name.setText(place.getName());
    }

    @Override
    public int getItemCount() {
        return nearbyPlaces.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView_name;

        public PlaceViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView_name = itemView.findViewById(R.id.textView_name);
        }
    }
}

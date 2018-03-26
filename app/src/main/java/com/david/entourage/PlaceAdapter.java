package com.david.entourage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

/**
 * Created by David on 3/21/2018.
 */

public class PlaceAdapter extends  RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private Context mCtx;

    public PlaceAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_layout, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place place = AppController.nearbyPlaces.get(position);
        holder.bind(place);
    }

    @Override
    public int getItemCount() {
        return AppController.nearbyPlaces.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView_name;

        public PlaceViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView_name = itemView.findViewById(R.id.textView_name);
        }

        public void bind(Place place){
            if(AppController.getPlacePhotos(place) != null && AppController.getPlacePhotos(place).size()>0){
                imageView.setImageBitmap(AppController.getPlacePhotos(place).get(0));
            }
            textView_name.setText(place.getName());
        }
    }
}

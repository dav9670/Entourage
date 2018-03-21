package com.david.entourage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by David on 3/21/2018.
 */

public class PlaceAdapter extends  RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private Context mCtx;
    private List<HashMap<String,String>> placeList;

    public PlaceAdapter(Context mCtx, List<HashMap<String, String>> placeList) {
        this.mCtx = mCtx;
        this.placeList = placeList;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout, null);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        HashMap<String,String> place = placeList.get(position);
        String imageUrl = place.get("icon");
        new DownloadImageTask(holder.imageView)
            .execute(place.get("icon"));
        holder.textView_name.setText(place.get("name"));
    }

    @Override
    public int getItemCount() {
        return placeList.size();
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

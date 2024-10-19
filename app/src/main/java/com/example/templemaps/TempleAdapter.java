package com.example.templemaps;

//this is for the List activity


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TempleAdapter extends RecyclerView.Adapter<TempleAdapter.TempleViewHolder> {

    private List<Temple> temples;

    public TempleAdapter(List<Temple> temples) {
        this.temples = temples;
    }

    @NonNull
    @Override
    public TempleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.temple_item_layout, parent, false);
        return new TempleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TempleViewHolder holder, int position) {
        Temple temple = temples.get(position);
        holder.templeNameTextView.setText(temple.getName());
        holder.templeDescriptionTextView.setText(temple.getDescription());

        String imageResourceId = temple.getImageResourceId();
        if (imageResourceId.equals("no_image")) {
            // If no image is specified in the JSON file, use the no_image_large.webp drawable
            holder.templeImageView.setImageResource(R.drawable.no_image_large);
        } else {
            // Set the temple image using setImageResource() with getResources().getIdentifier() and getImageResourceId()
            int resId = holder.templeImageView.getContext().getResources().getIdentifier(imageResourceId, "drawable", holder.templeImageView.getContext().getPackageName());
            holder.templeImageView.setImageResource(resId);
        }


    }

    @Override
    public int getItemCount() {
        return temples.size();
    }

    static class TempleViewHolder extends RecyclerView.ViewHolder {
        TextView templeNameTextView;
        TextView templeDescriptionTextView;
        ImageView templeImageView;

        public TempleViewHolder(View itemView) {
            super(itemView);
            templeNameTextView = itemView.findViewById(R.id.templeName);
            templeDescriptionTextView = itemView.findViewById(R.id.description);
            templeImageView = itemView.findViewById(R.id.imageview);
        }
    }
}
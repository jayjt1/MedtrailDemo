package com.medtrail.medtraildemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.medtrail.medtraildemo.ImageInfo;
import com.medtrail.medtraildemo.R;

import java.util.ArrayList;

public class FlickrAdapter extends RecyclerView.Adapter<FlickrAdapter.ViewHolder> {
    public ArrayList<ImageInfo> imgList;
    Context mContext;
    public FlickrAdapter(Context context, ArrayList<ImageInfo> imgList) {

        this.mContext = context;
        this.imgList = imgList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgResult;

        public ViewHolder(View itemView) {
            super(itemView);
            imgResult = (ImageView) itemView.findViewById(R.id.imgResult);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //This is what adds the code we've written in here to our target view

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FlickrAdapter.ViewHolder viewHolder, int i) {

        String largeUrl = imgList.get(i).getThumbURL();

        Glide.with(mContext)
                .load(largeUrl)
                .apply(RequestOptions.placeholderOf(R.mipmap.ic_launcher)
                .apply(new RequestOptions().diskCacheStrategy( DiskCacheStrategy.ALL ))
                .error(R.mipmap.ic_launcher)
                .override(300,300))
                .into(viewHolder.imgResult);
    }

    public ArrayList<ImageInfo> getImageInfo() {
        return imgList;
    }

    @Override
    public int getItemCount() {

        return imgList.size();
    }
}
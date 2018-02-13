package com.shudh4sure.shopping.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.model.OfferBannerModel;
import com.shudh4sure.shopping.utils.AppConstant;
import java.util.List;
public class HomeCategoryOfferAdapter extends RecyclerView.Adapter<HomeCategoryOfferAdapter.ViewHolder> {


    Activity main;
    List<OfferBannerModel> listData;
    LayoutInflater inflater;
    SharedPreferences mprefs;
    String strUserId = "";
    private OnItemClickListener mOnItemClickListener;

    public HomeCategoryOfferAdapter(Activity activity, List<OfferBannerModel> bean) {

        this.main = activity;
        this.listData = bean;
        this.inflater = (LayoutInflater.from(main));

        mprefs = main.getSharedPreferences(AppConstant.PREFS_NAME, Context.MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);

    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public HomeCategoryOfferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_offers, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final HomeCategoryOfferAdapter.ViewHolder holder, final int position) {
        final OfferBannerModel bean = listData.get(position);
        Glide.with(main)
                .load(bean.getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.ic_default_notification)
                .into(holder.offerImage);

        holder.offerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, v, 1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView offerImage;

        public ViewHolder(View v) {
            super(v);
            offerImage = (ImageView) v.findViewById(R.id.offerImage);
        }
    }
}








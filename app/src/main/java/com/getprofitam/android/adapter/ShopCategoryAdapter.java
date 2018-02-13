package com.getprofitam.android.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.getprofitam.android.R;
import com.getprofitam.android.model.HomeCategoryModel;

import java.util.List;



public class ShopCategoryAdapter extends RecyclerView.Adapter<ShopCategoryAdapter.ViewHolder> {

    Activity main;
    List<HomeCategoryModel> listData;
    LayoutInflater inflater;
    private HomeCategoryAdapter.OnItemClickListener mOnItemClickListener;


    public ShopCategoryAdapter(Activity activity, List<HomeCategoryModel> bean) {

        this.main = activity;
        this.listData = bean;
        this.inflater = (LayoutInflater.from(activity));

    }

    public void setOnItemClickListener(final HomeCategoryAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_new_category, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final HomeCategoryModel bean = listData.get(position);

        holder.tvHomeCategoryName.setText(bean.getName());
        Glide.with(main)
                .load(bean.getIcon())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.ic_default_product_one)
                .into(holder.imgHomeCategory);

        holder.lvCategoryLayout.setOnClickListener(new View.OnClickListener() {
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

        TextView tvHomeCategoryName,tvHomeCategoryName2;
        ImageView imgHomeCategory;
        LinearLayout lvCategoryLayout;

        public ViewHolder(View v) {
            super(v);
            tvHomeCategoryName = (TextView) v.findViewById(R.id.tvHomeCategoryName);
            tvHomeCategoryName2 = (TextView) v.findViewById(R.id.tvHomeCategoryName2);
            imgHomeCategory = (ImageView) v.findViewById(R.id.imgHomeCategory);
            lvCategoryLayout = (LinearLayout) v.findViewById(R.id.lvCategoryLayout);

        }

    }

}


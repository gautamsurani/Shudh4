package com.shudh4sure.shopping.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.activity.MyProfileActivity;
import com.shudh4sure.shopping.model.HomeCategoryModel;
import com.shudh4sure.shopping.utils.AppConstant;

import java.util.List;


public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {
    String p = "";
    Activity main;
    List<HomeCategoryModel> listData;
    LayoutInflater inflater;
    SharedPreferences mprefs;
    String strUserId = "";
    ProgressDialog loading;
    private OnItemClickListener mOnItemClickListener;


    public HomeCategoryAdapter(Activity activity, List<HomeCategoryModel> bean) {

        this.main = activity;
        this.listData = bean;
        this.inflater = (LayoutInflater.from(activity));

        mprefs = main.getSharedPreferences(AppConstant.PREFS_NAME, Context.MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);

    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public HomeCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_category, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final HomeCategoryAdapter.ViewHolder holder, final int position) {
        final HomeCategoryModel bean = listData.get(position);
        holder.tvHomeCategoryName.setText(bean.getName());
        /*p = (String) holder.tvHomeCategoryName.getText();
        if (listData.equals(null)) {
            retrycconnection();
            Toast.makeText(main, "Connection Refused", Toast.LENGTH_SHORT).show();
        } else {
        }*/
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
       /*   try {

       if(bean.getName().length()>13){
            String[] itemsDate = bean.getName().split(" ");
            holder.tvHomeCategoryName.setText(itemsDate[0]);
            holder.tvHomeCategoryName2.setText(itemsDate[1]);
        }else {
            holder.tvHomeCategoryName.setText(bean.getName());
            holder.tvHomeCategoryName2.setVisibility(View.GONE);
        }
            String[] itemsDate = bean.getName().split(" ");
            Log.e("One11 ", itemsDate[0]);
            Log.e("One ", itemsDate[1]);

            Log.e("Two", itemsDate[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
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

        TextView tvHomeCategoryName, tvHomeCategoryName2;
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

    /*public void retrycconnection() {
        final Dialog dialog = new Dialog(main);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_noconnection);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listData != null) {

                    dialog.dismiss();
                    loading = ProgressDialog.show(main, "", "Please wait...", false, false);
                } else {
                    Toast.makeText(main, R.string.connectionRefused, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }*/
}








package com.shudh4sure.shopping.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.model.OfferModel;

import java.util.ArrayList;


public class OfferZoneAdapter extends RecyclerView.Adapter<OfferZoneAdapter.ViewHolder> {

    Context context;
    Activity main;
    ArrayList<OfferModel> listData;
    LayoutInflater inflater;
    //  SharedPreferences mprefs;
    //  String strUserId = "";
    private OnItemClickListener mOnItemClickListener;


    public OfferZoneAdapter(Activity activity, Context context, ArrayList<OfferModel> bean) {

        this.main = activity;
        this.listData = bean;
        this.context = context;
        this.inflater = (LayoutInflater.from(context));

        // mprefs = context.getSharedPreferences(AppConstant.PREFS_NAME, context.MODE_PRIVATE);
        //  strUserId = mprefs.getString(AppConstant.USER_ID, null);

    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public OfferZoneAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_offer_promotions, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final OfferZoneAdapter.ViewHolder holder, final int position) {
        final OfferModel bean = listData.get(position);

        holder.tvOfferlines.setText(bean.getMessage());
        holder.tvOfferDates.setText(bean.getAdded_on());

      /*  Glide.with(context)
                .load(bean.getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.ic_launcher)
                .into(holder.imgofferimg);
*/
        Glide.with(context)
                .load(bean.getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.ic_default_notification)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);

                        return false;
                    }
                })
                .into(holder.imgofferimg);


        if(bean.getProduct().equalsIgnoreCase("")){
            holder.btnofferBuynow.setVisibility(View.GONE);
        }else {
            holder.btnofferBuynow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(position, v, 0);
                    }
                }
            });
        }

        holder.imgofferimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, view, 2);
                }
            }
        });


        holder.tvOfferShare.setOnClickListener(new View.OnClickListener() {
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

        TextView tvOfferlines,tvOfferDates,tvOfferShare;
        ImageView imgofferimg;
        Button btnofferBuynow;
        ProgressBar progressBar;

        public ViewHolder(View v) {
            super(v);
            tvOfferlines = (TextView) v.findViewById(R.id.tvOfferlines);
            tvOfferDates = (TextView) v.findViewById(R.id.tvOfferDates);
            tvOfferShare= (TextView) v.findViewById(R.id.tvOfferShare);
            imgofferimg = (ImageView) v.findViewById(R.id.imgofferimg);
            btnofferBuynow = (Button) v.findViewById(R.id.btnofferBuynow);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }

    }

}








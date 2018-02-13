package com.getprofitam.android.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getprofitam.android.R;
import com.getprofitam.android.model.SearchModel;

import java.util.ArrayList;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    //implements Filterable
    Activity context;
    private ArrayList<SearchModel> listData;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;

    public SearchAdapter(Activity context, ArrayList<SearchModel> bean) {

        this.listData = bean;
        this.context = context;
        this.inflater = (LayoutInflater.from(context));

    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productSearchtitle, productLISTSearchtitle;
        RelativeLayout relSearchlayout;

        public ViewHolder(View v) {
            super(v);
            relSearchlayout = (RelativeLayout) v.findViewById(R.id.relSearchlayout);
            productSearchtitle = (TextView) v.findViewById(R.id.productSearchtitle);
            productLISTSearchtitle = (TextView) v.findViewById(R.id.productLISTSearchtitle);
        }

    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_productlist, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final SearchAdapter.ViewHolder holder, final int position) {
        final SearchModel bean = listData.get(position);
        if (bean.getType().equalsIgnoreCase("category")) {
            holder.productLISTSearchtitle.setVisibility(View.VISIBLE);
            holder.productSearchtitle.setTextColor(context.getResources().getColor(R.color.subcatsearch));
            holder.productSearchtitle.setText(bean.getName());
        } else {
            holder.productSearchtitle.setTextColor(context.getResources().getColor(R.color.droverbackground));
            holder.productSearchtitle.setText(bean.getName());
        }

        holder.relSearchlayout.setOnClickListener(new View.OnClickListener() {
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


}








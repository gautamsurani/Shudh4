package com.shudh4sure.shopping.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.model.OrderDetailsModel;
import com.shudh4sure.shopping.utils.AppConstant;

import java.util.ArrayList;


public class ViewItemsAdapter extends RecyclerView.Adapter<ViewItemsAdapter.ViewHolder> {

    Context context;
    Activity main;
    ArrayList<OrderDetailsModel> listData;
    LayoutInflater inflater;
    SharedPreferences mprefs;
    String strUserId = "";
    private OnItemClickListener mOnItemClickListener;


    public ViewItemsAdapter(Activity activity, Context context, ArrayList<OrderDetailsModel> bean) {

        this.main = activity;
        this.listData = bean;
        this.context = context;
        this.inflater = (LayoutInflater.from(context));

        mprefs = context.getSharedPreferences(AppConstant.PREFS_NAME, Context.MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);

    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public ViewItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_viewitems, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewItemsAdapter.ViewHolder holder, final int position) {
        final OrderDetailsModel bean = listData.get(position);

        holder.tvviewtitle.setText(bean.getName());
        holder.tvviewQuanityt.setText(bean.getQuantity());
        holder.tvviewUnitPrice.setText(context.getResources().getString(R.string.Rs)+" "+bean.getPrice());
        holder.tvviewTotal.setText(context.getResources().getString(R.string.Rs)+" "+bean.getLine_total());



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

        TextView tvviewtitle,tvviewQuanityt,tvviewUnitPrice,tvviewTotal;

        public ViewHolder(View v) {
            super(v);
            tvviewtitle = (TextView) v.findViewById(R.id.tvviewtitle);
            tvviewQuanityt = (TextView) v.findViewById(R.id.tvviewQuanityt);
            tvviewUnitPrice= (TextView) v.findViewById(R.id.tvviewUnitPrice);
            tvviewTotal= (TextView) v.findViewById(R.id.tvviewTotal);
        }

    }

}








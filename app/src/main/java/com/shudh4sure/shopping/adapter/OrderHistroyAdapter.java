package com.shudh4sure.shopping.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.activity.OrderTrackActivity;
import com.shudh4sure.shopping.model.MyOrderModel;
import com.shudh4sure.shopping.utils.AppConstant;

import java.util.ArrayList;


public class OrderHistroyAdapter extends RecyclerView.Adapter<OrderHistroyAdapter.ViewHolder> {

    Context context;
    Activity main;
    ArrayList<MyOrderModel> listData;
    LayoutInflater inflater;
    SharedPreferences mprefs;
    String strUserId = "";
    private OnItemClickListener mOnItemClickListener;


    public OrderHistroyAdapter(Activity activity, Context context, ArrayList<MyOrderModel> bean) {

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
    public OrderHistroyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_orderhistory, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final OrderHistroyAdapter.ViewHolder holder, final int position) {
        final MyOrderModel bean = listData.get(position);

        holder.tvOrderId.setText("ORDER #"+bean.getOrderNo());
        holder.tvOrderShippingdate.setText(bean.getDate());
        if (bean.getStatus().equalsIgnoreCase("Canceled")) {
            holder.tvOrderStatus.setText(bean.getStatus());
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.red));
        }else {
            holder.tvOrderStatus.setText(bean.getStatus());
            holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.green));
        }

        holder.tvOrderAmount.setText(context.getResources().getString(R.string.Rs) + " " + String.valueOf(bean.getAmount()));

        holder.tvOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderTrackActivity.class);
                intent.putExtra("OrderId", listData.get(position).getOrderID());

                main.startActivity(intent);
                main.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

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

        TextView tvOrderId,tvOrderDetails,tvOrderStatus,tvOrderShippingdate,tvOrderAmount;

        public ViewHolder(View v) {
            super(v);
            tvOrderId = (TextView) v.findViewById(R.id.tvOrderId);
            tvOrderDetails = (TextView) v.findViewById(R.id.tvOrderDetails);
            tvOrderStatus = (TextView) v.findViewById(R.id.tvOrderStatus);
            tvOrderShippingdate = (TextView) v.findViewById(R.id.tvOrderShippingdate);
            tvOrderAmount = (TextView) v.findViewById(R.id.tvOrderAmount);


        }

    }

}








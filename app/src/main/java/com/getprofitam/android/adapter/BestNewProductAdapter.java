package com.getprofitam.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.getprofitam.android.R;
import com.getprofitam.android.dbhelper.GetprofitamDB;
import com.getprofitam.android.model.BestNewProductModel;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.MyUpdateWishList;
import com.getprofitam.android.utils.SmallBang;

import java.util.ArrayList;


public class BestNewProductAdapter extends RecyclerView.Adapter<BestNewProductAdapter.ViewHolder> {
    Activity main;
    GetprofitamDB getprofitamDB;
    ArrayList<BestNewProductModel> listData;
    LayoutInflater inflater;
    SharedPreferences mprefs;
    String strUserId = "";
    private OnItemClickListener mOnItemClickListener;
    ArrayList<String> ArrForvIEW = new ArrayList<>();
    MyUpdateWishList updateWishList;

    public BestNewProductAdapter(Activity context, ArrayList<BestNewProductModel> bean, GetprofitamDB getprofitamDB, ArrayList<String> Arr) {

        this.main = context;
        this.listData = bean;
        this.getprofitamDB = getprofitamDB;
        this.inflater = (LayoutInflater.from(context));
        ArrForvIEW = Arr;
        mprefs = context.getSharedPreferences(AppConstant.PREFS_NAME, Context.MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);

    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    @Override
    public BestNewProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recy_one_items, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final BestNewProductAdapter.ViewHolder holder, final int position) {
        final BestNewProductModel bean = listData.get(position);
        holder.mSmallBang = SmallBang.attach2Window(main);
        holder.title.setText(bean.getName());
        holder.titleinHindi.setText(bean.getCaption());
        holder.price.setText(main.getResources().getString(R.string.Rs) + " " + bean.getPrice());
        holder.cutprice.setText(main.getResources().getString(R.string.Rs) + " " + bean.getMrp());
        if (bean.getDiscount().equalsIgnoreCase("0")) {
            holder.lvDiscountbg.setVisibility(View.GONE);
        } else {
            holder.discount.setText("" + bean.getDiscount() + "%");
        }
        if (bean.getSold_out().equalsIgnoreCase("No")) {
            holder.tvSoldOut.setVisibility(View.GONE);
        } else {
            holder.lvDiscountbg.setVisibility(View.GONE);
            holder.tvSoldOut.setVisibility(View.VISIBLE);
        }
        //  holder.discount.setText(bean.getDiscount());
        Glide.with(main)
                .load(bean.getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.ic_default_product_one)
                .into(holder.image);

        boolean baa = ArrForvIEW.contains(bean.getProductID());
        if (baa) {
            holder.imgAddtoWishlist.setImageResource(R.drawable.f4);
        } else {
            holder.imgAddtoWishlist.setImageResource(R.drawable.f0);

        }

        holder.imgAddtoWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    ArrayList<String> WishLocalArr = new ArrayList<String>();
                    Cursor c = getprofitamDB.ShowTableWishList();
                    if (c.getCount() > 0) {
                        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                            WishLocalArr.add(c.getString(1));
                            mprefs.edit().putInt(AppConstant.USER_WISHLIST, c.getCount()).apply();
                        }
                        boolean b = WishLocalArr.contains(bean.getProductID());
                        if (b) {
                            holder.imgAddtoWishlist.setImageResource(R.drawable.f0);
                            getprofitamDB.DeleteWishListByID(bean.getProductID());
                        } else {
                            holder.imgAddtoWishlist.setImageResource(R.drawable.f4);
                            holder.mSmallBang.bang(view);
                            getprofitamDB.InsertWishListData(bean.getProductID(), bean.getName(),
                                    bean.getImage(), bean.getPrice(), bean.getMrp(), bean.getCaption());
                        }
                    } else {
                        mprefs.edit().remove(AppConstant.USER_WISHLIST).commit();
                        holder.imgAddtoWishlist.setImageResource(R.drawable.f4);
                        holder.mSmallBang.bang(view);
                        getprofitamDB.InsertWishListData(bean.getProductID(), bean.getName(),
                                bean.getImage(), bean.getPrice(), bean.getMrp(), bean.getMrp());
                    }
                    mOnItemClickListener.onItemClick(position, view, 0);
                }
            }
        });
        holder.lvProductLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, v, 1);
                }

                   /*Intent intent = new Intent(main, ProductDetailsActivity.class);
                    intent.putExtra("catID", bean.getProductID());
                    intent.putExtra("catName", bean.getName());
                     Log.e("getProductID()", bean.getProductID());
                    main.startActivity(intent);
                    main.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView titleinHindi;
        TextView price;
        TextView cutprice;
        TextView discount;
        ImageView imgAddtoWishlist;
        LinearLayout lvDiscountbg;
        LinearLayout lvProductLayout;
        TextView tvSoldOut;
        SmallBang mSmallBang;

        public ViewHolder(View v) {
            super(v);
            this.image = (ImageView) itemView.findViewById(R.id.image);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.titleinHindi = (TextView) itemView.findViewById(R.id.titleinHindi);
            this.price = (TextView) itemView.findViewById(R.id.price);
            this.cutprice = (TextView) itemView.findViewById(R.id.cutprice);
            this.discount = (TextView) itemView.findViewById(R.id.discount);
            this.tvSoldOut = (TextView) itemView.findViewById(R.id.tvSoldOut);
            this.cutprice.setPaintFlags(cutprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.imgAddtoWishlist = (ImageView) itemView.findViewById(R.id.imgAddtoWishlist);
            this.lvDiscountbg = (LinearLayout) itemView.findViewById(R.id.lvDiscountbg);
            this.lvProductLayout = (LinearLayout) itemView.findViewById(R.id.lvProductLayout);
        }
    }
}



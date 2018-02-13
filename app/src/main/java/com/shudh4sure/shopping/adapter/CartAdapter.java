package com.shudh4sure.shopping.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.activity.CartActivity;
import com.shudh4sure.shopping.dbhelper.Shudh4sureDB;
import com.shudh4sure.shopping.model.CartData;
import com.shudh4sure.shopping.utils.AppConstant;

import java.util.ArrayList;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Activity main;
    ArrayList<CartData> listData;
    LayoutInflater inflater;
    SharedPreferences mprefs;
    String strUserId = "";
    private OnItemClickListener mOnItemClickListener;
    Shudh4sureDB shudh4sureDB;
    Cursor cursorCart;

    public CartAdapter(Activity activity, ArrayList<CartData> bean) {

        this.main = activity;
        this.listData = bean;
        this.inflater = (LayoutInflater.from(main));

        mprefs = main.getSharedPreferences(AppConstant.PREFS_NAME, Context.MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        String LocalDbUserSocityIdName = strUserId + "_Shudh4sure_local.db";
        String FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;

        shudh4sureDB = new Shudh4sureDB(main, FinalLocalDBName);
        shudh4sureDB.OpenDB();

    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mycart, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final CartAdapter.ViewHolder holder, final int position) {
        final CartData bean = listData.get(position);

        int ThiQuantity = 1;
        String StrNewPtice = "";

        holder.tvCartProductname.setText(bean.getName());
        holder.tvCartCaption.setText(bean.getCaption());

        //holder.tvCartPrice.setText(main.getResources().getString(R.string.Rs)+" "+bean.getPrice());
        holder.tvCartProductQuanity.setText("Weight : " + bean.getWeight() + "");

        Cursor MyCurserThis = shudh4sureDB.ShowTableCartList();
        for (MyCurserThis.moveToFirst(); !MyCurserThis.isAfterLast(); MyCurserThis.moveToNext()) {
            if (MyCurserThis.getString(2).equalsIgnoreCase(bean.getStrPriceId())) {
                ThiQuantity = MyCurserThis.getInt(7);
                StrNewPtice = MyCurserThis.getString(5);
            }
        }
        try {
            holder.tvCartPrice.setText(main.getResources().getString(R.string.Rs) + " " + ThiQuantity * Double.parseDouble(StrNewPtice) + "");
            holder.tvCartQuantity.setText(String.valueOf(bean.getCountThis()));

        } catch (Exception ex) {
        }
        Glide.with(main)
                .load(bean.getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.ic_default_product_one)
                .into(holder.imgCartImage);

        holder.imgCartDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, view, 0);
                }
            }
        });

        holder.tvCartmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {

                    if (bean.getCountThis() == 1) {
                        bean.countThis = 1;
                        holder.tvCartQuantity.setText(String.valueOf(bean.countThis));

                    } else if (bean.getCountThis() > 1) {

                        int counter = bean.getCountThis();
                        int total = counter - 1;
                        shudh4sureDB.UpdateQuantityByPriceIdInDB(bean.getStrPriceId(), total);
                        int ThiQuantity = 1;
                        String StrNewPtice = "";
                        Cursor MyCurserThis = shudh4sureDB.ShowTableCartList();
                        for (MyCurserThis.moveToFirst(); !MyCurserThis.isAfterLast(); MyCurserThis.moveToNext()) {
                            if (MyCurserThis.getString(2).equalsIgnoreCase(bean.getStrPriceId())) {
                                ThiQuantity = MyCurserThis.getInt(7);
                                StrNewPtice = MyCurserThis.getString(5);

                            }
                        }
                        holder.tvCartPrice.setText(main.getResources().getString(R.string.Rs) + " " + ThiQuantity * Double.parseDouble(StrNewPtice) + "");
                        holder.tvCartQuantity.setText(String.valueOf(total));
                        CartActivity.tvCartTotalAmount.setText(main.getResources().getString(R.string.Rs) + " " + ThiQuantity * Double.parseDouble(StrNewPtice) + "");
                        Toast.makeText(main, "Quantity Updated Successfully", Toast.LENGTH_SHORT).show();
                    }

                    mOnItemClickListener.onItemClick(position, view, 1);
                }
            }
        });
        holder.tvCartmax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    int counter = bean.getCountThis();
                    int total = counter + 1;

                    shudh4sureDB.UpdateQuantityByPriceIdInDB(bean.getStrPriceId(), total);
                    int ThiQuantity = 1;
                    String StrNewPtice = "";
                    Cursor MyCurserThis = shudh4sureDB.ShowTableCartList();
                    for (MyCurserThis.moveToFirst(); !MyCurserThis.isAfterLast(); MyCurserThis.moveToNext()) {
                        if (MyCurserThis.getString(2).equalsIgnoreCase(bean.getStrPriceId())) {
                            ThiQuantity = MyCurserThis.getInt(7);
                            StrNewPtice = MyCurserThis.getString(5);
                        }
                    }
                    holder.tvCartPrice.setText(main.getResources().getString(R.string.Rs) + " " + ThiQuantity * Double.parseDouble(StrNewPtice) + "");
                    holder.tvCartQuantity.setText(String.valueOf(total));
                    Toast.makeText(main, "Quantity Updated Successfully", Toast.LENGTH_SHORT).show();
                    mOnItemClickListener.onItemClick(position, view, 2);
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

        TextView tvCartProductname, tvCartProductQuanity, tvCartPrice, tvCartQuantity, tvCartCaption;
        ImageView imgCartImage, imgCartDelete, tvCartmin, tvCartmax;

        public ViewHolder(View v) {
            super(v);
            tvCartProductname = (TextView) v.findViewById(R.id.tvCartProductname);
            tvCartProductQuanity = (TextView) v.findViewById(R.id.tvCartProductQuanity);
            tvCartPrice = (TextView) v.findViewById(R.id.tvCartPrice);
            tvCartCaption = (TextView) v.findViewById(R.id.tvCartCaption);
            tvCartQuantity = (TextView) v.findViewById(R.id.tvCartQuantity);
            imgCartImage = (ImageView) v.findViewById(R.id.imgCartImage);
            imgCartDelete = (ImageView) v.findViewById(R.id.imgCartDelete);
            tvCartmin = (ImageView) v.findViewById(R.id.tvCartmin);
            tvCartmax = (ImageView) v.findViewById(R.id.tvCartmax);

        }

    }

}








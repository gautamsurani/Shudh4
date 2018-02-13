package com.shudh4sure.shopping.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.activity.CartActivity;
import com.shudh4sure.shopping.dbhelper.Shudh4sureDB;
import com.shudh4sure.shopping.model.BestNewProductModel;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.MyUpdateWishList;
import com.shudh4sure.shopping.utils.SmallBang;

import java.util.ArrayList;


public class BestNewProductAdapter extends RecyclerView.Adapter<BestNewProductAdapter.ViewHolder> {
    Activity main;
    Shudh4sureDB shudh4sureDB;
    ArrayList<BestNewProductModel> listData;
    LayoutInflater inflater;
    SharedPreferences mprefs;
    String strUserId = "";
    private OnItemClickListener mOnItemClickListener;
    ArrayList<String> ArrForvIEW = new ArrayList<>();
    private AlertDialog packsizeDialog;
    MyUpdateWishList updateWishList;

    private CustomListAdapter customListAdapter;
    private ListView packList;

    public BestNewProductAdapter(Activity context, ArrayList<BestNewProductModel> bean, Shudh4sureDB shudh4sureDB, ArrayList<String> Arr) {

        this.main = context;
        this.listData = bean;
        this.shudh4sureDB = shudh4sureDB;
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

        if (bean.getPriceList().size() > 1) {
            try {
                holder.pOptions.setText(" " + main.getResources().getString(R.string.Rs) + bean.getPriceList().get(bean.getListPos()).getPrice() + " / " + bean.getPriceList().get(bean.getListPos()).getWeight() + " ");
                holder.pOptionsSingleWeight.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                holder.pOptions.setVisibility(View.GONE);
                holder.pOptionsSingleWeight.setVisibility(View.VISIBLE);
                holder.pOptionsSingleWeight.setText(bean.getPriceList().get(bean.getListPos()).getWeight());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

        Cursor c = shudh4sureDB.ShowTableCartList();
        int counter = shudh4sureDB.getQuentyty(bean.getPriceList().get(bean.getListPos()).getPriceID());

        if (bean.getSold_out().equalsIgnoreCase("No")) {
            // holder.pAddcartbtn.setVisibility(View.VISIBLE);
            //   holder.tvListSoldOut.setVisibility(View.GONE);

            if (shudh4sureDB.ifExistsPriceId(bean.getPriceList().get(bean.getListPos()).getPriceID())) {
                holder.pAddcartbtn.setVisibility(View.GONE);
                holder.addingitemLinear.setVisibility(View.VISIBLE);
                holder.itmQUAN.setText(String.valueOf(counter));
            } else {

                holder.pAddcartbtn.setVisibility(View.VISIBLE);
                holder.addingitemLinear.setVisibility(View.GONE);
            }
        } else {
            holder.pAddcartbtn.setVisibility(View.GONE);
            holder.addingitemLinear.setVisibility(View.GONE);
            holder.tvListSoldOut.setVisibility(View.VISIBLE);
        }

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
                    Cursor c = shudh4sureDB.ShowTableWishList();
                    if (c.getCount() > 0) {
                        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                            WishLocalArr.add(c.getString(1));
                            mprefs.edit().putInt(AppConstant.USER_WISHLIST, c.getCount()).apply();
                        }
                        boolean b = WishLocalArr.contains(bean.getProductID());
                        if (b) {
                            holder.imgAddtoWishlist.setImageResource(R.drawable.f0);
                            shudh4sureDB.DeleteWishListByID(bean.getProductID());
                        } else {
                            holder.imgAddtoWishlist.setImageResource(R.drawable.f4);
                            holder.mSmallBang.bang(view);
                            shudh4sureDB.InsertWishListData(bean.getProductID(), bean.getName(),
                                    bean.getImage(), bean.getPrice(), bean.getMrp(), bean.getCaption());
                        }
                    } else {
                        mprefs.edit().remove(AppConstant.USER_WISHLIST).commit();
                        holder.imgAddtoWishlist.setImageResource(R.drawable.f4);
                        holder.mSmallBang.bang(view);
                        shudh4sureDB.InsertWishListData(bean.getProductID(), bean.getName(),
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
            }
        });

        final ArrayList<String> StrSpinItem = new ArrayList<>();
        for (int i = 0; i < bean.getPriceList().size(); i++) {
            StrSpinItem.add(main.getResources().getString(R.string.Rs) + bean.getPriceList().get(i).getPrice() + " / " + bean.getPriceList().get(i).getWeight());
        }

        holder.pAddcartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shudh4sureDB.InsertCartListData(bean.getProductID(), bean.getPriceList().get(bean.getListPos()).getPriceID(),
                        bean.getName(), bean.getImage(),
                        bean.getPriceList().get(bean.getListPos()).getPrice(),
                        bean.getPriceList().get(bean.getListPos()).getMrp(), 1, bean.getPriceList().get(bean.getListPos()).getWeight(), bean.getCaption());
                shudh4sureDB.UpdateQuantityByPriceIdInDB(bean.getPriceList().get(bean.getListPos()).getPriceID(), 1);
                notifyMAIN(holder.getAdapterPosition());
                mOnItemClickListener.onItemClick(position, view, 4);
            }
        });

        holder.plusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int counter = shudh4sureDB.getQuentyty(bean.getPriceList().get(bean.getListPos()).getPriceID());

                //  int counter = bean.getProdQuan();
                int total = counter + 1;
                holder.itmQUAN.setText(String.valueOf(total));
                bean.setProdQuan(total);
                shudh4sureDB.UpdateQuantityByPriceIdInDB(bean.getPriceList().get(bean.getListPos()).getPriceID(), total);

                notifyMAIN(holder.getAdapterPosition());

                Toast.makeText(main, "Quantity Updated Successfully", Toast.LENGTH_SHORT).show();
                mOnItemClickListener.onItemClick(position, view, 2);

            }
        });
        holder.minusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*holder.itmQUAN.setText(String.valueOf(bean.getProdQuan()-1));
                bean.setProdQuan(bean.getProdQuan()-1);
                shudh4sureDB.UpdateQuantityByPriceIdInDB(bean.getPriceList().get(position).getPriceID(), bean.getProdQuan()-1);
                notifyMAIN(holder.getAdapterPosition());*/
                int counter = shudh4sureDB.getQuentyty(bean.getPriceList().get(bean.getListPos()).getPriceID());
                if (counter == 1) {

                    bean.setProdQuan(counter);
                    holder.itmQUAN.setText(String.valueOf(counter));
                    shudh4sureDB.DeleteCartListByPriceID(bean.getPriceList().get(bean.getListPos()).getPriceID());
                    shudh4sureDB.UpdateQuantityByPriceIdInDB(bean.getPriceList().get(bean.getListPos()).getPriceID(), 0);
                    holder.pAddcartbtn.setVisibility(View.VISIBLE);
                    holder.addingitemLinear.setVisibility(View.GONE);

                } else if (counter > 1) {
                    //int counter = bean.getProdQuan();
                    int total = counter - 1;
                    holder.itmQUAN.setText(String.valueOf(total));
                    bean.setProdQuan(total);
                    shudh4sureDB.UpdateQuantityByPriceIdInDB(bean.getPriceList().get(bean.getListPos()).getPriceID(), total);
                    notifyMAIN(holder.getAdapterPosition());
                    Toast.makeText(main, "Quantity Updated Successfully", Toast.LENGTH_SHORT).show();
                }
                mOnItemClickListener.onItemClick(position, view, 3);


            }
        });

        holder.pOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Log.e("SelectedSize", String.valueOf(bean.getPriceList().size()));
                //    Log.e("SelectedSize",bean.getPriceList().get(position).getPrice());


                final AlertDialog.Builder builder = new AlertDialog.Builder(main);
                LayoutInflater inflater = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                @SuppressLint("InflateParams") final View alertlayout = inflater.inflate(R.layout.product_options_view, null);

                packList = (ListView) alertlayout.findViewById(R.id.packList);
                customListAdapter = new CustomListAdapter(main, StrSpinItem, holder.getAdapterPosition());
                packList.setAdapter(customListAdapter);
                customListAdapter.notifyDataSetChanged();

                builder.setView(alertlayout);
                packsizeDialog = builder.create();

                packsizeDialog.show();


                //   packsizeDialog.show();
            }
        });
    }

    private class CustomListAdapter extends BaseAdapter {

        ArrayList<String> mCounting;
        int mainPos;

        CustomListAdapter(Context context, ArrayList<String> counting, int adapterPosition) {
            mCounting = counting;
            mainPos = adapterPosition;
        }

        @Override
        public int getCount() {
            return mCounting.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        private class ViewHolder {
            TextView pSize;
            int position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final CustomListAdapter.ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.product_options_items, parent, false);
                holder = new CustomListAdapter.ViewHolder();

                holder.pSize = (TextView) convertView.findViewById(R.id.pSize);

                convertView.setTag(holder);
                holder.position = position;
            } else {
                holder = (CustomListAdapter.ViewHolder) convertView.getTag();
            }

            holder.pSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //   Toast.makeText(context,mCounting.get(holder.position).trim()+"",Toast.LENGTH_SHORT).show();
                    listData.get(mainPos).setListPos(holder.position);
                    packsizeDialog.dismiss();

                    notifyMAIN(mainPos);
                }
            });
            holder.pSize.setTag(position);
            holder.pSize.setText(mCounting.get(holder.position).trim());

            if (holder.position == listData.get(mainPos).getListPos()) {
                holder.pSize.setBackgroundColor(ContextCompat.getColor(main, R.color.dropdown_bg));
            } else {
                holder.pSize.setBackgroundColor(Color.WHITE);
                holder.pSize.setTextColor(Color.BLACK);
            }
            return convertView;
        }
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
        Button pAddcartbtn;
        LinearLayout lvDiscountbg;
        LinearLayout lvProductLayout, addingitemLinear;
        TextView tvSoldOut, pOptions, itmQUAN, pOptionsSingleWeight, tvListSoldOut;
        SmallBang mSmallBang;
        ImageView minusItem, plusItem;

        public ViewHolder(View v) {
            super(v);
            this.image = (ImageView) itemView.findViewById(R.id.image);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.titleinHindi = (TextView) itemView.findViewById(R.id.titleinHindi);
            this.price = (TextView) itemView.findViewById(R.id.price);
            this.itmQUAN = (TextView) v.findViewById(R.id.itmQUAN);
            this.cutprice = (TextView) itemView.findViewById(R.id.cutprice);
            this.discount = (TextView) itemView.findViewById(R.id.discount);
            this.pAddcartbtn = (Button) v.findViewById(R.id.pAddcartbtn);
            this.tvSoldOut = (TextView) itemView.findViewById(R.id.tvSoldOut);
            this.minusItem = (ImageView) v.findViewById(R.id.minusItem);
            this.pOptionsSingleWeight = (TextView) v.findViewById(R.id.pOptionsSingleWeight);
            this.plusItem = (ImageView) v.findViewById(R.id.plusItem);
            this.cutprice.setPaintFlags(cutprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.imgAddtoWishlist = (ImageView) itemView.findViewById(R.id.imgAddtoWishlist);
            this.lvDiscountbg = (LinearLayout) itemView.findViewById(R.id.lvDiscountbg);
            this.lvProductLayout = (LinearLayout) itemView.findViewById(R.id.lvProductLayout);
            this.pOptions = (TextView) v.findViewById(R.id.pOptions);
            this.addingitemLinear = (LinearLayout) v.findViewById(R.id.addingitemLinear);
            this.tvListSoldOut = (TextView) v.findViewById(R.id.tvListSoldOut);
        }
    }

    private void notifyMAIN(int mainPos) {
        notifyItemChanged(mainPos);
    }
}



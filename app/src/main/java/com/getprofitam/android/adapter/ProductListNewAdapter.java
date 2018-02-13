package com.getprofitam.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.getprofitam.android.R;
import com.getprofitam.android.activity.ProductDetailsActivity;
import com.getprofitam.android.dbhelper.GetprofitamDB;
import com.getprofitam.android.pojo.ProductList;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.MyUpdateWishList;
import com.getprofitam.android.utils.SmallBang;

import java.util.ArrayList;


public class ProductListNewAdapter extends RecyclerView.Adapter<ProductListNewAdapter.ViewHolder> {

    Context context;
    Activity main;
    ArrayList<ProductList> listData;
    LayoutInflater inflater;
    SharedPreferences mprefs;
    String strUserId = "";
    private OnItemClickListener mOnItemClickListener;
    private CustomListAdapter customListAdapter;
    private ListView packList;
    private AlertDialog packsizeDialog;
    GetprofitamDB getprofitamDB;
    public static int viewFormatProductList;
    ArrayList<String> ArrForvIEW = new ArrayList<>();

    public ProductListNewAdapter(Activity activity, Context context, ArrayList<ProductList> bean, ArrayList<String> Arr) {

        this.main = activity;
        this.listData = bean;
        this.context = context;
        this.inflater = (LayoutInflater.from(context));
        ArrForvIEW = Arr;
        mprefs = context.getSharedPreferences(AppConstant.PREFS_NAME, context.MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        String LocalDbUserSocityIdName = strUserId + "_getprofitam_local.db";
        String FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;

        getprofitamDB = new GetprofitamDB(main, FinalLocalDBName);
        getprofitamDB.OpenDB();
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public ProductListNewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view

        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_productlist_items, parent, false);
            return new ProductListNewAdapter.ViewHolder(view);

        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recy_grid_items_productlist, parent, false);
            return new ProductListNewAdapter.ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ProductListNewAdapter.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final ProductList bean = listData.get(holder.getAdapterPosition());
        holder.mSmallBang = SmallBang.attach2Window(main);
        holder.pTitle.setText(bean.getName().trim());
        holder.pSubTitle.setText(bean.getCaption().trim());

        if (bean.getPriceList().size() > 1) {
            holder.pOptions.setText(" " + context.getResources().getString(R.string.Rs) + bean.getPriceList().get(bean.getListPos()).getPrice() + " / " + bean.getPriceList().get(bean.getListPos()).getWeight() + " ");
            holder.pOptionsSingleWeight.setVisibility(View.GONE);
        } else {
            holder.pOptions.setVisibility(View.GONE);
            holder.pOptionsSingleWeight.setVisibility(View.VISIBLE);
            holder.pOptionsSingleWeight.setText(bean.getPriceList().get(bean.getListPos()).getWeight());
        }
        holder.pPrice.setText(context.getResources().getString(R.string.Rs) + bean.getPriceList().get(bean.getListPos()).getPrice() + "");
        holder.lPrice.setText(context.getResources().getString(R.string.Rs) + bean.getPriceList().get(bean.getListPos()).getMrp() + "");
        if (!bean.getPriceList().get(bean.getListPos()).getDis().equalsIgnoreCase("0")) {
            holder.discount.setText(bean.getPriceList().get(bean.getListPos()).getDis() + " % off");
        }

        Cursor c = getprofitamDB.ShowTableCartList();
        int counter = getprofitamDB.getQuentyty(bean.getPriceList().get(bean.getListPos()).getPriceID());

        if (bean.getSoldOut().equalsIgnoreCase("No")) {
            // holder.pAddcartbtn.setVisibility(View.VISIBLE);
            //   holder.tvListSoldOut.setVisibility(View.GONE);

            if (getprofitamDB.ifExistsPriceId(bean.getPriceList().get(bean.getListPos()).getPriceID())) {
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

//        if (bean.getProdQuan()!=0){
//            Log.e("position ","Zero"+bean.getProdQuan());
//            holder.pAddcartbtn.setVisibility(View.GONE);
//            holder.addingitemLinear.setVisibility(View.VISIBLE);
//            holder.itmQUAN.setText(String.valueOf(bean.getProdQuan()));
//        }else {
//
//            holder.pAddcartbtn.setVisibility(View.VISIBLE);
//            holder.addingitemLinear.setVisibility(View.GONE);
//
//            /*if(holder.getAdapterPosition()==1) {
//                Log.e("position ","One");
//                holder.pAddcartbtn.setVisibility(View.GONE);
//                holder.addingitemLinear.setVisibility(View.GONE);
//            }
//            else {
//                Log.e("position ","Two");
//                holder.pAddcartbtn.setVisibility(View.VISIBLE);
//                holder.addingitemLinear.setVisibility(View.GONE);
//            }*/
//
//        }

        Glide.with(context)
                .load(bean.getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.ic_default_product_one)
                .into(holder.pImg);

        boolean baa = ArrForvIEW.contains(bean.getProductID());
        if (baa) {
            holder.imgAddtoWishlist.setImageResource(R.drawable.f4);

        } else {
            holder.imgAddtoWishlist.setImageResource(R.drawable.f0);

        }

        holder.imgAddtoWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ArrayList<String> WishLocalArr = new ArrayList<String>();
                ArrForvIEW.clear();
                Cursor c = getprofitamDB.ShowTableWishList();
                if (c.getCount() > 0) {
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        ArrForvIEW.add(c.getString(1));
                        mprefs.edit().putInt(AppConstant.USER_WISHLIST, c.getCount()).apply();

                    }

                    //    Log.e("c.getString(1)",c.getString(1));
                    boolean b = ArrForvIEW.contains(bean.getProductID());

                    if (b) {

                        holder.imgAddtoWishlist.setImageResource(R.drawable.f0);
                        getprofitamDB.DeleteWishListByID(bean.getProductID());
                        ArrForvIEW.remove(bean.getProductID());
                        mprefs.edit().putInt(AppConstant.USER_WISHLIST, getprofitamDB.getAllProduct().size()).apply();
                    } else {
                        Log.e("Add 1 ", bean.getProductID());
                        holder.imgAddtoWishlist.setImageResource(R.drawable.f4);
                        holder.mSmallBang.bang(view);
                        getprofitamDB.InsertWishListData(bean.getProductID(), bean.getName(),
                                bean.getImage(), bean.getPriceList().get(bean.getListPos()).getPrice(),
                                bean.getPriceList().get(bean.getListPos()).getMrp(), bean.getCaption());
                        ArrForvIEW.add(bean.getProductID());

                    }
                } else {
                    mprefs.edit().remove(AppConstant.USER_WISHLIST).commit();

                    holder.imgAddtoWishlist.setImageResource(R.drawable.f4);
                    holder.mSmallBang.bang(view);
                    getprofitamDB.InsertWishListData(bean.getProductID(), bean.getName(),
                            bean.getImage(), bean.getPriceList().get(bean.getListPos()).getPrice(),
                            bean.getPriceList().get(bean.getListPos()).getMrp(), bean.getCaption());
                    ArrForvIEW.add(bean.getProductID());
                }

                //   holder.imgAddtoWishlist.setImageResource(R.drawable.f4);

                //  mOnItemClickListener.onItemClick(position, view, 0);
            /*    ArrForvIEW.clear();
            //    ArrayList<String> WishLocalArr = new ArrayList<String>();
                Cursor c = getprofitamDB.ShowTableWishList();
                if (c.getCount() > 0) {
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        ArrForvIEW.add(c.getString(1));
                    }
                    Log.e("ArrForvIEW",ArrForvIEW.toString());
                    boolean b = ArrForvIEW.contains(bean.getProductID());
                    Log.e("bean.getProductID()",bean.getProductID());
                    if (b) {

                        getprofitamDB.DeleteWishListByID(bean.getProductID());
                        holder.imgAddtoWishlist.setImageResource(R.drawable.f0);
                        Log.e("Delete","Delete");
                    } else {
                        Log.e("Delete ui",bean.getProductID());
                        getprofitamDB.InsertWishListData(bean.getProductID(), bean.getName(),
                                bean.getImage(), bean.getPriceList().get(bean.getListPos()).getPrice(),
                                bean.getPriceList().get(bean.getListPos()).getMrp(),bean.getCaption());

                        holder.imgAddtoWishlist.setImageResource(R.drawable.f4);

                        Log.e("Add","Add");
                        //     holder.mSmallBang.bang(view);

                    }
                } else {
                    getprofitamDB.InsertWishListData(bean.getProductID(), bean.getName(),
                            bean.getImage(), bean.getPriceList().get(bean.getListPos()).getPrice(),
                            bean.getPriceList().get(bean.getListPos()).getMrp(),bean.getCaption());

                    holder.imgAddtoWishlist.setImageResource(R.drawable.f4);
                    Log.e("Add11","Add11");
                }
                notifyMAIN(holder.getAdapterPosition());
                mOnItemClickListener.onItemClick(position, view, 0);*/

                notifyMAIN(holder.getAdapterPosition());
                mOnItemClickListener.onItemClick(position, view, 0);

            }
        });


        final ArrayList<String> StrSpinItem = new ArrayList<>();
        for (int i = 0; i < bean.getPriceList().size(); i++) {
            StrSpinItem.add(context.getResources().getString(R.string.Rs) + bean.getPriceList().get(i).getPrice() + " / " + bean.getPriceList().get(i).getWeight());
        }

        holder.pAddcartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getprofitamDB.InsertCartListData(bean.getProductID(), bean.getPriceList().get(bean.getListPos()).getPriceID(),
                        bean.getName(), bean.getImage(),
                        bean.getPriceList().get(bean.getListPos()).getPrice(),
                        bean.getPriceList().get(bean.getListPos()).getMrp(), 1, bean.getPriceList().get(bean.getListPos()).getWeight(), bean.getCaption());
                getprofitamDB.UpdateQuantityByPriceIdInDB(bean.getPriceList().get(bean.getListPos()).getPriceID(), 1);
                notifyMAIN(holder.getAdapterPosition());
                mOnItemClickListener.onItemClick(position, view, 1);

            }
        });
        holder.plusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int counter = getprofitamDB.getQuentyty(bean.getPriceList().get(bean.getListPos()).getPriceID());

                //  int counter = bean.getProdQuan();
                int total = counter + 1;
                holder.itmQUAN.setText(String.valueOf(total));
                bean.setProdQuan(total);
                getprofitamDB.UpdateQuantityByPriceIdInDB(bean.getPriceList().get(bean.getListPos()).getPriceID(), total);

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
                getprofitamDB.UpdateQuantityByPriceIdInDB(bean.getPriceList().get(position).getPriceID(), bean.getProdQuan()-1);
                notifyMAIN(holder.getAdapterPosition());*/
                int counter = getprofitamDB.getQuentyty(bean.getPriceList().get(bean.getListPos()).getPriceID());
                if (counter == 1) {

                    bean.setProdQuan(counter);
                    holder.itmQUAN.setText(String.valueOf(counter));
                    getprofitamDB.DeleteCartListByPriceID(bean.getPriceList().get(bean.getListPos()).getPriceID());
                    getprofitamDB.UpdateQuantityByPriceIdInDB(bean.getPriceList().get(bean.getListPos()).getPriceID(), 0);
                    holder.pAddcartbtn.setVisibility(View.VISIBLE);
                    holder.addingitemLinear.setVisibility(View.GONE);

                } else if (counter > 1) {
                    //int counter = bean.getProdQuan();
                    int total = counter - 1;
                    holder.itmQUAN.setText(String.valueOf(total));
                    bean.setProdQuan(total);
                    getprofitamDB.UpdateQuantityByPriceIdInDB(bean.getPriceList().get(bean.getListPos()).getPriceID(), total);
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


                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                @SuppressLint("InflateParams") final View alertlayout = inflater.inflate(R.layout.product_options_view, null);

                packList = (ListView) alertlayout.findViewById(R.id.packList);
                customListAdapter = new CustomListAdapter(context, StrSpinItem, holder.getAdapterPosition());
                packList.setAdapter(customListAdapter);
                customListAdapter.notifyDataSetChanged();

                builder.setView(alertlayout);
                packsizeDialog = builder.create();

                packsizeDialog.show();


                //   packsizeDialog.show();
            }
        });

        holder.lvListlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(main, ProductDetailsActivity.class);
                intent.putExtra("catID", bean.getProductID());
                intent.putExtra("catName", bean.getName());
                main.startActivity(intent);
                main.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pTitle, pSubTitle, pPrice, pOptions, itmQUAN, lPrice, tvListSoldOut, pOptionsSingleWeight, discount;
        Button pAddcartbtn;
        ImageView pImg, minusItem, plusItem, imgAddtoWishlist;
        LinearLayout addingitemLinear, lvListlayout;
        SmallBang mSmallBang;

        public ViewHolder(View v) {
            super(v);
            this.pTitle = (TextView) v.findViewById(R.id.pTitle);
            this.pSubTitle = (TextView) v.findViewById(R.id.pSubTitle);
            this.pOptions = (TextView) v.findViewById(R.id.pOptions);
            this.pOptionsSingleWeight = (TextView) v.findViewById(R.id.pOptionsSingleWeight);
            this.discount = (TextView) v.findViewById(R.id.discount);
            this.lPrice = (TextView) v.findViewById(R.id.lPrice);
            this.pPrice = (TextView) v.findViewById(R.id.pPrice);
            this.itmQUAN = (TextView) v.findViewById(R.id.itmQUAN);
            this.tvListSoldOut = (TextView) v.findViewById(R.id.tvListSoldOut);
            this.addingitemLinear = (LinearLayout) v.findViewById(R.id.addingitemLinear);
            this.lvListlayout = (LinearLayout) v.findViewById(R.id.lvListlayout);

           /* this.pTitle.setTypeface(EasyFonts.freedom(context));
            this.pSubTitle.setTypeface(EasyFonts.cac_champagne(context));
            this.pOptions.setTypeface(EasyFonts.robotoRegular(context));*/
            this.pAddcartbtn = (Button) v.findViewById(R.id.pAddcartbtn);
            this.pImg = (ImageView) v.findViewById(R.id.pImg);
            this.minusItem = (ImageView) v.findViewById(R.id.minusItem);
            this.plusItem = (ImageView) v.findViewById(R.id.plusItem);
            this.imgAddtoWishlist = (ImageView) v.findViewById(R.id.imgAddtoWishlist);

            this.lPrice.setPaintFlags(this.lPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

    }

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (listData.size() == 0) {
            return 0;
        } else if (viewFormatProductList == 0) {
            return 0;
        } else if (viewFormatProductList == 1) {
            return 1;
        }
        return super.getItemViewType(position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
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

            final ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.product_options_items, parent, false);
                holder = new ViewHolder();

                holder.pSize = (TextView) convertView.findViewById(R.id.pSize);

                convertView.setTag(holder);
                holder.position = position;
            } else {
                holder = (ViewHolder) convertView.getTag();
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

    private void notifyMAIN(int mainPos) {
        notifyItemChanged(mainPos);
    }
}








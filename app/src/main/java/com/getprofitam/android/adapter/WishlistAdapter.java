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

import java.util.ArrayList;


public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

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

    public WishlistAdapter(Activity activity, Context context, ArrayList<ProductList> bean) {

        this.main = activity;
        this.listData = bean;
        this.context = context;
        this.inflater = (LayoutInflater.from(context));

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
    public WishlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view

        View view;
        if(viewType == 0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_productlist_items_wishlist, parent, false);
            return new WishlistAdapter.ViewHolder(view);

        }else if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recy_grid_items_wishlist, parent, false);
            return new WishlistAdapter.ViewHolder(view);
        }
         return null;
    }

    @Override
    public void onBindViewHolder(final WishlistAdapter.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final ProductList bean = listData.get(holder.getAdapterPosition());
        holder.pTitle.setText(bean.getName().trim());
        holder.pSubTitle.setText(bean.getCaption().trim());

        if(bean.getPriceList().size()>1){
            holder.pOptions.setText(" "+context.getResources().getString(R.string.Rs)+   bean.getPriceList().get(bean.getListPos()).getPrice()+" / "+bean.getPriceList().get(bean.getListPos()).getWeight()+" ");
            holder.pOptionsSingleWeight.setVisibility(View.GONE);
        }else {
            holder.pOptions.setVisibility(View.GONE);
            holder.pOptionsSingleWeight.setVisibility(View.VISIBLE);
            holder.pOptionsSingleWeight.setText(bean.getPriceList().get(bean.getListPos()).getWeight());
        }
        holder.pPrice.setText(context.getResources().getString(R.string.Rs)+   bean.getPriceList().get(bean.getListPos()).getPrice()+"");
        holder.lPrice.setText(context.getResources().getString(R.string.Rs)+   bean.getPriceList().get(bean.getListPos()).getMrp()+"");
        holder.discount.setText(bean.getPriceList().get(bean.getListPos()).getDis()+" %");

        Cursor c = getprofitamDB.ShowTableCartList();
        int counter = getprofitamDB.getQuentyty(bean.getPriceList().get(bean.getListPos()).getPriceID());

        if(bean.getSoldOut().equalsIgnoreCase("No")){
            // holder.pAddcartbtn.setVisibility(View.VISIBLE);
            //   holder.tvListSoldOut.setVisibility(View.GONE);

            if(getprofitamDB.ifExistsPriceId(bean.getPriceList().get(bean.getListPos()).getPriceID())){
                holder.pAddcartbtn.setVisibility(View.GONE);
                holder.addingitemLinear.setVisibility(View.VISIBLE);
                holder.itmQUAN.setText(String.valueOf(counter));
            }else {

                holder.pAddcartbtn.setVisibility(View.VISIBLE);
                holder.addingitemLinear.setVisibility(View.GONE);
            }
        }else {
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

        final ArrayList<String> StrSpinItem= new ArrayList<>();
        for (int i = 0; i < bean.getPriceList().size(); i++) {
            StrSpinItem.add(context.getResources().getString(R.string.Rs)+   bean.getPriceList().get(i).getPrice()+" / "+bean.getPriceList().get(i).getWeight());
        }

        holder.pAddcartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getprofitamDB.InsertCartListData(bean.getProductID(), bean.getPriceList().get(bean.getListPos()).getPriceID(),
                        bean.getName(), bean.getImage(),
                        bean.getPriceList().get(bean.getListPos()).getPrice(),
                        bean.getPriceList().get(bean.getListPos()).getMrp(), 1,bean.getPriceList().get(bean.getListPos()).getWeight(),bean.getCaption());
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

                }else if(counter > 1){
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
                Log.e("SelectedSize",String.valueOf(bean.getPriceList().size()));
                //    Log.e("SelectedSize",bean.getPriceList().get(position).getPrice());
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                @SuppressLint("InflateParams") final View alertlayout = inflater.inflate(R.layout.product_options_view, null);

                packList=(ListView)alertlayout.findViewById(R.id.packList);
                customListAdapter= new CustomListAdapter(context,StrSpinItem,holder.getAdapterPosition());
                packList.setAdapter(customListAdapter);
                customListAdapter.notifyDataSetChanged();
                builder.setView(alertlayout);
                packsizeDialog=builder.create();
                packsizeDialog.show();


                //   packsizeDialog.show();
            }
        });

        holder.lvListlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(main,ProductDetailsActivity.class);
                intent.putExtra("catID",bean.getProductID());
                intent.putExtra("catName",bean.getName());
                  main.startActivity(intent);
                main.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(position, view, 4);

            }
        });

    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pTitle,pSubTitle,pPrice,pOptions,itmQUAN,lPrice,tvListSoldOut,pOptionsSingleWeight,discount;
        Button pAddcartbtn;
        ImageView pImg,minusItem,plusItem,next;
        LinearLayout addingitemLinear,lvListlayout;

        public ViewHolder(View v) {
            super(v);
            this.pTitle = (TextView) v.findViewById(R.id.pTitle);
            this.pSubTitle = (TextView) v.findViewById(R.id.pSubTitle);
            this.pOptions = (TextView) v.findViewById(R.id.pOptions);
            this.pOptionsSingleWeight = (TextView) v.findViewById(R.id.pOptionsSingleWeight);
            this.lPrice= (TextView) v.findViewById(R.id.lPrice);
            this.pPrice = (TextView) v.findViewById(R.id.pPrice);
            this.itmQUAN = (TextView) v.findViewById(R.id.itmQUAN);
            this.discount = (TextView) v.findViewById(R.id.discount);

            this.tvListSoldOut = (TextView) v.findViewById(R.id.tvListSoldOut);
            this.addingitemLinear = (LinearLayout) v.findViewById(R.id.addingitemLinear);
            this.lvListlayout = (LinearLayout) v.findViewById(R.id.lvListlayout);

           /* this.pTitle.setTypeface(EasyFonts.freedom(context));
            this.pSubTitle.setTypeface(EasyFonts.cac_champagne(context));
            this.pOptions.setTypeface(EasyFonts.robotoRegular(context));*/
            this.pAddcartbtn = (Button) v.findViewById(R.id.pAddcartbtn);
            this.pImg = (ImageView) v.findViewById(R.id.pImg);
            this.next = (ImageView) v.findViewById(R.id.next);
            this.minusItem = (ImageView) v.findViewById(R.id.minusItem);
            this.plusItem = (ImageView) v.findViewById(R.id.plusItem);

            this.lPrice.setPaintFlags(this.lPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

    }

    @Override
    public int getItemCount() {
        return listData ==null ? 0: listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (listData.size()==0){
            return 0;
        }else if (viewFormatProductList==0){
            return 0;
        }else if (viewFormatProductList==1){
            return 1;
        }
        return super.getItemViewType(position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    private class CustomListAdapter extends BaseAdapter
    {

        ArrayList<String> mCounting;
        int mainPos;

        CustomListAdapter(Context context, ArrayList<String> counting, int adapterPosition)
        {
            mCounting=counting;
            mainPos=adapterPosition;
        }
        @Override
        public int getCount()
        {
            return mCounting.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        private  class ViewHolder
        {
            TextView pSize;
            int position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {

            final ViewHolder holder;

            if (convertView==null){
                LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView=inflater.inflate(R.layout.product_options_items,parent,false);
                holder=new ViewHolder();

                holder.pSize=(TextView)convertView.findViewById(R.id.pSize);

                convertView.setTag(holder);
                holder.position = position;
            }else{
                holder=(ViewHolder)convertView.getTag();
            }

            holder.pSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //   Toast.makeText(context,mCounting.get(holder.position).trim()+"",Toast.LENGTH_SHORT).show();
                    listData.get(mainPos).setListPos(holder.position);
                    Log.e("Selected_DD_Price",listData.get(mainPos).getPriceList().get(holder.position).getPrice());
                    Log.e("Selected_DD_PriceId",listData.get(mainPos).getPriceList().get(holder.position).getPriceID());
                    packsizeDialog.dismiss();
                    notifyMAIN(mainPos);
                }
            });
            holder.pSize.setTag(position);
            holder.pSize.setText(mCounting.get(holder.position).trim());

            if (holder.position==listData.get(mainPos).getListPos()){
                holder.pSize.setBackgroundColor(ContextCompat.getColor(main,R.color.dropdown_bg));
            }else {
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








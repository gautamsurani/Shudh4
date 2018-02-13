package com.getprofitam.android.activity;

import java.util.List;

/**
 * Created by welcome on 09-01-2017.
 */

public class Api_Model {

    public String message;
    public String msgcode;
    public String version;
    public String version_msg;
    public String cart_amount;
    public String cart_msg;
    public String userID;
    public String Amount;
    public String orderID;
    public String coupon_value;
    public String coupon_msg;
    public String walletBal;
    public String data;
    public String otp;
    public String time_slot_msg;
    public String next_days;
    public String status;
    public String order_type;
    public String order_date;
    public String del_date;
    public String grand_total;
    public String pay_value;
    public String subtotal;
    public String shipping;
    public String wallet;
    public String item;
    public String address;
    public String payu_wallet_fail;
    public String payu_wallet_success;
    public String payu_order_success;
    public String payu_order_fail;
    public String cod_success;
    public List<about> about;
    public List<contact> contact;
    public List<area_list> area_list;
    public List<pincode_list> pincode_list;
    public List<user_detail> user_detail;
    public List<order_list> order_list;
    public List<offer_list> offer_list;
    public List<share_data> share_data;
    public List<home_category_list> home_category_list;
    public List<banner_list> banner_list;
    public List<offer_banner> offer_banner;
    public List<best_product> best_product;
    public List<new_product> new_product;
    public List<product> product;
    public List<related_product> related_product;
    public List<transction_data> transction_data;
    public List<search_list> search_list;
    public List<get_detail> get_detail;
    public List<timeslot> timeslot;
    public List<products_list> products_list;
    public List<final_charges> final_charges;
    public List<add_money_data> add_money_data;
    public List<user_detail1> user_detail1;
    public List<online_payment_data> online_payment_data;

    public List<category_list_new> category_list_new;


    public class category_list_new {

        public String catID;
        public String name;
        public String icon;
        public String subcat;

        public List<subcat_list> subcat_list;

        public class subcat_list {
            public String catID, name, icon;
        }
    }

    public class about {
        public String image;
        public String text;
        public String facebook_link;
        public String google_link;
        public String linkdin_link;
        public String twitter_link;
        public String insta_link;
    }

    public class contact {
        public String address_line1;
        public String address_line2;
        public String address_line3;
        public String email;
        public String phone;
        public String website;
        public String call;
        public String time;
    }

    public class get_detail {
        public String userimage;
        public String name;
        public String phone;
        public String email;
        public String address;
        public String area;
        public String area_id;
        public String city;
        public String state;
        public String pincode;
        public String wallet;
        public String whatsapp_no;
        public String date_of_birth;
        public String mrg_anniversary;
        public String ship_charge;
    }

    public class timeslot {
        public String id;
        public String start_time;
    }


    public class area_list {
        public String areaID;
        public String name;
        public String shipping;
        public String on_order;

    }

    public class pincode_list {
        public String pincodeID;
        public String name;

    }

    public class user_detail {
        String userID;
        String phone;
        String name;
        String email;
        String userimage;
        String otp;
        String type;
    }

    public class user_detail1 {
        String userID;
        String phone;
        String otp;
        String type;

    }

    public class order_list {
        String orderID;
        String orderNo;
        String date;
        String status;
        String amount;
    }

    public class products_list {
        public String SR;
        public String name;
        public String weight;
        public String quantity;
        public String price;
        public String line_total;


    }

    public class final_charges {
        public String wallet_bal;
        public String cod;
        public String paytm;
        public String payu;
        public String cod_charges;
    }


    public class offer_list {
        String offer;
        String product;
        String image;
        String message;
        String added_on;
    }

    public class transction_data {
        String OrderID;
        String Remark;
        String symbol;
        String Amount;
        String type;
        String TransactionDate;
    }


    public class share_data {
        String image;
        String message;
        String share_image;
        String ref_key;
        String you_get;
        String you_friend_get;

    }


    public class home_category_list {
        public String catID;
        public String name;
        public String icon;
    }

    public class banner_list {
        public String sr;
        public String name;
        public String image;
        public String catID;
    }


    public class offer_banner {
        public String sr;
        public String name;
        public String image;
        public String catID;
    }

    public class new_product {
        public String productID;
        public String name;
        public String caption;
        public String image;
        public String discount;
        public String price;
        public String mrp;
        public String sold_out;
    }

    public class best_product {
        public String productID;
        public String name;
        public String caption;
        public String image;
        public String discount;
        public String price;
        public String mrp;
        public String sold_out;
    }

    public class search_list {
        public String ID;
        public String name;
        public String type;

    }


    public class related_product {
        public String productID;
        public String name;
        public String caption;
        public String image;
        public String discount;
        public String price;
        public String mrp;
        public String sold_out;
    }


    public class product {
        public String productID;
        public String name;
        public String product_code;
        public String caption;
        public String image;
        public String sold_out;
        public String description;
        List<image_list> image_list;
        List<price_list> price_list;

        public class image_list {
            String sr;
            String small_image;
            String large_image;
        }

        public class price_list {
            String sr;
            String price_ID;
            String price;
            String mrp;
            String weight;
            String dis;
        }
    }

    public class add_money_data {
        String userID;
        String product_info;
        String amount;
        String first_name;
        String email;
        String trans_id;
        String payu_key;
        String payment_hash;
        String vas_for_mobile_sdk_hash;
        String payment_related_details_for_mobile_sdk_hash;
        String udf1;
        String udf2;
        String payu_wallet_fail;
        String payu_wallet_success;

    }

    public class online_payment_data {
        String userID;
        String product_info;
        String amount;
        String first_name;
        String email;
        String trans_id;
        String udf1;
        String udf2;
        String payu_key;
        String payment_hash;
        String vas_for_mobile_sdk_hash;
        String payment_related_details_for_mobile_sdk_hash;
    }


}

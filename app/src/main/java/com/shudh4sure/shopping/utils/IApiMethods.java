package com.shudh4sure.shopping.utils;


import com.shudh4sure.shopping.activity.Api_Model;
import com.shudh4sure.shopping.pojo.Data;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by welcome on 09-04-2016.
 */
public interface IApiMethods {
    @GET("/index.php")
    Api_Model AboutUs(
            @Query("view") String view,
            @Query("userID") String userID
    );

    @GET("/index.php")
    Api_Model Home(
            @Query("view") String view,
            @Query("userID") String userID,
            @Query("deviceID") String deviceID,
            @Query("tokenId") String tokenId,
            @Query("appV") String appV
    );

    @GET("/index.php")
    Api_Model BestNewProducts(
            @Query("view") String view,
            @Query("userID") String userID
    );

    @GET("/index.php")
    Api_Model RelatedProducts(
            @Query("view") String view,
            @Query("userID") String userID,
            @Query("productID") String productID
    );


    @GET("/index.php")
    Api_Model HelpUsers(
            @Query("view") String view,
            @Query("userID") String userID,
            @Query("name") String name,
            @Query("email") String email,
            @Query("phone") String phone,
            @Query("msg") String msg
    );

    @GET("/index.php")
    Api_Model FeedbackUsers(
            @Query("view") String view,
            @Query("userID") String userID,
            @Query("name") String name,
            @Query("email") String email,
            @Query("phone") String phone,
            @Query("msg") String msg
    );

    @GET("/index.php")
    Api_Model ContactUs(
            @Query("view") String view,
            @Query("userID") String userID
    );

    @GET("/index.php")
    Api_Model UserInfo(
            @Query("view") String view,
            @Query("userID") String userID,
            @Query("userPhone") String userPhone,
            @Query("day") String day
    );

    @GET("/index.php")
    Api_Model ForgotPassword(
            @Query("view") String view,
            @Query("page") String page,
            @Query("user") String user
    );

    @GET("/index.php")
    Api_Model getAvailableTimeSlot(
            @Query("view") String view,
            @Query("userID") String userID,
            @Query("userPhone") String userPhone,
            @Query("day") String day,
            @Query("type") String type
    );


    @GET("/index.php")
    Api_Model AreaPincodeList(
            @Query("view") String view,
            @Query("areaID") String areaID
    );

    @GET("/index.php")
    Api_Model GetLogin(

            @Query("view") String view,
            @Query("page") String page,
            @Query("user") String user,
            @Query("pass") String pass
    );

    @GET("/index.php")
    Api_Model ChangePassword(

            @Query("view") String view,
            @Query("page") String page,
            @Query("userID") String userID,
            @Query("userPhone") String userPhone,
            @Query("current_pass") String current_pass,
            @Query("new_pass") String new_pass
    );


    @GET("/index.php")
    Api_Model GetRegister(

            @Query("view") String view,
            @Query("page") String page,
            @Query("name") String name,
            @Query("email") String email,
            @Query("phone") String phone,
            @Query("pass") String pass,
            @Query("whatsapp_no") String whatsapp_no,
            @Query("pincode") String pincode,
            @Query("area_id") String area_id,
            @Query("city") String city,
            @Query("address") String address,
            @Query("refer_code") String refer_code,
            @Query("other_area_name") String other_area_name
    );

    @GET("/index.php")
    Api_Model GetOTPVerified(

            @Query("view") String view,
            @Query("page") String page,
            @Query("userPhone") String userPhone,
            @Query("userID") String userID,
            @Query("otp") String otp
    );


    @GET("/index.php")
    Api_Model ResendOTPVerified(

            @Query("view") String view,
            @Query("page") String page,
            @Query("userPhone") String userPhone,
            @Query("userID") String userID
    );


    @GET("/index.php")
    Api_Model MyOrderlist(
            @Query("view") String view,
            @Query("page") String page,
            @Query("userID") String userID,
            @Query("pagecode") String pagecode

    );

    @GET("/index.php")
    Data Productlist(
            @Query("view") String view,
            @Query("userID") String userID,
            @Query("catID") String catID,
            @Query("pagecode") String pagecode,
            @Query("type") String type

    );


    @GET("/index.php")
    Data UserLastChoiceLsit(
            @Query("view") String view,
            @Query("userID") String userID,
            @Query("userPhone") String userPhone,
            @Query("pagecode") String pagecode

    );

    @GET("/index.php")
    Data ProductlistSearch(
            @Query("view") String view,
            @Query("userID") String userID,
            @Query("catID") String catID,
            @Query("pagecode") String pagecode,
            @Query("type") String type,
            @Query("search") String search

    );

    @GET("/index.php")
    Api_Model getAllUserOffers(
            @Query("view") String key,
            @Query("userID") String userID
    );

    @GET("/index.php")
    Api_Model RferDetail(

            @Query("view") String view,
            @Query("userID") String userID
    );

    @GET("/index.php")
    Api_Model getWalletHistory(
            @Query("view") String key,
            @Query("userID") String userID,
            @Query("userPhone") String userPhone,
            @Query("pagecode") String pagecode

    );

    @GET("/index.php")
    Api_Model OrderDetails(
            @Query("view") String view,
            @Query("page") String page,
            @Query("userID") String userID,
            @Query("orderID") String orderID
    );

    @GET("/index.php")
    Api_Model OrderFinalCharges(
            @Query("view") String view,
            @Query("userID") String userID,
            @Query("userPhone") String userPhone,
            @Query("subtotal") String subtotal,
            @Query("area_id") String area_id
    );


    @GET("/index.php")
    Api_Model AddWalletMoney(
            @Query("view") String view,
            @Query("userID") String userID,
            @Query("page") String page,
            @Query("userPhone") String userPhone,
            @Query("amount") String amount,
            @Query("type") String type

    );

    @GET("/index.php")
    Api_Model PlaceOrder(
            @Query("view") String view,
            @Query("orderDetails") String orderDetails
    );

    @GET("/index.php")
    Data AddtoWishlist(
            @Query("view") String view,
            @Query("page") String page,
            @Query("userID") String userID,
            @Query("pagecode") String pagecode,
            @Query("products") String products,
            @Query("type") String type
    );


    @GET("/index.php")
    Api_Model ApplyCoupon(
            @Query("view") String view,
            @Query("userID") String userID,
            @Query("subtotal") String subtotal,
            @Query("coupon") String coupon

    );

    @GET("/index.php")
    Api_Model getNewCategory(
            @Query("view") String key
    );

}

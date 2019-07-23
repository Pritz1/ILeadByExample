package com.eis.ileadbyexample.Api;

import com.eis.ileadbyexample.Models.BtnEnDsResponce;
import com.eis.ileadbyexample.Models.DefaultResponse;
import com.eis.ileadbyexample.Models.ErrorBooleanResponce;
import com.eis.ileadbyexample.Models.LoginResponse;
import com.eis.ileadbyexample.Pojo.FetchProfileRes;
import com.eis.ileadbyexample.Pojo.HistoryList;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("userlogin.php")
    Call<LoginResponse> userLogin(
            @Field("ecode") String email,
            @Field("password") String password,
            @Field("mobono") String mPhoneNumber,
            @Field("DBPrefix") String DBPrefix
    );

    @FormUrlEncoded
    @POST("registerUser.php")
    Call<DefaultResponse> registerUser(
            @Field("id") String lid,
            @Field("password") String password,
            @Field("mobno") String mobileno,
            @Field("IMEI") String IMEI,
            @Field("DBPrefix") String DBPrefix
    );

    @FormUrlEncoded
    @POST("verifyuser.php")
    Call<DefaultResponse> checkOTP(
            @Field("otp") String OTPPASS,
            @Field("DBPrefix") String DBPrefix
    );

    @FormUrlEncoded
    @POST("resendotp.php")
    Call<DefaultResponse> resendOTP(
            @Field("id") String id,
            @Field("contact") String contact,
            @Field("DBPrefix") String DBPrefix
    );

    @FormUrlEncoded
    @POST("logout.php")
    Call<DefaultResponse> Logout(
            @Field("ecode") String ecode,
            @Field("DBPrefix") String DBPrefix
    );

    @FormUrlEncoded
    @POST("fcinsertion.php")
    Call<DefaultResponse> FCALL(
            @Field("ecode") String ecode,
            @Field("latlang") String latlang,
            @Field("loc") String loc,
            @Field("pincode") String pincode,
            @Field("DBPrefix") String DBPrefix
    );

    @FormUrlEncoded
    @POST("lcinsertion.php")
    Call<DefaultResponse> LCALL(
            @Field("ecode") String ecode,
            @Field("latlang") String latlang,
            @Field("loc") String loc,
            @Field("pincode") String pincode,
            @Field("DBPrefix") String DBPrefix
    );

    @FormUrlEncoded
    @POST("checkstatus.php")
    Call<ErrorBooleanResponce> islogincontinue(
            @Field("ecode") String ecode,
            @Field("DBPrefix") String DBPrefix
    );

    @FormUrlEncoded
    @POST("checkcall.php")
    Call<BtnEnDsResponce> btnenable(
            @Field("ecode") String ecode,
            @Field("DBPrefix") String DBPrefix
    );

    @FormUrlEncoded
    @POST("allcallhistory.php")
    Call<HistoryList> historyList(
            @Field("ecode") String ecode,
            @Field("DBPrefix") String DBPrefix
    );

    @FormUrlEncoded
    @POST("user_profile.php")
    Call<FetchProfileRes> user_profile(
            @Field("ecode") String ecode,
            @Field("DBPrefix") String DBPrefix
    );

    @FormUrlEncoded
    @POST("changepassword.php")
    Call<com.eis.ileadbyexample.Pojo.DefaultResponse> changepassword(
            @Field("ecode") String ecode,
            @Field("oldpass") String oldpass,
            @Field("newpass") String newpass,
            @Field("DBPrefix") String DBPrefix
    );
}

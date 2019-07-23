package com.eis.ileadbyexample.Activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.eis.ileadbyexample.Api.RetrofitClient;
import com.eis.ileadbyexample.Models.DefaultResponse;
import com.eis.ileadbyexample.Others.ViewDialog;
import com.eis.ileadbyexample.R;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPVerification extends AppCompatActivity {

    private PinEntryEditText otp;
    private Button subotpbtn;
    private String id,dbprefix;
    private String contact;
    RelativeLayout rl;
    ViewDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        getSupportActionBar().hide();
        progress = new ViewDialog(this);
        otp = findViewById(R.id.pinView);
        subotpbtn = findViewById(R.id.chkotp);
        rl = findViewById(R.id.rl);

        id = getIntent().getStringExtra("ecode");
        dbprefix = getIntent().getStringExtra("dbprefix");
        contact = getIntent().getStringExtra("mobile");
        contact = contact.substring(2,12);
    }

    public void CheckOTP(View arg) {
        String OTPPASS = otp.getText().toString().trim();

        if (OTPPASS.isEmpty()) {
            otp.setError("OTP required");
            otp.requestFocus();
            return;
        }

        if (OTPPASS.length() < 4) {
            otp.setError("Please enter password which we have sent to you !");
            otp.requestFocus();
            return;
        }
        progress.show();
        Call<DefaultResponse> call = RetrofitClient
                .getInstance().getApi().checkOTP(OTPPASS,dbprefix);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse defaultResponse = response.body();
                progress.dismiss();
                if (!defaultResponse.isError()) {

                    Intent intent = new Intent(OTPVerification.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                } else {
                    Snackbar snackbar = Snackbar.make(rl, defaultResponse.getErrormsg(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                progress.dismiss();
                if (t instanceof IOException) {
                    Snackbar snackbar = Snackbar.make(rl, "INCORRECT OTP", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(rl, "Data Conversion Issue ! Contact to admin", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    public void ReSendOTP(View arg){
        progress.show();
        Call<DefaultResponse> call = RetrofitClient
                .getInstance().getApi().resendOTP(id.trim(),contact.trim(),dbprefix);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse defaultResponse = response.body();
                progress.dismiss();
                if (!defaultResponse.isError()) {
                    Snackbar snackbar = Snackbar.make(rl, defaultResponse.getErrormsg(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(rl, defaultResponse.getErrormsg(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                progress.dismiss();
                if (t instanceof IOException) {
                    Snackbar snackbar = Snackbar.make(rl, "Internet Issue ! Failed to process your request !", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(rl, "Data Conversion Issue ! Contact to admin", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }
}

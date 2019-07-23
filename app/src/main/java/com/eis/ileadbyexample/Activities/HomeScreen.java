package com.eis.ileadbyexample.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.eis.ileadbyexample.Api.RetrofitClient;
import com.eis.ileadbyexample.Fragments.Frg_History;
import com.eis.ileadbyexample.Fragments.Profile_Frag;
import com.eis.ileadbyexample.Frg_Home;
import com.eis.ileadbyexample.Models.DefaultResponse;
import com.eis.ileadbyexample.Others.ViewDialog;
import com.eis.ileadbyexample.R;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreen extends AppCompatActivity {

    private TextView mTextMessage;
    BottomNavigationView bottomNavigationView;
    Fragment fragment = null;
    ViewDialog progress;
    SharedPreferences settings;
    private static final String PREFRENCES_NAME = "my_shared_preff";
    public String ecode,dbprefix;
    ConstraintLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        getSupportActionBar().hide();
        progress = new ViewDialog(HomeScreen.this);
        mTextMessage = findViewById(R.id.message);
        container = findViewById(R.id.container);
        bottomNavigationView = findViewById(R.id.navigation);
        settings = getSharedPreferences(PREFRENCES_NAME,
                Context.MODE_PRIVATE);
        ecode = settings.getString("ecode", "");
        dbprefix = settings.getString("dbprefix", "");
        fragment = new Frg_Home();
        loadFragment(fragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new Frg_Home();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_dashboard:

                        fragment = new Frg_History();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_profile:
                        fragment = new Profile_Frag();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_notifications:
                        dologout();
                        return true;
                }
                return loadFragment(fragment);
            }

        });

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new Frg_Home();
                        return true;
                    case R.id.navigation_dashboard:
                        fragment = new Frg_History();
                        return true;
                    case R.id.navigation_profile:
                        fragment = new Profile_Frag();
                        return true;
                    case R.id.navigation_notifications:
                        dologout();
                        return true;
                }
                return false;
            }
        };
    }
    private void dologout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Logout ?");
        builder.setMessage("Are you sure you wants to Logout ?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
        progress.show();
        Call<DefaultResponse> call = RetrofitClient
                .getInstance().getApi().Logout(ecode.trim(),dbprefix);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse defaultResponse = response.body();
                progress.dismiss();
                if (!defaultResponse.isError()) {

                    settings.edit().clear().apply();
                    Intent intent = new Intent(HomeScreen.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                } else {
                    Snackbar snackbar = Snackbar.make(container, defaultResponse.getErrormsg(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                progress.dismiss();
                if (t instanceof IOException) {
                    Snackbar snackbar = Snackbar.make(container, "Internet Issue ! Failed to process your request !", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(container, "Data Conversion Issue ! Contact to admin", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment


        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}

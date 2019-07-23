package com.eis.ileadbyexample.Activities;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.eis.ileadbyexample.Api.RetrofitClient;
import com.eis.ileadbyexample.Others.ViewDialog;
import com.eis.ileadbyexample.Pojo.DefaultResponse;
import com.eis.ileadbyexample.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {

    TextInputEditText oldpass,newpass,confpass;
    Button update;
    ViewDialog progress;
    public String ecode,dbprefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = new ViewDialog(this);
        oldpass = findViewById(R.id.oldpass);
        newpass = findViewById(R.id.newpass);
        confpass = findViewById(R.id.confpass);
        update = findViewById(R.id.update);
        ecode = getIntent().getStringExtra("ecode");
        dbprefix = getIntent().getStringExtra("dbprefix");

    }

    public void DoUpdate(View arg){

        String op = oldpass.getText().toString().trim();
        String np = newpass.getText().toString().trim();
        String cp = confpass.getText().toString().trim();

        if (op.isEmpty()) {
            oldpass.setError("Password is required");
            oldpass.requestFocus();
            return;
        }

        if (np.isEmpty()) {
            newpass.setError("Password is required");
            newpass.requestFocus();
            return;
        }

        if (np.length() < 8) {
            newpass.setError("Password is to short !");
            newpass.requestFocus();
            return;
        }

        if (cp.isEmpty()) {
            confpass.setError("Confirm password is required");
            confpass.requestFocus();
            return;
        }

        if (cp.length() < 8) {
            confpass.setError("Password is to short !");
            confpass.requestFocus();
            return;
        }

        if (!cp.equalsIgnoreCase(np)) {
            confpass.setError("Password not matched !");
            confpass.requestFocus();
            return;
        }

        progress.show();
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().changepassword(ecode, op, cp, dbprefix);
        call.enqueue(new Callback<DefaultResponse>() {

            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse dResponse = response.body();

                progress.dismiss();

                if (!dResponse.isError()) {
                    finish();
                    Toast.makeText(ChangePassword.this, dResponse.getErrormsg(), Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(ChangePassword.this, dResponse.getErrormsg(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(ChangePassword.this, "Failed to process your request !", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

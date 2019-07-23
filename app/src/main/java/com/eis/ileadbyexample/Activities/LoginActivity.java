package com.eis.ileadbyexample.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eis.ileadbyexample.Api.RetrofitClient;
import com.eis.ileadbyexample.Models.LoginResponse;
import com.eis.ileadbyexample.Others.SharedPrefManager;
import com.eis.ileadbyexample.Others.ViewDialog;
import com.eis.ileadbyexample.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public EditText editTextEmail;
    public TextInputEditText editTextPassword;
    public Button registerButton;
    public FloatingActionButton loginButton;
    RelativeLayout rl;
    String mPhoneNumber;
    Spinner spnArea;
    boolean allgranted = false;
    RequestQueue requestQueue;
    ViewDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        progress = new ViewDialog(this);
        requestQueue = Volley.newRequestQueue(this);
        editTextEmail = findViewById(R.id.lid);
        editTextPassword = findViewById(R.id.pass);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);
        rl = findViewById(R.id.rl);
        spnArea=findViewById(R.id.spnarea);
        getArea();


    }

    @Override
    protected void onStart() {
        super.onStart();
        requestStoragePermission();
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(this, HomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public void DoLogin(View arg) {
        if (allgranted) {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_NUMBERS},0);
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mPhoneNumber = tMgr.getDeviceId();


            String mobileno = mPhoneNumber.trim();

            if (email.isEmpty()) {
                editTextEmail.setError("Login ID is required");
                editTextEmail.requestFocus();
                return;
            }

            if (email.length() < 5) {
                editTextEmail.setError("Enter a valid login ID");
                editTextEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
                return;
            }

            if (password.length() < 8) {
                editTextPassword.setError("Please enter valid password");
                editTextPassword.requestFocus();
                return;
            }

            if (mobileno.isEmpty()) {
                Snackbar snackbar = Snackbar.make(rl, "Not able to read device ID.", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                return;
            }

            progress.show();
            Call<LoginResponse> call = RetrofitClient
                    .getInstance().getApi().userLogin(email, password, mPhoneNumber, spnArea.getSelectedItem().toString().trim());
            call.enqueue(new Callback<LoginResponse>() {

                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    LoginResponse loginResponse = response.body();

                    progress.dismiss();


                    if (!loginResponse.isError()) {
                        // Log.d("message",loginResponse.getMessage());
                        if (loginResponse.getMessage().equalsIgnoreCase("SUCCESS")) {
                            if (loginResponse.getUser().getUser_status().equalsIgnoreCase("verified")) {
                                if (loginResponse.getMessage().equalsIgnoreCase("SUCCESS")) {
                                    SharedPrefManager.getInstance(LoginActivity.this).saveUser(loginResponse.getUser());
                                    SharedPreferences prefs = getSharedPreferences("my_shared_preff", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("dbprefix", spnArea.getSelectedItem().toString().trim());
                                    editor.commit();
                                    Intent intent = new Intent(LoginActivity.this, HomeScreen.class);
                                    //intent.putExtra("mobileno",mPhoneNumber);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Snackbar snackbar = Snackbar.make(rl, loginResponse.getMessage(), Snackbar.LENGTH_INDEFINITE);
                                    snackbar.show();
                                }
                            } else if (loginResponse.getUser().getUser_status().equalsIgnoreCase("deleted")) {
                                Snackbar snackbar = Snackbar.make(rl, "User is deleted. not allowed to logged in sorry !", Snackbar.LENGTH_INDEFINITE);
                                snackbar.show();
                            }/* else if (loginResponse.getUser().getUser_status().equalsIgnoreCase("registered")) {
                                Intent intent = new Intent(LoginActivity.this, OTPVerification.class);
                                intent.putExtra("ecode", loginResponse.getUser().getEcode());
                                intent.putExtra("mobile", mPhoneNumber);
                                intent.putExtra("dbprefix", spnArea.getSelectedItem().toString().trim());
                                startActivity(intent);
                            }*/
                        }
                    } else {
                        Snackbar snackbar = Snackbar.make(rl, loginResponse.getMessage(), Snackbar.LENGTH_INDEFINITE);
                        snackbar.show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    progress.dismiss();
                    if (t instanceof IOException) {
                        Snackbar snackbar = Snackbar.make(rl, "Internet Issue ! Failed to process your request !", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        Snackbar snackbar = Snackbar.make(rl, "Data Conversion Issue !", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            });
        }else{
            requestStoragePermission();
        }
    }
    public void newUser(View view) {
        if (allgranted) {
            startActivity(new Intent(getApplicationContext(), RegisterUser.class));
        }else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            allgranted = true;
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Need Permissions ?");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void getArea() {

        progress.show();
        StringRequest stringRequest =new StringRequest(Request.Method.GET, RetrofitClient.BASE_URL+"getdbnames.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                List<String> arrayList =new ArrayList<>();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String[] dblist = jsonObject.getString("dbnames").split(",");
                    for (String s: dblist) {
                       // Log.d("dbnames",s);
                        arrayList.add(s);
                    }

                    //arrayList.add(jsonObject.getString("area_name"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this,  android.R.layout.simple_spinner_dropdown_item, arrayList);
                adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                spnArea.setAdapter(adapter);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();

            }
        }){};
        requestQueue.add(stringRequest);
    }
}

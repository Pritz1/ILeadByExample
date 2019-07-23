package com.eis.ileadbyexample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eis.ileadbyexample.Activities.LoginActivity;
import com.eis.ileadbyexample.Activities.StartLocationAlert;
import com.eis.ileadbyexample.Api.RetrofitClient;
import com.eis.ileadbyexample.Models.BtnEnDsResponce;
import com.eis.ileadbyexample.Models.DefaultResponse;
import com.eis.ileadbyexample.Models.ErrorBooleanResponce;
import com.eis.ileadbyexample.Others.SharedPrefManager;
import com.eis.ileadbyexample.Others.ViewDialog;
import com.eis.ileadbyexample.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

public class Frg_Home extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    private MapView mapView;
    String Mylocation;
    private Button fcall,lcall;
    SharedPreferences settings;
    private static final String PREFRENCES_NAME = "my_shared_preff";
    public String ecode,latlang,loc,pincode,dbprefix;
    ViewDialog progressDialoge;
    boolean allgranted = false;
    View view;
    TextView reload;
    LocationManager locationManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_frg__home,container, false);

        progressDialoge=new ViewDialog(getActivity());

        fcall = view.findViewById(R.id.fcall);
        reload = view.findViewById(R.id.reload);
        fcall.setEnabled(false);
        fcall.setBackground(getResources().getDrawable(R.drawable.tintbackground));
        lcall = view.findViewById(R.id.lcall);
        lcall.setEnabled(false);
        lcall.setBackground(getResources().getDrawable(R.drawable.tintbackground));
        //lcall.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.tintcolor));
        new StartLocationAlert(getActivity());


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        /*mapFrag = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);*/
        settings = getActivity().getSharedPreferences(PREFRENCES_NAME, Context.MODE_PRIVATE);
        ecode = settings.getString("ecode", "");
        dbprefix = settings.getString("dbprefix", "");
        //latlang = "92.351651321, 67.321685132198";
        //loc = "nerul";

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Please wait reloading map....", Toast.LENGTH_SHORT).show();
                //mapView.invalidate();

                if(mGoogleMap != null) {
                    mGoogleMap.clear();
                    onMapReady(mGoogleMap);
                }
            }
        });

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        getUserStatus();
        requestStoragePermission();


        /*
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            turnGPSOn();
            //Toast.makeText(getActivity(), "GPS is turn on. do not disable it", Toast.LENGTH_SHORT).show();
        }*/

        fcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allgranted == true && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setTitle("Alert ?");
                builder.setMessage("Are you sure this is your current location and you want to mark as your first call ? \n \nIf displayed location is not accurate then click on Reload / Refresh button .");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String ec = ecode == null ? "" : ecode.trim();
                                String ltlg = latlang == null ? "" : latlang.trim();
                                String l = loc == null ? "" : loc.trim();
                                String pin = pincode == null ? "" : pincode.trim();
                                progressDialoge.show();
                                Call<DefaultResponse> call = RetrofitClient
                                        .getInstance().getApi().FCALL(ec,ltlg,l,pin,dbprefix);
                                call.enqueue(new Callback<DefaultResponse>() {
                                    @Override
                                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                                        DefaultResponse defaultResponse = response.body();
                                        progressDialoge.dismiss();
                                        if (!defaultResponse.isError()) {
                                            lcall.setEnabled(true);
                                            lcall.setBackground(getResources().getDrawable(R.drawable.background));
                                            fcall.setEnabled(false);
                                            fcall.setBackground(getResources().getDrawable(R.drawable.tintbackground));
                                            Snackbar snackbar = Snackbar.make(view, defaultResponse.getErrormsg(), Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        } else {
                                            Snackbar snackbar = Snackbar.make(view, defaultResponse.getErrormsg(), Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<DefaultResponse> call, Throwable t) {
                                        progressDialoge.dismiss();
                                        if (t instanceof IOException) {
                                            Snackbar snackbar = Snackbar.make(view, "Internet Issue ! Failed to process your request !", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        } else {
                                            Snackbar snackbar = Snackbar.make(view, "Data Conversion Issue ! Contact to admin", Snackbar.LENGTH_LONG);
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
                }else if(allgranted == false){
                    requestStoragePermission();
                }else if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    //turnGPSOn();
                    Toast.makeText(getActivity(), "GPS is disable trun it on first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(allgranted == true && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setTitle("Alert !");
                builder.setMessage("Are you sure this is your current location and you want to mark as your last call ? \n \nIf displayed location is not accurate then click on Reload / Refresh button .");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String ec = ecode == null ? "" : ecode.trim();
                                String ltlg = latlang == null ? "" : latlang.trim();
                                String l = loc == null ? "" : loc.trim();
                                String pin = pincode == null ? "" : pincode.trim();
                                progressDialoge.show();
                                Call<DefaultResponse> call = RetrofitClient
                                        .getInstance().getApi().LCALL(ec,ltlg,l,pin,dbprefix);
                                call.enqueue(new Callback<DefaultResponse>() {
                                    @Override
                                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                                        DefaultResponse defaultResponse = response.body();
                                        progressDialoge.dismiss();
                                        if (!defaultResponse.isError()) {
                                            lcall.setEnabled(false);
                                            lcall.setBackground(getResources().getDrawable(R.drawable.tintbackground));
                                            fcall.setEnabled(false);
                                            fcall.setBackground(getResources().getDrawable(R.drawable.tintbackground));
                                            Snackbar snackbar = Snackbar.make(view, defaultResponse.getErrormsg(), Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        } else {
                                            Snackbar snackbar = Snackbar.make(view, defaultResponse.getErrormsg(), Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<DefaultResponse> call, Throwable t) {
                                        progressDialoge.dismiss();
                                        if (t instanceof IOException) {
                                            Snackbar snackbar = Snackbar.make(view, "Internet Issue ! Failed to process your request !", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        } else {
                                            Snackbar snackbar = Snackbar.make(view, "Data Conversion Issue ! Contact to admin", Snackbar.LENGTH_LONG);
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
                }else if(allgranted == false){
                    requestStoragePermission();
                }else if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    //turnGPSOn();
                    Toast.makeText(getActivity(), "GPS is disable turn it on first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view ;
    }

    private void getBtnStatus() {
        //progressDialoge.show();
        //Log.d("progress 1-->",ecode);
        Call<BtnEnDsResponce> call2 = RetrofitClient
                .getInstance().getApi().btnenable(ecode,dbprefix);
        call2.enqueue(new Callback<BtnEnDsResponce>() {
            @Override
            public void onResponse(Call<BtnEnDsResponce> call2, Response<BtnEnDsResponce> response) {
                BtnEnDsResponce res = response.body();
                progressDialoge.dismiss();
                //Log.d("progress 2-->",ecode);
                if (res.isFc()) {
                    lcall.setEnabled(false);
                    lcall.setBackground(getResources().getDrawable(R.drawable.tintbackground));
                    fcall.setEnabled(true);
                    fcall.setBackground(getResources().getDrawable(R.drawable.background));
                }

                if (res.isLc()) {
                    lcall.setEnabled(true);
                    lcall.setBackground(getResources().getDrawable(R.drawable.background));
                    fcall.setEnabled(false);
                    fcall.setBackground(getResources().getDrawable(R.drawable.tintbackground));
                }
            }

            @Override
            public void onFailure(Call<BtnEnDsResponce> call2, Throwable t) {
                progressDialoge.dismiss();
                //Log.d("progress 3-->",ecode);
            }
        });
    }

    private void getUserStatus() {
        progressDialoge.show();
        //Log.d("progress 4-->",ecode);
        Call<ErrorBooleanResponce> call1 = RetrofitClient
                .getInstance().getApi().islogincontinue(ecode,dbprefix);
        call1.enqueue(new Callback<ErrorBooleanResponce>() {
            @Override
            public void onResponse(Call<ErrorBooleanResponce> call1, Response<ErrorBooleanResponce> response) {
                ErrorBooleanResponce res = response.body();

                //Log.d("progress 5-->",ecode);
                if (res.isError()) {
                    progressDialoge.dismiss();
                    SharedPrefManager.getInstance(getActivity().getApplicationContext()).clear();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }else{
                    getBtnStatus();
                }
            }

            @Override
            public void onFailure(Call<ErrorBooleanResponce> call1, Throwable t) {
                progressDialoge.dismiss();
                //Log.d("progress 6-->",ecode);
            }
        });
    }

    private void requestStoragePermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            //turnGPSOn();
                            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                Toast.makeText(getActivity(), "Turn on GPS now.", Toast.LENGTH_SHORT).show();
                            }
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
                        showSettingsDialog();
                        //Toast.makeText(getActivity().getApplicationContext(), "System failed to access location. Please goto app setting and enable location manually", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mGoogleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.uber_style));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
                Toast.makeText(getActivity(),"Style parsing failed.", Toast.LENGTH_SHORT).show();
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
            Toast.makeText(getActivity(),"Can't find style.", Toast.LENGTH_SHORT).show();
        }

        mLocationRequest = new LocationRequest();
        //mLocationRequest.setInterval(30000); // two minute interval
        //mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);

                Mylocation = "Location: " + location.getLatitude() + " " + location.getLongitude();
                latlang = location.getLatitude() + "," + location.getLongitude();
                loc = getAddress(location.getLatitude(), location.getLongitude());

                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }


                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                mGoogleMap.addMarker(new MarkerOptions().position(latLng).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(getActivity(),R.drawable.ic_placeholder,"You are here")))).setTitle("This is your current location");

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(latLng); //Taking Point A (First LatLng)
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                mGoogleMap.moveCamera(cu);
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

                /*MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));*/

                //loc = getAddress(location.getLatitude(), location.getLongitude());
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());   //error solved --> Use getContext() instead of getActivity()
        String add="";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);

            String ad1,ad2,ad3,ad4,ad5,ad6,ad7;
            ad1= obj.getAddressLine(0) != null ? obj.getAddressLine(0) : "";
            ad2= obj.getCountryName() != null ? obj.getCountryName() : "";
            ad3= obj.getCountryCode() != null ? obj.getCountryCode() : "";
            ad4= obj.getAdminArea() != null ? obj.getAdminArea() : "";
            ad5= obj.getPostalCode() != null ? obj.getPostalCode() : "";
            ad6= obj.getSubAdminArea() != null ? obj.getSubAdminArea() : "";
            ad7= obj.getLocality() != null ? obj.getLocality() : "";

            add = ad1;
            add = add + ", " + ad2;
            add = add + ", " + ad3;
            add = add + ", " + ad4;
            add = add + ", " + ad5;
            add = add + ", " + ad6;
            add = add + ", " + ad7;
            //add = add + ", " + obj.getSubThoroughfare();
            pincode = ad5;
            //Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return  add;
    }

    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        ImageView markerImage = (ImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }
}

package com.eis.ileadbyexample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eis.ileadbyexample.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class LocationDetails extends FragmentActivity implements OnMapReadyCallback /*{

    TextView fctime,lctime,fcadd,lcad,ttltime,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        getSupportActionBar().hide();

        fctime = findViewById(R.id.fctime);
        lctime = findViewById(R.id.lctime);
        fcadd = findViewById(R.id.fstadd);
        lcad = findViewById(R.id.lstadd);
        ttltime = findViewById(R.id.ttlwrk);
        date = findViewById(R.id.date);

        fctime.setText(getIntent().getStringExtra("fctime"));
        fcadd.setText(getIntent().getStringExtra("fcadd"));
        lctime.setText(getIntent().getStringExtra("lctime"));
        lcad.setText(getIntent().getStringExtra("lcadd"));
        ttltime.setText(getIntent().getStringExtra("wrktime"));
        date.setText(getIntent().getStringExtra("date"));

    }
}*/
{

    private GoogleMap mMap;
    TextView fctime,lctime,fcadd,lcad,ttltime,date;
    ProgressDialog progressDialog;
    String fclatlang,lclatlang;
    TextView back;
    List<LatLng> polygon = new ArrayList<>();
    CardView ttlcard,lscard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        fctime = findViewById(R.id.fctime);
        lctime = findViewById(R.id.lctime);
        fcadd = findViewById(R.id.fstadd);
        lcad = findViewById(R.id.lstadd);
        ttltime = findViewById(R.id.ttlwrk);
        date = findViewById(R.id.date);
        back = findViewById(R.id.back);
        ttlcard = findViewById(R.id.ttlcard);
        lscard = findViewById(R.id.lscard);

        ttlcard.setVisibility(View.GONE);
        lscard.setVisibility(View.GONE);

        fctime.setText(getIntent().getStringExtra("fctime"));
        fcadd.setText(getIntent().getStringExtra("fcadd"));
        lctime.setText(getIntent().getStringExtra("lctime"));
        lcad.setText(getIntent().getStringExtra("lcadd"));
        ttltime.setText(getIntent().getStringExtra("wrktime"));
        date.setText(getIntent().getStringExtra("date"));
        fclatlang = getIntent().getStringExtra("fclatlang");
        lclatlang = getIntent().getStringExtra("lclatlang");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog = new ProgressDialog(LocationDetails.this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            LocationDetails.this, R.raw.uber_style));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
                Toast.makeText(LocationDetails.this,"Style parsing failed.", Toast.LENGTH_SHORT).show();
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
            Toast.makeText(LocationDetails.this,"Can't find style.", Toast.LENGTH_SHORT).show();
        }

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                String[] location = fclatlang.split(",");

                Double lat = Double.valueOf(location[0]);
                Double lang = Double.valueOf(location[1]);

                LatLng cc = new LatLng(lat, lang);
                polygon.add(cc);

                mMap.addMarker(new MarkerOptions().position(cc).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(LocationDetails.this, R.drawable.ic_fc_pin, "You are here")))).setTitle("This is your first call location");

            /*mMap.addMarker(new MarkerOptions().position(cc).title("First Call"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cc,11));
            Marker marker = mMap.addMarker(new MarkerOptions().position(cc).title("First Call"));
            marker.showInfoWindow();*/
                LatLng cc1 = null;
                if(lclatlang != null && !lclatlang.equalsIgnoreCase("")){
                    String[] location1 = lclatlang.split(",");

                    lat = Double.valueOf(location1[0]);
                    lang = Double.valueOf(location1[1]);

                    cc1 = new LatLng(lat, lang);
                    polygon.add(cc1);

                    mMap.addMarker(new MarkerOptions().position(cc1).
                            icon(BitmapDescriptorFactory.fromBitmap(
                                    createCustomMarker(LocationDetails.this, R.drawable.ic_lc_pin, "You are here")))).setTitle("This is your last call location");

                }

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(cc); //Taking Point A (First LatLng)
                if(lclatlang != null && !lclatlang.equalsIgnoreCase("")){
                    assert cc1 != null;
                    builder.include(cc1); //Taking Point A (First LatLng)
                    ttlcard.setVisibility(View.VISIBLE);
                    lscard.setVisibility(View.VISIBLE);
                }
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                mMap.moveCamera(cu);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);

            /*mMap.addMarker(new MarkerOptions().position(cc1).title("Last Call"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cc1));
            Marker marker1 = mMap.addMarker(new MarkerOptions().position(cc1).title("Last Call"));
            marker1.showInfoWindow();*/
            }});
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



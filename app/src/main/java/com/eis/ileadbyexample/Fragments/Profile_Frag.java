package com.eis.ileadbyexample.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eis.ileadbyexample.Activities.ChangePassword;
import com.eis.ileadbyexample.Api.RetrofitClient;
import com.eis.ileadbyexample.Others.ViewDialog;
import com.eis.ileadbyexample.Pojo.FetchProfileRes;
import com.eis.ileadbyexample.Pojo.ProfileItem;
import com.eis.ileadbyexample.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile_Frag extends Fragment {

    View view;
    ViewDialog progressDialog;
    private static final String PREFRENCES_NAME = "my_shared_preff";
    SharedPreferences settings;
    public String ecode,dbprefix;
    TextView name,address,contact,id;
    LinearLayout changepass;
    List<ProfileItem> proitem = new ArrayList<>();
    MaterialCardView m1,m2,m3,m4,m5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_, container, false);

        progressDialog=new ViewDialog(getActivity());
        name = view.findViewById(R.id.name);
        address = view.findViewById(R.id.address);
        contact = view.findViewById(R.id.contact);
        id = view.findViewById(R.id.id);
        changepass = view.findViewById(R.id.changepass);
        m1 = view.findViewById(R.id.m1);
        m2 = view.findViewById(R.id.m2);
        m3 = view.findViewById(R.id.m3);
        m4 = view.findViewById(R.id.m4);
        m5 = view.findViewById(R.id.m5);

        m1.setVisibility(View.GONE);
        m2.setVisibility(View.GONE);
        m3.setVisibility(View.GONE);
        m4.setVisibility(View.GONE);
        m5.setVisibility(View.GONE);

        settings = getActivity().getSharedPreferences(PREFRENCES_NAME, Context.MODE_PRIVATE);
        ecode = settings.getString("ecode", "");
        dbprefix = settings.getString("dbprefix", "");

        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePassword.class);
                intent.putExtra("ecode", ecode);
                intent.putExtra("dbprefix", dbprefix);
                startActivity(intent);
            }
        });

        getProfile();

        return view;
    }

    private void getProfile() {
        progressDialog.show();
            retrofit2.Call<FetchProfileRes> call = RetrofitClient.getInstance().getApi().user_profile(ecode,dbprefix);
            call.enqueue(new Callback<FetchProfileRes>() {
                @Override
                public void onResponse(Call<FetchProfileRes> call, Response<FetchProfileRes> response) {
                    progressDialog.dismiss();
                    FetchProfileRes res = response.body();
                    assert res != null;
                    if(res.getProfile().size()>0){
                        m1.setVisibility(View.VISIBLE);
                        m2.setVisibility(View.VISIBLE);
                        m3.setVisibility(View.VISIBLE);
                        m4.setVisibility(View.VISIBLE);
                        m5.setVisibility(View.VISIBLE);
                        proitem = res.getProfile();
                        name.setText(proitem.get(0).getPre()+". "+proitem.get(0).getEName());
                        contact.setText("+91 "+proitem.get(0).getMobileno());
                        id.setText(ecode);
                        address.setText(proitem.get(0).getAdd1()+" "+proitem.get(0).getAdd2()+" "+proitem.get(0).getAdd3()+" "+proitem.get(0).getCity()+" "+proitem.get(0).getState()+" "+proitem.get(0).getPinCode());
                    }else{
                        m1.setVisibility(View.VISIBLE);
                        m2.setVisibility(View.VISIBLE);
                        m3.setVisibility(View.VISIBLE);
                        m4.setVisibility(View.VISIBLE);
                        name.setText("-");
                        address.setText("-");
                        contact.setText("-");
                        id.setText("-");
                        Toast.makeText(getActivity(), "Empty data !", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<FetchProfileRes> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Failed to fetch profile data !", Toast.LENGTH_LONG).show();
                }
            });
    }
}

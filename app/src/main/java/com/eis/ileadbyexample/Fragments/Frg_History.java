package com.eis.ileadbyexample.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eis.ileadbyexample.LocationDetails;
import com.eis.ileadbyexample.Api.RetrofitClient;
import com.eis.ileadbyexample.Others.ViewDialog;
import com.eis.ileadbyexample.Pojo.CallHistoryItem;
import com.eis.ileadbyexample.Pojo.HistoryList;
import com.eis.ileadbyexample.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class Frg_History extends Fragment {

    RecyclerView rv_hist;
    List<CallHistoryItem> hislist = new ArrayList<>();
    LinearLayout listview , ErrorView;
    private static final String PREFRENCES_NAME = "my_shared_preff";
    SharedPreferences settings;
    public String ecode,dbprefix;
    View view;
    ViewDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_frg__history, container, false);
        progressDialog=new ViewDialog(getActivity());
        rv_hist = view.findViewById(R.id.rv_history);
        listview = view.findViewById(R.id.l2);
        ErrorView = view.findViewById(R.id.l1);
        //int resId = R.anim.layout_animation_fall_down;
        int resId = R.anim.layout_animation_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);
        rv_hist.setLayoutAnimation(animation);
        rv_hist.setNestedScrollingEnabled(false);
        rv_hist.setLayoutManager(new LinearLayoutManager(getActivity()));

        rv_hist.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view=LayoutInflater.from(getActivity()).inflate(R.layout.history_adapter, viewGroup,false);
                Holder holder=new Holder(view);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final Holder myHolder= (Holder) viewHolder;
                final CallHistoryItem model = hislist.get(i);
                myHolder.date.setText("Date : " + model.getRepDate());
                myHolder.first.setText("First Call : " + model.getFCTime());
                myHolder.last.setText("Last Call : " + model.getLSTime());
                myHolder.itemView.setTag(i);
                myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(),LocationDetails.class);
                        intent.putExtra("date", model.getRepDate().trim());
                        intent.putExtra("fctime", model.getFCTime());
                        intent.putExtra("lctime", model.getLSTime());
                        intent.putExtra("fcadd", model.getFCLocAddress());
                        intent.putExtra("lcadd", model.getLSLocAddress());
                        intent.putExtra("fclatlang", model.getFCLatlang());
                        intent.putExtra("lclatlang", model.getLSLatlang());
                        intent.putExtra("wrktime", model.getWKTimeDiff());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public int getItemCount() {
                return hislist.size();
            }
            class Holder extends RecyclerView.ViewHolder {
                TextView date,first,last;
                public Holder(@NonNull View itemView) {
                    super(itemView);
                    date = itemView.findViewById(R.id.date);
                    first = itemView.findViewById(R.id.first);
                    last = itemView.findViewById(R.id.last);
                }
            } }
        );

        settings = getActivity().getSharedPreferences(PREFRENCES_NAME, Context.MODE_PRIVATE);
        ecode = settings.getString("ecode", "");
        dbprefix = settings.getString("dbprefix", "");

        getHistList();
        return view;
    }

    private void getHistList() {
        //data variables call
        progressDialog.show();
        Call<HistoryList> call = RetrofitClient
                .getInstance().getApi().historyList(ecode.trim(),dbprefix);
        call.enqueue(new Callback<HistoryList>() {
            @Override
            public void onResponse(Call<HistoryList> call, retrofit2.Response<HistoryList> response) {
                HistoryList historyList = response.body();
                progressDialog.dismiss();
                hislist = historyList.getCallHistory();
                rv_hist.getAdapter().notifyDataSetChanged();

                if (hislist.size() == 0) {
                    listview.setVisibility(View.GONE);
                    ErrorView.setVisibility(View.VISIBLE);
                }else {
                    listview.setVisibility(View.VISIBLE);
                    ErrorView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<HistoryList> call, Throwable t) {
                progressDialog.dismiss();
                if (t instanceof IOException) {
                    Snackbar snackbar = Snackbar.make(view, "Internet Issue ! Failed to process your request !", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(view, "Data Conversion Issue ! Contact to admin", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
        /*
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, RetrofitClient.BASE_URL+"allcallhistory", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();
                Gson gson = new Gson();
                HistoryList res = gson.fromJson(response, HistoryList.class);
                hislist = res.getCallHistory();
                rv_hist.getAdapter().notifyDataSetChanged();

                if (hislist.size() == 0) {
                    listview.setVisibility(View.GONE);
                    ErrorView.setVisibility(View.VISIBLE);
                }else {
                    listview.setVisibility(View.VISIBLE);
                    ErrorView.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> param = new HashMap<String,String>();
                param.put("ecode", ecode.trim());
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);*/
    }
}

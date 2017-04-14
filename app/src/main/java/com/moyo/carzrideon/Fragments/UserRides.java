package com.moyo.carzrideon.Fragments;

/**
 * Created by Nikil on 11/15/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.moyo.carzrideon.Volley.VolleyJsonRequest;
import com.moyo.carzrideon.Adapters.UserRidesAdapter;
import com.moyo.carzrideon.Models.UserRidesModel;
import com.moyo.carzrideon.R;
import com.facebook.AccessToken;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UserRides extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<UserRidesModel> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserRidesAdapter mAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public UserRides() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.layout_fragment_userrides, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        mAdapter = new UserRidesAdapter(movieList, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        fetchUserRides();
        return view;
    }

    public void fetchUserRides() {
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("rideon", Context.MODE_PRIVATE);

        String mobile = sharedPreferences.getString("mobileNumber", "N/A");


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("mobile", mobile);
        params.put("fb_id", AccessToken.getCurrentAccessToken().getUserId() + "");
        HashMap<String, String> headers = new HashMap<String, String>();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "fetchinguserrides", params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Gson gson = new Gson();
                UserRidesModel referncesModel = gson.fromJson(response.toString(), UserRidesModel.class);
                if (referncesModel.getError().equalsIgnoreCase("false")) {
                    if (referncesModel.getUsers() != null && referncesModel.getUsers().size() > 0) {

                        movieList.clear();
                        mAdapter.notifyDataSetChanged();
                        movieList.addAll(referncesModel.getUsers());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "No accepted rides available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(SplashActivity.this, "Unable to connect server", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                });
        requestQueue.add(jsObjRequest);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onRefresh() {
        fetchUserRides();
    }
}

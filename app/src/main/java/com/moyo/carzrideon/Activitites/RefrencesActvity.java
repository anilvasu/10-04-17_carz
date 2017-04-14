package com.moyo.carzrideon.Activitites;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.moyo.carzrideon.Views.CustomProgressDialog;
import com.moyo.carzrideon.Volley.VolleyJsonRequest;
import com.moyo.carzrideon.Adapters.RefernceAdapter;
import com.moyo.carzrideon.Models.ReferncesModel;
import com.moyo.carzrideon.R;
import com.facebook.AccessToken;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RefrencesActvity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private List<ReferncesModel> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RefernceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refrences_actvity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().hide();
            ImageView left_arrow = (ImageView) findViewById(R.id.left_arrow);
            left_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        mAdapter = new RefernceAdapter(movieList,RefrencesActvity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareRefernceData();
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        prepareRefernceData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void prepareRefernceData() {
        final Dialog progressDialog = CustomProgressDialog.show(RefrencesActvity.this);

        SharedPreferences sharedPreferences = getSharedPreferences("rideon", Context.MODE_PRIVATE);

        String mobile = sharedPreferences.getString("mobileNumber", "N/A");


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("mobile", mobile);
        params.put("fb_id", AccessToken.getCurrentAccessToken().getUserId() + "");
        HashMap<String, String> headers = new HashMap<String, String>();
        RequestQueue requestQueue = Volley.newRequestQueue(RefrencesActvity.this);

        VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "fetchingalerts", params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                progressDialog.cancel();
                swipeRefreshLayout.setRefreshing(false);
                Gson gson = new Gson();
                ReferncesModel referncesModel = gson.fromJson(response.toString(), ReferncesModel.class);
                if (referncesModel.getError().equalsIgnoreCase("false")) {
                    if (referncesModel.getUsers() != null && referncesModel.getUsers().size() > 0) {
                        movieList.clear();
                        mAdapter.notifyDataSetChanged();
                        movieList.addAll(referncesModel.getUsers());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(RefrencesActvity.this, "No users available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RefrencesActvity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(SplashActivity.this, "Unable to connect server", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        progressDialog.cancel();
                    }
                });
        requestQueue.add(jsObjRequest);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}

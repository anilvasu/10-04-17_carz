package com.moyo.carzrideon.Activitites;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.moyo.carzrideon.Models.UserPostedRidesModel;
import com.moyo.carzrideon.Volley.VolleyJsonRequest;
import com.moyo.carzrideon.Adapters.RidersAdapter;
import com.moyo.carzrideon.Models.RidersModel;
import com.moyo.carzrideon.Models.UserRidesModel;
import com.moyo.carzrideon.R;
import com.moyo.carzrideon.Views.CustomProgressDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RideInfoActivity extends AppCompatActivity {


    private TextView passengerName, passengerGender, passengerAge, passengerSource, passengerDestination, passengerMobile,
            vehicleModel, seatsAvailable, cost, startTime, message;
    public static final String DEFAULT = "N/A";
    private CircleImageView fbPicRideChoser;
    private List<RidersModel> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RidersAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_info);
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

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Alert", "Lets See if it Works !!!");
                paramThrowable.printStackTrace();
            }
        });

        passengerName = (TextView) findViewById(R.id.passengerName);
        passengerGender = (TextView) findViewById(R.id.passengerGender);
        passengerAge = (TextView) findViewById(R.id.passengerAge);
        passengerSource = (TextView) findViewById(R.id.passengerSource);
        passengerMobile = (TextView) findViewById(R.id.passengerMobile);
        passengerDestination = (TextView) findViewById(R.id.passengerDestination);
        fbPicRideChoser = (CircleImageView) findViewById(R.id.fbPicRideChoser);
        vehicleModel = (TextView) findViewById(R.id.vehicleModel);
        seatsAvailable = (TextView) findViewById(R.id.passengerSeats);
        cost = (TextView) findViewById(R.id.passengerAmount);
        startTime = (TextView) findViewById(R.id.passengerStartTime);
        message = (TextView) findViewById(R.id.message);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new RidersAdapter(movieList, RideInfoActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra("postedobject")) {
                UserPostedRidesModel userPostedRidesModel = (UserPostedRidesModel) intent.getSerializableExtra("postedobject");
                printUserDetails(userPostedRidesModel);
                mAdapter.bottomLayoutVisibility(true);
                getRidersInfo(userPostedRidesModel.getId());
            } else if (intent.hasExtra("acceptedobject")) {
                UserRidesModel userRidesModel = (UserRidesModel) intent.getSerializableExtra("acceptedobject");
                printUserRideDetails(userRidesModel);
                mAdapter.bottomLayoutVisibility(false);
                getRidersInfo(userRidesModel.getId());
            }
        }

    }

    public String converDateTime(String dateTime) {
        try {


            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = f.parse(dateTime);
            String day = (String) android.text.format.DateFormat.format("dd", date);
            String stringMonth = (String) android.text.format.DateFormat.format("MMM", date);
            String hours = (String) android.text.format.DateFormat.format("HH", date);
            String minutes = (String) android.text.format.DateFormat.format("mm", date);
            Log.d("time", date + "");
            return day + " " + stringMonth + ", " + hours + ":" + minutes;
        } catch (Exception e) {
            return "";
        }
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

    public void getRidersInfo(String task_id) {
        final Dialog progressDialog = CustomProgressDialog.show(RideInfoActivity.this);


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ride_id", task_id);

        HashMap<String, String> headers = new HashMap<String, String>();
        RequestQueue requestQueue = Volley.newRequestQueue(RideInfoActivity.this);

        VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "fetchingridersinfo", params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                progressDialog.cancel();
                Gson gson = new Gson();
                RidersModel referncesModel = gson.fromJson(response.toString(), RidersModel.class);
                if (referncesModel.getError().equalsIgnoreCase("false")) {
                    if (referncesModel.getUsers() != null && referncesModel.getUsers().size() > 0) {
                        movieList.addAll(referncesModel.getUsers());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(RideInfoActivity.this, "No users available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RideInfoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    public String checkNullNA(String s) {
        if (s != null && !s.isEmpty()) {
            return s;
        } else return "N/A";
    }

    public void printUserRideDetails(UserRidesModel userRidesModel) {
        if (userRidesModel != null) {
            passengerName.setText(userRidesModel.getName() + "");
            passengerAge.setText(checkNullNA(userRidesModel.getDob()));
            passengerGender.setText(checkNullNA(userRidesModel.getGender()));
            passengerMobile.setText(checkNullNA(userRidesModel.getMobile()));
            passengerSource.setText(userRidesModel.getSource());
            passengerDestination.setText(userRidesModel.getDestination());
            message.setText("Message: " + checkNullNA(userRidesModel.getMessage()));
            if ((Integer.parseInt(userRidesModel.getSeats()) - Integer.parseInt(userRidesModel.getSeats_available())) == 1) {
                seatsAvailable.setText("1 seat");
            } else if ((Integer.parseInt(userRidesModel.getSeats()) - Integer.parseInt(userRidesModel.getSeats_available())) <= 0) {
                seatsAvailable.setText("No seats available");
            } else {
                seatsAvailable.setText((Integer.parseInt(userRidesModel.getSeats()) - Integer.parseInt(userRidesModel.getSeats_available())) + " Seats ");

            }
            vehicleModel.setText(userRidesModel.getCar_model());
            cost.setText("" + userRidesModel.getCost() );
            startTime.setText(converDateTime(userRidesModel.getStart_time()));
            String imgURL = "https://graph.facebook.com/" + userRidesModel.getFb_id() + "/picture?type=large";

            Glide.with(RideInfoActivity.this).load(imgURL).into(fbPicRideChoser);
        }
    }

    public void printUserDetails(UserPostedRidesModel userPostedRidesModel) {

        if (userPostedRidesModel != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("rideon", Context.MODE_PRIVATE);
            String name = sharedPreferences.getString("name", DEFAULT);
            String age = sharedPreferences.getString("age", DEFAULT);
            String gender = sharedPreferences.getString("gender", DEFAULT);
            String imgURL = sharedPreferences.getString("imageURL", DEFAULT);

            passengerName.setText(name);
            passengerAge.setText(  age);
            passengerGender.setText(  gender);
            passengerMobile.setVisibility(View.GONE);
            passengerSource.setText(userPostedRidesModel.getSource());
            passengerDestination.setText(userPostedRidesModel.getDestination());
            message.setText("Message: " + checkNullNA(userPostedRidesModel.getMessage()));


            if ((Integer.parseInt(userPostedRidesModel.getSeats()) - Integer.parseInt(userPostedRidesModel.getSeats_available())) == 1) {
                seatsAvailable.setText("1 seat");
            } else if ((Integer.parseInt(userPostedRidesModel.getSeats()) - Integer.parseInt(userPostedRidesModel.getSeats_available())) <= 0) {
                seatsAvailable.setText("No seats available");
            } else {
                seatsAvailable.setText((Integer.parseInt(userPostedRidesModel.getSeats()) - Integer.parseInt(userPostedRidesModel.getSeats_available())) + " Seats ");

            }
            vehicleModel.setText( userPostedRidesModel.getCar_model());
            cost.setText("" + userPostedRidesModel.getCost()  );
            startTime.setText("Start Time: " + converDateTime(userPostedRidesModel.getStart_time()));
            Glide.with(RideInfoActivity.this).load(imgURL).into(fbPicRideChoser);
        }

    }

}

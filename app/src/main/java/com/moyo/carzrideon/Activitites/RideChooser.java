package com.moyo.carzrideon.Activitites;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.moyo.carzrideon.Constants.Constants;
import com.moyo.carzrideon.Models.FetchingRides;
import com.moyo.carzrideon.Models.UserCompleteDetailsModel;
import com.moyo.carzrideon.R;
import com.moyo.carzrideon.Views.CustomProgressDialog;
import com.moyo.carzrideon.Volley.VolleyJsonRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RideChooser extends AppCompatActivity implements View.OnClickListener {
    private TextView passengerName, passengerSource, passengerDestination,
            vehicleModel, seatsAvailable, cost, startTime, termsandcondtionsTextview;
    public static final String DEFAULT = "N/A";
    private CircleImageView fbPicRideChoser;
    private Button accept;
    private FetchingRides fetchingRides;
    private CheckBox termsandconditions;
    private EditText message;
    private RelativeLayout linearTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_chooser);

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


        passengerName = (TextView) findViewById(R.id.passengerName);

        passengerSource = (TextView) findViewById(R.id.passengerSource);
        passengerDestination = (TextView) findViewById(R.id.passengerDestination);
        fbPicRideChoser = (CircleImageView) findViewById(R.id.fbPicRideChoser);
        vehicleModel = (TextView) findViewById(R.id.vehicleModel);
        seatsAvailable = (TextView) findViewById(R.id.passengerSeats);
        cost = (TextView) findViewById(R.id.passengerAmount);
        startTime = (TextView) findViewById(R.id.passengerStartTime);
        accept = (Button) findViewById(R.id.accept);
        message = (EditText) findViewById(R.id.message);
        linearTop = (RelativeLayout) findViewById(R.id.linearTop);
        termsandconditions = (CheckBox) findViewById(R.id.termsandcondition);
        termsandcondtionsTextview = (TextView) findViewById(R.id.termsandconditiontext);
        TextView txt = (TextView) findViewById(R.id.termsandconditiontext);
        txt.setPaintFlags(txt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RideChooser.this, "Terms and condition", Toast.LENGTH_SHORT).show();
            }
        });

        termsandcondtionsTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TERMS_AND_CONDITIONS_URL));
                startActivity(browserIntent);
            }
        });


        Bundle extras = getIntent().getExtras();


        if (extras != null) {

            fetchingRides = (FetchingRides) extras.getSerializable("ridedetails");
            passengerName.setText(fetchingRides.getName()+", "+fetchingRides.getDob()+" "+fetchingRides.getGender());


            passengerSource.setText(fetchingRides.getSource());
            passengerDestination.setText(fetchingRides.getDestination());
            vehicleModel.setText(fetchingRides.getCar_model());
            if ((Integer.parseInt(fetchingRides.getSeats()) - Integer.parseInt(fetchingRides.getSeats_available())) == 1) {
                seatsAvailable.setText("One seat available");
            } else if ((Integer.parseInt(fetchingRides.getSeats()) - Integer.parseInt(fetchingRides.getSeats_available())) <= 0) {
                seatsAvailable.setText("No seats available");
            } else {
                seatsAvailable.setText((Integer.parseInt(fetchingRides.getSeats()) - Integer.parseInt(fetchingRides.getSeats_available())) + " Seats Available ");

            }
            cost.setText(fetchingRides.getCost());
            startTime.setText( converDateTime(fetchingRides.getStart_time()));
            String imgURL = "https://graph.facebook.com/" + fetchingRides.getFb_id() + "/picture?type=large";

            Glide.with(RideChooser.this).load(imgURL).into(fbPicRideChoser);

            accept.setOnClickListener(this);

            linearTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fetchUserCompleteDetails(fetchingRides);
                }
            });
        }


    }

    public void fetchUserCompleteDetails(final FetchingRides ridersModel) {
        final Dialog progressDialog = CustomProgressDialog.show(RideChooser.this);


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("fb_id", ridersModel.getFb_id());

        HashMap<String, String> headers = new HashMap<String, String>();
        final RequestQueue requestQueue = Volley.newRequestQueue(RideChooser.this);

        VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "fetchingusercompletedetails", params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                progressDialog.cancel();
                try {
                    if (response.getString("error").equalsIgnoreCase("false")) {
                        Gson gson = new Gson();
                        UserCompleteDetailsModel userCompleteDetailsModel = gson.fromJson(response.toString(), UserCompleteDetailsModel.class);
                        if (userCompleteDetailsModel.getUsers() != null && userCompleteDetailsModel.getUsers().size() > 0) {

                            printUserDetails(ridersModel, userCompleteDetailsModel.getUsers().get(0));
                        } else {
                            Toast.makeText(RideChooser.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(RideChooser.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RideChooser.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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


    public void printUserDetails(FetchingRides ridersModel, UserCompleteDetailsModel userCompleteDetailsModel) {

        final Dialog dialog = new Dialog(RideChooser.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_popup_usercompletedetails);

        TextView name, age, gender, message, mobile, status, mutualFriends, ridesPosted, ridesTaken;
        CircleImageView circleImageView;
        Button ok;

        //TEXTVIEW INTILIZATION
        name = (TextView) dialog.findViewById(R.id.driverName);
        age = (TextView) dialog.findViewById(R.id.driverAge);
        status = (TextView) dialog.findViewById(R.id.driverStatus);
        gender = (TextView) dialog.findViewById(R.id.driverGender);
        message = (TextView) dialog.findViewById(R.id.message);
        mobile = (TextView) dialog.findViewById(R.id.driverMobile);
        circleImageView = (CircleImageView) dialog.findViewById(R.id.fbPicPlacing);
        mutualFriends = (TextView) dialog.findViewById(R.id.mutualfriends);
        ridesPosted = (TextView) dialog.findViewById(R.id.ridesPosted);
        ridesTaken = (TextView) dialog.findViewById(R.id.ridesaccepted);
        ok = (Button) dialog.findViewById(R.id.ok);

        name.setText("Name: " + checkNullNA(ridersModel.getName()));
        age.setText("Age: " + checkNullNA(ridersModel.getDob()));
        gender.setText("Gender: " + checkNullNA(ridersModel.getGender()));

        message.setVisibility(View.GONE);
        status.setVisibility(View.GONE);
        mobile.setVisibility(View.GONE);

        String imgURL = "https://graph.facebook.com/" + ridersModel.getFb_id() + "/picture?type=large";
        Glide.with(RideChooser.this).load(imgURL).into(circleImageView);

        if (Integer.parseInt(userCompleteDetailsModel.getRefernce_count()) == 0) {
            mutualFriends.setText("No mutual friends available");
        } else if (Integer.parseInt(userCompleteDetailsModel.getRefernce_count()) == 1) {
            mutualFriends.setText("one mutual friend available");
        } else {
            mutualFriends.setText(userCompleteDetailsModel.getRefernce_count() + " mutual friends available");
        }

        ridesPosted.setText("Rides Provided: " + userCompleteDetailsModel.getRideposted_count());
        ridesTaken.setText("Rides Taken: " + userCompleteDetailsModel.getRidetaken_count());
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(dialog.getWindow().getAttributes());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;

        int width = displaymetrics.widthPixels;


        dialog.getWindow().setLayout(width, lp.height);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.accept) {
           /* */
            if (termsandconditions.isChecked())
                acceptRide();
            else
                Toast.makeText(RideChooser.this, "Please accept terms and conditions", Toast.LENGTH_SHORT).show();


        }
    }

    public void acceptRide() {

        final CustomProgressDialog customProgressDialog = CustomProgressDialog.show(RideChooser.this);
        SharedPreferences sharedPreferences = getSharedPreferences("rideon", Context.MODE_PRIVATE);
        String DEFAULT = "N/A";
        String apikey = sharedPreferences.getString("apikey", DEFAULT);
        String name = sharedPreferences.getString("name", DEFAULT);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ride_id", fetchingRides.getId() + "");
        params.put("fb_id", fetchingRides.getFb_id() + "");
        params.put("user_name", name + "");
        params.put("message", message.getText().toString() + "");
        params.put("Authorization", apikey);

        RequestQueue requestQueue = Volley.newRequestQueue(RideChooser.this);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", apikey);

        VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "placeride", params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                customProgressDialog.cancel();
                try {
                    if (response.getString("error").equalsIgnoreCase("false")) {
                        Toast.makeText(RideChooser.this, "Ride accepted. Rider will catch you soon", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RideChooser.this, Confirmed.class);
                        intent.putExtra("flag", "taken");
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RideChooser.this, "Something went wrong/Ride not available", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RideChooser.this, "Something went wrong/Ride not available", Toast.LENGTH_LONG).show();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(SplashActivity.this, "Unable to connect server", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        customProgressDialog.cancel();
                        Toast.makeText(RideChooser.this, "Something went wrong/Ride not available", Toast.LENGTH_LONG).show();

                    }
                }) {


        };

        requestQueue.add(jsObjRequest);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}

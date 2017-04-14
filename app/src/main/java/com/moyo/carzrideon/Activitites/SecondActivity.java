package com.moyo.carzrideon.Activitites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.moyo.carzrideon.R;
import com.moyo.carzrideon.Views.CustomProgressDialog;
import com.moyo.carzrideon.Volley.VolleyJsonRequest;
import com.moyo.carzrideon.Models.UserLogin;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    EditText name, gender, age, mobileNum, referenceNum;
    private Button next;
    private String myName, myGender, myAge, imgURL, userId;
    private CircleImageView fbPic;
    private CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        name = (EditText) findViewById(R.id.nameEdit);
        gender = (EditText) findViewById(R.id.genderEdit);
        age = (EditText) findViewById(R.id.ageEdit);
        mobileNum = (EditText) findViewById(R.id.mobileNumber);
        referenceNum = (EditText) findViewById(R.id.referenceNumber);
        next = (Button) findViewById(R.id.next);
        fbPic = (CircleImageView) findViewById(R.id.fbPic);


        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Alert", "Lets See if it Works !!!");
                paramThrowable.printStackTrace();
            }
        });

        Bundle extras = getIntent().getExtras();
        myName = extras.getString("name");
        myGender = extras.getString("gender");
        myAge = extras.getString("age");
        imgURL = extras.getString("imgURL");

        if (null != myName && !myName.isEmpty()) {
            name.setText(myName);
            name.setFocusable(false);
            name.setClickable(true);
        }
        if (null != myGender && !myGender.isEmpty()) {
            gender.setText(myGender);
            gender.setFocusable(false);
            gender.setClickable(true);
        }
        if (null != myAge && !myAge.isEmpty()) {
            age.setText(myAge);
            age.setFocusable(false);
            age.setClickable(true);
        }
        Glide.with(this).load(imgURL).into(fbPic);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next) {
            if (null == name.getText().toString() || name.getText().toString().matches("")) {
                Toast.makeText(SecondActivity.this, "Please enter your name", Toast.LENGTH_LONG).show();
            } else {
                if (null == gender.getText().toString() || gender.getText().toString().matches("")) {
                    Toast.makeText(SecondActivity.this, "Please enter gender", Toast.LENGTH_LONG).show();
                } else {
                    if (null == age.getText().toString() || age.getText().toString().matches("")) {
                        Toast.makeText(SecondActivity.this, "Please enter age", Toast.LENGTH_LONG).show();
                    } else {
                        if (null == mobileNum.getText().toString() || mobileNum.getText().toString().matches("")
                                || mobileNum.length() != 10) {
                            Toast.makeText(SecondActivity.this, "Please enter mobile number", Toast.LENGTH_LONG).show();
                        } else {


                           /* Intent intent = new Intent(SecondActivity.this, SearchAndPlace.class);
                            startActivity(intent);
                            finish();*/
                            UpdateUserDetails();
                        }
                    }
                }
            }
        }
    }


    public void UpdateUserDetails() {


        String ref_Status = "";
        if (referenceNum.getText().toString().isEmpty()) {
            ref_Status = "0";
        } else {
            ref_Status = "1";
        }

        customProgressDialog = CustomProgressDialog.show(SecondActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name.getText().toString() + "");
        params.put("email", name.getText().toString() + "");
        params.put("mobile", mobileNum.getText().toString() + "");
        params.put("fb_id", AccessToken.getCurrentAccessToken().getUserId() + "");
        params.put("gender", gender.getText().toString() + "");
        params.put("ref_status", ref_Status + "");
        params.put("ref_number", referenceNum.getText().toString() + "");
        params.put("dob", age.getText().toString() + "");
        RequestQueue requestQueue = Volley.newRequestQueue(SecondActivity.this);

        HashMap<String, String> headers = new HashMap<String, String>();
        VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "register", params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                customProgressDialog.cancel();
                Gson gson = new Gson();
                UserLogin userLogin = gson.fromJson(response.toString(), UserLogin.class);
                if (userLogin.getError().equalsIgnoreCase("false")) {
                    if (userLogin.getMobile() != null && !userLogin.getMobile().isEmpty()) {

                            SharedPreferences sharedPreferences = getSharedPreferences("rideon", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("name", name.getText().toString());
                            editor.putString("gender", gender.getText().toString());
                            editor.putString("age", age.getText().toString());
                            editor.putString("mobileNumber", mobileNum.getText().toString());
                            editor.putString("imageURL", imgURL);
                            editor.putString("ref_status",userLogin.getRef_status());
                            editor.putString("apikey",userLogin.getApiKey());
                            if (null != referenceNum.getText().toString() || !referenceNum.getText().toString().matches("")) {
                                editor.putString("referenceNumber", referenceNum.getText().toString());
                            }
                            editor.commit();

                            startActivity(new Intent(SecondActivity.this, SearchAndPlace.class));


                    }
                    else
                    {

                    }
                } else {
                    Toast.makeText(SecondActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                    startActivity(new Intent(SecondActivity.this, MainActivity.class));
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(SplashActivity.this, "Unable to connect server", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        customProgressDialog.cancel();
                    }
                });
        requestQueue.add(jsObjRequest);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}

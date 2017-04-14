package com.moyo.carzrideon.Activitites;

/**
 * Created by Arshan on 09-Oct-2016.
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
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
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Alert", "Lets See if it Works !!!");
                paramThrowable.printStackTrace();
            }
        });



        if(isLoggedIn())
        {
            //startActivity(new Intent(SplashScreen.this,SecondActivity.class));
            getUserInfo();
        }
        else
        {
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);

            // close this activity
            finish();
        }


    }

    public void getUserInfo()
    {
        final Dialog progressDialog = CustomProgressDialog.show(SplashScreen.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("fb_id", AccessToken.getCurrentAccessToken().getUserId()+"");

        HashMap<String, String> headers = new HashMap<String, String>();
        RequestQueue requestQueue = Volley.newRequestQueue(SplashScreen.this);

        VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "fetchuserdetailsbyfbid", params,headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                progressDialog.cancel();
                Gson gson = new Gson();
                UserLogin userLogin = gson.fromJson(response.toString(),UserLogin.class);
                //Toast.makeText(SplashScreen.this,userLogin.getMobile()+"",Toast.LENGTH_SHORT).show();
                if (null != userLogin.getMobile() && !userLogin.getMobile().isEmpty()) {

                   if(userLogin.getRef_status()!=null && userLogin.getRef_status().equalsIgnoreCase("3"))
                   {

                       Toast.makeText(SplashScreen.this,"your refernce is refused,Please try with other number",Toast.LENGTH_SHORT).show();
                       String imgURL = "https://graph.facebook.com/" + AccessToken.getCurrentAccessToken().getUserId() + "/picture?type=large";
                       Intent intent = new Intent(SplashScreen.this, SecondActivity.class);
                       intent.putExtra("name", userLogin.getName());
                       intent.putExtra("gender", userLogin.getGender());
                       intent.putExtra("age", userLogin.getDob());
                       intent.putExtra("imgURL",imgURL);
                       startActivity(intent);
                       finish();
                   }
                    else {
                       String imgURL = "https://graph.facebook.com/" + AccessToken.getCurrentAccessToken().getUserId() + "/picture?type=large";
                       SharedPreferences sharedPreferences = getSharedPreferences("rideon", Context.MODE_PRIVATE);
                       SharedPreferences.Editor editor = sharedPreferences.edit();
                       editor.putString("name", userLogin.getName());
                       editor.putString("gender", userLogin.getGender());
                       editor.putString("age", userLogin.getDob());
                       editor.putString("mobileNumber", userLogin.getMobile());
                       editor.putString("imageURL", imgURL);
                       editor.putString("apikey", userLogin.getApiKey());
                       editor.putString("ref_status", userLogin.getRef_status());
                       editor.commit();


                       Intent i = new Intent(SplashScreen.this, SearchAndPlace.class);
                       startActivity(i);
                       finish();
                   }
                } else {
                    Toast.makeText(SplashScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));

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


     public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
}

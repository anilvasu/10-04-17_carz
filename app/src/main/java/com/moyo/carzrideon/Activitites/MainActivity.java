package com.moyo.carzrideon.Activitites;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;
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
import com.moyo.carzrideon.Firebase.Config;
import com.moyo.carzrideon.Firebase.NotificationUtils;
import com.moyo.carzrideon.Models.UserLogin;
import com.moyo.carzrideon.R;
import com.moyo.carzrideon.Views.CustomProgressDialog;
import com.moyo.carzrideon.Volley.VolleyJsonRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends Activity {

    private LoginButton loginButton;
    CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private CustomProgressDialog customProgressDialog;
    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        printKeyHash(MainActivity.this);
        fireBaseFunction();
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {


            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

            }
        };

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Alert", "Lets See if it Works !!!");
                paramThrowable.printStackTrace();
            }
        });
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email", "user_birthday", "user_friends", "read_custom_friendlists");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                customProgressDialog = CustomProgressDialog.show(MainActivity.this);
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {


                                // Application code
                                try {
                                    Log.v("LoginActivity", response.toString());

                                    if (object != null) {
                                        getUserInfo(object);

                                        // LoginManager.getInstance().logOut();


                                    } else {
                                        customProgressDialog.cancel();
                                        LoginManager.getInstance().logOut();
                                        Toast.makeText(MainActivity.this, "Something went wrong,Please try again", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception e) {
                                    customProgressDialog.cancel();
                                    LoginManager.getInstance().logOut();
                                    Toast.makeText(MainActivity.this, "Something went wrong,Please try again", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,first_name,middle_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    public void fireBaseFunction() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    Log.d("message", message);
                }
            }
        };

        displayFirebaseRegId();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Log.d("Firebase id", "Firebase reg id: " + regId);
        else
            Log.d("Firebase id", "Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void getUserInfo(final JSONObject object) {
        final Dialog progressDialog = CustomProgressDialog.show(MainActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("fb_id", AccessToken.getCurrentAccessToken().getUserId() + "");
        HashMap<String, String> headers = new HashMap<String, String>();
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "fetchuserdetailsbyfbid", params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                progressDialog.cancel();
                Gson gson = new Gson();
                UserLogin userLogin = gson.fromJson(response.toString(), UserLogin.class);
                //Toast.makeText(SplashScreen.this,userLogin.getMobile()+"",Toast.LENGTH_SHORT).show();
                if (null != userLogin.getMobile() && !userLogin.getMobile().isEmpty()) {
                    customProgressDialog.cancel();

                    String imgURL = "https://graph.facebook.com/" + AccessToken.getCurrentAccessToken().getUserId() + "/picture?type=large";
                    SharedPreferences sharedPreferences = getSharedPreferences("rideon", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", userLogin.getName());
                    editor.putString("gender", userLogin.getGender());
                    editor.putString("age", userLogin.getDob());
                    editor.putString("mobileNumber", userLogin.getMobile());
                    editor.putString("imageURL", imgURL);
                    editor.putString("ref_status", userLogin.getRef_status());
                    editor.putString("apikey", userLogin.getApiKey());
                    editor.commit();

                    Intent i = new Intent(MainActivity.this, SearchAndPlace.class);
                    startActivity(i);
                    finish();


                } else {
                    String email = checkFacebookExisitence(object, "email");
                    String birthday = checkFacebookExisitence(object, "birthday");
                    String id = checkFacebookExisitence(object, "id");
                    String name = checkFacebookExisitence(object, "name");
                    String gender = checkFacebookExisitence(object, "gender");
                    String age = checkFacebookExisitence(object, "age");
                    String firstname = checkFacebookExisitence(object, "first_name");
                    String middlename = checkFacebookExisitence(object, "middle_name");
                    String lastname = checkFacebookExisitence(object, "last_name");
                    String imgURL = "https://graph.facebook.com/" + id + "/picture?type=large";
                    // 01/31/1980 format

                    String ageNow = getAge(birthday);
                    customProgressDialog.cancel();
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("gender", gender);
                    intent.putExtra("age", ageNow);
                    intent.putExtra("imgURL", imgURL);
                    startActivity(intent);

                    finish();
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

    public String getAge(String dob) {

        if (dob != null && !dob.isEmpty()) {
            StringBuilder str = new StringBuilder(dob);
            int year = Integer.parseInt(str.substring(str.length() - 4));
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            String ageNow = String.valueOf(currentYear - year);
            return ageNow;
        } else return "";
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    public String checkNull(String string) {
        if (string == null || string.isEmpty())
            return "";
        else
            return string;
    }

    public String checkFacebookExisitence(JSONObject object, String parameter) {
        if (object.has(parameter)) {
            try {
                return checkNull(object.getString(parameter));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else
            return "";

        return "";
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isLoggedIn()) {
            //startActivity(new Intent(MainActivity.this,SecondActivity.class));
        }
    }


    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (android.content.pm.Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return key;
    }

}

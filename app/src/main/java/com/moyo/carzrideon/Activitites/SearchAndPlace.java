package com.moyo.carzrideon.Activitites;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.moyo.carzrideon.Constants.Constants;
import com.moyo.carzrideon.Firebase.Config;
import com.moyo.carzrideon.Firebase.NotificationUtils;
import com.moyo.carzrideon.R;
import com.moyo.carzrideon.Views.CustomProgressDialog;
import com.moyo.carzrideon.Volley.VolleyJsonRequest;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAndPlace extends AppCompatActivity implements View.OnClickListener {

    private Button searchPlace, placeRide;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PERMISSION_REQUEST_CODE = 1;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    private CircleImageView fbPicPlacing;
    private TextView driverName, driverGender, driverAge;
    public static final String DEFAULT = "N/A";
    private CustomProgressDialog customProgressDialog;
    private ActionBarDrawerToggle mDrawerToggle;
    private DatePicker datePicker;
    private Calendar calendar;
    private AdView mAdView;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_and_place);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Alert", "Lets See if it Works !!!");
                paramThrowable.printStackTrace();
            }
        });

        searchPlace = (Button) findViewById(R.id.searchPlace);
        placeRide = (Button) findViewById(R.id.placeRide);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);

        try {
            mAdView = (AdView) findViewById(R.id.adView);
         /*  mAdView.setAdSize(AdSize.SMART_BANNER);
           mAdView.setAdUnitId(getString(R.string.banner_home_footer));*/
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("CAA2A2EF6D80AF83D6CFE8254D69FB69")
                    .build();
            mAdView.loadAd(adRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        }
        searchPlace.setOnClickListener(this);
        placeRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("rideon", Context.MODE_PRIVATE);
                String DEFAULT = "N/A";
                String ref_status = sharedPreferences.getString("ref_status", DEFAULT);
                Log.d("ref_Status", ref_status);
                if (ref_status.equalsIgnoreCase("0") || ref_status.equalsIgnoreCase("2")) {
                    Intent intent = new Intent(SearchAndPlace.this, PlacingRide.class);
                    startActivity(intent);
                } else {
                    Log.d("status", "one");
                    Toast.makeText(SearchAndPlace.this, "You can't post a ride until your refernce number is approved", Toast.LENGTH_SHORT).show();
                }
            }
        });


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
                    // txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();
        /**
         * Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();


                if (menuItem.getItemId() == R.id.refernces) {
                    startActivity(new Intent(SearchAndPlace.this, RefrencesActvity.class));

                }

                if (menuItem.getItemId() == R.id.rides) {
                    startActivity(new Intent(SearchAndPlace.this, RidesActivity.class));
                }
                if (menuItem.getItemId() == R.id.logout) {
                    customProgressDialog = CustomProgressDialog.show(SearchAndPlace.this);
                    LoginManager.getInstance().logOut();
                    SharedPreferences settings = getSharedPreferences("rideon", Context.MODE_PRIVATE);
                    settings.edit().clear().commit();
                    startActivity(new Intent(SearchAndPlace.this, MainActivity.class));
                    finish();

                }

                if (menuItem.getItemId() == R.id.contactus) {
                    startActivity(new Intent(SearchAndPlace.this, ContactUsActivity.class));
                }
                if (menuItem.getItemId() == R.id.aboutus) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ABOUT_US_URL));
                    startActivity(browserIntent);
                }

                return false;
            }

        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(" ");
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SearchAndPlace.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        printingNavigationHeaderData();


    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);

    }


    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    myDateListener, year, month, day);
            datePickerDialog.setTitle("Select ride date");
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return datePickerDialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {

        String month_temp = "", date_temp;
        if (month < 10) {
            month_temp = "0" + month;
        } else {
            month_temp = month + "";
        }

        if (day < 10) {
            date_temp = "0" + day;
        } else {
            date_temp = day + "";
        }


        Intent intent = new Intent(SearchAndPlace.this, LocationChooser.class);
        intent.putExtra("ride_date", new StringBuilder().append(year).append("-")
                .append(month_temp).append("-").append(date_temp).toString());
        startActivity(intent);

    }

    public void printingNavigationHeaderData() {
        View header = mNavigationView.getHeaderView(0);
        driverName = (TextView) header.findViewById(R.id.driverName);
        driverAge = (TextView) header.findViewById(R.id.driverAge);
        driverGender = (TextView) header.findViewById(R.id.driverGender);
        fbPicPlacing = (CircleImageView) header.findViewById(R.id.fbPicPlacing);
        SharedPreferences sharedPreferences = getSharedPreferences("rideon", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", DEFAULT);
        String age = sharedPreferences.getString("age", DEFAULT);
        String gender = sharedPreferences.getString("gender", DEFAULT);
        String imgURL = sharedPreferences.getString("imageURL", DEFAULT);
        driverName.setText(name);
        driverGender.setText("Gender: " + gender);
        driverAge.setText("Age: " + age);
        Glide.with(SearchAndPlace.this).load(imgURL).into(fbPicPlacing);
        navigationMenu();
    }

    public void navigationMenu() {
        TextView rides, refernce, help, invite_friends, about_us, rate_us;
        Button logout;

        rides = (TextView) mNavigationView.findViewById(R.id.rides);
        refernce = (TextView)  mNavigationView.findViewById(R.id.refernces);
        help = (TextView)  mNavigationView.findViewById(R.id.help);
        invite_friends = (TextView)  mNavigationView.findViewById(R.id.invite_frieds);
        about_us = (TextView)  mNavigationView.findViewById(R.id.aboutus);
        rate_us = (TextView)  mNavigationView.findViewById(R.id.rateus);
        logout = (Button)  mNavigationView.findViewById(R.id.logout);

        rides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SearchAndPlace.this, RidesActivity.class));
            }
        });

        refernce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchAndPlace.this, RefrencesActvity.class));
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchAndPlace.this, ContactUsActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog = CustomProgressDialog.show(SearchAndPlace.this);
                LoginManager.getInstance().logOut();
                SharedPreferences settings = getSharedPreferences("rideon", Context.MODE_PRIVATE);
                settings.edit().clear().commit();
                startActivity(new Intent(SearchAndPlace.this, MainActivity.class));
                finish();
            }
        });
    }


    public static boolean checkLocationSettings(Context context) {

        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return (locationMode != Settings.Secure.LOCATION_MODE_OFF && locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY); //check location mode

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.searchPlace) {


            if (checkLocationSettings(SearchAndPlace.this)) {
                if (ActivityCompat.checkSelfPermission(SearchAndPlace.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchAndPlace.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    ActivityCompat.requestPermissions(SearchAndPlace.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);

                    return;
                }

                   /* Intent intent = new Intent(SearchAndPlace.this, LocationChooser.class);
                    startActivity(intent);*/
                Toast.makeText(SearchAndPlace.this, "Please select ride date", Toast.LENGTH_SHORT).show();
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);

                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                // datePicker.setMinDate(System.currentTimeMillis());
                setDate(searchPlace);
            } else {
                showLocationModeError();
            }

        }
    }

    public void showLocationModeError() {


        final Dialog dialog = new Dialog(SearchAndPlace.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_popup_locationmodeerror);

        TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialogtitle);
        dialogTitle.setText("Please enable location settings with High Accuracy Mode");
        // set the custom dialog components - text, image and button


        Button dialogButton = (Button) dialog.findViewById(R.id.update);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
            }
        });

        dialog.show();
    }


    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);


        if (!TextUtils.isEmpty(regId)) {
            Log.d("Firebase id", "Firebase reg id: " + regId);
            SharedPreferences sharedPreferences = getSharedPreferences("rideon", Context.MODE_PRIVATE);
            String DEFAULT = "N/A";
            String apikey = sharedPreferences.getString("apikey", DEFAULT);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("fcm_id", regId + "");
            params.put("Authorization", apikey);

            RequestQueue requestQueue = Volley.newRequestQueue(SearchAndPlace.this);

            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", apikey);

            VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "updateFcmID", params, headers, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("response", response.toString());

                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Toast.makeText(SplashActivity.this, "Unable to connect server", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();

                        }
                    }) {


            };

            requestQueue.add(jsObjRequest);
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } else
            Log.d("Firebase id", "Firebase Reg Id is not received yet!");
    }


    @Override
    protected void onResume() {
        super.onResume();

        try {

            if (mAdView != null) {
                mAdView.resume();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

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


        if (mAdView != null) {
            mAdView.pause();
        }

        if (customProgressDialog != null && customProgressDialog.isShowing())
            customProgressDialog.cancel();


        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Snackbar.make(view,"Permission Granted, Now you can access location data.",Snackbar.LENGTH_LONG).show();
                      /* Intent intent = new Intent(SearchAndPlace.this, LocationChooser.class);
                    startActivity(intent);*/
                    Toast.makeText(SearchAndPlace.this, "Please select ride date", Toast.LENGTH_SHORT).show();
                    calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);

                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    // datePicker.setMinDate(System.currentTimeMillis());
                    setDate(searchPlace);


                } else {

                    // Snackbar.make(view,"Permission Denied, You cannot access location data.",Snackbar.LENGTH_LONG).show();
                    Toast.makeText(SearchAndPlace.this, "Please provide us permission to access your location", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else return false;
    }


}

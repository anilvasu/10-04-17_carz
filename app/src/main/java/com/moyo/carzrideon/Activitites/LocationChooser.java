package com.moyo.carzrideon.Activitites;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.moyo.carzrideon.R;
import com.moyo.carzrideon.Volley.VolleyJsonRequest;
import com.moyo.carzrideon.Models.FetchingRides;
import com.moyo.carzrideon.Views.CustomProgressDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LocationChooser extends AppCompatActivity implements View.OnClickListener, RidesAdapter.RidesClickListener,
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    private List<FetchingRides> ridesGetSetList = new ArrayList<>();
    private EditText destination,source,ride_dateEdittext;
    private ImageView clearDest;

    private RidesAdapter ridesAdapter;
    private RecyclerView recyclerView;
    private Double dLat = 0.0, dLang = 0.0, sLat = 0.0, sLang = 0.0;
    private Integer distance = 0;
    public static final String DEFAULT = "N/A";
    private CustomProgressDialog customProgressDialog;
    private String ride_date="";
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int REQUEST_LOCATION = 2;
    private DatePicker datePicker;
    private Calendar calendar;

    private int year, month, day;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_chooser);

        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
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
        destination = (EditText) findViewById(R.id.destination);
        source = (EditText) findViewById(R.id.source);
        clearDest = (ImageView) findViewById(R.id.clearDest);

      //  selDest = (TextView) findViewById(R.id.selDest);
        recyclerView = (RecyclerView) findViewById(R.id.rides_recycle);
        ride_dateEdittext = (EditText) findViewById(R.id.ride_date);
        destination.setOnClickListener(this);
        source.setOnClickListener(this);
        clearDest.setOnClickListener(this);


        ridesAdapter = new RidesAdapter(ridesGetSetList, LocationChooser.this);
        ridesAdapter.setClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(ridesAdapter);
        recyclerView.setOnClickListener(this);

        Intent intent = getIntent();
        if(intent!=null)
        {
           ride_date = intent.getStringExtra("ride_date") ;
            ride_dateEdittext.setText("Selected date: "+ride_date);
        }

        ride_dateEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);

                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                // datePicker.setMinDate(System.currentTimeMillis());
                setDate(ride_dateEdittext);
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog datePickerDialog =  new DatePickerDialog(this,
                    myDateListener, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            return  datePickerDialog;
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

        String month_temp = "",date_temp;
        if(month<10)
        {
            month_temp="0"+month;
        }
        else
        {
            month_temp = month+"";
        }

        if(day<10)
        {
            date_temp="0"+day;
        }
        else
        {
            date_temp = day+"";
        }
        ride_dateEdittext.setText(new StringBuilder().append(year).append("-")
                .append(month_temp).append("-").append(date_temp));



        ride_date = new StringBuilder().append(year).append("-")
                .append(month_temp).append("-").append(date_temp).toString();

        if(source.getText().toString().isEmpty() || destination.getText().toString().isEmpty())
        {
            Toast.makeText(LocationChooser.this,"Please select your source and destination",Toast.LENGTH_SHORT).show();
        }
        else
        {
            fetchingRides();
        }
    }


    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                String latlang = "" + place.getLatLng();
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());
                Log.d("LatLang", latlang);

                dLat = Double.valueOf(getLattitude(latlang));
                dLang = Double.valueOf(getLangitude(latlang));
                Log.d("Lat", "" + dLat);
                Log.d("Lang", "" + dLang);

                destination.setText(place.getName());
                clearDest.setVisibility(View.VISIBLE);
                //selDest.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                //Make call to Async
                //new GetRides().execute();
                fetchingRides();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                String latlang = "" + place.getLatLng();
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());
                Log.d("LatLang", latlang);

                sLat = Double.valueOf(getLattitude(latlang));
                sLang = Double.valueOf(getLangitude(latlang));
                Log.d("Lat", "" + sLat);
                Log.d("Lang", "" + sLang);



                source.setText(place.getName());

              //  selDest.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                //Make call to Async
                //new GetRides().execute();
                fetchingRides();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    //Methods to get lattitude and langitude
    public String getLattitude(String lat) {
        String latt = null;
        latt = lat.substring(lat.lastIndexOf("(") + 1, lat.indexOf(","));
        return latt;
    }

    public String getLangitude(String lang) {
        String langg = null;
        langg = lang.substring(lang.lastIndexOf(",") + 1, lang.indexOf(")"));
        return langg;
    }

    //Method to get distance b/w source and destination
    private Integer getDistance(double lat1, double lon1, double lat2, double lon2) {
        Location locationA = new Location("Source");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);
        Location locationB = new Location("Destination");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);
        distance = Math.round(locationA.distanceTo(locationB));
        return distance;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.destination) {
            try {
                Intent destIntent =
                        new PlaceAutocomplete
                                .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .build(this);
                startActivityForResult(destIntent, 1);

            } catch (GooglePlayServicesRepairableException e) {
                Toast.makeText(LocationChooser.this, "Something went wrong. Try agian!", Toast.LENGTH_LONG).show();
                Log.d("Repairable", "" + e.getLocalizedMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                Toast.makeText(LocationChooser.this, "Something went wrong. Try agian!", Toast.LENGTH_LONG).show();
                Log.d("ServicesNotAvailable", "" + e.getLocalizedMessage());
            }
        }
        else if (view.getId() == R.id.source)
        {
            try {
                Intent destIntent =
                        new PlaceAutocomplete
                                .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .build(this);
                startActivityForResult(destIntent, 2);

            } catch (GooglePlayServicesRepairableException e) {
                Toast.makeText(LocationChooser.this, "Something went wrong. Try agian!", Toast.LENGTH_LONG).show();
                Log.d("Repairable", "" + e.getLocalizedMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                Toast.makeText(LocationChooser.this, "Something went wrong. Try agian!", Toast.LENGTH_LONG).show();
                Log.d("ServicesNotAvailable", "" + e.getLocalizedMessage());
            }
        }


        else if (view.getId() == R.id.clearDest) {
            destination.getText().clear();
            clearDest.setVisibility(View.GONE);

           //
            // selDest.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * Method to display the location on UI
     */
    private void displayLocation() {

       if(customProgressDialog.isShowing())
           customProgressDialog.cancel();

        boolean hasPermissionFine = (ContextCompat.checkSelfPermission(LocationChooser.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermissionCoarse = (ContextCompat.checkSelfPermission(LocationChooser.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionFine || !hasPermissionCoarse) {
            ActivityCompat.requestPermissions(LocationChooser.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        } else {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            sLat = mLocation.getLatitude();
            sLang = mLocation.getLongitude();
            Log.d("curren location:", String.valueOf(sLat) + "::" + String.valueOf(sLang));
            source.setText("Your location");
            //locationText.setText(String.valueOf(sLat)+"::"+String.valueOf(sLang));
        } else {
            Toast.makeText(this, "Location not Detected.", Toast.LENGTH_SHORT).show();
           // showSettingsAlert();
        }
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            customProgressDialog = CustomProgressDialog.show(LocationChooser.this);
        }
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed. Error: " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
      /*  mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);*/
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if (location != null) {
            sLat = location.getLatitude();
            sLang = location.getLongitude();
            Log.d("curren location:::", String.valueOf(sLat) + "::" + String.valueOf(sLang));
        } else {
            Toast.makeText(this, "Location not Detected. Check your location settings", Toast.LENGTH_SHORT).show();
            //showSettingsAlert();
        }
        Toast.makeText(this, "Updated: " + mLastUpdateTime, Toast.LENGTH_SHORT).show();
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationChooser.this);
        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to enable now?");
        // On pressing Settings button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
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

    //Method to load rides
    public void fetchingRides() {

        if(!source.getText().toString().isEmpty() && !destination.getText().toString().isEmpty()) {
            SharedPreferences sharedPreferences = getSharedPreferences("rideon", Context.MODE_PRIVATE);


            String apikey = sharedPreferences.getString("apikey", DEFAULT);
            customProgressDialog = CustomProgressDialog.show(LocationChooser.this);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("source_latitiude", sLat + "");
            params.put("source_longitude", sLang + "");
            params.put("destination_latitude", dLat + "");
            params.put("destination_longitude", dLang + "");
            params.put("Authorization", apikey);
            params.put("ride_date",ride_date);
            RequestQueue requestQueue = Volley.newRequestQueue(LocationChooser.this);
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", apikey);

            Log.d("params",apikey+" "+sLat+" "+sLang+" "+dLat+" "+dLang+" "+ride_date);
            VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "fetchriders", params, headers, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        Log.d("response",response.toString());
                        if (ridesGetSetList.size() > 0) {
                            ridesGetSetList.clear();
                            ridesAdapter.notifyDataSetChanged();
                        }

                       /* JSONArray usersArray = response.getJSONArray("users");
                        int arrSize = usersArray.length();
                        Log.d("arrSze", "" + arrSize);*/

                        Gson gson = new Gson();
                        FetchingRides fetchingRides = gson.fromJson(response.toString(), FetchingRides.class);

                        if (fetchingRides.getError().equalsIgnoreCase("false")) {
                            if (fetchingRides.getUsers() != null && fetchingRides.getUsers().size() > 0) {
                                ridesGetSetList.addAll(fetchingRides.getUsers());
                                customProgressDialog.cancel();
                                ridesAdapter.notifyDataSetChanged();

                            } else {

                                customProgressDialog.cancel();
                                Toast.makeText(LocationChooser.this, "No rides available", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            customProgressDialog.cancel();
                            Toast.makeText(LocationChooser.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        customProgressDialog.cancel();
                        e.printStackTrace();
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
                    }) {
            };
            requestQueue.add(jsObjRequest);
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }

    public String findLocation(String lat, String lang) {
        String loc = null;
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lang), 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            //String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            loc = address;

            Log.d("address", address);
            Log.d("city", city);
            Log.d("state", state);
            Log.d("country", country);
            Log.d("knownName", knownName);

        } catch (IOException e) {
            Log.d("Exception", "" + e.getLocalizedMessage());
        }
        return loc;
    }

    @Override
    public void itemClicked(View view, int position) {

    }
}
package com.moyo.carzrideon.Activitites;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.moyo.carzrideon.Constants.Constants;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlacingRide extends AppCompatActivity implements View.OnClickListener {
    private EditText driverCarModel, driverSeats, driverAmount, driverSource, driverDestination, startTime, driveDate, message;
    private ImageView clearSource, clearDestination;
    private CircleImageView fbPicPlacing;
    private TextView driverName, driverGender, driverAge, termsandconditionsTextview;
    private Button submit;
    public static final String DEFAULT = "N/A";
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Double sLat = 0.0, sLang = 0.0, dLat = 0.0, dLang = 0.0;
    private Integer distance = 0;
    private String mobile;
    private CustomProgressDialog customProgressDialog;
    private String apikey;
    private CheckBox termsandconditions;
    private int hour, minutes;
    private DatePicker datePicker;
    private Calendar calendar;

    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placing_ride);

        // Getting data from Shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("rideon", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", DEFAULT);
        String age = sharedPreferences.getString("age", DEFAULT);
        String gender = sharedPreferences.getString("gender", DEFAULT);
        mobile = sharedPreferences.getString("mobileNumber", DEFAULT);
        String imgURL = sharedPreferences.getString("imageURL", DEFAULT);
        apikey = sharedPreferences.getString("apikey", DEFAULT);

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
        driverCarModel = (EditText) findViewById(R.id.driverCarModel);
        driverSeats = (EditText) findViewById(R.id.driverSeats);
        driverAmount = (EditText) findViewById(R.id.driverAmount);
        driverSource = (EditText) findViewById(R.id.driverSource);
        driverDestination = (EditText) findViewById(R.id.driverDestination);
        termsandconditions = (CheckBox) findViewById(R.id.termsandcondition);
        clearSource = (ImageView) findViewById(R.id.clearSource);
        clearDestination = (ImageView) findViewById(R.id.clearDestination);
        driveDate = (EditText) findViewById(R.id.driverDate);
        driverName = (TextView) findViewById(R.id.driverName);
        driverAge = (TextView) findViewById(R.id.driverAge);
        startTime = (EditText) findViewById(R.id.driverStartTime);
        driverGender = (TextView) findViewById(R.id.driverGender);
        termsandconditionsTextview = (TextView) findViewById(R.id.termsandconditiontext);
        fbPicPlacing = (CircleImageView) findViewById(R.id.fbPicPlacing);
        message = (EditText) findViewById(R.id.message);
        submit = (Button) findViewById(R.id.submit);

        try {
            termsandconditionsTextview.setPaintFlags(termsandconditionsTextview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG | Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String text1 = "I agree with";
            String text2 = "Terms and conditions";

            Spannable spannable = new SpannableString(text2);
            spannable.setSpan(getResources().getColor(R.color.textGreen), text1.length(), (text1 + text2).length(), termsandconditions.getPaintFlags());
            termsandconditionsTextview.setText(spannable, TextView.BufferType.SPANNABLE);
            termsandconditionsTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TERMS_AND_CONDITIONS_URL));
                    startActivity(browserIntent);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            termsandconditionsTextview.setPaintFlags(termsandconditionsTextview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            termsandconditionsTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TERMS_AND_CONDITIONS_URL));
                    startActivity(browserIntent);
                }
            });


        }

        driverName.setText(name);
        driverGender.setText("Gender: " + gender);
        driverAge.setText("Age: " + age);

        driverSource.setOnClickListener(this);
        driverDestination.setOnClickListener(this);
        clearSource.setOnClickListener(this);
        clearDestination.setOnClickListener(this);
        submit.setOnClickListener(this);

        Glide.with(PlacingRide.this).load(imgURL).into(fbPicPlacing);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });


        driveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);

                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                // datePicker.setMinDate(System.currentTimeMillis());
                setDate(driveDate);

            }
        });

        Calendar datetime = Calendar.getInstance();
        String temp_month = "", temp_date = "";
        if ((datetime.get(Calendar.MONTH) + 1) < 10)
            temp_month = "0" + (datetime.get(Calendar.MONTH) + 1);
        else
            temp_month = (datetime.get(Calendar.MONTH) + 1) + "";

        if (datetime.get(Calendar.DATE) < 10)
            temp_date = "0" + datetime.get(Calendar.DATE);
        else
            temp_date = datetime.get(Calendar.DATE) + "";


        driveDate.setText(datetime.get(Calendar.YEAR) + "-" + temp_month + "-" + temp_date);

    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    myDateListener, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
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
        driveDate.setText(new StringBuilder().append(year).append("-")
                .append(month_temp).append("-").append(date_temp));


        if (hour != 0 && minutes != 0) {
            String sMinute = "00", sHour = "00";
            int hourOfDay2 = 0;
            int hourOfDay = hour;
            int minute = minutes;
            Calendar datetime = Calendar.getInstance();
            Calendar c = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);
            String temp_month = "", temp_date = "";
            if ((c.get(Calendar.MONTH) + 1) < 10)
                temp_month = "0" + (c.get(Calendar.MONTH) + 1);
            else
                temp_month = (c.get(Calendar.MONTH) + 1) + "";

            if (c.get(Calendar.DATE) < 10)
                temp_date = "0" + c.get(Calendar.DATE);
            else
                temp_date = c.get(Calendar.DATE) + "";

            if (driveDate.getText().toString().equalsIgnoreCase(c.get(Calendar.YEAR) + "-" + temp_month + "-" + temp_date)) {
//            it's after current
                if (datetime.getTimeInMillis() > c.getTimeInMillis()) {

                    if (hourOfDay > 12) {
                        hourOfDay2 = hourOfDay - 12;
                    } else {
                        hourOfDay2 = hourOfDay;
                    }
                    if (minute < 10) {
                        sMinute = "0" + minute;
                    } else {
                        sMinute = minute + "";
                    }
                    if (hourOfDay2 < 10) {
                        sHour = "0" + hourOfDay2;
                    } else {
                        sHour = hourOfDay2 + "";
                    }
                    if (hourOfDay < 13) {
                        startTime.setText(sHour + ":" + sMinute + " AM");
                    } else {
                        startTime.setText(sHour + ":" + sMinute + " PM");
                    }

                } else {
                    startTime.setText("");
                    Toast.makeText(PlacingRide.this, "Start time should be a future, not the past", Toast.LENGTH_SHORT).show();
                }


            } else {
                hour = hourOfDay;
                minutes = minute;
                if (hourOfDay > 12) {
                    hourOfDay2 = hourOfDay - 12;
                } else {
                    hourOfDay2 = hourOfDay;
                }
                if (minute < 10) {
                    sMinute = "0" + minute;
                } else {
                    sMinute = minute + "";
                }
                if (hourOfDay2 < 10) {
                    sHour = "0" + hourOfDay2;
                } else {
                    sHour = hourOfDay2 + "";
                }
                if (hourOfDay < 13) {
                    startTime.setText(sHour + ":" + sMinute + " AM");
                } else {
                    startTime.setText(sHour + ":" + sMinute + " PM");
                }
            }

        }

    }


    public void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);


        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    String sMinute = "00", sHour = "00";
                    int hourOfDay2 = 0;

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        Calendar datetime = Calendar.getInstance();
                        Calendar c = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);
                        String temp_month = "", temp_date = "";
                        if ((c.get(Calendar.MONTH) + 1) < 10)
                            temp_month = "0" + (c.get(Calendar.MONTH) + 1);
                        else
                            temp_month = (c.get(Calendar.MONTH) + 1) + "";

                        if (c.get(Calendar.DATE) < 10)
                            temp_date = "0" + c.get(Calendar.DATE);
                        else
                            temp_date = c.get(Calendar.DATE) + "";

                        if (driveDate.getText().toString().equalsIgnoreCase(c.get(Calendar.YEAR) + "-" + temp_month + "-" + temp_date)) {
//            it's after current
                            if (datetime.getTimeInMillis() > c.getTimeInMillis()) {
                                hour = hourOfDay;
                                minutes = minute;
                                if (hourOfDay > 12) {
                                    hourOfDay2 = hourOfDay - 12;
                                } else {
                                    hourOfDay2 = hourOfDay;
                                }
                                if (minute < 10) {
                                    sMinute = "0" + minute;
                                } else {
                                    sMinute = minute + "";
                                }
                                if (hourOfDay2 < 10) {
                                    sHour = "0" + hourOfDay2;
                                } else {
                                    sHour = hourOfDay2 + "";
                                }
                                if (hourOfDay < 13) {
                                    startTime.setText(sHour + ":" + sMinute + " AM");
                                } else {
                                    startTime.setText(sHour + ":" + sMinute + " PM");
                                }

                            } else {
                                startTime.setText("");
                                Toast.makeText(PlacingRide.this, "Start time should be a future, not the past", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            hour = hourOfDay;
                            minutes = minute;
                            if (hourOfDay > 12) {
                                hourOfDay2 = hourOfDay - 12;
                            } else {
                                hourOfDay2 = hourOfDay;
                            }
                            if (minute < 10) {
                                sMinute = "0" + minute;
                            } else {
                                sMinute = minute + "";
                            }
                            if (hourOfDay2 < 10) {
                                sHour = "0" + hourOfDay2;
                            } else {
                                sHour = hourOfDay2 + "";
                            }
                            if (hourOfDay < 13) {
                                startTime.setText(sHour + ":" + sMinute + " AM");
                            } else {
                                startTime.setText(sHour + ":" + sMinute + " PM");
                            }
                        }


                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
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
                Log.d("LatLangS", latlang);

                sLat = Double.valueOf(getLattitude(latlang));
                sLang = Double.valueOf(getLangitude(latlang));
                Log.d("sLat", "" + sLat);
                Log.d("sLang", "" + sLang);

                driverSource.setText(place.getName());
                clearSource.setVisibility(View.VISIBLE);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                String latlang = "" + place.getLatLng();
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());
                Log.d("LatLangD", latlang);

                dLat = Double.valueOf(getLattitude(latlang));
                dLang = Double.valueOf(getLangitude(latlang));
                Log.d("dLat", "" + dLat);
                Log.d("dLang", "" + dLang);
                distance = getDistance(sLat, sLang, dLat, dLang);
                Log.d("Distance", "" + distance);


                driverDestination.setText(place.getName());
                clearDestination.setVisibility(View.VISIBLE);

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

    private Integer getDistance(double lat1, double lon1, double lat2, double lon2) {
        Location locationA = new Location("Source");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);
        Location locationB = new Location("Destination");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);
        int distance = Math.round(locationA.distanceTo(locationB));
        return distance;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.driverSource) {
            try {
                Intent destIntent =
                        new PlaceAutocomplete
                                .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .build(this);
                startActivityForResult(destIntent, 1);
            } catch (GooglePlayServicesRepairableException e) {
                Toast.makeText(PlacingRide.this, "Something went wrong. Try agian!", Toast.LENGTH_LONG).show();
                Log.d("Repairable", "" + e.getLocalizedMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                Toast.makeText(PlacingRide.this, "Something went wrong. Try agian!", Toast.LENGTH_LONG).show();
                Log.d("ServicesNotAvailable", "" + e.getLocalizedMessage());
            }
        } else if (view.getId() == R.id.driverDestination) {
            try {
                Intent destIntent =
                        new PlaceAutocomplete
                                .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .build(this);
                startActivityForResult(destIntent, 2);
            } catch (GooglePlayServicesRepairableException e) {
                Toast.makeText(PlacingRide.this, "Something went wrong. Try agian!", Toast.LENGTH_LONG).show();
                Log.d("Repairable", "" + e.getLocalizedMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                Toast.makeText(PlacingRide.this, "Something went wrong. Try agian!", Toast.LENGTH_LONG).show();
                Log.d("ServicesNotAvailable", "" + e.getLocalizedMessage());
            }
        } else if (view.getId() == R.id.clearSource) {
            driverSource.getText().clear();
            clearSource.setVisibility(View.GONE);
        } else if (view.getId() == R.id.clearDestination) {
            driverDestination.getText().clear();
            clearDestination.setVisibility(View.GONE);
        } else if (view.getId() == R.id.submit) {
            if (null == driverCarModel.getText().toString() || driverCarModel.getText().toString().isEmpty()) {
                Toast.makeText(PlacingRide.this, "Please Provide Car Model", Toast.LENGTH_LONG).show();
            } else {
                if (null == driverSeats.getText().toString() || driverSeats.getText().toString().isEmpty()
                        || driverSeats.length() > 2) {
                    Toast.makeText(PlacingRide.this, "Please Provide Number Of Seats Available", Toast.LENGTH_LONG).show();
                } else {
                    if (null == startTime.getText().toString() || startTime.getText().toString().isEmpty()) {
                        Toast.makeText(PlacingRide.this, "Please enter starting time", Toast.LENGTH_LONG).show();
                    } else {
                        if (null == driverAmount.getText().toString() || driverAmount.getText().toString().isEmpty()) {
                            Toast.makeText(PlacingRide.this, "Please Provide Fare Per Seat", Toast.LENGTH_LONG).show();
                        } else {
                            if (null == driverSource.getText().toString() || driverSource.getText().toString().isEmpty()) {
                                Toast.makeText(PlacingRide.this, "Please Select Start Place", Toast.LENGTH_LONG).show();
                            } else {
                                if (null == driverDestination.getText().toString() || driverDestination.getText().toString().isEmpty()) {
                                    Toast.makeText(PlacingRide.this, "Please Select End Place", Toast.LENGTH_LONG).show();
                                } else {

                                    if (null == driveDate.getText().toString() || driveDate.getText().toString().isEmpty()) {

                                        Toast.makeText(PlacingRide.this, "Please enter ride date", Toast.LENGTH_LONG).show();
                                    } else {
                                        if (termsandconditions.isChecked())
                                            placeRide();
                                        else
                                            Toast.makeText(PlacingRide.this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void placeRide() {

        final Calendar c = Calendar.getInstance();
        String month = "";
        String date = "", hour_temp = "", minutes_temp = "";
        if ((c.get(Calendar.MONTH) + 1) < 10) {
            month = "" + c.get(Calendar.MONTH) + 1;
        } else {
            month = "" + c.get(Calendar.MONTH) + 1;
        }
        if ((c.get(Calendar.DATE) + 1) < 10) {
            date = "0" + c.get(Calendar.DATE);
        } else {
            date = "" + c.get(Calendar.DATE);
        }

        if (hour < 10) {
            hour_temp = "0" + hour;
        } else {
            hour_temp = hour + "";
        }

        if (minutes < 10) {
            minutes_temp = "0" + minutes;
        } else {
            minutes_temp = minutes + "";
        }


        customProgressDialog = CustomProgressDialog.show(PlacingRide.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("car_model", driverCarModel.getText().toString() + "");
        params.put("seats", driverSeats.getText().toString() + "");
        params.put("start_time", driveDate.getText().toString() + " " + hour_temp + ":" + minutes_temp + ":00");
        params.put("cost", driverAmount.getText().toString() + "");
        params.put("source_latitiude", sLat + "");
        params.put("source_longitude", sLang + "");
        params.put("destination_latitude", dLat + "");
        params.put("destination_longitude", dLang + "");
        params.put("source", driverSource.getText().toString());
        params.put("destination", driverDestination.getText().toString());
        params.put("ride_date", driveDate.getText().toString());
        params.put("message", message.getText().toString());

        params.put("Authorization", apikey);
        RequestQueue requestQueue = Volley.newRequestQueue(PlacingRide.this);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", apikey);

        VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "rides", params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                customProgressDialog.cancel();

                try {
                    if (response.getString("error") != null && response.getString("error").equalsIgnoreCase("false")) {
                        Toast.makeText(PlacingRide.this, "Ride Offer Sucessfully Posted", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PlacingRide.this, Confirmed.class);
                        intent.putExtra("flag", "offered");
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
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

package com.moyo.carzrideon.Adapters;

/**
 * Created by Nikil on 11/25/2016.
 * <p>
 * Created by Nikil on 11/23/2016.
 * <p>
 * Created by Nikil on 11/23/2016.
 */


/**
 * Created by Nikil on 11/23/2016.
 */

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.moyo.carzrideon.Activitites.RideInfoActivity;
import com.moyo.carzrideon.Models.RidersModel;
import com.moyo.carzrideon.Models.UserCompleteDetailsModel;
import com.moyo.carzrideon.Views.CustomProgressDialog;
import com.moyo.carzrideon.Volley.VolleyJsonRequest;
import com.moyo.carzrideon.R;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RidersAdapter extends RecyclerView.Adapter<RidersAdapter.MyViewHolder> {

    private List<RidersModel> moviesList;
    private Context context;
    private boolean bottomLayoutVisiblity;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, age, gender, message, mobile, status;
        public CircleImageView circleImageView;
        public LinearLayout bottomLayout;
        public Button accept, reject;
        public LinearLayout mainlayout;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.driverName);
            age = (TextView) view.findViewById(R.id.driverAge);
            status = (TextView) view.findViewById(R.id.driverStatus);
            gender = (TextView) view.findViewById(R.id.driverGender);
            message = (TextView) view.findViewById(R.id.message);
            mobile = (TextView) view.findViewById(R.id.driverMobile);
            circleImageView = (CircleImageView) view.findViewById(R.id.fbPicPlacing);
            bottomLayout = (LinearLayout) view.findViewById(R.id.bottomLayout);
            accept = (Button) view.findViewById(R.id.accept);
            reject = (Button) view.findViewById(R.id.reject);
            mainlayout = (LinearLayout) view.findViewById(R.id.mainlayout);
        }
    }


    public RidersAdapter(List<RidersModel> moviesList, Context context) {

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Alert", "Lets See if it Works !!!");
                paramThrowable.printStackTrace();
            }
        });


        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_ridersinfo, parent, false);

        return new MyViewHolder(itemView);
    }

    public void bottomLayoutVisibility(boolean visibility) {
        bottomLayoutVisiblity = visibility;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final RidersModel movie = moviesList.get(position);
        holder.name.setText("Name: " + checkNullNA(movie.getName()));
        holder.age.setText("Age: " + checkNullNA(movie.getDob()));
        holder.gender.setText("Gender: " + checkNullNA(movie.getGender()));
        holder.message.setText("Message: " + checkNullNA(movie.getMessage()));

        if (movie.getStatus().equalsIgnoreCase("0")) {
            holder.status.setText("Status: Pending");
            holder.mobile.setVisibility(View.GONE);
        } else if (movie.getStatus().equalsIgnoreCase("1")) {
            holder.status.setText("Status: Accepted");
            holder.mobile.setVisibility(View.VISIBLE);
            holder.mobile.setText("Mobile: "+checkNullNA(movie.getMobile()));
        } else if (movie.getStatus().equalsIgnoreCase("2")) {
            holder.status.setText("Status: Rejected");
            holder.mobile.setVisibility(View.GONE);
        }

        String imgURL = "https://graph.facebook.com/" + movie.getFb_id() + "/picture?type=large";
        Glide.with(context).load(imgURL).into(holder.circleImageView);
        if (bottomLayoutVisiblity) {
            if (movie.getStatus() != null && movie.getStatus().equalsIgnoreCase("0"))
                holder.bottomLayout.setVisibility(View.VISIBLE);
            else
                holder.bottomLayout.setVisibility(View.GONE);
        } else {
            holder.bottomLayout.setVisibility(View.GONE);
        }
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptorrejectRide(movie.getId(), "1", position,movie.getFb_id());
            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptorrejectRide(movie.getId(), "2", position,movie.getFb_id());
            }
        });

        holder.mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fetchUserCompleteDetails(movie);
            }
        });
    }


    public void fetchUserCompleteDetails(final RidersModel ridersModel) {
        final Dialog progressDialog = CustomProgressDialog.show(context);


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("fb_id", ridersModel.getFb_id());

        HashMap<String, String> headers = new HashMap<String, String>();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

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

                            printUserDetails(ridersModel,userCompleteDetailsModel.getUsers().get(0));
                        }
                        else
                        {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    public void printUserDetails(RidersModel ridersModel, UserCompleteDetailsModel userCompleteDetailsModel) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_popup_usercompletedetails);

        TextView name, age, gender, message, mobile, status,mutualFriends,ridesPosted,ridesTaken;
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
        message.setText("Message: " + checkNullNA(ridersModel.getMessage()));

        if (ridersModel.getStatus().equalsIgnoreCase("0")) {
            status.setText("Status: Pending");
            mobile.setVisibility(View.GONE);
        } else if (ridersModel.getStatus().equalsIgnoreCase("1")) {
            status.setText("Status: Accepted");
            mobile.setVisibility(View.VISIBLE);
            mobile.setText(checkNullNA(ridersModel.getMobile()));
        } else if (ridersModel.getStatus().equalsIgnoreCase("2")) {
            status.setText("Status: Rejected");
            mobile.setVisibility(View.GONE);
        }

        String imgURL = "https://graph.facebook.com/" + ridersModel.getFb_id() + "/picture?type=large";
        Glide.with(context).load(imgURL).into(circleImageView);

        if(Integer.parseInt(userCompleteDetailsModel.getRefernce_count()) == 0 )
        {
            mutualFriends.setText("No mutual friends available");
        }
        else if (Integer.parseInt(userCompleteDetailsModel.getRefernce_count()) == 1)
        {
            mutualFriends.setText("one mutual friend available");
        }
        else
        {
            mutualFriends.setText(userCompleteDetailsModel.getRefernce_count()+" mutual friends available");
        }

        ridesPosted.setText("Rides Provided: "+userCompleteDetailsModel.getRideposted_count());
        ridesTaken.setText("Rides Taken: "+userCompleteDetailsModel.getRidetaken_count());
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
        ((RideInfoActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;

        int width = displaymetrics.widthPixels;


        dialog.getWindow().setLayout(width, lp.height);
    }

    public String checkNullNA(String s) {
        if (s != null && !s.isEmpty()) {
            return s;
        } else return "N/A";
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void acceptorrejectRide(final String ride_id, final String Status, final int position , final String fb_id) {
        final Dialog progressDialog = CustomProgressDialog.show(context);


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("status", Status);
        params.put("ride_id", ride_id + "");
        params.put("fb_id", fb_id + "");
        HashMap<String, String> headers = new HashMap<String, String>();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "acceptorrejectride", params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                progressDialog.cancel();
                try {
                    if (response.getString("error").equalsIgnoreCase("false")) {
                        Toast.makeText(context, "Updated succesfully", Toast.LENGTH_SHORT).show();
                        moviesList.get(position).setStatus(Status);
                        notifyDataSetChanged();
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
                        progressDialog.cancel();
                    }
                });
        requestQueue.add(jsObjRequest);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}


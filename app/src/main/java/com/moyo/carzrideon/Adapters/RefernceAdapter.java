package com.moyo.carzrideon.Adapters;

/**
 * Created by Nikil on 11/23/2016.
 */

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.moyo.carzrideon.Models.ReferncesModel;
import com.moyo.carzrideon.Views.CustomProgressDialog;
import com.moyo.carzrideon.Volley.VolleyJsonRequest;
import com.moyo.carzrideon.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RefernceAdapter extends RecyclerView.Adapter<RefernceAdapter.MyViewHolder> {

    private List<ReferncesModel> moviesList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, passengerName, passengerGender;
        public Button accept, reject;
        public CircleImageView profilePic;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            accept = (Button) view.findViewById(R.id.accept);
            reject = (Button) view.findViewById(R.id.reject);
            profilePic = (CircleImageView) view.findViewById(R.id.fbPicRideChoser);
            passengerGender = (TextView) view.findViewById(R.id.passengerGender);
            passengerName = (TextView) view.findViewById(R.id.passengerName);
        }
    }


    public RefernceAdapter(List<ReferncesModel> moviesList, Context context) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_refernces, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ReferncesModel movie = moviesList.get(position);


        holder.passengerName.setText(movie.getName());
        holder.passengerGender.setText(movie.getGender() + ", " + movie.getDob());
        String imgURL = "https://graph.facebook.com/" + movie.getFb_id() + "/picture?type=large";
        Glide.with(context).load(imgURL).into(holder.profilePic);
        if (!movie.getRef_status().equalsIgnoreCase("pending")) {
            holder.accept.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
            holder.title.setText(movie.getName() + " used your number as refernce and you " + movie.getRef_status().toLowerCase() + " the request.");
        } else {
            holder.accept.setVisibility(View.VISIBLE);
            holder.reject.setVisibility(View.VISIBLE);
            holder.title.setText(movie.getName() + " used your number as refernce, do you know him?");
            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptOrRejectRefrence(movie, "3");
                }
            });
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptOrRejectRefrence(movie, "2");
                }
            });
        }
    }


    public void acceptOrRejectRefrence(final ReferncesModel referncesModel, String Status) {
        final Dialog progressDialog = CustomProgressDialog.show(context);


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("status", Status);
        params.put("fb_id", referncesModel.getFb_id() + "");
        HashMap<String, String> headers = new HashMap<String, String>();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        VolleyJsonRequest jsObjRequest = new VolleyJsonRequest(Request.Method.POST, "acceptorrejectrefernce", params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("response", response.toString());
                progressDialog.cancel();
                try {
                    if (response.getString("error").equalsIgnoreCase("false")) {
                        Toast.makeText(context, "Updated succesfully", Toast.LENGTH_SHORT).show();
                        moviesList.remove(referncesModel);
                        notifyDataSetChanged();
                    } else {
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

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void updateRefernceStatus() {

    }

}

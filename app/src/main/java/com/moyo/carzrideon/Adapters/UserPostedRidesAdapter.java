package com.moyo.carzrideon.Adapters;

/**
 * Created by Nikil on 11/23/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moyo.carzrideon.Activitites.RideInfoActivity;
import com.moyo.carzrideon.Models.UserPostedRidesModel;
import com.moyo.carzrideon.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserPostedRidesAdapter extends RecyclerView.Adapter<UserPostedRidesAdapter.MyViewHolder> {

    private List<UserPostedRidesModel> moviesList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title, source, destination, seatsavailable, carModel, price;
        public LinearLayout parentLayout;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            source = (TextView) view.findViewById(R.id.dSource);
            destination = (TextView) view.findViewById(R.id.dDestination);
            seatsavailable = (TextView) view.findViewById(R.id.seatsavailable);
            parentLayout = (LinearLayout) view.findViewById(R.id.parentlayout);
            carModel = (TextView) view.findViewById(R.id.carmodel);
            price = (TextView) view.findViewById(R.id.price);
        }
    }


    public UserPostedRidesAdapter(List<UserPostedRidesModel> moviesList, Context context) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_userpostedrides, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final UserPostedRidesModel movie = moviesList.get(position);
        holder.title.setText(converDateTime(movie.getCreated_at()));
        holder.source.setText(movie.getSource() + "");
        holder.destination.setText(movie.getDestination() + "");
        holder.carModel.setText(movie.getCar_model());
        holder.price.setText(movie.getCost());
        int seats_available = (Integer.parseInt(movie.getSeats()) - Integer.parseInt(movie.getSeats_available()));
        if (seats_available == 1)
            holder.seatsavailable.setText(seats_available + " seat");
        else if (seats_available > 1)
            holder.seatsavailable.setText(seats_available + " seats");


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.startActivity(new Intent(context, RideInfoActivity.class).putExtra("postedobject", movie));
            }
        });
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
    public int getItemCount() {
        return moviesList.size();
    }

    public void updateRefernceStatus() {

    }

}

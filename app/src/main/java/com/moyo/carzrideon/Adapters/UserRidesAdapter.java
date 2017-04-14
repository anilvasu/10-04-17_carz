package com.moyo.carzrideon.Adapters;

/**
 * Created by Nikil on 11/23/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moyo.carzrideon.Activitites.RideInfoActivity;
import com.moyo.carzrideon.Models.UserRidesModel;
import com.moyo.carzrideon.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRidesAdapter extends RecyclerView.Adapter<UserRidesAdapter.MyViewHolder> {

    private List<UserRidesModel> moviesList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title, source, destination, date, riderName, riderGender;
        public LinearLayout parentLayout;
        public CircleImageView riderProfilePic;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            source = (TextView) view.findViewById(R.id.dSource);
            destination = (TextView) view.findViewById(R.id.dDestination);
            date = (TextView) view.findViewById(R.id.date);
            parentLayout = (LinearLayout) view.findViewById(R.id.parentlayout);
            riderProfilePic = (CircleImageView) view.findViewById(R.id.riderProfilePic);
            riderName = (TextView) view.findViewById(R.id.riderName);
            riderGender = (TextView) view.findViewById(R.id.riderGender);
        }
    }


    public UserRidesAdapter(List<UserRidesModel> moviesList, Context context) {
        this.moviesList = moviesList;
        this.context = context;

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Alert", "Lets See if it Works !!!");
                paramThrowable.printStackTrace();
            }
        });

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_userrides, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final UserRidesModel movie = moviesList.get(position);


        holder.riderName.setText(movie.getName());
        holder.riderGender.setText(movie.getGender() + ", " + movie.getDob());

        String imgURL = "https://graph.facebook.com/" + movie.getFb_id() + "/picture?type=large";
        Glide.with(context).load(imgURL).into(holder.riderProfilePic);
        if (movie.getStatus() != null && movie.getStatus().equalsIgnoreCase("0")) {
           /*String first ="You requested a ride with "+movie.getName()+" and the status is" ;
           String next = "<font color='#F44336'> PENDING</font>";
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
               holder.title.setText(Html.fromHtml(first + next,0));
           }
           else
           {
               holder.title.setText(Html.fromHtml(first + next));
           }*/

            holder.title.setText(movie.getSeats() + " seats requested, Still need to approve ");
        } else if (movie.getStatus() != null && movie.getStatus().equalsIgnoreCase("1")) {
          /* String first ="You requested a ride with "+movie.getName()+" and the status is" ;
           String next = "<font color='#F44336'> ACCEPTED</font>";
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
               holder.title.setText(Html.fromHtml(first + next,0));
           }
           else
           {
               holder.title.setText(Html.fromHtml(first + next));
           }*/

            holder.title.setText(movie.getSeats() + " seats requested, Approved ");
        } else if (movie.getStatus() != null && movie.getStatus().equalsIgnoreCase("2")) {
          /* String first ="You requested a ride with "+movie.getName()+" and the status is" ;
           String next = "<font color='#F44336'> REJECTED</font>";
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
               holder.title.setText(Html.fromHtml(first + next,0));
           }
           else
           {
               holder.title.setText(Html.fromHtml(first + next));
           }*/

            holder.title.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.title.setText(movie.getSeats() + " seats requested, Rejected ");
        }

        holder.source.setText(movie.getSource() + "");

        holder.destination.setText(movie.getDestination() + "");
        holder.date.setText(converDateTime(movie.getRide_date()));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.startActivity(new Intent(context, RideInfoActivity.class).putExtra("acceptedobject", movie));
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

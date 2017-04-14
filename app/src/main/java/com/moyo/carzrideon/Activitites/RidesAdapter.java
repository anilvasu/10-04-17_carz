package com.moyo.carzrideon.Activitites;

/**
 * Created by Arshan on 10-Oct-2016.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moyo.carzrideon.Models.FetchingRides;
import com.bumptech.glide.Glide;
import com.moyo.carzrideon.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.MyViewHolder> {

    private List<FetchingRides> ridesGetSetListsList;
    private RidesClickListener ridesClickListener;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView dName, dCarModel, dSource, dDestination, dPrice, dseatsAvailable,dDate;
        public CircleImageView dPic;
        public LinearLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            dName = (TextView) view.findViewById(R.id.dName);
            dCarModel = (TextView) view.findViewById(R.id.carmodel);
            dPic = (CircleImageView) view.findViewById(R.id.dPic);

            dSource = (TextView) view.findViewById(R.id.dSource);
            dDestination = (TextView) view.findViewById(R.id.dDestination);
            relativeLayout = (LinearLayout) view.findViewById(R.id.mainRelative);

            dPrice = (TextView) view.findViewById(R.id.price);
            dseatsAvailable = (TextView) view.findViewById(R.id.seatsavailable);
            dDate = (TextView) view.findViewById(R.id.date);
            /*dName.setOnClickListener(this);
            dAge.setOnClickListener(this);
            dPic.setOnClickListener(this);
            dGender.setOnClickListener(this);
            dSource.setOnClickListener(this);
            dDestination.setOnClickListener(this);*/
        }

        @Override
        public void onClick(View v) {
            if (ridesClickListener != null) {
                ridesClickListener.itemClicked(v, getPosition());
            }
        }
    }

    public RidesAdapter(List<FetchingRides> ridesGetSetListsList, Context context) {
        this.ridesGetSetListsList = ridesGetSetListsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_row_rides, parent, false);
        ImageView iv = (ImageView) itemView.findViewById(R.id.dPic);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final FetchingRides ridesGetSet = ridesGetSetListsList.get(position);
        holder.dName.setText(ridesGetSet.getName() + ", " + ridesGetSet.getDob() + " " + ridesGetSet.getGender());
        holder.dCarModel.setText(ridesGetSet.getCar_model() + " ( " + (Integer.parseInt(ridesGetSet.getSeats()) - Integer.parseInt(ridesGetSet.getSeats_available())) + " seats )");

        int seats = Integer.parseInt(ridesGetSet.getSeats());

        if (seats == 1)
            holder.dseatsAvailable.setText(ridesGetSet.getSeats() + " seat available");
        else if (seats > 1)
            holder.dseatsAvailable.setText(ridesGetSet.getSeats() + " seats available");

        holder.dPrice.setText(ridesGetSet.getCost()+" /Seat");
        holder.dDate.setText(converDateTime(ridesGetSet.getStart_time()));
     /*   String source = "<font color='#F44336'>"+ ridesGetSet.getSource()+"</font>";
        String to =  "<font color='#000000'> to </font>";;
        String destination = "<font color='#F44336'>"+ ridesGetSet.getDestination()+"</font>";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.dSource.setText(Html.fromHtml(source +to+destination,0));
        }
        else
        {
            holder.dSource.setText(Html.fromHtml(source + to+destination));
        }*/


        //   holder.dDestination.setText(ridesGetSet.getDestination());
        String imgURL = "https://graph.facebook.com/" + ridesGetSet.getFb_id() + "/picture?type=large";
        Glide.with(context).load(imgURL).into(holder.dPic);

        //  holder.dSource.setText(ridesGetSet.getso());
        // holder.dDestination.setText(ridesGetSet.getdDestination());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context, RideChooser.class);
                intent.putExtra("ridedetails", ridesGetSet);

                context.startActivity(intent);
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
    public void setClickListener(RidesClickListener ridesClickListener) {
        this.ridesClickListener = ridesClickListener;
    }

    @Override
    public int getItemCount() {
        return ridesGetSetListsList.size();
    }

    public interface RidesClickListener {
        public void itemClicked(View view, int position);

    }
}
package com.moyo.carzrideon.Models;

import android.graphics.Bitmap;

/**
 * Created by Arshan on 10-Oct-2016.
 */

public class ShowingRides {
    private String dName;
    private String dAge;
    private String dGender;
    private String dSource;
    private String dDestination;
    private Bitmap dPic;

    public ShowingRides(String name, String age, Bitmap pic, String gender, String source, String destination) {
        this.dName = name;
        this.dAge = age;
        this.dPic = pic;
        this.dGender = gender;
        this.dSource = source;
        this.dDestination = destination;
    }
    public ShowingRides() {

    }

    public String getdDestination() {
        return dDestination;
    }

    public void setdDestination(String dDestination) {
        this.dDestination = dDestination;
    }

    public String getdSource() {
        return dSource;
    }

    public void setdSource(String dSource) {
        this.dSource = dSource;
    }



    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public String getdAge() {
        return dAge;
    }

    public void setdAge(String dAge) {
        this.dAge = dAge;
    }

    public String getdGender() {
        return dGender;
    }

    public void setdGender(String dGender) {
        this.dGender = dGender;
    }

    public Bitmap getdPic() {
        return dPic;
    }

    public void setdPic(Bitmap dPic) {
        this.dPic = dPic;
    }
}

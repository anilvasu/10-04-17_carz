package com.moyo.carzrideon.Models;

import java.util.List;

/**
 * Created by Nikil on 1/8/2017.
 */
public class UserCompleteDetailsModel {



    private String refernce_count,ridetaken_count,rideposted_count;
    private List<UserCompleteDetailsModel> users;

    public List<UserCompleteDetailsModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserCompleteDetailsModel> users) {
        this.users = users;
    }

    public String getRefernce_count() {
        return refernce_count;
    }

    public void setRefernce_count(String refernce_count) {
        this.refernce_count = refernce_count;
    }

    public String getRidetaken_count() {
        return ridetaken_count;
    }

    public void setRidetaken_count(String ridetaken_count) {
        this.ridetaken_count = ridetaken_count;
    }

    public String getRideposted_count() {
        return rideposted_count;
    }

    public void setRideposted_count(String rideposted_count) {
        this.rideposted_count = rideposted_count;
    }


}

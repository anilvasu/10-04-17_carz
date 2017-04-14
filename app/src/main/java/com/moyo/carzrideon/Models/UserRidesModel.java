package com.moyo.carzrideon.Models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Nikil on 11/24/2016.
 */
public class UserRidesModel implements Serializable {




    private String  ride_id;
    private String ride_fb_id;
    private String ride_task_id;
    private String sno;
    private String fb_id;
    private String name;
    private String email;
    private String mobile;
    private String gender;
    private String dob;
    private String ref_number;
    private String ref_status;
    private String created_at;
    private String api_key;
    private String error;
    private String source;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;
    private String car_model,seats,seats_available,cost,start_time,id;

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getSeats_available() {
        return seats_available;
    }

    public void setSeats_available(String seats_available) {
        this.seats_available = seats_available;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRide_date() {
        return ride_date;
    }

    public void setRide_date(String ride_date) {
        this.ride_date = ride_date;
    }

    private String ride_date;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    private String destination;
    private List<UserRidesModel> users;

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getRide_fb_id() {
        return ride_fb_id;
    }

    public void setRide_fb_id(String ride_fb_id) {
        this.ride_fb_id = ride_fb_id;
    }

    public String getRide_task_id() {
        return ride_task_id;
    }

    public void setRide_task_id(String ride_task_id) {
        this.ride_task_id = ride_task_id;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getRef_number() {
        return ref_number;
    }

    public void setRef_number(String ref_number) {
        this.ref_number = ref_number;
    }

    public String getRef_status() {
        return ref_status;
    }

    public void setRef_status(String ref_status) {
        this.ref_status = ref_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<UserRidesModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserRidesModel> users) {
        this.users = users;
    }
}

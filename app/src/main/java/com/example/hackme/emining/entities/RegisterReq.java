package com.example.hackme.emining.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kongsin on 11/12/2558.
 */
public class RegisterReq {
    @SerializedName("user")
    public String user;
    @SerializedName("password")
    public String password;
    @SerializedName("email")
    public String email;
}

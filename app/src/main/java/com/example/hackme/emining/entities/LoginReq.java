package com.example.hackme.emining.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kongsin on 11/12/2558.
 */
public class LoginReq {
    @SerializedName("username")
    public String username;
    @SerializedName("password")
    public String password;
}

package com.example.hackme.emining.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kongsin on 10/12/2558.
 */
public class UpdateAccountReq {
    @SerializedName("userID")
    public String userID;
    @SerializedName("updateType")
    public String updateType;
    @SerializedName("userEmail")
    public String userEmail;
    @SerializedName("userName")
    public String userName;
    @SerializedName("oldUserPassword")
    public String oldUserPassword;
    @SerializedName("newUserPassword")
    public String newUserPassword;
    @SerializedName("type")
    public int type;
}

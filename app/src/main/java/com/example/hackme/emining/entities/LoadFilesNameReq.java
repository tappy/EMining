package com.example.hackme.emining.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kongsin on 10/12/2558.
 */
public class LoadFilesNameReq {
    @SerializedName("userID")
    public String userID;
    @SerializedName("tableName")
    public String tableName;
}

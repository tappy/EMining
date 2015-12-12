package com.example.hackme.emining.entities;

import com.google.gson.annotations.SerializedName;

import java.io.File;

/**
 * Created by kongsin on 10/12/2558.
 */
public class AddDataReq {
    @SerializedName("filUpload")
    public File filUpload;
    @SerializedName("userID")
    public String userID;
}

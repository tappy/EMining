package com.example.hackme.emining.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kongsin on 10/12/2558.
 */
public class GetClusterModelReq {
    @SerializedName("param")
    public String param;
    @SerializedName("userid")
    public String userId;
}

package com.example.hackme.emining.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kongsin on 10/12/2558.
 */
public class DeleteTableReq {
    @SerializedName("idUpload")
    public String idUpload;
    @SerializedName("uploadTable")
    public String uploadTable;
    @SerializedName("fileName")
    public String fileName;
    @SerializedName("userid")
    public String userid;
}

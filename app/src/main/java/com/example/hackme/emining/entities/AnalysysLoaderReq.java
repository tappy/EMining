package com.example.hackme.emining.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kongsin on 11/12/2558.
 */
public class AnalysysLoaderReq {
    @SerializedName("tableName")
    public String tableName;
    @SerializedName("algorithm")
    public String algorithm;
    @SerializedName("userid")
    public String userid;

    @SerializedName("class_count")
    public String class_count;
    @SerializedName("max_iteria")
    public String max_iteria;
    @SerializedName("seed")
    public String seed;
    @SerializedName("missing_value")
    public String missing_value;

    @SerializedName("binarySplit")
    public String binarySplit;
    @SerializedName("confidentFactor")
    public String confidentFactor;
    @SerializedName("minNumObj")
    public String minNumObj;
    @SerializedName("numFolds")
    public String numFolds;
    @SerializedName("reduceErrorPuning")
    public String reduceErrorPuning;
    @SerializedName("treeSeed")
    public String treeSeed;
    @SerializedName("subTree")
    public String subTree;
    @SerializedName("unPruned")
    public String unPruned;
    @SerializedName("useLaplace")
    public String useLaplace;

    @SerializedName("classindex")
    public String classindex;
    @SerializedName("delta")
    public String delta;
    @SerializedName("lowerBoundMinSupport")
    public String lowerBoundMinSupport;
    @SerializedName("minMetric")
    public String minMetric;
    @SerializedName("numRules")
    public String numRules;
    @SerializedName("significanceLevel")
    public String significanceLevel;
    @SerializedName("upperBoundMinSupport")
    public String upperBoundMinSupport;

}

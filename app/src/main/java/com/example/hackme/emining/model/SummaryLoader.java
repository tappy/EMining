package com.example.hackme.emining.model;

import com.example.hackme.emining.entities.SummayLoaderReq;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kongsin on 11/12/2558.
 */
public class SummaryLoader {
    public SummaryLoader(SummayLoaderReq req, ModelLoader.DataLoadingListener listener) {
        String json = new Gson().toJson(req);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SummayLoaderReq", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ModelLoader().sendJsonData("getTreeModel.php", jsonObject.toString(), listener);

    }
}

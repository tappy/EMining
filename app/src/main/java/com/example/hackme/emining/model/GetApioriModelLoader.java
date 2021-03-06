package com.example.hackme.emining.model;

import com.example.hackme.emining.entities.GetApioriModelReq;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kongsin on 10/12/2558.
 */
public class GetApioriModelLoader {
    public GetApioriModelLoader(GetApioriModelReq req, ModelLoader.DataLoadingListener listener) {
        String json = new Gson().toJson(req);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("GetApioriModelReq", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ModelLoader().sendJsonData("getAprioryModel.php", jsonObject.toString(), listener);
    }
}

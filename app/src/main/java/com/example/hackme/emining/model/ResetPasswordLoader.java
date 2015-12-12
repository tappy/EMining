package com.example.hackme.emining.model;

import com.example.hackme.emining.entities.ResetPasswordReq;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kongsin on 10/12/2558.
 */
public class ResetPasswordLoader {
    public ResetPasswordLoader(ResetPasswordReq req, ModelLoader.DataLoadingListener listener) {
        String json = new Gson().toJson(req);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ResetPasswordReq", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ModelLoader().sendJsonData("resetPassword.php", jsonObject.toString(), listener);
    }
}

package com.example.hackme.emining.model;

import com.example.hackme.emining.entities.LoginReq;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kongsin on 11/12/2558.
 */
public class LoginLoader {
    public LoginLoader(LoginReq req, ModelLoader.DataLoadingListener listener) {
        String json = new Gson().toJson(req);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LoginReq", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ModelLoader().sendJsonData("checkUser.php", jsonObject.toString(), listener);
    }
}

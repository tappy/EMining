package com.example.hackme.emining.model;

import com.example.hackme.emining.entities.TableLoaderReq;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kongsin on 10/12/2558.
 */
public class TablesLoader {

    public TablesLoader(TableLoaderReq req, ModelLoader.DataLoadingListener listener) {
        String json = new Gson().toJson(req);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("TableLoaderReq", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ModelLoader().sendJsonData("loadListTable.php", jsonObject.toString(), listener);
    }

}

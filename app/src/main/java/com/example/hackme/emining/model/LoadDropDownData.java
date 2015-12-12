package com.example.hackme.emining.model;
import com.example.hackme.emining.entities.TableLoaderReq;

/**
 * Created by kongsin on 9/12/2558.
 */
public class LoadDropDownData {

    public void loading(String userID, ModelLoader.DataLoadingListener listener) {
        TableLoaderReq req = new TableLoaderReq();
        req.userId = userID;
        new TablesLoader(req, listener);
    }

}

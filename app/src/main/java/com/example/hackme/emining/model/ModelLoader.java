package com.example.hackme.emining.model;

import android.util.Log;

import com.example.hackme.emining.Helpers.WebServiceConfig;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

/**
 * Created by kongsin on 10/12/2558.
 */
public class ModelLoader {
    private OkHttpClient client;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType TEXT
            = MediaType.parse("text/text; charset=utf-8");
    public static final MediaType FILE
            = MediaType.parse("text/csv; charset=utf-8");

    public ModelLoader() {
        client = new OkHttpClient();
    }

    public void sendJsonData(String pathName, String json, final DataLoadingListener listener) {
        Log.d("REQ", json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(WebServiceConfig.getHost(pathName))
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("RES", String.valueOf(e.getMessage()));
                listener.onFailed(String.valueOf(e.getMessage()));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String data = response.body().string();
                Log.d("RES", data);
                listener.onLoaded(data);
            }
        });
    }

    public void sendTextData(String pathName, String text, final DataLoadingListener listener) {

        RequestBody body = RequestBody.create(TEXT, text);
        Request request = new Request.Builder()
                .url(WebServiceConfig.getHost(pathName))
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                listener.onFailed(String.valueOf(e.getMessage()));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String data = response.body().string();
                Log.d("RES", data);
                listener.onLoaded(data);
            }
        });
    }

    public void uploadFile(String filename, RequestBody requestBody, final DataLoadingListener listener) {

        Request request = new Request.Builder()
                .url(WebServiceConfig.getHost(filename))
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                listener.onFailed(String.valueOf(e.getMessage()));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String data = response.body().string();
                Log.d("RES", data);
                listener.onLoaded(data);
            }
        });
    }

    public interface DataLoadingListener {
        void onLoaded(String data);

        void onFailed(String message);
    }

}

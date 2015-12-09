package com.example.hackme.emining.model;

import android.util.Log;

import com.example.hackme.emining.Helpers.WebServiceConfig;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kongsin on 9/12/2558.
 */
public class LoadDropDownData {

    public String loading(String userID) {
        try {
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(new WebServiceConfig().getHost("tableNameList.php"));
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("user_id", userID));
            post.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
            }
            Log.d("res1", builder.toString());
            return builder.toString();
        } catch (Exception e) {
            return null;
        }
    }

}

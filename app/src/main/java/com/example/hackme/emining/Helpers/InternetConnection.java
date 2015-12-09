package com.example.hackme.emining.Helpers;


import android.content.Context;
import android.net.ConnectivityManager;

public class InternetConnection {

        public static boolean isNetworkAvailable(Context context)
        {
            return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
        }
}

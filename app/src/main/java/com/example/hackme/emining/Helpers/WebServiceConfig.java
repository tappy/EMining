package com.example.hackme.emining.Helpers;

/**
 * Created by hackme on 21/10/2557.
 */
public class WebServiceConfig {
//    public static String host = "http://10.42.0.1/ServerEMining/";
//    public static String host = "http://10.0.3.2/ServerEMining/";

    //    public static String host = "http://192.168.56.1/ServerEMining/";
    public static String host = "http://192.168.137.1/ServerEMining/";
    //public String host = "http://www.tappy.zz.mu/ServerEMining/";
    public static String getHost(String filename) {
        return host + filename;
    }
}

package com.example.hackme.emining;

/**
 * Created by hackme on 21/10/2557.
 */
public class webServiceConfig {
    //public String host = "http://10.42.0.1/ServerEMining/";
    public String host = "http://10.0.3.2/ServerEMining/";
    //public String host = "http://192.168.10.90/ServerEMining/";
    //public String host = "http://math.sci.ubu.ac.th/ServerEMining/";
    //public String host = "http://www.tappy.zz.mu/ServerEMining/";
    public String getHost(String filename) {
        return host + filename;
    }
}

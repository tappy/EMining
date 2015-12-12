package com.example.hackme.emining.Helpers;

/**
 * Created by kongsin on 10/12/2558.
 */
public class StringHelper {
    public static String getNumRuled(String val) {
        return val.trim().split(" ")[0];
    }

    public static String getPart1(String val) {
        String[] mval = val.trim().split(" ");
        String line = "";
        for (int i = 1; i < mval.length; i++) {
            line += mval[i];
            if (i < (mval.length - 2)) line += " , ";
            else line += "  ";
        }
        return line;
    }
}

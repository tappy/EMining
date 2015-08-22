package com.example.hackme.emining;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class database_manager extends SQLiteOpenHelper {
    private static final String DBNAME = "EMining";
    //private static final String TABLEMODEL = "model_table";
    private static final String TABLELOGIN = "login_table";
    private static final int VERSION = 1;
    private SQLiteDatabase db;
    private Context context;

    public database_manager(Context context) {
        super(context, DBNAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLELOGIN + " " +
                "(id INTEGER PRIMARY KEY," +
                "user_id TEXT(6)," +
                "username TEXT(255)," +
                "email TEXT(255))";
        db.execSQL(sql);
        Log.d("create table", "Create user table");

    }

    public boolean exitsTable(String tableName) {
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        return cursor.getCount() > 0;
    }



    public String[] saveSession(String user, String id,String email) {
        db = getWritableDatabase();
        if (exitsTable(TABLELOGIN)) db.execSQL("DELETE FROM " + TABLELOGIN + " WHERE 1");
        ContentValues con = new ContentValues();
        con.put("id", 1);
        con.put("user_id", id);
        con.put("username", user);
        con.put("email",email);
        db.insert(TABLELOGIN, null, con);
        return getSession();
    }

    public boolean upgradeSession(String attr,String val){
        db = getWritableDatabase();
        ContentValues con = new ContentValues();
        con.put(attr,val);
        db.update(TABLELOGIN,con,"id=1",null);
        return true;
    }


    public String[] getSession() {
        int col = 4;
        String[] val = new String[col];
        db = getReadableDatabase();
        Cursor c1 = db.rawQuery("SELECT * FROM " + TABLELOGIN, null);
        while (c1.moveToNext()) {
            for (int i = 0; i < col; i++) {
                val[i] = c1.getString(i);
            }
        }
        return val;
    }

    public String getLoginId() {
        return getSession()[1];
    }

/*    public String geLogintUserName() {
        return getSession()[2];
    }*/

    public boolean existUser() {
        db = getReadableDatabase();
        Cursor c1 = db.rawQuery("SELECT * FROM " + TABLELOGIN, null);
        Log.d("row count", "" + c1.getCount());
        return c1.getCount() > 0;
    }

    public void logout_action() {
        clearUser();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void clearUser(){
        db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLELOGIN + " WHERE 1");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

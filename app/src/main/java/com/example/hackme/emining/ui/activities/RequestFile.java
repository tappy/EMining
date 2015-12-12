package com.example.hackme.emining.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackme.emining.R;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.model.ModelLoader;
import com.example.hackme.emining.model.UploadFileLoader;

import org.json.JSONObject;
import java.io.File;


public class RequestFile extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tv32;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requesting_file);

        intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        progressBar = (ProgressBar) findViewById(R.id.progressBar4);
        tv32 = (TextView) findViewById(R.id.textView32);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("text/csv")) {
                handleSendFile(intent); // Handle single image being sent
            } else {
                handleArff(intent);
            }
        } else {
            handleArff(intent);
        }
    }


    void handleArff(Intent intent) {
        Uri fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (fileUri != null) {
            File file = new File(getRealPathFromURI(fileUri));
            String[] ftype = file.getName().split("\\.");

            if (ftype[(ftype.length - 1)].equals("arff") || ftype[(ftype.length - 1)].equals("csv")) {
                if (new DatabaseManager(getBaseContext()).existUser()) {
                    newUploadFile(file, new DatabaseManager(getBaseContext()).getLoginId());
                } else {
                    Intent login = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(login);
                }
            } else {
                tv32.setText("ไฟล์ไม่รองรับ");
                AlertDialog adl = new AlertDialog.Builder(RequestFile.this)
                        .setTitle("อัพโหลดไม่สำเร็จ")
                        .setMessage("ไฟล์ไม่รองรับ อัพโหลดไม่สำเร็จ")
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.closeBtn), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create();
                adl.show();
            }
        } else {
            tv32.setText("อัพโหลดไม่สำเร็จ");
            Toast.makeText(getBaseContext(), "อัพโหลดไม่สำเร็จ", Toast.LENGTH_LONG).show();
            AlertDialog adl = new AlertDialog.Builder(RequestFile.this)
                    .setTitle("อัพโหลดไม่สำเร็จ")
                    .setMessage("การอัพโหลดไม่สำเร็จ")
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.closeBtn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create();
            adl.show();
        }
    }

    void handleSendFile(Intent intent) {
        Uri fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (fileUri != null) {
            File file = new File(getRealPathFromURI(fileUri));
            if (new DatabaseManager(getBaseContext()).existUser()) {
                newUploadFile(file, new DatabaseManager(getBaseContext()).getLoginId());
            } else {
                Intent login = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(login);
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = this.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    public void newUploadFile(File file, String userId) {
        new UploadFileLoader(file, userId, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(String data) {
                try {
                    JSONObject jso = new JSONObject(data);
                    if (jso.getInt("StatusID") == 1) {
                        tv32.setText("อัพโหลดสำเร็จ");
                        AlertDialog adl = new AlertDialog.Builder(RequestFile.this)
                                .setTitle("อัพโหลดสำเร็จ")
                                .setMessage("การอัพโหลดสำเร็จแล้ว")
                                .setCancelable(true)
                                .setNegativeButton("วิเคราะห์เหมืองข้อมูล", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent in = new Intent(RequestFile.this, MainPage.class);
                                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(in);
                                    }
                                })
                                .setPositiveButton(getString(R.string.closeBtn), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        intent = null;
                                        finish();
                                    }
                                })
                                .create();
                        adl.show();
                    } else {
                        tv32.setText("อัพโหลดไม่สำเร็จ");
                        Toast.makeText(getBaseContext(), "อัพโหลดไม่สำเร็จ", Toast.LENGTH_LONG).show();
                        AlertDialog adl = new AlertDialog.Builder(RequestFile.this)
                                .setTitle("อัพโหลดไม่สำเร็จ")
                                .setMessage("การอัพโหลดไม่สำเร็จ")
                                .setCancelable(true)
                                .setPositiveButton(getString(R.string.closeBtn), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        intent = null;
                                        finish();
                                    }
                                })
                                .create();
                        adl.show();
                        intent = null;
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    intent = null;
                    finish();
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }
}

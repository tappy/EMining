package com.example.hackme.emining.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackme.emining.R;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.Helpers.WebServiceConfig;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class RequestFile extends Activity {

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

        if (intent == null) {
            finish();
        }

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
                    new newUploadFile().execute(file.getPath(), new WebServiceConfig().getHost("uploadFile.php"));
                } else {
                    Intent login = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(login);
                }
            } else {
                tv32.setText("ไฟล์ไม่รองรับ");
                //Toast.makeText(getBaseContext(), "ไฟล์ไม่รองรับ", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getBaseContext(), "อัพโหลดไม่สำเร็จ", Toast.LENGTH_LONG).show();
                AlertDialog adl = new AlertDialog.Builder(RequestFile.this)
                        .setTitle("อัพโหลดไม่สำเร็จ")
                        .setMessage("ไฟล์ไม่รองรับ อัพโหลดไม่สำเร็จ")
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.closeBtn),new DialogInterface.OnClickListener() {
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
                    .setPositiveButton(getString(R.string.closeBtn),new DialogInterface.OnClickListener() {
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
                new newUploadFile().execute(file.getPath(), new WebServiceConfig().getHost("uploadFile.php"));
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

    public class newUploadFile extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                StringBuilder builder = new StringBuilder();
                String fileName = params[0];
                String serverURL = params[1];
                File file = new File(fileName);
                FileBody fb = new FileBody(file);
                StringBody user = new StringBody(new DatabaseManager(getBaseContext()).getLoginId());
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(serverURL);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("filUpload", fb);
                reqEntity.addPart("userID", user);
                post.setEntity(reqEntity);
                HttpResponse response = client.execute(post);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String body;
                while ((body = rd.readLine()) != null) {
                    builder.append(body.toString());
                }
                return builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            try {
                JSONObject jso = new JSONObject(s);
                if (jso.getInt("StatusID") == 1) {
                    tv32.setText("อัพโหลดสำเร็จ");
                    //Toast.makeText(getBaseContext(), "อัพโหลดสำเร็จ", Toast.LENGTH_LONG).show();
                    AlertDialog adl = new AlertDialog.Builder(RequestFile.this)
                            .setTitle("อัพโหลดสำเร็จ")
                            .setMessage("การอัพโหลดสำเร็จแล้ว")
                            .setCancelable(true)
                            .setNegativeButton("วิเคราะห์เหมืองข้อมูล",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent in = new Intent(RequestFile.this, MainPage.class);
                                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(in);
                                }
                            })
                            .setPositiveButton(getString(R.string.closeBtn),new DialogInterface.OnClickListener() {
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
                            .setPositiveButton(getString(R.string.closeBtn),new DialogInterface.OnClickListener() {
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
    }

}

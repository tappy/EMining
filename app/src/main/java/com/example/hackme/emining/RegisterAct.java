package com.example.hackme.emining;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class RegisterAct extends Activity {

    public EditText usemail, usname, uspass;
    public String mstruser, mstrpass, mstremail;
    public ProgressDialog progressDialog;
    public StringBuilder stringBuilder;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getActionBar().hide();
        context = getBaseContext();
        usname = (EditText) findViewById(R.id.userName);
        uspass = (EditText) findViewById(R.id.userPassword);
        usemail = (EditText) findViewById(R.id.userEmail);
        uspass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE) {
                    saveAction(uspass);
                }
                return false;
            }
        });
    }

    public void saveAction(View v) {
        if (checkinputregister()) {
            new register().execute(getmStruser(), getmStrpass(), getmStremail());
        }
    }

    public void cancelRegis(View v){
        onBackPressed();
    }

    public void alertDialog(String... config) {
        new AlertDialog.Builder(this)
                .setTitle(config[0])
                .setMessage(config[1])
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(true)
                .setNegativeButton(config[2], null)
                .show();
    }

    public void SuccessfulAlertDialog(String... config) {
        new AlertDialog.Builder(this)
                .setTitle(config[0])
                .setMessage(config[1])
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onBackPressed();
                    }
                })
                .setNegativeButton(config[2], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("กำลังดำเนินการ กรุณารอสักครู่");
        progressDialog.show();
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_action, menu);
        return true;
    }

    public boolean checkinputregister() {
        setValueInput();
        if (chkValue(getmStruser(), 6) && chkValue(getmStrpass(), 6)) {
            if(isValidEmail(getmStremail())){
                return true;
            }else {
                alertDialog("Error",getString(R.string.input_email_alert_text), "close");
                return false;
            }
        } else {
            alertDialog("Error",getString(R.string.input_text_length_alert_text), "close");
            return false;
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) return false;
        else return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean chkValue(String val, int lenght) {
        if (val.trim() == null) return false;
        else return val.length() >= lenght;
    }

    public String getmStruser() {
        return mstruser;
    }

    public void setmStruser(String struser) {
        this.mstruser = struser;
    }

    public String getmStrpass() {
        return mstrpass;
    }

    public void setmStrpass(String strpass) {
        this.mstrpass = strpass;
    }

    public String getmStremail() {
        return mstremail;
    }

    public void setmStremail(String stremail) {
        this.mstremail = stremail;
    }

    public void setValueInput() {
        setmStruser(usname.getText().toString());
        setmStrpass(uspass.getText().toString());
        setmStremail(usemail.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (checkinputregister()) {
                new register().execute(getmStruser(), getmStrpass(), getmStremail());
            }
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public class register extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(0);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            stringBuilder = new StringBuilder();
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(new webServiceConfig().getHost("register.php"));
                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("user", params[0]));
                params1.add(new BasicNameValuePair("password", params[1]));
                params1.add(new BasicNameValuePair("email", params[2]));
                httpPost.setEntity(new UrlEncodedFormEntity(params1));
                HttpResponse response = client.execute(httpPost);
                if (response.getStatusLine().getStatusCode() == 200) {
                    InputStream inputStream = response.getEntity().getContent();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                }
                Log.d("res", stringBuilder.toString());
                return true;
            } catch (Exception e) {
                Log.e("err", e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean v) {
            super.onPostExecute(v);
            removeDialog(0);
            try {
                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                if (jsonObject.getInt("status") == 1) {
                    SuccessfulAlertDialog("Result", getString(R.string.res_regis_succ), "OK");
                } else if (jsonObject.getInt("status") == 2) {
                    alertDialog("Result", getString(R.string.res_regis_exist), "Close");
                }else if(jsonObject.getInt("status") == 3){
                    alertDialog("Result", getString(R.string.res_regis_email_exist), "Close");
                }else if(jsonObject.getInt("status") == 4){
                    alertDialog("Result", getString(R.string.res_regis_user_and_email_exist), "Close");
                } else {
                    alertDialog("Result", getString(R.string.res_regis_err), "Close");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}

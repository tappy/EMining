package com.example.hackme.emining.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hackme.emining.R;
import com.example.hackme.emining.entities.RegisterReq;
import com.example.hackme.emining.model.ModelLoader;
import com.example.hackme.emining.model.RegisterLoader;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity {

    public EditText usemail, usname, uspass;
    public String mstruser, mstrpass, mstremail;
    public ProgressDialog progressDialog;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        context = getBaseContext();
        usname = (EditText) findViewById(R.id.userName);
        uspass = (EditText) findViewById(R.id.userPassword);
        usemail = (EditText) findViewById(R.id.userEmail);
        uspass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveAction(uspass);
                }
                return false;
            }
        });
    }

    public void saveAction(View v) {
        if (checkinputregister()) {
            RegisterReq req = new RegisterReq();
            req.user = getmStruser();
            req.email = getmStremail();
            req.password = getmStrpass();
            register(req);
        }
    }

    public void cancelRegis(View v) {
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
            if (isValidEmail(getmStremail())) {
                return true;
            } else {
                alertDialog("Error", getString(R.string.input_email_alert_text), "close");
                return false;
            }
        } else {
            alertDialog("Error", getString(R.string.input_text_length_alert_text), "close");
            return false;
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean chkValue(String val, int lenght) {
        return val.trim() != null && val.length() >= lenght;
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
                RegisterReq req = new RegisterReq();
                req.user = getmStruser();
                req.email = getmStremail();
                req.password = getmStrpass();
                register(req);
            }
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void register(RegisterReq req) {
        new RegisterLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(final String data) {
               RegisterActivity.this.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       try {
                           JSONObject jsonObject = new JSONObject(data);
                           if (jsonObject.getInt("status") == 1) {
                               SuccessfulAlertDialog("Result", getString(R.string.res_regis_succ), "OK");
                           } else if (jsonObject.getInt("status") == 2) {
                               alertDialog("Result", getString(R.string.res_regis_exist), "Close");
                           } else if (jsonObject.getInt("status") == 3) {
                               alertDialog("Result", getString(R.string.res_regis_email_exist), "Close");
                           } else if (jsonObject.getInt("status") == 4) {
                               alertDialog("Result", getString(R.string.res_regis_user_and_email_exist), "Close");
                           } else {
                               alertDialog("Result", getString(R.string.res_regis_err), "Close");
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
               });
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }
}

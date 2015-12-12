package com.example.hackme.emining.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hackme.emining.R;
import com.example.hackme.emining.entities.LoginReq;
import com.example.hackme.emining.entities.ResetPasswordReq;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.model.LoginLoader;
import com.example.hackme.emining.model.ModelLoader;
import com.example.hackme.emining.model.ResetPasswordLoader;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    public ProgressDialog progressDialog;
    private EditText user, pass;
    private AlertDialog.Builder aBuilder, builder;
    private RelativeLayout rel;
    private View rootView;
    private EditText edem;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();


        if (new DatabaseManager(getBaseContext()).existUser()) {
            Intent red = new Intent(getBaseContext(), MainPage.class);
            startActivity(red);
        }

        user = (EditText) findViewById(R.id.userName);
        pass = (EditText) findViewById(R.id.userPassword);
        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    logAct();
                }
                return false;
            }
        });
        aBuilder = new AlertDialog.Builder(this);
        builder = new AlertDialog.Builder(this);
        rel = (RelativeLayout) findViewById(R.id.content);
        TextView lose_pass = (TextView) findViewById(R.id.lose_pass);
        lose_pass.setText(getString(R.string.lose_pass) + "?");
        lose_pass.setClickable(true);
        lose_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView = getLayoutInflater().inflate(R.layout.lose_log_layout, rel, false);
                edem = (EditText) rootView.findViewById(R.id.lose_log_email);
                aBuilder.setTitle(getString(R.string.lose_pass));
                aBuilder.setView(rootView);
                aBuilder.setCancelable(true);
                aBuilder.setNegativeButton(getString(R.string.send_email), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetPassword(edem.getText().toString());
                    }
                });
                aBuilder.setPositiveButton(getString(R.string.closeBtn), null);
                aBuilder.show();
            }
        });
    }

    public void resetPassword(String email) {
        ResetPasswordReq req = new ResetPasswordReq();
        req.email = email;
        new ResetPasswordLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(String data) {
                AlertDialog.Builder alb = new AlertDialog.Builder(rootView.getContext());
                alb.setTitle(getString(R.string.alert));
                alb.setCancelable(true);
                alb.setIcon(android.R.drawable.ic_dialog_alert);
                alb.setPositiveButton(getString(R.string.closeBtn), null);
                try {
                    Log.d("remove", "removedialog");
                    JSONObject js = new JSONObject(data);
                    Log.d("json", js.get("result").toString());
                    if (js.get("result").toString().equals("0")) {
                        alb.setMessage(getString(R.string.send_email_secc));
                    } else if (js.get("result").toString().equals("1")) {
                        alb.setMessage(getString(R.string.system_error));
                    } else if (js.get("result").toString().equals("2")) {
                        alb.setMessage(getString(R.string.system_error));
                    } else if (js.get("result").toString().equals("3")) {
                        alb.setMessage(getString(R.string.email_not_found));
                    } else if (js.get("result").toString().equals("4")) {
                        alb.setMessage(getString(R.string.system_error));
                    }
                } catch (JSONException e) {
                    alb.setMessage(getString(R.string.system_error));
                }
                alb.show();
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 0) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("เข้าสู่ระบบ");
            progressDialog.setMessage("กำลังดำเนินการกรุณารอสักครู่");
            progressDialog.show();
            progressDialog.setCancelable(true);
            return progressDialog;
        } else if (id == 1) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.lose_pass));
            progressDialog.setCancelable(true);
            progressDialog.setTitle(getString(R.string.please_wait));
            progressDialog.setMessage(getString(R.string.sending_password_to_email));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            return progressDialog;
        } else if (id == 2) {
            builder.setTitle(getString(R.string.alert));
            builder.setMessage(getString(R.string.login_invalid_alert_text));
            builder.setCancelable(true);
            builder.setPositiveButton(getString(R.string.closeBtn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Close
                }
            });
            return builder.create();
        } else if (id == 3) {
            builder.setTitle(getString(R.string.alert));
            builder.setMessage(getString(R.string.input_text_length_alert_text));
            builder.setCancelable(true);
            builder.setPositiveButton(getString(R.string.closeBtn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Close
                }
            });
            return builder.create();
        } else if (id == 4) {
            builder.setTitle("");
            builder.setMessage("");
            builder.setCancelable(true);
            builder.setPositiveButton(getString(R.string.closeBtn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Close
                }
            });
            return builder.create();
        } else {
            return null;
        }

    }

    public void loginAction(View v) {
        logAct();
    }

    private void logAct() {
        if (user.getText().length() >= 6 && pass.getText().length() >= 6)
            login(user.getText().toString(), pass.getText().toString());
        else showDialog(3);
    }

    public void registerAction(View v) {
        Intent regisIntent = new Intent(this, RegisterActivity.class);
        startActivity(regisIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void login(String userName, String password) {
        LoginReq req = new LoginReq();
        req.username = userName;
        req.password = password;
        showDialog(0);
        new LoginLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(final String data) {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        removeDialog(0);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(data);
                            DatabaseManager db = new DatabaseManager(getBaseContext());
                            db.getWritableDatabase();
                            if (jsonObject.getBoolean("stulog")) {
                                db.saveSession(jsonObject.getString("username"), jsonObject.getString("id"), jsonObject.getString("email"));
                            } else {
                                if (db.existUser()) db.clearUser();
                                showDialog(2);
                            }
                            if (db.existUser()) {
                                Intent intent = new Intent(getBaseContext(), MainPage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        removeDialog(0);
                    }
                });
            }
        });
    }
}

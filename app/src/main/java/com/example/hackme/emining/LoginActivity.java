package com.example.hackme.emining;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.RelativeLayout;
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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {
    public ProgressDialog progressDialog;
    private EditText user, pass;
    private TextView lose_pass;
    private AlertDialog.Builder aBuilder, builder;
    private RelativeLayout rel;
    private View rootView;
    private EditText edem;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();


        if(new database_manager(getBaseContext()).existUser()){
            Intent red=new Intent(getBaseContext(),MainPage.class);
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
        lose_pass = (TextView) findViewById(R.id.lose_pass);
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
                        if (edem.getText().length() > 0) {
                            resetPassword res = new resetPassword();
                            res.execute(edem.getText().toString());
                        }
                    }
                });
                aBuilder.setPositiveButton(getString(R.string.closeBtn), null);
                aBuilder.show();
            }
        });
    }

    public class resetPassword extends AsyncTask<String, Void, String> {
        @Override
        public void onPreExecute() {
            showDialog(1);
            Log.d("show", "show dialog");
        }

        @Override
        public String doInBackground(String... params) {
            try {
                Log.d("do in background", "do in background");
                StringBuilder builder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(new webServiceConfig().getHost("resetPassword.php"));
                List<NameValuePair> list = new ArrayList();
                list.add(new BasicNameValuePair("Email", params[0]));
                post.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                if (code == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        builder.append(line);
                    }
                }
                String ret = builder.toString();
                Log.d("ret", ret);
                return ret;
            } catch (Exception e) {
                Log.d("do in background", "error do in background");
                return null;
            }
        }

        @Override
        public void onPostExecute(String s) {
            removeDialog(1);
            AlertDialog.Builder alb = new AlertDialog.Builder(rootView.getContext());
            alb.setTitle(getString(R.string.alert));
            alb.setCancelable(true);
            alb.setIcon(android.R.drawable.ic_dialog_alert);
            alb.setPositiveButton(getString(R.string.closeBtn), null);
            try {
                Log.d("remove", "removedialog");
                JSONObject js = new JSONObject(s);
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
            new ckeckLogin().execute(user.getText().toString(), pass.getText().toString());
        else showDialog(3);
    }

    public void registerAction(View v) {
        Intent regisIntent = new Intent(this, RegisterAct.class);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ckeckLogin extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            showDialog(0);

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            StringBuilder builder = new StringBuilder();
            try {
                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&";
                data += URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");
                URL url = new URL(new webServiceConfig().getHost("checkUser.php"));
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(data);
                out.flush();
                BufferedReader mread = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = mread.readLine()) != null) {
                    builder.append(line);
                }
                Log.d("Login", builder.toString());
                if (builder.toString() != "") {
                    return new JSONObject(builder.toString());
                } else {
                    Log.d("Login", "Login error");
                    return new JSONObject("");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            removeDialog(0);
            try {
                database_manager db = new database_manager(getBaseContext());
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

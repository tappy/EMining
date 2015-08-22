package com.example.hackme.emining;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link settingFlag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class settingFlag extends Fragment {

    private ImageButton imgbtn1, imgbtn2, imgbtn3;
    private View rooView, nodeView1, nodeView2, nodeView3;
    private EditText emailEdit, userNameEdit, passwordEdit;
    private database_manager dbms;

    public static settingFlag newInstance() {
        settingFlag settingfragment = new settingFlag();
        return settingfragment;
    }

    public settingFlag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        rooView = inflater.inflate(R.layout.fragment_setting_flag, container, false);

        dbms = new database_manager(rooView.getContext());

        final database_manager dbms = new database_manager(rooView.getContext());
        String[] acc = dbms.getSession();
        emailEdit = (EditText) rooView.findViewById(R.id.emailEdit);
        userNameEdit = (EditText) rooView.findViewById(R.id.userNameEdit);
        passwordEdit = (EditText) rooView.findViewById(R.id.passwordEdit);

        emailEdit.setText(acc[3]);
        userNameEdit.setText(acc[2]);
        passwordEdit.setText("password");

        imgbtn1 = (ImageButton) rooView.findViewById(R.id.btnEdit1);
        imgbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeView1 = inflater.inflate(R.layout.edit_email_layout, container, false);
                final EditText newEmail = (EditText) nodeView1.findViewById(R.id.newEmail);
                newEmail.setText(emailEdit.getText());
                final AlertDialog.Builder al = showDialog(getString(R.string.title_edit_email), nodeView1, true);
                al.setNegativeButton(getString(R.string.save_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Byte type = 0;
                        updateAcount up = new updateAcount(type);
                        up.execute(dbms.getLoginId(), newEmail.getText().toString());
                    }
                });

                al.setPositiveButton(getString(R.string.cancelBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog mdiDialog1 = al.create();
                mdiDialog1.show();
            }
        });
        imgbtn2 = (ImageButton) rooView.findViewById(R.id.btnEdit2);
        imgbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeView2 = inflater.inflate(R.layout.edit_username_layout, container, false);
                final EditText newUserName = (EditText) nodeView2.findViewById(R.id.newUserName);
                newUserName.setText(userNameEdit.getText());
                final AlertDialog.Builder al = showDialog(getString(R.string.title_edit_username), nodeView2, true);
                al.setNegativeButton(getString(R.string.save_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dbms.existUser()) {
                            Byte type = 1;
                            updateAcount up = new updateAcount(type);
                            up.execute(dbms.getLoginId(), newUserName.getText().toString());
                        }
                    }
                });

                al.setPositiveButton(getString(R.string.cancelBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog mdiDialog1 = al.create();
                mdiDialog1.show();
            }
        });
        imgbtn3 = (ImageButton) rooView.findViewById(R.id.btnEdit3);
        imgbtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeView3 = inflater.inflate(R.layout.edit_password_layout, container, false);
                final EditText oldPass = (EditText) nodeView3.findViewById(R.id.oldUserPassword);
                final EditText newPass = (EditText) nodeView3.findViewById(R.id.newUserPassword);
                final EditText confirmNewPass = (EditText) nodeView3.findViewById(R.id.confirmNewUserPassword);
                final AlertDialog.Builder al = showDialog(getString(R.string.title_edit_password), nodeView3, true);
                al.setNeutralButton(getString(R.string.save_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (newPass.getText().toString().equals(confirmNewPass.getText().toString())) {
                            Byte type = 2;
                            updateAcount up = new updateAcount(type);
                            up.execute(dbms.getLoginId(), oldPass.getText().toString(), newPass.getText().toString());
                        } else {
                            Toast.makeText(nodeView3.getContext(), getString(R.string.text_dialog_conf_pass), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                al.setPositiveButton(getString(R.string.cancelBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog mdiDialog1 = al.create();
                mdiDialog1.show();
            }
        });


        return rooView;
    }

    private class updateAcount extends AsyncTask<String, Void, String> {

        private byte type;
        private ProgressDialog ps;

        updateAcount(byte type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            ps = new ProgressDialog(rooView.getContext());
            ps.setTitle(getString(R.string.update));
            ps.setMessage(getString(R.string.update));
            ps.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            ps.show();
        }

        @Override
        protected String doInBackground(String... params) {

            StringBuilder stringBuilder = new StringBuilder();
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(new webServiceConfig().getHost("updateAccount.php"));
                List<NameValuePair> params1 = new ArrayList<>();
                params1.add(new BasicNameValuePair("userID", params[0]));
                params1.add(new BasicNameValuePair("updateType", String.valueOf(type)));


                switch (type) {
                    case 0: {
                        params1.add(new BasicNameValuePair("userEmail", params[1]));
                    }
                    ;
                    break;
                    case 1: {
                        params1.add(new BasicNameValuePair("userName", params[1]));
                    }
                    ;
                    break;
                    case 2: {
                        params1.add(new BasicNameValuePair("oldUserPassword", params[1]));
                        params1.add(new BasicNameValuePair("newUserPassword", params[2]));
                    }
                    ;
                    break;
                }


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

                return stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            ps.dismiss();
            Log.d("res up ac",s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getInt("result") == 1) {
                    switch (jsonObject.getInt("type")) {
                        case 0: {
                            dbms.upgradeSession("email", jsonObject.getString("userEmail"));
                            emailEdit.setText(dbms.getSession()[3]);
                            Toast.makeText(rooView.getContext(), getString(R.string.update_success), Toast.LENGTH_LONG).show();
                        }
                        ;
                        break;
                        case 1: {
                            dbms.upgradeSession("username", jsonObject.getString("userName"));
                            userNameEdit.setText(dbms.getSession()[2]);
                            Toast.makeText(rooView.getContext(), getString(R.string.update_success), Toast.LENGTH_LONG).show();
                        }
                        ;
                        break;
                        case 2: {
                            Toast.makeText(rooView.getContext(), getString(R.string.update_success), Toast.LENGTH_LONG).show();
                        }
                        ;
                        break;
                    }
                } else if (jsonObject.getInt("result") == 0) {
                    Toast.makeText(rooView.getContext(), getString(R.string.update_failed), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(rooView.getContext(), getString(R.string.update_failed), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private AlertDialog.Builder showDialog(String title, View v, boolean cancelable) {
        AlertDialog.Builder al = new AlertDialog.Builder(rooView.getContext());
        al.setTitle(title);
        al.setCancelable(cancelable);
        al.setView(v);
        return al;
    }
}

package com.example.hackme.emining.ui.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.hackme.emining.R;
import com.example.hackme.emining.entities.UpdateAccountReq;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.model.ModelLoader;
import com.example.hackme.emining.model.UpdateAccountLoader;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingFragment extends Fragment {

    private View rooView, nodeView1, nodeView2, nodeView3;
    private EditText emailEdit;
    private EditText userNameEdit;
    private DatabaseManager dbms;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    public SettingFragment() {
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

        dbms = new DatabaseManager(rooView.getContext());

        final DatabaseManager dbms = new DatabaseManager(rooView.getContext());
        String[] acc = dbms.getSession();
        emailEdit = (EditText) rooView.findViewById(R.id.emailEdit);
        userNameEdit = (EditText) rooView.findViewById(R.id.userNameEdit);
        EditText passwordEdit = (EditText) rooView.findViewById(R.id.passwordEdit);

        emailEdit.setText(acc[3]);
        userNameEdit.setText(acc[2]);
        passwordEdit.setText("password");

        ImageButton imgbtn1 = (ImageButton) rooView.findViewById(R.id.btnEdit1);
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
                        UpdateAccountReq req = new UpdateAccountReq();
                        req.userID = dbms.getLoginId();
                        req.userEmail = newEmail.getText().toString();
                        updateAccount(req, type);
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
        ImageButton imgbtn2 = (ImageButton) rooView.findViewById(R.id.btnEdit2);
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
                        if (dbms.existUser()) {
                            Byte type = 1;
                            UpdateAccountReq req = new UpdateAccountReq();
                            req.userID = dbms.getLoginId();
                            req.userName = newUserName.getText().toString();
                            updateAccount(req, type);
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
        ImageButton imgbtn3 = (ImageButton) rooView.findViewById(R.id.btnEdit3);
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
                            UpdateAccountReq req = new UpdateAccountReq();
                            req.userID = dbms.getLoginId();
                            req.oldUserPassword = oldPass.getText().toString();
                            req.newUserPassword = newPass.getText().toString();
                            updateAccount(req, type);
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

    public void updateAccount(UpdateAccountReq req, byte type) {
        ProgressDialog ps;
        ps = new ProgressDialog(rooView.getContext());
        ps.setTitle(getString(R.string.update));
        ps.setMessage(getString(R.string.update));
        ps.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        ps.show();
        req.updateType = String.valueOf(type);
        new UpdateAccountLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.getInt("result") == 1) {
                        switch (jsonObject.getInt("type")) {
                            case 0:
                                dbms.upgradeSession("email", jsonObject.getString("userEmail"));
                                emailEdit.setText(dbms.getSession()[3]);
                                Toast.makeText(rooView.getContext(), getString(R.string.update_success), Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                dbms.upgradeSession("username", jsonObject.getString("userName"));
                                userNameEdit.setText(dbms.getSession()[2]);
                                Toast.makeText(rooView.getContext(), getString(R.string.update_success), Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(rooView.getContext(), getString(R.string.update_success), Toast.LENGTH_LONG).show();
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

            @Override
            public void onFailed(String message) {

            }
        });

    }

    private AlertDialog.Builder showDialog(String title, View v, boolean cancelable) {
        AlertDialog.Builder al = new AlertDialog.Builder(rooView.getContext());
        al.setTitle(title);
        al.setCancelable(cancelable);
        al.setView(v);
        return al;
    }
}

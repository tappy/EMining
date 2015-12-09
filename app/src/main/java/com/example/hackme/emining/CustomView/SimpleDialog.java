package com.example.hackme.emining.customView;

import android.app.AlertDialog;
import android.content.Context;

import com.example.hackme.emining.R;

public class SimpleDialog extends AlertDialog.Builder {

    public SimpleDialog(Context context, String title, String message) {
        super(context);
        setTitle(title);
        setMessage(message);
        setIcon(android.R.drawable.ic_dialog_alert);
        setPositiveButton(context.getString(R.string.closeBtn), null);
        show();
    }
}

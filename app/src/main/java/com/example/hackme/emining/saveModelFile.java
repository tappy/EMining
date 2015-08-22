package com.example.hackme.emining;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hackme on 3/8/15.
 */
public class saveModelFile {
    private Context context;
    private String val;
    private AlertDialog.Builder als;
    private Dialog all;
    private String fileType;


    public saveModelFile(Context context, String val) {
        this.context = context;
        this.val = val;
        fileType="txt";
    }

    public saveModelFile(Context context, String val,String type) {
        this.context = context;
        this.val = val;
        fileType=type;
    }

    public void setFileName(final int alg) {
        als = new AlertDialog.Builder(context);
        final EditText edname = new EditText(context);
        edname.setHint(context.getString(R.string.set_file_name));
        switch (alg) {
            case 0:
                edname.setText("Cluster_model");
                ;
                break;
            case 1:
                edname.setText("Tree_model");
                ;
                break;
            case 2:
                edname.setText("Apriori_model");
                ;
                break;
            default:
                edname.setText("my_model");
                ;
        }
        als.setTitle(context.getString(R.string.set_file_name));
        als.setView(edname);
        als.setCancelable(true);
        als.setPositiveButton(context.getString(R.string.closeBtn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        als.setNegativeButton(context.getString(R.string.save_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (edname.getText().length() > 0) {

                    try {
                        String root = Environment.getExternalStorageDirectory().toString();
                        File myDir;
                        switch (alg) {
                            case 0:
                                myDir = new File(root + "/KAT_Model/ClusterModel");
                                break;
                            case 1:
                                myDir = new File(root + "/KAT_Model/TreeModel");
                                break;
                            case 2:
                                myDir = new File(root + "/KAT_Model/AprioriModel");
                                break;
                            default:
                                myDir = new File(root + "/KAT_Model/");
                        }
                        myDir.mkdirs();
                        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
                        Date date = new Date();
                        String fname = edname.getText() + "-" + dateFormat.format(date) +"."+ fileType;
                        final File file = new File(myDir, fname);
                        if (file.exists()) file.delete();
                        FileOutputStream out = new FileOutputStream(file);
                        PrintWriter pw = new PrintWriter(out);
                        pw.println(val);
                        pw.flush();
                        pw.close();
                        out.close();

                        //Toast.makeText(getBaseContext(),"บันทึกสำเร็จแล้วที่ "+file.getAbsolutePath(),Toast.LENGTH_LONG).show();
                        AlertDialog adl = new AlertDialog.Builder(context)
                                .setTitle("บันทึกสำเร็จ")
                                .setMessage("บันทึกสำเร็จแล้วที่ " + file.getCanonicalPath())
                                .setCancelable(true)
                                .setNegativeButton("แชร์ไฟล์นี้", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent sendIntent = new Intent();
                                        sendIntent.setAction(Intent.ACTION_SEND);
                                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                                        sendIntent.setType("*/" + fileType + "");
                                        context.startActivity(sendIntent);
                                    }
                                })
                                .setPositiveButton(context.getString(R.string.closeBtn), null)
                                .create();
                        adl.show();
                    } catch (Exception e) {
                        Log.e("", "");
                    }
                } else {
                    new simpleDialog(context, context.getString(R.string.alert), context.getString(R.string.pls_ent_file_name));
                }
            }
        });
        all = als.create();
        all.show();
    }
}

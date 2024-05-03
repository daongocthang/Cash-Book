package com.standalone.core.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.standalone.core.R;
import com.standalone.core.dialogs.ProgressDialog;

public class DialogUtil {
    public static void showAlertDialog(Context context, String msg) {
        new MaterialAlertDialogBuilder(context)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public static ProgressDialog showProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        return progressDialog;
    }
}

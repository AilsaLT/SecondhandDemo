package com.ghl.wuhan.secondhand.util;

import android.app.Dialog;
import android.content.Context;

public class DialogUIUtils {


    /**
     * Show loading dialog dialog.
     *
     * @param context the context
     * @param showMes the show mes
     * @return the dialog
     */
    public static Dialog showLoadingDialog(Context context, String showMes){
        Dialog progressDialog;
        progressDialog = CustomProgressTransDialog.createLoadingDialog(context, showMes);
        return progressDialog;
    }

    /**
     * Dismiss.
     *
     * @param dialog the dialog
     */
    public static void dismiss(Dialog dialog){

        if(dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
    }

}

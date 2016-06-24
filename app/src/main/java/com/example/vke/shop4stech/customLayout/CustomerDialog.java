package com.example.vke.shop4stech.customLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

/**
 * Created by shaohb on 2016/6/19.
 */
public class CustomerDialog {

    private AlertDialog alertDialog;

    private AlertDialog.Builder builder;

    private Context context;

    public CustomerDialog(Context context){
        this.context =  context;
        builder = new AlertDialog.Builder(context);
    }

    public void createDialog(String title, String pButtonText, String nButtonText, View view, final Callback callback){
        alertDialog = builder.setTitle(title)
                .setView(view)
                .setPositiveButton(pButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.callback();
                    }
                })
                .setNegativeButton(nButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.callback();
                    }
                })
                .show();
    }

    public void dismiss(){
        alertDialog.dismiss();
    }

    public interface Callback{
        public void callback();

    }

}

package com.at.nikhil.speciesai;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Utility {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNEL_STORAGE = 123;
    private boolean status = false;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean chcekPermission(final Context context){
        int CURRENTAPIVERSION = Build.VERSION.SDK_INT;
        if (CURRENTAPIVERSION >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertbuilder = new AlertDialog.Builder(context);
                    alertbuilder.setCancelable(true);
                    alertbuilder.setTitle("Permission Necessary!");
                    alertbuilder.setMessage("External storage permission is necessary");
                    alertbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNEL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertbuilder.create();
                    alert.show();
                }
                else{
                    ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNEL_STORAGE);
                }
                return false;
            }
            else{
                return true;
            }
        }
        else{
            return true;
        }
    }


}

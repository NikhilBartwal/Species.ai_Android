package com.at.nikhil.speciesai;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    protected String userChosenTask;
    protected Uri currImageURI;
    protected static final int SELECT_FILE = 200;
    protected static final int REQUEST_CAMERA = 100;
    protected String currImagePath;
    protected int type = -1;
    protected boolean phototakenByCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    protected void selectImage(final Context context){
        final CharSequence[] items = {"Take Photo" , "Open Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Upload Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.chcekPermission(context);
                if(items[item].equals("Take Photo")){
                    userChosenTask = "Take Photo";
                    if(result){
                        cameraIntent();
                    }
                }
                else if(items[item].equals("Open Gallery")){
                    userChosenTask = "Open Gallery";
                    if(result){
                        galleryIntent();
                    }
                }
                else if(items[item].equals("Cancel")){
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    protected void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(imageFile != null){
                Uri imageURI = FileProvider.getUriForFile(this,
                        "com.at.nikhil.speciesai",
                        imageFile);
                currImageURI = imageURI;
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageURI);
                startActivityForResult(intent,REQUEST_CAMERA);
            }

        }
    }

    protected void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(Intent.createChooser(intent,"SELECT File"),SELECT_FILE);
        }
    }

    protected File createImageFile() throws IOException {
        File image = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "image.jpg");
        image.createNewFile();
        /*String imageFileName = "image";
        File storagedir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
         //       ".jpg",         /* suffix */
          //      storagedir      /* directory */
        //);
        //File image = File.createTempFile(imageFileName,".jpg",storagedir);
        currImagePath = image.getAbsolutePath();
        return image;

    }
}
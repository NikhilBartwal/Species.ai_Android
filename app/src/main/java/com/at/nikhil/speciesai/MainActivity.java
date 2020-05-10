package com.at.nikhil.speciesai;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.support.model.Model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity {
    private String userChosenTask;
    private String currImagePath;
    private Uri currImageURI;
    private Button plant_uploaad,animal_upload,bird_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plant_uploaad = findViewById(R.id.plant_upload);
        animal_upload = findViewById(R.id.animal_upload);
        bird_upload = findViewById(R.id.bird_upload);

        plant_uploaad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 0;
                selectImage();
            }
        });

        animal_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 1;
                selectImage();
            }
        });

        animal_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 2;
                selectImage();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNEL_STORAGE :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    switch (userChosenTask) {
                        case "Take Photo":
                            cameraIntent();
                            break;
                        case "Open Gallery":
                            galleryIntent();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "As you wish!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CAMERA){
                phototakenByCamera = true;
                onCaptureImageResult(data);
            }
            else if(requestCode == SELECT_FILE){
                phototakenByCamera = false;
                onSelectFromGalleryResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Uri imageUri = currImageURI;
        sendImage(imageUri,true);

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri imageUri = data.getData();
        sendImage(imageUri,false);

    }

    private void sendImage(Uri imageUri,boolean fromCamera) {
        Intent intent = new Intent(this,ImageViewer.class);
        intent.putExtra("imageURI",imageUri.toString());
        intent.putExtra("fromCamera",fromCamera);
        intent.putExtra("type",type);
    }


}

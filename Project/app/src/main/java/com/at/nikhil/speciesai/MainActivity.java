package com.at.nikhil.speciesai;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
    private Button plant_upload,animal_upload,bird_upload,search;
    private SwitchCompat funmodeSwitch;
    private TextView searchQuery;
    private boolean funmode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plant_upload = findViewById(R.id.plant_upload);
        animal_upload = findViewById(R.id.animal_upload);
        bird_upload = findViewById(R.id.bird_upload);
        search = findViewById(R.id.search_button);
        searchQuery = findViewById(R.id.searchQuery);
        funmodeSwitch = findViewById(R.id.funswitch);
        funmodeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(funmodeSwitch.isChecked()){
                    Toast.makeText(MainActivity.this,"Fun-Mode Activated!",Toast.LENGTH_LONG).show();
                    funmode = true;
                }
                else{
                    Toast.makeText(MainActivity.this,"Fun-Mode Deactivated!",Toast.LENGTH_LONG).show();
                    funmode = false;
                }
            }
        });
        plant_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 0;
                selectImage(MainActivity.this);
            }
        });

        animal_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 1;
                selectImage(MainActivity.this);
            }
        });

        bird_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 2;
                selectImage(MainActivity.this);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSearchQuery(searchQuery.getText().toString());
            }
        });

    }

    private void sendSearchQuery(String query){
        Intent intent = new Intent(MainActivity.this,SpeciesInfo.class);
        intent.putExtra("Prediction",query);
        intent.putExtra("type",3);
        intent.putExtra("camera",false);
        startActivity(intent);
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
                photoTakenByCamera = true;
                onCaptureImageResult(data);
            }
            else if(requestCode == SELECT_FILE){
                photoTakenByCamera = false;
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
        intent.putExtra("funmode",funmode);
        startActivity(intent);
    }


}

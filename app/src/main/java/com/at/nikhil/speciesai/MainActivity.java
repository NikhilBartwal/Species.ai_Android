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

public class MainActivity extends AppCompatActivity {
    private String userChosenTask;
    private static final int REQUEST_CAMERA = 100;
    private static final int SELECT_FILE = 200;
    private ImageView ivImage;
    private Button button;
    private Button rotate_button;
    private Button resize_button;
    private Button predict_button;
    private TextView textView;
    private  TextView predictionResult;
    private String currImagePath;
    private Uri currImageURI;
    private boolean UPLOADED = false;
    private Classifier classifier;
    private boolean phototakenByCamera = false;
    private int type = -1;
    private Button plant_select;
    private Button animal_select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivImage = findViewById(R.id.ivImage);
        button = findViewById(R.id.button);
        rotate_button = findViewById(R.id.rotate_button);
        resize_button = findViewById(R.id.resize_button);
        predict_button = findViewById(R.id.predict_button);
        plant_select = findViewById(R.id.plant_select);
        animal_select = findViewById(R.id.animal_select);
        textView = findViewById(R.id.textView);
        predictionResult = findViewById(R.id.predictionResult);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        rotate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UPLOADED){
                    Bitmap bm = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap rot_bm = Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),matrix,true);
                    ivImage.setImageBitmap(rot_bm);
                    String result = "Image Dimension: " +  rot_bm.getHeight() + " , " + rot_bm.getWidth();
                    textView.setText(result);
                    bm.recycle();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Upload An Image First!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        resize_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UPLOADED){
                    Bitmap bm = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
                    Matrix matrix = new Matrix();
                    float scaleHeight = ((float) 512)/bm.getHeight();
                    float scaleWidth = ((float) 512/bm.getWidth());
                    matrix.postScale(scaleWidth,scaleHeight);
                    Bitmap res_bm = Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),matrix,true);
                    ivImage.setImageBitmap(res_bm);
                    String result = "Image Dimension: " +  res_bm.getHeight() + " , " + res_bm.getWidth();
                    textView.setText(result);
                    bm.recycle();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Upload An Image First!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        predict_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
                if(UPLOADED && type != -1){
                    final Model.Device device = Model.Device.GPU;
                    try {
                        classifier = Classifier.create(MainActivity.this,device,type);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    List<Classifier.Recognition> results = classifier.recognizeImage(bm,phototakenByCamera);
                    String prediction = results.get(0).getTitle();
                    predictionResult.setText("Prediction: " + prediction);
                    classifier.close();
                }
                else if(!UPLOADED){
                    Toast.makeText(getApplicationContext(),"Please Upload An Image First",Toast.LENGTH_SHORT).show();
                }
                else if(type == -1){
                    Toast.makeText(getApplicationContext(),"Please Select a type first",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Oops! Some Error Occurred.Kindly Restart the app!",Toast.LENGTH_LONG).show();
                }
            }
        });

        plant_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 0;
                plant_select.setBackgroundColor(Color.parseColor("#8c8c8c"));
                animal_select.setBackgroundColor(Color.parseColor("#d8d8d8"));
            }
        });

        animal_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 1;
                animal_select.setBackgroundColor(Color.parseColor("#8c8c8c"));
                plant_select.setBackgroundColor(Color.parseColor("#d8d8d8"));
            }
        });
    }

    private void selectImage(){
        final CharSequence[] items = {"Take Photo" , "Open Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Upload Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.chcekPermission(MainActivity.this);
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

    private void cameraIntent() {
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

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(Intent.createChooser(intent,"SELECT File"),SELECT_FILE);
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat(getString(R.string.dateFormat), Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timestamp;
        File storagedir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storagedir      /* directory */
        );
        //File image = File.createTempFile(imageFileName,".jpg",storagedir);
        currImagePath = image.getAbsolutePath();
        return image;

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
            UPLOADED = true;
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
        Bitmap image = null;
        try{
            image = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),currImageURI);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        ivImage.setImageBitmap(image);
        int width = image.getWidth();
        int height = image.getHeight();
        String result = "Image Dimension: " +  height + " , " + width;
        textView.setText(result);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if(data != null){
            try{
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),data.getData());
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);
        int width = bm.getWidth();
        int height = bm.getHeight();
        String result = "Image Dimension: " +  height + " , " + width;
        textView.setText(result);
    }




}

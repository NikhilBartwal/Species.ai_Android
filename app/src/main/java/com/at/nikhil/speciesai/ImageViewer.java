package com.at.nikhil.speciesai;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.support.model.Model;

import java.io.IOException;
import java.util.List;

public class ImageViewer extends BaseActivity {

    private ImageView ivImage;
    private TextView image_dims,predictionResult;
    private Button reupload_button;
    private Button predict_button;
    private Classifier classifier;
    private boolean UPLOADED = false;
    private String imageUriString;
    private TextView first_result_tv,second_result_tv,third_result_tv;
    private TextView first_result_score,second_result_score,third_result_score;
    private CardView first,second,third;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ivImage = findViewById(R.id.ivImage);
        image_dims = findViewById(R.id.image_dims);
        predictionResult = findViewById(R.id.predictionResult);
        reupload_button = findViewById(R.id.reupload_button);
        predict_button = findViewById(R.id.predict_button);

        first_result_tv = findViewById(R.id.first_result_tv);
        second_result_tv = findViewById(R.id.second_result_tv);
        third_result_tv = findViewById(R.id.third_result_tv);
        first_result_score = findViewById(R.id.first_result_score);
        second_result_score = findViewById(R.id.second_result_score);
        third_result_score = findViewById(R.id.third_result_score);

        first = findViewById(R.id.first_result_cv);
        second = findViewById(R.id.second_result_cv);
        third = findViewById(R.id.third_result_cv);

        Bundle data = getIntent().getExtras();
        imageUriString = data.getString("imageURI");
        currImageURI = Uri.parse(imageUriString);
        phototakenByCamera = data.getBoolean("fromCamera");
        type = data.getInt("type");
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),currImageURI);
            ivImage.setImageBitmap(bm);
            UPLOADED = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        reupload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(ImageViewer.this);
            }
        });

        predict_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
                if(UPLOADED && type != -1){
                    final Model.Device device = Model.Device.GPU;
                    try {
                        classifier = Classifier.create(ImageViewer.this,device,type);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    List<Classifier.Recognition> results = classifier.recognizeImage(bm,phototakenByCamera);
                    first_result_tv.setText(results.get(0).getTitle());
                    first_result_score.setText(String.format(getString(R.string.floatLocale),results.get(0).getConfidence()*100.0f));
                    second_result_tv.setText(results.get(1).getTitle());
                    second_result_score.setText(String.format(getString(R.string.floatLocale),results.get(1).getConfidence()*100.0f));
                    third_result_tv.setText(results.get(2).getTitle());
                    third_result_score.setText(String.format(getString(R.string.floatLocale),results.get(2).getConfidence()*100.0f));
                    predictionResult.setVisibility(View.VISIBLE);
                    first.setVisibility(View.VISIBLE);
                    second.setVisibility(View.VISIBLE);
                    third.setVisibility(View.VISIBLE);
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

        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPrediction(first_result_tv.getText().toString());
            }
        });

        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPrediction(second_result_tv.getText().toString());
            }
        });

        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPrediction(third_result_tv.getText().toString());
            }
        });

    }

    private void sendPrediction(String prediction){
        Intent intent = new Intent(ImageViewer.this,SpeciesInfo.class);
        intent.putExtra("Prediction",prediction);
        intent.putExtra("imageURI",imageUriString);
        intent.putExtra("type",type);
        startActivity(intent);
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
        imageUriString = currImageURI.toString();
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),imageUri);
            ivImage.setImageBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri imageUri = data.getData();
        imageUriString = imageUri.toString();
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),imageUri);
            ivImage.setImageBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

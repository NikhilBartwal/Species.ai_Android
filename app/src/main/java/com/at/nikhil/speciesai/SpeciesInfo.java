package com.at.nikhil.speciesai;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class SpeciesInfo extends AppCompatActivity implements DataCellAdapter.ItemClicked {
    private String prediction;
    private Uri imageUri;
    private ImageView imageDisplay;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<DataCell> dataCells;
    private int type;
    private String table;
    private boolean photoTakenByCamera;
    private TextView infoTitle;
    private String url;
    private Button more_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species_info);
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataCells = new ArrayList<DataCell>();
        imageDisplay = findViewById(R.id.imageDisplay);
        infoTitle = findViewById(R.id.infoTitle);
        more_button = findViewById(R.id.more_button);

        Bundle data = getIntent().getExtras();
        prediction = data.getString("Prediction");
        imageUri = Uri.parse(data.getString("imageURI"));
        photoTakenByCamera = data.getBoolean("camera");
        type = data.getInt("type");
        if(type == 0)
            table = "plantdata";
        else if(type == 1)
            table = "animaldata";
        else{
            table = "birddata";
        }
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),imageUri);
            imageDisplay.setImageBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        ArrayList<SpeciesData> speciesData = databaseAccess.getData(table,prediction);
        for(int i=0;i<speciesData.size();i++){
            if(speciesData.get(i).getValue() != null) {
                if (i == 0)
                    infoTitle.setText(speciesData.get(i).getValue());
                else if (i == 3)
                    url = speciesData.get(i).getValue();
                else
                    dataCells.add(new DataCell(speciesData.get(i).getKey(), speciesData.get(i).getValue()));
            }
        }

        mAdapter = new DataCellAdapter(this,dataCells);
        recyclerView.setAdapter(mAdapter);

        more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
               startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClicked(int index) {

    }
}

package com.at.nikhil.speciesai;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

        Bundle data = getIntent().getExtras();
        prediction = data.getString("Prediction");
        imageUri = Uri.parse(data.getString("imageURI"));
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),imageUri);
            imageDisplay.setImageBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataCells.add(new DataCell("Name","Species.AI"));
        dataCells.add(new DataCell("Prediction",prediction));

        mAdapter = new DataCellAdapter(this,dataCells);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClicked(int index) {

    }
}

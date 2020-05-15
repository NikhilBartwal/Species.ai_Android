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
    private int type;
    private String table;

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
            if(speciesData.get(i).getValue() != null)
                dataCells.add(new DataCell(speciesData.get(i).getKey(),speciesData.get(i).getValue()));
            else
                continue;
        }

        mAdapter = new DataCellAdapter(this,dataCells);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClicked(int index) {

    }
}

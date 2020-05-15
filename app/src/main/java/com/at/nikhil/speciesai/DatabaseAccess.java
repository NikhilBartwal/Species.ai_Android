package com.at.nikhil.speciesai;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    public DatabaseAccess(Context context){
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context){
        if(instance == null)
            instance = new DatabaseAccess(context);
        return instance;
    }

    public void open(){
        this.database = openHelper.getWritableDatabase();
    }

    public void close(){
        if(database != null)
            this.database.close();
    }

    public ArrayList<SpeciesData> getData(String table,String prediction){
        ArrayList<SpeciesData> list = new ArrayList<>();
        String sql = "SELECT * FROM " + table + " WHERE name = ?";
        Cursor cursor = database.rawQuery(sql,new String[]{prediction});
        cursor.moveToFirst();
        int numColumns = cursor.getColumnCount();
        for(int i=0;i<numColumns;i++){
            list.add(new SpeciesData(cursor.getColumnName(i),cursor.getString(i)));
        }
        cursor.close();
        return list;
    }

}

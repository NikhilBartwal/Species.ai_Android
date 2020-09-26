package com.at.nikhil.speciesai;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final String DatabaseName = "species.db";
    private static final int DatabaseNumber = 1;

    public DatabaseOpenHelper(Context context){
        super(context,DatabaseName,null,DatabaseNumber);
    }
}

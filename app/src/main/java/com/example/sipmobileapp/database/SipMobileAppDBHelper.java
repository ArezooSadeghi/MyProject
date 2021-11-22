package com.example.sipmobileapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SipMobileAppDBHelper extends SQLiteOpenHelper {

    public SipMobileAppDBHelper(@Nullable Context context) {
        super(context, SipMobileAppSchema.DB_NAME, null, SipMobileAppSchema.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String stringBuilder = "CREATE TABLE " + SipMobileAppSchema.ServerDataTable.NAME + " (" +
                SipMobileAppSchema.ServerDataTable.Cols.PRIMARY_KEY + " INTEGER PRIMARY KEY," +
                SipMobileAppSchema.ServerDataTable.Cols.CENTER_NAME + " TEXT," +
                SipMobileAppSchema.ServerDataTable.Cols.IP_ADDRESS + " TEXT," +
                SipMobileAppSchema.ServerDataTable.Cols.PORT + " TEXT" +
                ");";
        sqLiteDatabase.execSQL(stringBuilder);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

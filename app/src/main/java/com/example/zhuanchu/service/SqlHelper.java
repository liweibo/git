package com.example.zhuanchu.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zhuanchu.HomeActivity;

public class SqlHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "wuxian.db";
    private static final int DATABASE_VERSION = 1;

    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table upload(id interger primary key, name varchar(20))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

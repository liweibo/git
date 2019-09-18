package com.example.zhuanchu.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zhuanchu.HomeActivity;

public class SqlHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "wuxian.db";
    private static final int DATABASE_VERSION = 5;

    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println( "55555555555" );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println( "98745632100123456789111222" );
        String sql = "create table upload(id interger primary key, name varchar(20))";
        db.execSQL(sql);
        String sql2 = "create table package(id interger primary key, name varchar(20))";
        db.execSQL(sql2);
        System.out.println( "98745632100123456789" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS upload");
            db.execSQL("DROP TABLE IF EXISTS package");
            this.onCreate(db);
        } catch (Exception e) {
        }
    }
}

package com.example.zhuanchu;

import android.app.Application;
import android.content.Context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyApplication extends Application {

    private static Context context;
    HashMap hashmap = null;
    public int check = -1;

    public MyApplication() {
        hashmap = new HashMap();

    }

    public void setCheck(int check) {
        this.check = check;
    }

    public int getCheck() {
        return check;
    }

    public HashMap getSucFail() {
        return hashmap;
    }

    public void setSucFail(int position, String sucfail) {
        this.hashmap.put(position, sucfail);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    public static Context getContext() {
        return context;
    }


}

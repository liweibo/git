package com.example.zhuanchu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.zhuanchu.adapter.VerticalAdapter;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Route( path = "/app/filelist" )
public class FilelistActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filelist);

        findViewById(R.id.homeback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/home").navigation();
            }
        });

        /*
         * 导航修改的内容
         */
        findViewById(R.id.systembar).findViewById(R.id.toSystem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/system").navigation();
            }
        });

        findViewById(R.id.systembar).findViewById(R.id.toDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/home").navigation();
            }
        });

        findViewById(R.id.systembar).findViewById(R.id.toPack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/pack").navigation();
            }
        });

        findViewById(R.id.systembar).findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/upload").navigation();
            }
        });

        ImageView image = findViewById(R.id.systembar).findViewById(R.id.downImg);
        image.setImageResource(R.drawable.down_c);
        TextView textView = findViewById(R.id.systembar).findViewById(R.id.downText);
        textView.setTextColor(Color.parseColor("#35ae5d"));

        JSONArray jsonArray = null;
        try {
            String path = Environment.getExternalStorageDirectory() + "/CRRC";
            File file = new File(path);

            if (!file.exists()) {
                file.mkdir();
            }

            path = Environment.getExternalStorageDirectory() + "/CRRC/DOWNLOAD";

            file = new File( path );
            if (!file.exists()) {
                file.mkdir();
            }

            String filename = "";
            jsonArray = new JSONArray();

            File[] files = file.listFiles();
            for (File spec : files) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", spec.getName());
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateTime=df.format(new Date(spec.lastModified()));
                jsonObject.put("time", dateTime );
                jsonObject.put("check", false );
                jsonArray.put( jsonObject );
                //filename += spec.getName();
            }

            TextView textView1 = findViewById(R.id.files);
            textView1.setText( jsonArray.getJSONObject(0).getString("name") );
        } catch (Exception e) {

        }


//
//        List<String> datas = new ArrayList<>();
//        for(int i=0;i<30;i++){
//            datas.add("item "+i);
//        }


        RecyclerView recyclerView = findViewById(R.id.listView);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        recyclerView.setLayoutManager(flexboxLayoutManager);

        recyclerView.setAdapter(new VerticalAdapter(this, jsonArray));


    }


}

package com.example.zhuanchu;

import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.zhuanchu.adapter.UploadAdapter;
import com.example.zhuanchu.adapter.ViewAdapter;
import com.example.zhuanchu.adapter.WifiAdapter;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Route(path = "/app/fileview")
public class FileView extends AppCompatActivity {

    private JSONArray jsonArray = new JSONArray();
    private String path = "";
    private File file = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fileview);
        path = Environment.getExternalStorageDirectory() + "/CRRC";
        readFile( path );

//        try {
//            String path = Environment.getExternalStorageDirectory() + "/CRRC";
//
//            File file = new File(path);
//
//            File[] files = file.listFiles();
//
//            jsonArray = new JSONArray();
//
//            for (File spec : files) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("name", spec.getName());
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String dateTime=df.format(new Date(spec.lastModified()));
//                jsonObject.put("time", dateTime );
//                jsonObject.put("file", spec);
//                jsonObject.put("check", false);
//
//
//                if( spec.isDirectory() ){
//                    jsonObject.put("style", "1");
//                    jsonArray.put( jsonObject );
//                }
//                //filename += spec.getName();
//            }
//
//            for (File spec : files) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("name", spec.getName());
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String dateTime=df.format(new Date(spec.lastModified()));
//                jsonObject.put("time", dateTime );
//                jsonObject.put("file", spec);
//                jsonObject.put("check", false);
//                if( spec.isFile() ){
//                    jsonObject.put("style", "2");
//                    jsonArray.put( jsonObject );
//                }
//                //filename += spec.getName();
//            }
//
//
//
//        }catch (Exception e){
//
//        }

        final RecyclerView recyclerView = findViewById(R.id.listviews);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        recyclerView.setLayoutManager(flexboxLayoutManager);

        final ViewAdapter viewAdapter = new ViewAdapter(this, jsonArray);

        recyclerView.setAdapter(viewAdapter);

        viewAdapter.setOnItemClickListener(new ViewAdapter.OnItemClickListener() {
            @Override
            public void tClick(int i) {

                for(int k=0;k<jsonArray.length();k++){
                    try {
                        System.out.println( jsonArray.getJSONObject(k).getString("name") );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    if( jsonArray.getJSONObject(i).getString("type").equals("1") ){
                        path = path + "/" + jsonArray.getJSONObject(i).getString("name");
                        readFile( path );
                        //ViewAdapter viewAdapter = new ViewAdapter( FileView.this, jsonArray);
                        //recyclerView.setAdapter(viewAdapter);
                        viewAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        findViewById(R.id.prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println( file.getPath() );
                System.out.println( file.getParent() );
                System.out.println( file.getPath().substring(file.getPath().lastIndexOf("/")) );

                if( file.getPath().substring(file.getPath().lastIndexOf("/")).equals("/CRRC") ){
                    return;
                }

                path = file.getParent();

                readFile( file.getParent() );
                viewAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/system").navigation();
            }
        });


    }

    public void readFile(String path){
        try {

            file = new File(path);

            File[] files = file.listFiles();

            removedata( jsonArray );

            System.out.println( jsonArray.length() );


            for (File spec : files) {

                System.out.println(  spec.getName() );

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", spec.getName());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateTime=df.format(new Date(spec.lastModified()));
                jsonObject.put("time", dateTime );
                jsonObject.put("file", spec);
                jsonObject.put("check", false);


                if( spec.isDirectory() ){
                    jsonObject.put("type", "1");
                    jsonArray.put( jsonObject );
                }
            }

            for (File spec : files) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", spec.getName());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateTime=df.format(new Date(spec.lastModified()));
                jsonObject.put("time", dateTime );
                jsonObject.put("file", spec);
                jsonObject.put("check", false);


                if( spec.isFile() ){
                    jsonObject.put("type", "2");
                    jsonArray.put( jsonObject );
                }
                //filename += spec.getName();
            }
        }catch (Exception e){

        }
    }

    public void removedata(JSONArray jsonArray){
        if( jsonArray.length() > 0 ){
            jsonArray.remove( 0 );
            removedata(jsonArray);
        }
    }

}

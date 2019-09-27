package com.example.zhuanchu;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.zhuanchu.adapter.NavbarAdapter;
import com.example.zhuanchu.adapter.ViewAdapter;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.kongzue.dialog.v2.DialogSettings;
import com.kongzue.dialog.v2.SelectDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.leefeng.promptlibrary.PromptDialog;

import static com.kongzue.dialog.v2.DialogSettings.STYLE_IOS;


public class FileView extends AppCompatActivity {


    private JSONArray jsonArray = new JSONArray();
    private String path = "";
    private File file = null;
    private PromptDialog promptDialog;
    private JSONArray navjson = new JSONArray();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fileview);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("已下载文件");
        actionBar.setDisplayHomeAsUpEnabled(true);
        promptDialog = new PromptDialog(this);

        path = Environment.getExternalStorageDirectory() + "/CRRC";
        readFile( path );

        final RecyclerView recyclerView = findViewById(R.id.listviews);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        recyclerView.setLayoutManager(flexboxLayoutManager);

        final ViewAdapter viewAdapter = new ViewAdapter(this, jsonArray);

        recyclerView.setAdapter(viewAdapter);

        /*
         * 文件导航
         * */
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", "CRRC");
            jsonObject.put("path", path);
        } catch (JSONException e) {
            e.printStackTrace();
        };
        navjson.put(jsonObject);

        RecyclerView recyclerView1 = findViewById(R.id.navlists);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView1.setLayoutManager(linearLayoutManager);

        final NavbarAdapter navbarAdapter = new NavbarAdapter(this, navjson);

        recyclerView1.setAdapter(navbarAdapter);

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
                        JSONObject jo = new JSONObject();
                        jo.put("name", jsonArray.getJSONObject(i).getString("name"));
                        jo.put("path", path);
                        navjson.put( jo );
                        readFile( path );
                        //ViewAdapter viewAdapter = new ViewAdapter( FileView.this, jsonArray);
                        //recyclerView.setAdapter(viewAdapter);


                        viewAdapter.notifyDataSetChanged();
                        navbarAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        navbarAdapter.setOnItemClickListener(new NavbarAdapter.OnItemClickListener() {
            @Override
            public void tClick(int i) {
                System.out.println( "你点击的是：" + i );
                System.out.println( navjson.length() );
                int slength = navjson.length();
                for(int k = 0; k < slength; k++){
                    try {
                        if( k == i ){
                            path =navjson.getJSONObject(k).getString("path");
                        }
                        if( k > i && k < slength ){
                            navjson.remove( navjson.length() - 1 );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                readFile( path );

                System.out.println( "长度是：" + navjson.length() );

                navbarAdapter.notifyDataSetChanged();
                viewAdapter.notifyDataSetChanged();
            }
        });

        viewAdapter.selectOnItemClickListener(new ViewAdapter.SItemClickListener() {
            @Override
            public void sClick(int i) {
                try {
                    if( jsonArray.getJSONObject(i).getString("select").equals("0") ){
                        jsonArray.getJSONObject(i).put("select", "1");
                    }else{
                        jsonArray.getJSONObject(i).put("select", "0");
                    }
                    System.out.println( jsonArray.getJSONObject(i).getString("select") );
                } catch (JSONException e) {
                    e.printStackTrace();
                };
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

                navjson.remove( navjson.length() - 1 );

                viewAdapter.notifyDataSetChanged();
                navbarAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.removefile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogSettings.style = STYLE_IOS;
                DialogSettings.use_blur = true;
                DialogSettings.blur_alpha = 200;
                SelectDialog.show(FileView.this, "删除文件", "你确定要删除该文件吗", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < jsonArray.length(); i++){
                            try {
                                if( jsonArray.getJSONObject(i).getString("select").equals("1") ){
                                    DeleteFolder( path + "/" + jsonArray.getJSONObject(i).getString("name") );
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        readFile( path );
                        viewAdapter.notifyDataSetChanged();
                    }
                }, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            }
        });



//        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                promptDialog.showLoading("加载中...");
//                ARouter.getInstance().build("/app/system").navigation();
//                promptDialog.dismiss();
//
//            }
//        });


    }

    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param filePath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public boolean DeleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            promptDialog.showLoading("加载中...");
            finish();
            promptDialog.dismiss();

            SharedPreferences pref = FileView.this.getSharedPreferences("tab", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("tabnum", 3);
            editor.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                jsonObject.put("select", "0");
                if( spec.getName().equals("UPLOAD") || spec.getName().equals("DOWNLOAD") ){
                    jsonObject.put("check", false);
                }else{
                    jsonObject.put("check", true);
                }

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
                jsonObject.put("select", "0");
                if( spec.getName().equals("UPLOAD") || spec.getName().equals("DOWNLOAD") ){
                    jsonObject.put("check", false);
                }else{
                    jsonObject.put("check", true);
                }

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

    @Override
    public void onBackPressed() {
        SharedPreferences pref = FileView.this.getSharedPreferences("tab", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("tabnum", 3);
        editor.commit();
        super.onBackPressed();
    }
}

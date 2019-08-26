package com.example.zhuanchu;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.zhuanchu.adapter.UploadAdapter;
import com.example.zhuanchu.adapter.VerticalAdapter;
import com.example.zhuanchu.service.CompressOperate_zip4j;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Route( path = "/app/upload")
public class UploadActivity extends AppCompatActivity {

    private JSONArray jsonArray = null;
    private String url = "http://www.rongswift.com:8089/chengdu/uploadmore";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);

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

        ImageView image = findViewById(R.id.systembar).findViewById(R.id.uploadimg);
        image.setImageResource(R.drawable.ic_fileupload_upload_c);
        TextView textView = findViewById(R.id.systembar).findViewById(R.id.uploadtext);
        textView.setTextColor(Color.parseColor("#35ae5d"));

        findViewById(R.id.uploadbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( jsonArray == null || jsonArray.length() == 0 ){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder( UploadActivity.this );
                    alertDialog.setMessage("请选择需要上传的目录");
                    alertDialog.show();
                    return;
                }

                try {
                    String path = Environment.getExternalStorageDirectory() + "/CRRC";
                    File file = new File(path);

                    if (!file.exists()) {
                        file.mkdir();
                    }

                    String path2 = Environment.getExternalStorageDirectory() + "/CRRC/UPLOAD";

                    file = new File( path2 );
                    if (!file.exists()) {
                        file.mkdir();
                    }

                    JSONObject choose = null;

                    for(int i=0;i<jsonArray.length();i++){
                        if( jsonArray.getJSONObject(i).getBoolean("check") ){
                            choose = jsonArray.getJSONObject(i);
                        }
                    }

                    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMddHH:mm:ss");
                    String packtime = dateFormat.format(new Date());

                    String packname = choose.getString("name") + "_" + packtime + "_" + "A" + "_重庆机务段.zip";

                    CompressOperate_zip4j.compressZip4j(path + "/DOWNLOAD/" + choose.getString("name"), path2 + "/" + packname, "123456" );

                    final AlertDialog.Builder dialog = new AlertDialog.Builder( UploadActivity.this );
                    dialog.setMessage("打包成功");
                    final AlertDialog success = dialog.show();


                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            success.dismiss();
                        }
                    },1500);

                }catch (Exception e){

                }

                //打包成ZIP

            }
        });

        //JSONArray jsonArray = null;
        try {
            String path = Environment.getExternalStorageDirectory() + "/CRRC";
            File file = new File(path);

            if (!file.exists()) {
                file.mkdir();
            }

            path = Environment.getExternalStorageDirectory() + "/CRRC/UPLOAD";

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
                jsonObject.put("file", spec);
                jsonObject.put("check", false);
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


        RecyclerView recyclerView = findViewById(R.id.listUpload);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        recyclerView.setLayoutManager(flexboxLayoutManager);

        recyclerView.setAdapter(new UploadAdapter(this, jsonArray));


        findViewById(R.id.uploadbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                MediaType MutilPart_Form_Data = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");

                for(int i=0;i<jsonArray.length();i++){
                    try {
                        System.out.println( jsonArray.getJSONObject(i).getString("name") );
                        System.out.println( jsonArray.getJSONObject(i).getString("check") );
                        System.out.println( jsonArray.getJSONObject(i).getString("file") );
                        if( jsonArray.getJSONObject(i).getBoolean("check") ){
                            File fileOne = new File(jsonArray.getJSONObject(i).getString("file")); //生成文件
                            requestBodyBuilder.addFormDataPart("file", jsonArray.getJSONObject(i).getString("file"), RequestBody.create(MutilPart_Form_Data, fileOne ) );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //接口测试
                requestBodyBuilder.addFormDataPart("type", "aa");
                requestBodyBuilder.addFormDataPart("ab", "ab");
                requestBodyBuilder.addFormDataPart("tip", "tip");
                requestBodyBuilder.addFormDataPart("number", "number");
                requestBodyBuilder.addFormDataPart("time", "time");


                RequestBody requestBody = requestBodyBuilder.build();

                System.out.println( 8888 );
                //String url = "https://www.baidu.com";
                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.newBuilder().connectTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                        .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                        .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS);

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                final Call call = okHttpClient.newCall(request);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response response = call.execute();
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), "上传文件成功", Toast.LENGTH_LONG).show();
                            Looper.loop();
                        } catch (IOException e) {
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), "上传文件失败", Toast.LENGTH_LONG).show();
                            Looper.loop();
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

    }
}

package com.example.zhuanchu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
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
import com.example.zhuanchu.service.ExMultipartBody;
import com.example.zhuanchu.service.UploadProgressListener;
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

    Context context;
    private JSONArray jsonArrayup = null;
    private String url = "http://39.108.162.8:8089/chengdu/uploadmore";
    SharedPreferences sharedPreferencesUp = null;
    private ProgressDialog pdialogUp;

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
            jsonArrayup = new JSONArray();

            File[] files = file.listFiles();
            for (File spec : files) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", spec.getName());
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateTime=df.format(new Date(spec.lastModified()));
                jsonObject.put("time", dateTime );
                jsonObject.put("file", spec);
                jsonObject.put("check", false);
                jsonArrayup.put( jsonObject );
                //filename += spec.getName();
            }

            TextView textView1 = findViewById(R.id.files);
            textView1.setText( jsonArrayup.getJSONObject(0).getString("name") );
        } catch (Exception e) {

        }



        RecyclerView recyclerView = findViewById(R.id.listUpload);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        recyclerView.setLayoutManager(flexboxLayoutManager);

        recyclerView.setAdapter(new UploadAdapter(this, jsonArrayup));


        findViewById(R.id.uploadbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                * 判断是否有网络
                * */
                boolean network = isNetworkAvailable(UploadActivity.this);
                if( !network ){

                    Toast.makeText(UploadActivity.this, "没有检测到网络", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean mobile = isMobile( UploadActivity.this );
                sharedPreferencesUp = getSharedPreferences("data",MODE_PRIVATE);

                boolean networkinit = sharedPreferencesUp.getBoolean("network", false);

                System.out.println( networkinit );

                if( mobile && !networkinit ){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(UploadActivity.this);
                    dialog.setTitle("网络检测");
                    TextView textView1 = new TextView(UploadActivity.this);
                    textView1.setText("当前为4G网络，要继续上传吗?");
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            uploadFile(UploadActivity.this);
                        }
                    });
                    dialog.setNegativeButton("取消", null);
                    dialog.show();
                    return;
                }

                uploadFile(UploadActivity.this);

            }
        });

    }

    public void uploadFile(Context context){

        pdialogUp = new ProgressDialog( context );
        pdialogUp.setTitle("任务正在执行中");
        pdialogUp.setMessage("正在上传中，敬请等待...");
        pdialogUp.setCancelable(false);
        pdialogUp.setMax(100);
        pdialogUp.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pdialogUp.setIndeterminate(false);
        pdialogUp.setProgress(0);
        pdialogUp.show();

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        MediaType MutilPart_Form_Data = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");



        for(int i=0;i<jsonArrayup.length();i++){
            try {
                System.out.println( jsonArrayup.getJSONObject(i).getString("name") );
                System.out.println( jsonArrayup.getJSONObject(i).getString("check") );
                System.out.println( jsonArrayup.getJSONObject(i).getString("file") );
                if( jsonArrayup.getJSONObject(i).getBoolean("check") ){
                    File fileOne = new File(jsonArrayup.getJSONObject(i).getString("file")); //生成文件
                    requestBodyBuilder.addFormDataPart("file", jsonArrayup.getJSONObject(i).getString("file"), RequestBody.create(MutilPart_Form_Data, fileOne ) );
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

        ExMultipartBody exMultipartBody = new ExMultipartBody(requestBodyBuilder.build(), new UploadProgressListener() {
            @Override
            public void onProgress(long total, long current) {
                System.out.println( current + "----" + total );
                pdialogUp.setProgress((int) Math.ceil(current*100/total));
                if( current == total ){
                    pdialogUp.dismiss();
                }
            }
        });


        RequestBody requestBody = requestBodyBuilder.build();

        System.out.println( 8888 );
        //String url = "https://www.baidu.com";
        OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.newBuilder().connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
//                .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
//                .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS);

        System.out.println( url );

        Request request = new Request.Builder()
                .url(url)
                .post(exMultipartBody)
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

    /**
     * 检查是否有网络
     */
    public static boolean isNetworkAvailable(Context context) {

        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isAvailable();
    }


    /**
     * 检查是否是WIFI
     */
    public static boolean isWifi(Context context) {

        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检查是否是移动网络
     */
    public static boolean isMobile(Context context) {

        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }


    private static NetworkInfo getNetworkInfo(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

}

package com.example.zhuanchu;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.zhuanchu.adapter.VerticalAdapter;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.example.zhuanchu.service.CompressOperate_zip4j;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Route( path = "/app/pack")
public class PackActivity extends AppCompatActivity {

    private JSONArray jsonArray = null;
    private ProgressDialog pdialog;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pack);

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

        ImageView image = findViewById(R.id.systembar).findViewById(R.id.packImg);
        image.setImageResource(R.drawable.dabao_c);
        TextView textView = findViewById(R.id.systembar).findViewById(R.id.filepack);
        textView.setTextColor( Color.parseColor("#35ae5d")  );

        findViewById(R.id.packbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( jsonArray == null || jsonArray.length() == 0 ){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder( PackActivity.this );
                    alertDialog.setMessage("请选择需要打包的目录");
                    alertDialog.show();
                    return;
                }

                try {
                    final String path = Environment.getExternalStorageDirectory() + "/CRRC";
                    File file = new File(path);

                    if (!file.exists()) {
                        file.mkdir();
                    }

                    final String path2 = Environment.getExternalStorageDirectory() + "/CRRC/UPLOAD";

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

                    final String packname = choose.getString("name") + "_" + packtime + "_" + "A" + "_重庆机务段.zip";

                    pdialog = new ProgressDialog( PackActivity.this );
                    pdialog.setTitle("任务正在执行中");
                    pdialog.setMessage("正在下载中，敬请等待...");
                    pdialog.setCancelable(false);
                    pdialog.setMax(100);
                    pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pdialog.setIndeterminate(false);
                    pdialog.setProgress(0);
                    pdialog.show();

                    Toast toast = new Toast( PackActivity.this );

                    final JSONObject finalChoose = choose;
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                CompressOperate_zip4j.compressZip4j(path + "/DOWNLOAD/" + finalChoose.getString("name"), path2 + "/" + packname, "123456", pdialog, PackActivity.this );
                                //pdialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //Toast.makeText(getApplicationContext(), "打包成功", Toast.LENGTH_LONG).show();
                        }
                    });
                    thread.start();




//                    final AlertDialog.Builder dialog = new AlertDialog.Builder( PackActivity.this );
//                    dialog.setMessage("打包成功");
//                    final AlertDialog success = dialog.show();
//
//
//                    Timer timer = new Timer();
//                    timer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            success.dismiss();
//                        }
//                    },1500);

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

package com.example.zhuanchu;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

@Route( path = "/app/system")
public class SystemActivity extends AppCompatActivity {

    SharedPreferences sharedPreferencesSet = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system);

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

        findViewById(R.id.systembar).findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/upload").navigation();
            }
        });

        findViewById(R.id.systembar).findViewById(R.id.toPack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/pack").navigation();
            }
        });

        ImageView image = findViewById(R.id.systembar).findViewById(R.id.systemImg);
        image.setImageResource(R.drawable.persion_c);
        TextView textView = findViewById(R.id.systembar).findViewById(R.id.systemText);
        textView.setTextColor( Color.parseColor("#35ae5d")  );

        sharedPreferencesSet = getSharedPreferences("data",MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferencesSet.edit();


        /*
         * 初始化wifi,4g打开按钮
         * */
        boolean networkinit = sharedPreferencesSet.getBoolean("network", false);
        boolean wifiinit = sharedPreferencesSet.getBoolean("wifi", false);
        ImageView networkImg = findViewById(R.id.networkset);
        ImageView wifiImg = findViewById(R.id.wifiset);
        if( networkinit ){
            networkImg.setImageResource(R.drawable.kaiqi);
        }else{
            networkImg.setImageResource(R.drawable.kaiqi_c);
        }
        if( wifiinit ){
            wifiImg.setImageResource(R.drawable.kaiqi);
        }else{
            wifiImg.setImageResource(R.drawable.kaiqi_c);
        }


        findViewById(R.id.networkset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean networkstate = sharedPreferencesSet.getBoolean("network", false);

                ImageView imageView = findViewById(R.id.networkset);
                if( networkstate ){
                    imageView.setImageResource(R.drawable.kaiqi_c);
                    editor.putBoolean("network", false);
                }else{
                    imageView.setImageResource(R.drawable.kaiqi);
                    editor.putBoolean("network", true);
                }
                editor.commit();
            }
        });

        findViewById(R.id.wifiset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean wifistate = sharedPreferencesSet.getBoolean("wifi", false);

                ImageView imageView = findViewById(R.id.wifiset);
                if( wifistate ){
                    imageView.setImageResource(R.drawable.kaiqi_c);
                    editor.putBoolean("wifi", false);
                }else{
                    imageView.setImageResource(R.drawable.kaiqi);
                    editor.putBoolean("wifi", true);
                }
                editor.commit();
            }
        });

        findViewById(R.id.toFileView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/fileview").navigation();
            }
        });

    }
}

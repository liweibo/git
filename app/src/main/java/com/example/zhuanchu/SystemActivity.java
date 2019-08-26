package com.example.zhuanchu;

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
    }
}

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
import com.suke.widget.SwitchButton;

import me.leefeng.promptlibrary.PromptDialog;

@Route( path = "/app/system")
public class SystemActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;
    private PromptDialog promptDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system);

        promptDialog = new PromptDialog(this);

        /*
         * 导航修改的内容
         */
        findViewById(R.id.systembar).findViewById(R.id.toSystem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptDialog.showLoading("加载中...");
                ARouter.getInstance().build("/app/system").navigation();
                promptDialog.dismiss();
            }
        });

        findViewById(R.id.systembar).findViewById(R.id.toDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptDialog.showLoading("加载中...");
                ARouter.getInstance().build("/app/home").navigation();
                promptDialog.dismiss();
            }
        });

        findViewById(R.id.systembar).findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptDialog.showLoading("加载中...");
                ARouter.getInstance().build("/app/upload").navigation();
                promptDialog.dismiss();
            }
        });

        findViewById(R.id.systembar).findViewById(R.id.toPack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptDialog.showLoading("加载中...");
                ARouter.getInstance().build("/app/pack").navigation();
                promptDialog.dismiss();
            }
        });

        ImageView image = findViewById(R.id.systembar).findViewById(R.id.systemImg);
        image.setImageResource(R.drawable.persion_c);
        TextView textView = findViewById(R.id.systembar).findViewById(R.id.systemText);
        textView.setTextColor( Color.parseColor("#35ae5d")  );

        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();


        /*
         * 初始化wifi,4g打开按钮
         * */
        boolean networkinit = sharedPreferences.getBoolean("network", false);//4g
        boolean wifiinit = sharedPreferences.getBoolean("wifi", false);//wifi
        com.suke.widget.SwitchButton switchButtoncheck = (com.suke.widget.SwitchButton)
                findViewById(R.id.switch_button4G);
        switchButtoncheck.setChecked(networkinit);
        com.suke.widget.SwitchButton switchButtoncheckwifi = (com.suke.widget.SwitchButton)
                findViewById(R.id.switch_buttonWifi);
        switchButtoncheckwifi.setChecked(wifiinit);
        switchButtoncheck.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    editor.putBoolean("network", isChecked);
            }
        });
        switchButtoncheckwifi.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                editor.putBoolean("wifi", isChecked);
            }
        });



        findViewById(R.id.toFileView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("查看文件");
                ARouter.getInstance().build("/app/fileview").navigation();
            }
        });

    }
}

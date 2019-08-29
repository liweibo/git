package com.example.zhuanchu;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

@Route(path = "/app/auth")
public class AuthActivity extends AppCompatActivity {
    private TextView tv_forget;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        //initView();
        //setListener();
        tv_forget = findViewById(R.id.tv_forget);
        fab = findViewById(R.id.fab);
        ARouter.openDebug();
        ARouter.init(getApplication());

        ARouter.getInstance().build("/app/system").navigation();

        findViewById(R.id.bt_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/wifi").navigation();
            }
        });

        tv_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AuthActivity.this, fab, fab.getTransitionName());
                startActivity(new Intent(AuthActivity.this, AuthfailActivity.class), options.toBundle());
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AuthActivity.this, fab, fab.getTransitionName());
                startActivity(new Intent(AuthActivity.this, AuthfailActivity.class), options.toBundle());
            }
        });
    }
}

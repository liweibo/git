package com.example.zhuanchu;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.leefeng.promptlibrary.PromptDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AuthActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btGo;
    private FloatingActionButton fab;
    private PromptDialog promptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        initView();
        setListener();
        getSupportActionBar().hide();
        promptDialog = new PromptDialog(this);

        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.gplus_color_2), true);

        /*
        * 获取远程版本信息
        * */
        final AlertDialog.Builder dialog = new AlertDialog.Builder( AuthActivity.this );
        PackageManager packageManager = this.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            final String version = packageInfo.versionName;
            String url = "http://www.rongswift.com:8089/forsee/getType?type=wuxian";
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println( e );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println( response.body() + "--------" );

                    String dataResult = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject( dataResult );
                        System.out.println( jsonObject.getJSONArray("result").length() + "*-*//////-----" );
                        String version_master = jsonObject.getJSONArray("result").getJSONObject(0).getString("number");
                        System.out.println( version_master );
                        if( !version_master.equals(version) ){
                            System.out.println( "有新的apk可以更新" );
                            Looper.prepare();
                            dialog.setTitle("更新apk");
                            TextView textView = new TextView(AuthActivity.this);
                            textView.setHint("最新版本" + version_master);
                            textView.setGravity(Gravity.CENTER);
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            dialog.setView( textView );
                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.out.println( "点击了确定" );
                                    Uri uri = Uri.parse("https://www.rongswift.com/wuxian.html");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            });
                            dialog.setNegativeButton("取消",null);
                            dialog.show();
                            Looper.loop();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            System.out.println( version );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btGo = findViewById(R.id.bt_go);
        fab = findViewById(R.id.fab);
    }

    private void setListener() {
        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptDialog.showLoading("加载中...");
                Intent i2 = new Intent(AuthActivity.this, HomeActivity.class);
                startActivity(i2);
                promptDialog.dismiss();

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AuthActivity.this, fab, fab.getTransitionName());
                startActivity(new Intent(AuthActivity.this, AuthfailActivity.class), options.toBundle());

//                startActivity(new Intent(AuthActivity.this, AuthfailActivity.class));
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onRestart() {
        super.onRestart();
        fab.setVisibility(View.GONE);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }
}



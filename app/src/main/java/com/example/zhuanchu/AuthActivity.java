package com.example.zhuanchu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.githang.statusbar.StatusBarCompat;
import com.kongzue.dialog.v2.DialogSettings;
import com.kongzue.dialog.v2.MessageDialog;
import com.kongzue.dialog.v2.SelectDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import me.leefeng.promptlibrary.PromptDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kongzue.dialog.v2.DialogSettings.STYLE_IOS;


public class AuthActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btGo;
    private FloatingActionButton fab;
    private PromptDialog promptDialog;
    private CheckBox cb_psw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        initView();
        setListener();
        getSupportActionBar().hide();
        promptDialog = new PromptDialog(this);

        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.authbar), true);

        /*
         * 获取远程版本信息
         * */
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AuthActivity.this);
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
                    System.out.println(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println(response.body() + "--------");

                    String dataResult = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(dataResult);
                        System.out.println(jsonObject.getJSONArray("result").length() + "*-*//////-----");
                        final String version_master = jsonObject.getJSONArray("result").getJSONObject(0).getString("number");
                        System.out.println(version_master);
                        if (!version_master.equals(version)) {
                            System.out.println("有新的apk可以更新");
                            Looper.prepare();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogSettings.style = STYLE_IOS;
                                    DialogSettings.use_blur = true;
                                    DialogSettings.blur_alpha = 200;
                                    SelectDialog.show(AuthActivity.this, "版本更新", "存在新版本：V" + version_master +
                                            "，需下载安装吗?", "确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Uri uri = Uri.parse("https://www.rongswift.com/wuxian.html");
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(intent);
                                        }
                                    }, "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                }
                            });

                            Looper.loop();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            System.out.println(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void initView() {

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btGo = findViewById(R.id.bt_go);
        fab = findViewById(R.id.fab);

        cb_psw = findViewById(R.id.cb_psw);

        SharedPreferences sharedPreferences = getSharedPreferences("userpsw",
                Activity.MODE_PRIVATE);
        String user = sharedPreferences.getString("user", "");
        String psw = sharedPreferences.getString("psw", "");
        if (!user.equals("")&&!psw.equals("")){
            cb_psw.setChecked(true);
            etUsername.setText(user);
            etPassword.setText(psw);

        }else
        {
            cb_psw.setChecked(false);
        }

    }

    private void setListener() {
        cb_psw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences pref =AuthActivity.this.getSharedPreferences("userpsw", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                if (b){//勾选
                    //判空，存储
                    String usr = etUsername.getText().toString().trim();
                    String psw = etPassword.getText().toString().trim();

                    if (!usr.equals("")&&!psw.equals("")){
                        editor.putString("user",usr).commit();
                        editor.putString("psw",psw).commit();
                    }else{
                        DialogSettings.style = STYLE_IOS;
                        DialogSettings.use_blur = true;
                        DialogSettings.blur_alpha = 200;
                        MessageDialog.show(AuthActivity.this,
                                "提示", "请输入完整用户名密码", "知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        cb_psw.setChecked(false);

                                    }
                                });
                    }


                }else{
                    //存储的数据清空
                    editor.putString("user","").commit();
                    editor.putString("psw","").commit();
                }
            }
        });

        etUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_psw.setChecked(false);
            }
        });
        etPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_psw.setChecked(false);
            }
        });

        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("测试1");

                final String users = etUsername.getText().toString().trim();
                final String psw = etPassword.getText().toString().trim();
                if (users.trim().length() > 0 && psw.trim().length() > 0) {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .add("userid", users).add("password", psw)
                            .build();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://mro.csrzic.com/comm/mro/authenticateuser")
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            // .post(RequestBody.create(MEDIA_TYPE_TEXT, postBody))
                            .post(formBody)
                            // 表单提交
                            .build();
                    System.out.println("测试11");

                    okHttpClient.newCall(request).enqueue(new Callback() {


                        @Override
                        public void onFailure(Call call, IOException e) {
                            System.out.println("错误：" + e.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogSettings.style = STYLE_IOS;
                                    DialogSettings.use_blur = true;
                                    DialogSettings.blur_alpha = 200;
                                    MessageDialog.show(AuthActivity.this,
                                            "提示", "网络状况不佳", "知道了", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            });
                                }
                            });

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String dataResult = response.body().string();
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(dataResult);
                                boolean actionResult = jsonObject.getBoolean("actionResult");
                                System.out.println("测试2");
                                if (actionResult) {
                                    Intent i2 = new Intent(AuthActivity.this, HomeActivity.class);
                                    startActivity(i2);
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DialogSettings.style = STYLE_IOS;
                                            DialogSettings.use_blur = true;
                                            DialogSettings.blur_alpha = 200;
                                            MessageDialog.show(AuthActivity.this,
                                                    "提示", "用户名或密码输入错误", "知道了", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            etUsername.setText("");
                                                            etPassword.setText("");
                                                        }
                                                    });
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    DialogSettings.style = STYLE_IOS;
                    DialogSettings.use_blur = true;
                    DialogSettings.blur_alpha = 200;
                    MessageDialog.show(AuthActivity.this,
                            "提示", "请输入用户名、密码", "知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                }


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



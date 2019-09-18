package com.example.zhuanchu;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.example.zhuanchu.adapter.UploadAdapter;
import com.example.zhuanchu.adapter.VerticalAdapter;
import com.example.zhuanchu.bean.UploadFile;
import com.example.zhuanchu.bean.javaBean.JsonRootBean;
import com.example.zhuanchu.bean.pojo15.JsonRootBean15;
import com.example.zhuanchu.service.AuthService;
import com.example.zhuanchu.service.CompressOperate_zip4j;
import com.example.zhuanchu.service.ExMultipartBody;
import com.example.zhuanchu.service.GetInfoForMultiList;
import com.example.zhuanchu.service.MultiViewPager;
import com.example.zhuanchu.service.SqlHelper;
import com.example.zhuanchu.service.UploadProgressListener;
import com.example.zhuanchu.service.ViewFindUtils;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.widget.MsgView;
import com.githang.statusbar.StatusBarCompat;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.kongzue.dialog.v2.DialogSettings;
import com.kongzue.dialog.v2.MessageDialog;
import com.kongzue.dialog.v2.SelectDialog;
import com.scottyab.aescrypt.AESCrypt;
import com.suke.widget.SwitchButton;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import devlight.io.library.ntb.NavigationTabBar;
import me.leefeng.promptlibrary.PromptDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kongzue.dialog.v2.DialogSettings.STYLE_IOS;

public class HomeActivity extends AppCompatActivity {
    private List<String> datajutiCheXing = new ArrayList<>();
    private List<String> dataCheHao = new ArrayList<>();
    private List<String> dataShebei = new ArrayList<>();
    private List<String> dataIpPswUser = new ArrayList<>();

    private String chexingValue = "";
    private String jutiChexingValue = "";
    private String chexianghaoValue = "";
    private String cmdValue = " ";
    private String shebeiValue = "";

   private String token = "";
   private  boolean getToken = false;
    private WifiManager wifiManager;
    private Socket socket;
    OutputStream outputStream;
    InputStream is;
    private String _host = "10.0.1.5";
    private String _port = "21";
    private String _user = "CSR";
    private String _pass = "12345678";
    private String cameraPath;
    private String imgFileName;


    // 输入流读取器对象
    InputStreamReader isr;
    BufferedReader br;

    // 接收服务器发送过来的消息
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static EditText host, port, name, pass;
    public static boolean haveCheck = true;
    public ImageButton imbtnOcr;


    public EditText editext_chehao;
    public EditText editext_peishu;
    public NiceSpinner edit_spinnerCheXing;
    public NiceSpinner edit_spinnerjuTiCheXing;
    public NiceSpinner edit_spinnerCheXingCheHao;
    public NiceSpinner edit_spinnerCheXingCheHaoSheBei;
    public NiceSpinner edit_spinnerCheXingCheHaoCMD;
    public LinearLayout fl_cmd;


    public CheckBox cb_remember;
    private static final int REQUEST_CALL_PHONE = 100;
    public String img64;
    private PromptDialog promptDialog;
    private boolean haveDismiss = false;
    private String jsonRe = "";

    List<String> chexingList = new LinkedList<>();

    JSONArray jsonArray = null;

    View mDecorView;
    SegmentTabLayout mTabLayout_3;
    String[] mTitles_3 = {"已上传", "未上传"};
    ArrayList<Fragment> mFragments = new ArrayList<>();
    ArrayList<Fragment> mFragments2 = new ArrayList<>();

    Context context;
    private JSONArray jsonArrayup = null;
    private String url = "http://39.108.162.8:8089/chengdu/uploadmore";
    SharedPreferences sharedPreferencesUp = null;
    private ProgressDialog pdialogUp;

    SharedPreferences sharedPreferencesSet = null;
    ActionBar actionBar = null;
    View uploadView = null;
    UploadAdapter uploadAdapter = null;
    VerticalAdapter verticalAdapter = null;
    List<String> arrs = new ArrayList<>();
    List<String> removeUoloads = new ArrayList<>();
    SqlHelper sqlHelper = null;
    SQLiteDatabase sqLiteDatabase = null;
    int uploadIndex = 0;
    private static final int COMPLETED = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                fragmnetWeishangchuan(uploadView, uploadIndex);
            }
        }
    };
    RecyclerView recyclerView = null;
    FlexboxLayoutManager flexboxLayoutManager = null;

    View packView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeoncreate);

        SharedPreferences sharedPreferences = getSharedPreferences("tab",
                Activity.MODE_PRIVATE);
        int tabnum = sharedPreferences.getInt("tabnum", 0);


        actionBar = this.getSupportActionBar();
        if (tabnum == 3) {
            actionBar.setTitle("系统设置");
        } else if (tabnum == 2) {
            actionBar.setTitle("文件上传");

        } else if (tabnum == 1) {
            actionBar.setTitle("文件打包");

        } else if (tabnum == 0) {
            actionBar.setTitle("信息配置");

        } else {
            actionBar.setTitle("信息配置");
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorfocus), true);
        initUI();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SharedPreferences pref = HomeActivity.this.getSharedPreferences("tab", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("tabnum", 0);
            editor.commit();
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUI() {
        final MultiViewPager viewPager = (MultiViewPager) findViewById(R.id.vp_horizontal_ntbhome);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((MultiViewPager) container).removeView((View) object);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view = null;
                if (position == 0) {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.home, null, false);
                    initdownload(view);
                } else if (position == 1) {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.pack, null, false);
                    packView = view;
                    initPack(view);
                } else if (position == 2) {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.upload_ntb, null, false);
                    uploadView = view;

                    initUpload(view);
                } else if (position == 3) {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.system, null, false);
                    initSet(view);
                }


                container.addView(view);
                return view;
            }
        });


        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontalhome);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.down),
                        Color.parseColor(colors[0]))
                        .title("下载")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.dabao),
                        Color.parseColor(colors[1]))
                        .title("打包")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.uploadf),
                        Color.parseColor(colors[2]))
                        .title("上传").badgeTitle("9")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.sets),
                        Color.parseColor(colors[3]))
                        .title("设置").badgeTitle("9")
                        .build()
        );
        SharedPreferences sharedPreferences = getSharedPreferences("tab",
                Activity.MODE_PRIVATE);
        int tabnum = sharedPreferences.getInt("tabnum", 0);

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, tabnum);
        navigationTabBar.setOnPageChangeListener(new MultiViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();
                if (position == 3) {
                    actionBar.setTitle("系统设置");
                } else if (position == 2) {
                    actionBar.setTitle("文件上传");
                    if( uploadView != null ){
                        fragmnetWeishangchuan(uploadView, uploadIndex);
                    }
                } else if (position == 1) {
                    actionBar.setTitle("文件打包");
                    initPack(packView);

                } else if (position == 0) {
                    actionBar.setTitle("信息配置");

                } else {
                    actionBar.setTitle("信息配置");
                }
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);
    }


    //以下4点，根据position的值，选择对应的一个即可，即在instantiateItem中被调用

    //1  init下载首页的UI组件，以及逻辑代码，也就是homeactivity页面的内容
    public void initdownload(View view) {
        imbtnOcr = (ImageButton) view.findViewById(R.id.imagebtnCamera);

        editext_chehao = (EditText) view.findViewById(R.id.editext_chehao);
        editext_peishu = (EditText) view.findViewById(R.id.et_peishu);
        edit_spinnerCheXing = view.findViewById(R.id.edit_spinnerCheXing);
        edit_spinnerjuTiCheXing = view.findViewById(R.id.edit_spinnerjuTiCheXing);
        edit_spinnerCheXingCheHao = view.findViewById(R.id.edit_spinnerCheXingCheHao);
        edit_spinnerCheXingCheHaoSheBei = view.findViewById(R.id.edit_spinnerCheXingCheHaoSheBei);
        edit_spinnerCheXingCheHaoCMD = view.findViewById(R.id.edit_spinnerCheXingCheHaoCMD);

        fl_cmd = (LinearLayout) view.findViewById(R.id.fl_cmd);
        cb_remember = (CheckBox) view.findViewById(R.id.remember_info);
        promptDialog = new PromptDialog(this);


        SharedPreferences sharedPreferences = getSharedPreferences("myInfo",
                Activity.MODE_PRIVATE);
        String chexing = sharedPreferences.getString("chexing", "");
        String jutichecing = sharedPreferences.getString("jutichecing", "");
        String chexianghao = sharedPreferences.getString("chexianghao", "");
        String shebei = sharedPreferences.getString("shebei", "");
        String cmd = sharedPreferences.getString("cmd", "");
        String chehao = sharedPreferences.getString("chehao", "");
        String peishu = sharedPreferences.getString("peishu", "");
        String views = sharedPreferences.getString("visible", "");
        System.out.println("存储车型：" + chexing);
        Boolean remember = sharedPreferences.getBoolean("remember", false);//记住信息

        if (remember) {
            cb_remember.setChecked(true);
        } else {
            cb_remember.setChecked(false);
        }

        if (chexing.equals("机车")) {
            chexingList.clear();
            chexingList.add("机车");
            chexingList.add("动车");
            chexingList.add("城轨");
        } else if (chexing.equals("动车")) {
            chexingList.clear();
            chexingList.add("动车");
//            chexingList.add("请选择");
            chexingList.add("机车");
            chexingList.add("城轨");

        } else if (chexing.equals("城轨")) {
            chexingList.clear();
            chexingList.add("城轨");
            chexingList.add("机车");
            chexingList.add("动车");
        } else {
            chexingList.clear();
            chexingList.add("请选择");
            chexingList.add("机车");
            chexingList.add("动车");
            chexingList.add("城轨");

        }

        List<String> datasets = new ArrayList<>(chexingList);
        edit_spinnerCheXing.attachDataSource(datasets);


        edit_spinnerjuTiCheXing.setText(jutichecing);
        edit_spinnerCheXingCheHao.setText(chexianghao);
        edit_spinnerCheXingCheHaoSheBei.setText(shebei);


        if (cmd.length() > 1 && views.equals("VISI")) {
            fl_cmd.setVisibility(View.VISIBLE);
        }
        edit_spinnerCheXingCheHaoCMD.setText(cmd);
        editext_chehao.setText(chehao);
        editext_peishu.setText(peishu);


        imbtnOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editext_chehao.setText("");
                inspectPermission();
            }
        });

        editext_chehao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_remember.setChecked(false);
            }
        });
        editext_peishu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_remember.setChecked(false);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                jsonRe = getJson();
            }
        }).start();
        spinnerMethod();


        //跳转
        initSwich(view);
    }


    //2  init打包首页的UI组件，以及逻辑代码，...
    public void initPack(final View view) {

        final ProgressDialog pdialog = new ProgressDialog(HomeActivity.this);

        Context context;

        view.findViewById(R.id.packbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (jsonArray == null || jsonArray.length() == 0) {
                    DialogSettings.style = STYLE_IOS;
                    DialogSettings.use_blur = true;
                    DialogSettings.blur_alpha = 200;
                    MessageDialog.show(HomeActivity.this,
                            "提示", "请选择需要打包的目录", "知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    return;
                }
                try {
                    final String path = Environment.getExternalStorageDirectory() + "/CRRC";
                    File file = new File(path);

                    if (!file.exists()) {
                        file.mkdir();
                    }
                    final String path2 = Environment.getExternalStorageDirectory() + "/CRRC/UPLOAD";
                    file = new File(path2);
                    if (!file.exists()) {
                        file.mkdir();
                    }

                    JSONObject choose = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i).getBoolean("check")) {
                            choose = jsonArray.getJSONObject(i);
                        }
                    }

                    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMddHH:mm:ss");
                    String packtime = dateFormat.format(new Date());

                    final String packname = choose.getString("name") + ".zip";

                    pdialog.setTitle("文件正在压缩打包");
                    pdialog.setMessage("敬请等待...");
                    pdialog.setProgressDrawable(getResources().getDrawable(R.drawable.myprogressbarstyle));
                    pdialog.setCancelable(false);
                    pdialog.setMax(100);
                    pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                    pdialog.setIndeterminate(false);
                    pdialog.setProgress(0);
                    pdialog.show();

//                    Toast toast = new Toast(PackActivity.this);

                    final JSONObject finalChoose = choose;
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                CompressOperate_zip4j.compressZip4j(path + "/DOWNLOAD/" + finalChoose.getString("name"), path2 + "/" + packname, "123456", pdialog, HomeActivity.this, finalChoose.getString("name"));
                                //pdialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                            //Toast.makeText(getApplicationContext(), "打包成功", Toast.LENGTH_LONG).show();
                        }
                    });
                    thread.start();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i).getBoolean("check")) {
                            jsonArray.getJSONObject(i).put("state", "1");
                        }
                    }
                    verticalAdapter.notifyDataSetChanged();


                } catch (Exception e) {

                }

                //打包成ZIP

            }
        });

        try {
            String path = Environment.getExternalStorageDirectory() + "/CRRC";
            File file = new File(path);

            if (!file.exists()) {
                file.mkdir();
            }

            path = Environment.getExternalStorageDirectory() + "/CRRC/DOWNLOAD";

            file = new File(path);
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
                String dateTime = df.format(new Date(spec.lastModified()));
                jsonObject.put("time", dateTime);
                jsonObject.put("check", false);

                Cursor cursor = sqLiteDatabase.query("package", null, "name='"+ spec.getName() +"'", null, null, null, null);
                if( cursor.getCount() > 0 ){
                    jsonObject.put("state", "1");
                }else{
                    jsonObject.put("state", "0");
                }
                jsonArray.put(jsonObject);

                //filename += spec.getName();
            }

            TextView textView1 = view.findViewById(R.id.files);
            textView1.setText(jsonArray.getJSONObject(0).getString("name"));
        } catch (Exception e) {

        }

        verticalAdapter = new VerticalAdapter(this, jsonArray);

        RecyclerView recyclerView = view.findViewById(R.id.listView);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        recyclerView.setLayoutManager(flexboxLayoutManager);

        recyclerView.setAdapter( verticalAdapter );

    }


    //3   init上传首页的UI组件，以及逻辑代码，...
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initUpload(View view) {

        sqlHelper = new SqlHelper(HomeActivity.this);
        sqLiteDatabase = sqlHelper.getWritableDatabase();

        //Cursor cursor = sqLiteDatabase.query("upload", null, null, null, null, null, null);
        //System.out.println( cursor.getCount() );


        //view
        mTabLayout_3 =(SegmentTabLayout)view.findViewById(R.id.tl_3);
        tl_3(view);





//
//        try {
//            String path = Environment.getExternalStorageDirectory() + "/CRRC";
//            File file = new File(path);
//
//            if (!file.exists()) {
//                file.mkdir();
//            }
//
//            path = Environment.getExternalStorageDirectory() + "/CRRC/UPLOAD";
//
//            file = new File(path);
//            if (!file.exists()) {
//                file.mkdir();
//            }
//
//            String filename = "";
//            jsonArrayup = new JSONArray();
//
//            File[] files = file.listFiles();
//            for (File spec : files) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("name", spec.getName());
//                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String dateTime = df.format(new Date(spec.lastModified()));
//                jsonObject.put("time", dateTime);
//                jsonObject.put("file", spec);
//                jsonObject.put("check", false);
//                jsonArrayup.put(jsonObject);
//                //filename += spec.getName();
//            }
//
//            TextView textView1 = view.findViewById(R.id.files);
//            textView1.setText(jsonArrayup.getJSONObject(0).getString("name"));
//        } catch (Exception e) {
//
//        }
//
//
//        RecyclerView recyclerView = view.findViewById(R.id.listUpload);
//        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
//        recyclerView.setLayoutManager(flexboxLayoutManager);
//
//        recyclerView.setAdapter(new UploadAdapter(this, jsonArrayup));
//
//
//        view.findViewById(R.id.uploadbtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                /*
//                 * 判断是否有网络
//                 * */
//                boolean network = isNetworkAvailable(HomeActivity.this);
//                if (!network) {
//
//                    Toast.makeText(HomeActivity.this, "没有检测到网络", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                boolean mobile = isMobile(HomeActivity.this);
//                sharedPreferencesUp = getSharedPreferences("data", MODE_PRIVATE);
//
//                boolean networkinit = sharedPreferencesUp.getBoolean("network", false);//4g
//
//                System.out.println(networkinit);
//
//                if (mobile && !networkinit) {
//                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(HomeActivity.this);
//                    dialog.setTitle("网络检测");
//                    TextView textView1 = new TextView(HomeActivity.this);
//                    textView1.setText("当前为4G网络，要继续上传吗?");
//                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            sharedPreferencesUp.edit().putBoolean("network",true).commit();
//                            uploadFile(HomeActivity.this);
//                        }
//                    });
//                    dialog.setNegativeButton("取消", null);
//                    dialog.show();
//                    return;
//                }
//
//                uploadFile(HomeActivity.this);
//
//            }
//        });
    }


    public void fragmnetWeishangchuan(View view, int number) {
        System.out.println( "未上传2" );
        File[] files = getUploadFiles();
        removedata(jsonArrayup);

        if( number == 1 ){
            try {
                for (File spec : files) {
                    Cursor cursor = sqLiteDatabase.query("upload", null, "name='"+ spec.getName() +"'", null, null, null, null);
                    while(cursor.moveToNext())
                    {
                        //光标移动成功
                        //把数据取出
                        System.out.println( cursor.getString(cursor.getColumnIndex("name")) );
                    }


                    if( cursor.getCount() == 0 ){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", spec.getName());
                        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateTime = df.format(new Date(spec.lastModified()));
                        jsonObject.put("time", dateTime);
                        jsonObject.put("file", spec);
                        jsonObject.put("check", false);
                        jsonArrayup.put(jsonObject);
                    }
                }


            } catch (Exception e) {

            }

            uploadAdapter = new UploadAdapter(this, jsonArrayup);

            recyclerView = view.findViewById(R.id.listUpload);
            flexboxLayoutManager = new FlexboxLayoutManager(this);
            recyclerView.setLayoutManager(flexboxLayoutManager);
            recyclerView.setAdapter( uploadAdapter );
        }

        if( number == 0 ){
            try {
                for (File spec : files) {
                    Cursor cursor = sqLiteDatabase.query("upload", null, "name='"+ spec.getName() +"'", null, null, null, null);
                    while(cursor.moveToNext())
                    {
                        //光标移动成功
                        //把数据取出
                        System.out.println( cursor.getString(cursor.getColumnIndex("name")) );
                    }


                    if( cursor.getCount() > 0 ){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", spec.getName());
                        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateTime = df.format(new Date(spec.lastModified()));
                        jsonObject.put("time", dateTime);
                        jsonObject.put("file", spec);
                        jsonObject.put("check", false);
                        jsonArrayup.put(jsonObject);
                    }
                }


            } catch (Exception e) {

            }

            uploadAdapter = new UploadAdapter(this, jsonArrayup);
            recyclerView = view.findViewById(R.id.listUpload_ready);
            flexboxLayoutManager = new FlexboxLayoutManager(this);
            recyclerView.setLayoutManager(flexboxLayoutManager);
            recyclerView.setAdapter( uploadAdapter );
        }


        view.findViewById(R.id.uploadbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * 判断是否有网络
                 * */
                boolean network = isNetworkAvailable(HomeActivity.this);
                if (!network) {

                    Toast.makeText(HomeActivity.this, "没有检测到网络", Toast.LENGTH_LONG).show();
                    return;
                }

                String wifiresult = getSSID();
                System.out.println( wifiresult );
                if( wifiresult.indexOf( "SHGZ" ) >= 0 ){
                    DialogSettings.style = STYLE_IOS;
                    DialogSettings.use_blur = true;
                    DialogSettings.blur_alpha = 200;
                    SelectDialog.show(HomeActivity.this, "WIFI设置", "请切换能上网的WIFI", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent();
                            i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(i);
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    return;
                }

                boolean mobile = isMobile(HomeActivity.this);
                sharedPreferencesUp = getSharedPreferences("data", MODE_PRIVATE);

                boolean networkinit = sharedPreferencesUp.getBoolean("network", false);//4g

                System.out.println(networkinit);

                if (mobile && !networkinit) {
                    DialogSettings.style = STYLE_IOS;
                    DialogSettings.use_blur = true;
                    DialogSettings.blur_alpha = 200;
                    SelectDialog.show(context, "网络检测", "当前为4G网络，要继续上传吗?", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferencesUp.edit().putBoolean("network", true).commit();
                            uploadFile(HomeActivity.this);
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    return;
                }
                uploadFile(HomeActivity.this);
            }
        });
    }


    private void tl_3(View view) {
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.vp_2);

        //同时，还要给Viewpager设置选中监听，才能使SegmentTablayout和ViewPager双向同步。
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                uploadIndex = position;
                System.out.println( "position=" + position );
                if( uploadAdapter != null ){
                    fragmnetWeishangchuan(uploadView, uploadIndex);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }
            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }
            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((ViewPager) container).removeView((View) object);
            }
            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view = null;
                if (position == 0) {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.fragment_simple_card_fragment_yishagchuan, null, false);
                    //initYishangchuan(view);


                } else if (position == 1) {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.fr_simple_card, null, false);
                    //initWeishangchaun(view);
                }
                container.addView(view);
                return view;
            }
        });





        mTabLayout_3.setTabData(mTitles_3);
        mTabLayout_3.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mTabLayout_3.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(1);
    }

//    private class MyPagerAdapter extends FragmentPagerAdapter {
//        public MyPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public int getCount() {
//            return 2;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            System.out.println("当前fragment的TItle："+ mTitles_3[position]);
//
//            return mTitles_3[position];
//
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            System.out.println("当前fragment："+mFragments.get(position));
//            return mFragments.get(position);
//
//        }
//
//
//    }


    public File[] getUploadFiles(){
        File[] files = null;
        try {
            String path = Environment.getExternalStorageDirectory() + "/CRRC";
            File file = new File(path);

            if (!file.exists()) {
                file.mkdir();
            }
            path = Environment.getExternalStorageDirectory() + "/CRRC/UPLOAD";

            file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            String filename = "";
            jsonArrayup = new JSONArray();
            files = file.listFiles();

            return files;
        } catch (Exception e) {

        }
        return files;
    }

    public void removedata(JSONArray jsonArray){
        if( jsonArray.length() > 0 ){
            jsonArray.remove( 0 );
            removedata(jsonArray);
        }
    }

    //4   init设置首页的UI组件，以及逻辑代码，...
    public void initSet(final View view) {


        sharedPreferencesSet = getSharedPreferences("data", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferencesSet.edit();

//wifi下自动上传
        com.suke.widget.SwitchButton switchButtonwifi = (com.suke.widget.SwitchButton)
                view.findViewById(R.id.switch_buttonWifi);
        boolean networkstatecome = sharedPreferencesSet.getBoolean("wifi", false);
        switchButtonwifi.setChecked(networkstatecome);//初始化的值是开启还是关闭
        switchButtonwifi.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (!isChecked) {
                    editor.putBoolean("wifi", false);
                } else {
                    editor.putBoolean("wifi", true);
                }
                editor.commit();
            }
        });

//4g下是否继续上传
        com.suke.widget.SwitchButton switchButton4G = (com.suke.widget.SwitchButton)
                view.findViewById(R.id.switch_button4G);
        boolean networkstatecome4G = sharedPreferencesSet.getBoolean("network", false);
        switchButtonwifi.setChecked(networkstatecome4G);//初始化的值是开启还是关闭
        switchButtonwifi.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (!isChecked) {
                    editor.putBoolean("network", false);
                } else {
                    editor.putBoolean("network", true);
                }
                editor.commit();
            }
        });


        view.findViewById(R.id.toFileView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, FileView.class));
            }
        });
        NiceSpinner ns = (NiceSpinner) view.findViewById(R.id.edit_spinnerDelete);
        List<String> ncdataList = new ArrayList<>();
        ncdataList.add("请选择");
        ncdataList.add("上传完删除");
        ncdataList.add("保留一天删除");
        ncdataList.add("保留两天删除");
        ncdataList.add("保留三天删除");
        ncdataList.add("保留一周删除");

        SharedPreferences preupload = HomeActivity.this.getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editorupload = preupload.edit();
        int selectNumber = preupload.getInt("uploadinfo", 4);

        List<String> ncdata = new ArrayList<>(ncdataList);
        ns.attachDataSource(ncdata);
        ns.setSelectedIndex(selectNumber);

        ns.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                System.out.println( position );
                SharedPreferences preupload = HomeActivity.this.getSharedPreferences("data", MODE_PRIVATE);
                SharedPreferences.Editor editorupload = preupload.edit();

                preupload.edit().putInt("uploadinfo", position).commit();

                System.out.println( preupload.getInt("uploadinfo", 4) );

            }
        });

    }

    public void uploadFile(Context context) {

        pdialogUp = new ProgressDialog(context);
        pdialogUp.setTitle("文件正在上传");
        pdialogUp.setMessage("敬请等待...");
        pdialogUp.setProgressDrawable(getResources().getDrawable(R.drawable.myprogressbarstyle));
        pdialogUp.setCancelable(false);
        pdialogUp.setMax(100);
        pdialogUp.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pdialogUp.setIndeterminate(false);
        pdialogUp.setProgress(0);
        pdialogUp.show();

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        MediaType MutilPart_Form_Data = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");

        arrs.clear();

        for (int i = 0; i < jsonArrayup.length(); i++) {
            try {
                System.out.println(jsonArrayup.getJSONObject(i).getString("name"));
                System.out.println(jsonArrayup.getJSONObject(i).getString("check"));
                System.out.println(jsonArrayup.getJSONObject(i).getString("file"));
                if (jsonArrayup.getJSONObject(i).getBoolean("check")) {
                    File fileOne = new File(jsonArrayup.getJSONObject(i).getString("file")); //生成文件
                    arrs.add(jsonArrayup.getJSONObject(i).getString("name"));
                    removeUoloads.add( jsonArrayup.getJSONObject(i).getString("file") );
                    requestBodyBuilder.addFormDataPart("file", jsonArrayup.getJSONObject(i).getString("file"), RequestBody.create(MutilPart_Form_Data, fileOne));
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
                System.out.println(current + "----" + total);
                pdialogUp.setProgress((int) Math.ceil(current * 100 / total));
                if (current == total) {
                    pdialogUp.dismiss();
                }
            }
        });


        RequestBody requestBody = requestBodyBuilder.build();

        System.out.println(8888);
        //String url = "https://www.baidu.com";
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder().connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS);

        System.out.println(url);
        SharedPreferences preupload = HomeActivity.this.getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editorupload = preupload.edit();
        final int selectNumber = preupload.getInt("uploadinfo", 4);

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

                    for(int i=0;i<arrs.size();i++){
                        Cursor cursor = sqLiteDatabase.query("upload", null, "name='"+ arrs.get(i) +"'", null, null, null, null);
                        System.out.println( cursor.getCount() + "------" );
                        System.out.println( arrs.get(i) );
                        if( cursor.getCount() == 0 ){
                            sqLiteDatabase.execSQL("insert into upload(name) values('"+ arrs.get(i)+"')");
                        }
                    }

                    if( selectNumber == 1 ){
                        for( int i = 0; i < removeUoloads.size(); i++ ){
                            DeleteFolder( removeUoloads.get(i) );
                        }
                    }



                    Message message = new Message();
                    message.what = COMPLETED;
                    handler.sendMessage(message);
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

    /**
     * 获取当前连接WIFI的SSID
     */
    public String getSSID() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm != null) {
            WifiInfo winfo = wm.getConnectionInfo();
            if (winfo != null) {
                String s = winfo.getSSID();
                if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                    return s.substring(1, s.length() - 1);
                }
            }
        }
        return "";
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

    private String getJson() {
        InputStream is = null;
        String result = "";
        String xkl = "";

        try {
            is = getAssets().open("tegs.txt");
            int lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            result = new String(buffer, "utf8");
            String keys = "ijijkjkjkjlkklok";
            try {
                xkl = AESCrypt.decrypt(keys, result);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return xkl;
    }


    public static void saveFile(String str, String fileName) {
        // 创建String对象保存文件名路径
        try {
            // 创建指定路径的文件 fileName取名可为fileName.txt
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            // 如果文件不存在
            if (file.exists()) {
                // 创建新的空文件
                file.delete();
            }
            file.createNewFile();
            // 获取文件的输出流对象
            FileOutputStream outStream = new FileOutputStream(file);
            // 获取字符串对象的byte数组并写入文件流
            outStream.write(str.getBytes());
            // 最后关闭文件输出流
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


//    NiceSpinner

    private void spinnerMethod() {
        //车型Spinner
        List<String> jutichexingList = new ArrayList<>();
        List<String> chexianghaoList = new ArrayList<>();
        List<String> shebeiList = new ArrayList<>();
        List<String> cmdList = new ArrayList<>();
//        chexingList.clear();
//        chexingList.add("请选择");
//        chexingList.add("机车");
//        chexingList.add("动车");
//        chexingList.add("城轨");

        jutichexingList.add("请选择");

        chexianghaoList.add("请选择");

        shebeiList.add("请选择");

        cmdList.add("请选择");


//        List<String> dataset = new ArrayList<>(chexingList);
        final List<String> datasetjutichexingList = new ArrayList<>(jutichexingList);
        final List<String> datasetchexianghaoList = new ArrayList<>(chexianghaoList);
        final List<String> datasetshebeiList = new ArrayList<>(shebeiList);
        final List<String> datasetcmdList = new ArrayList<>(cmdList);

//        edit_spinnerCheXing.attachDataSource(dataset);
        edit_spinnerjuTiCheXing.attachDataSource(datasetjutichexingList);
        edit_spinnerCheXingCheHao.attachDataSource(datasetchexianghaoList);
        edit_spinnerCheXingCheHaoSheBei.attachDataSource(datasetshebeiList);
        edit_spinnerCheXingCheHaoCMD.attachDataSource(datasetcmdList);

        //车型列表点击时
        edit_spinnerCheXing.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override

            //选中车型时

            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                cb_remember.setChecked(false);
                //当选择车型为"请选择"时，此时下面的列表应设为请选择。
                if (item.equals("请选择")) {
                    //（多个）
                    //具体车型
                    datasetjutichexingList.clear();
                    datasetjutichexingList.add("请选择");
                    edit_spinnerjuTiCheXing.setText("请选择");
                    edit_spinnerjuTiCheXing.attachDataSource(datasetjutichexingList);//刷新数据列表

                    //车厢号
                    datasetchexianghaoList.clear();
                    datasetchexianghaoList.add("请选择");
                    edit_spinnerCheXingCheHao.setText("请选择");
                    edit_spinnerCheXingCheHao.attachDataSource(datasetchexianghaoList);//刷新数据列表

                    //设备
                    datasetshebeiList.clear();
                    datasetshebeiList.add("请选择");
                    edit_spinnerCheXingCheHaoSheBei.setText("请选择");
                    edit_spinnerCheXingCheHaoSheBei.attachDataSource(datasetshebeiList);//刷新数据列表

                    //cmd
                    fl_cmd.setVisibility(View.GONE);

                    datasetcmdList.clear();
                    datasetcmdList.add("请选择");
                    edit_spinnerCheXingCheHaoCMD.setText(" ");
                    edit_spinnerCheXingCheHaoCMD.attachDataSource(datasetcmdList);//刷新数据列表

                } else {

                    //选中车型时，应加载具体车型的数据

                    chexingValue = item;
                    //下面两行代码清除作用
                    datasetjutichexingList.clear();
                    datasetjutichexingList.add("请选择");

                    jutichexing();//获取数据
                    datasetjutichexingList.addAll(datajutiCheXing);
                    edit_spinnerjuTiCheXing.attachDataSource(datasetjutichexingList);//刷新数据列表


                    //当重新选定动车或机车或城轨时，车厢号，设备 ，cmd ，也应为 请选择  （多个）

                    //车厢号
                    datasetchexianghaoList.clear();
                    datasetchexianghaoList.add("请选择");
                    edit_spinnerCheXingCheHao.setText("请选择");
                    edit_spinnerCheXingCheHao.attachDataSource(datasetchexianghaoList);//刷新数据列表

                    //设备
                    datasetshebeiList.clear();
                    datasetshebeiList.add("请选择");
                    edit_spinnerCheXingCheHaoSheBei.setText("请选择");
                    edit_spinnerCheXingCheHaoSheBei.attachDataSource(datasetshebeiList);//刷新数据列表

                    //cmd
                    fl_cmd.setVisibility(View.GONE);
                    datasetcmdList.clear();
                    datasetcmdList.add("请选择");
                    edit_spinnerCheXingCheHaoCMD.setText(" ");
                    edit_spinnerCheXingCheHaoCMD.attachDataSource(datasetcmdList);//刷新数据列表

                }
            }
        });


        //具体车型列表点击时
        edit_spinnerjuTiCheXing.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override

            //选中具体车型时

            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                cb_remember.setChecked(false);
                String item = (String) parent.getItemAtPosition(position);
                //当选择具体车型为"请选择"时，此时下面的列表应设为请选择。
                if (item.equals("请选择")) {
                    //车厢号  （多个）
                    datasetchexianghaoList.clear();
                    datasetchexianghaoList.add("请选择");
                    edit_spinnerCheXingCheHao.setText("请选择");
                    edit_spinnerCheXingCheHao.attachDataSource(datasetchexianghaoList);//刷新数据列表

                    //设备
                    datasetshebeiList.clear();
                    datasetshebeiList.add("请选择");
                    edit_spinnerCheXingCheHaoSheBei.setText("请选择");
                    edit_spinnerCheXingCheHaoSheBei.attachDataSource(datasetshebeiList);//刷新数据列表

                    //cmd
                    fl_cmd.setVisibility(View.GONE);
                    datasetcmdList.clear();
                    datasetcmdList.add("请选择");
                    edit_spinnerCheXingCheHaoCMD.setText(" ");
                    edit_spinnerCheXingCheHaoCMD.attachDataSource(datasetcmdList);//刷新数据列表

                } else {
                    //选中具体车型时，应加载车厢号的数据

                    jutiChexingValue = item;
                    //下面两行代码清除作用
                    datasetchexianghaoList.clear();
                    datasetchexianghaoList.add("请选择");

                    jutichexingchehao();//获取数据
                    datasetchexianghaoList.addAll(dataCheHao);
                    edit_spinnerCheXingCheHao.attachDataSource(datasetchexianghaoList);//刷新数据列表


                    //设备
                    datasetshebeiList.clear();
                    datasetshebeiList.add("请选择");
                    edit_spinnerCheXingCheHaoSheBei.setText("请选择");
                    edit_spinnerCheXingCheHaoSheBei.attachDataSource(datasetshebeiList);//刷新数据列表

                    //cmd
                    fl_cmd.setVisibility(View.GONE);
                    datasetcmdList.clear();
                    datasetcmdList.add("请选择");
                    edit_spinnerCheXingCheHaoCMD.setText(" ");
                    edit_spinnerCheXingCheHaoCMD.attachDataSource(datasetcmdList);//刷新数据列表
                }
            }
        });


        //车厢号列表点击时
        edit_spinnerCheXingCheHao.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override

            //选中具体车型时

            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                cb_remember.setChecked(false);
                String item = (String) parent.getItemAtPosition(position);
                //当选择具体车型为"请选择"时，此时下面的列表应设为请选择。
                if (item.equals("请选择")) {
                    //设备  （多个）
                    datasetshebeiList.clear();
                    datasetshebeiList.add("请选择");
                    edit_spinnerCheXingCheHaoSheBei.setText("请选择");
                    edit_spinnerCheXingCheHaoSheBei.attachDataSource(datasetshebeiList);//刷新数据列表

                    //cmd
                    fl_cmd.setVisibility(View.GONE);
                    datasetcmdList.clear();
                    datasetcmdList.add("请选择");
                    edit_spinnerCheXingCheHaoCMD.setText(" ");
                    edit_spinnerCheXingCheHaoCMD.attachDataSource(datasetcmdList);//刷新数据列表

                } else {
                    //选中车厢号时，应加载对应设备的数据
                    if (item == "NA") {
                        chexianghaoValue = " ";

                    } else {
                        chexianghaoValue = item;
                    }
                    //下面两行代码清除作用
                    datasetshebeiList.clear();
                    datasetshebeiList.add("请选择");

                    jutichexingchehaoshebei();//获取数据
                    datasetshebeiList.addAll(dataShebei);
                    edit_spinnerCheXingCheHaoSheBei.attachDataSource(datasetshebeiList);//刷新数据列表

                    //cmd
                    fl_cmd.setVisibility(View.GONE);
                    datasetcmdList.clear();
                    datasetcmdList.add("请选择");
                    edit_spinnerCheXingCheHaoCMD.setText(" ");
                    edit_spinnerCheXingCheHaoCMD.attachDataSource(datasetcmdList);//刷新数据列表
                }
            }
        });


        //设备列表点击时
        edit_spinnerCheXingCheHaoSheBei.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override

            //选中设备时

            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                cb_remember.setChecked(false);
                String item = (String) parent.getItemAtPosition(position);

                List<String> cmdList = GetInfoForMultiList.jiDongChengCheXingCheHaoSheBeiCMD(jsonRe, JsonRootBean15.class,
                        chexingValue, jutiChexingValue, chexianghaoValue, item);
                if (cmdList.size() > 0) {
                    fl_cmd.setVisibility(View.VISIBLE);
                    jutichexingchehaoshebeicmd(cmdList);

                    //当选择 设备 为"请选择"时，此时下面的cmd列表应设为请选择。
                    if (item.equals("请选择")) {
                        //cmd
                        datasetcmdList.clear();
                        datasetcmdList.add("请选择");
                        edit_spinnerCheXingCheHaoCMD.setText("请选择");
                        edit_spinnerCheXingCheHaoCMD.attachDataSource(datasetcmdList);//刷新数据列表


                    } else {
                        //选中设备时，应加载对应cmd的数据
                        shebeiValue = item;
                        //下面两行代码清除作用
                        datasetcmdList.clear();
                        datasetcmdList.add("请选择");

                        datasetcmdList.addAll(cmdList);
                        edit_spinnerCheXingCheHaoCMD.attachDataSource(datasetcmdList);//刷新数据列表
                    }


                } else {
                    fl_cmd.setVisibility(View.GONE);
                    cmdList.clear();
                    datasetcmdList.clear();
                    edit_spinnerCheXingCheHaoCMD.setText(" ");
                }


            }
        });


    }


    private void jutichexing() {
        List<String> jutichexing = GetInfoForMultiList.jiDongChengCheXing(jsonRe, JsonRootBean15.class, chexingValue);

        datajutiCheXing.clear();

        for (int i = 0; i < jutichexing.size(); i++) {
            datajutiCheXing.add(jutichexing.get(i));
        }

    }


    private void jutichexingchehao() {
        List<String> jutichexingchehao = GetInfoForMultiList.jiDongChengCheXingCheHao(jsonRe, JsonRootBean15.class, chexingValue, jutiChexingValue);

        dataCheHao.clear();

        for (int i = 0; i < jutichexingchehao.size(); i++) {
            if (jutichexingchehao.get(i).equals(" ")) {//没有车号
                dataCheHao.add("NA");
                break;//遇到na，只执行一次就可
            } else {
                dataCheHao.add(jutichexingchehao.get(i));
            }
        }
    }


    private void jutichexingchehaoshebei() {
        List<String> jutichexingchehaoshebei = GetInfoForMultiList.jiDongChengCheXingCheHaoSheBei(jsonRe, JsonRootBean15.class,
                chexingValue, jutiChexingValue, chexianghaoValue);

        dataShebei.clear();

        for (int i = 0; i < jutichexingchehaoshebei.size(); i++) {
            dataShebei.add(jutichexingchehaoshebei.get(i));
        }

    }


    private void jutichexingchehaoshebeicmd(List<String> dataCMDs) {

        SharedPreferences pref = HomeActivity.this.getSharedPreferences("myInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (dataCMDs.size() > 0) {
            editor.putString("visible", "VISI");
            editor.commit();
        } else {
            cmdValue = " ";
            editor.putString("visible", "NOVISI");
            editor.commit();
        }

    }


    private void initSwich(View view) {
        cb_remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//记住信息
                    //先检查信息是否填写完毕完整
                    String checing = edit_spinnerCheXing.getText().toString().trim();
                    String jutichecing = edit_spinnerjuTiCheXing.getText().toString().trim();
                    String chexianghao = edit_spinnerCheXingCheHao.getText().toString().trim();
                    String shebei = edit_spinnerCheXingCheHaoSheBei.getText().toString().trim();
                    String cmd = edit_spinnerCheXingCheHaoCMD.getText().toString();
                    String chehao = editext_chehao.getText().toString().trim();
                    String peishu = editext_peishu.getText().toString().trim();
                    //记住信息后，退出，再次点击取消记住，再点击记住信息，


//                    cmd分两种情况 没有cmd选项时，就不要加入判空，有cmd选项，参与判空。

                    if (cmd.length() > 0 && fl_cmd.getVisibility() == View.VISIBLE) {
                        System.out.println("进来1");
                        if (checing.length() != 0
                                && jutichecing.length() != 0
                                && chexianghao.length() != 0
                                && shebei.length() != 0
                                && cmd.length() != 0
                                && chehao.length() != 0
                                && peishu.length() != 0) {

                            SharedPreferences pref = HomeActivity.this.getSharedPreferences("myInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("chexing", checing);
                            editor.putString("jutichecing", jutichecing);
                            editor.putString("chexianghao", chexianghao);
                            editor.putString("shebei", shebei);
                            editor.putString("cmd", cmd);
                            editor.putString("chehao", chehao);
                            editor.putString("peishu", peishu);
                            editor.putBoolean("remember", true);//是否记住信息标志
                            editor.commit();
                        } else//信息填写不完整 则弹出提示框
                        {
                            DialogSettings.style = STYLE_IOS;
                            DialogSettings.use_blur = true;
                            DialogSettings.blur_alpha = 200;
                            MessageDialog.show(HomeActivity.this,
                                    "提示", "信息填写不完整", "知道了", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            cb_remember.setChecked(false);
                        }
                    } else {
                        System.out.println("进来2");
                        if (checing.length() != 0
                                && jutichecing.length() != 0
                                && chexianghao.length() != 0
                                && shebei.length() != 0
                                && chehao.length() != 0
                                && peishu.length() != 0) {

                            SharedPreferences pref = HomeActivity.this.getSharedPreferences("myInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("chexing", checing);
                            editor.putString("jutichecing", jutichecing);
                            editor.putString("chexianghao", chexianghao);
                            editor.putString("shebei", shebei);
                            editor.putString("cmd", " ");
                            editor.putString("chehao", chehao);
                            editor.putString("peishu", peishu);
                            editor.putBoolean("remember", true);//是否记住信息标志
                            editor.commit();
                        } else//信息填写不完整 则弹出提示框
                        {
                            DialogSettings.style = STYLE_IOS;
                            DialogSettings.use_blur = true;
                            DialogSettings.blur_alpha = 200;
                            MessageDialog.show(HomeActivity.this,
                                    "提示", "信息填写不完整", "知道了", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            cb_remember.setChecked(false);
                        }
                    }

                    System.out.println("保存的信息：" + checing + "，" + chexianghao + "，" + shebei + "，" + cmd + "，"
                            + chehao + "，" + peishu);
                } else {
                    SharedPreferences pref = HomeActivity.this.getSharedPreferences("myInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("remember", false);//是否记住信息标志

                    editor.putString("chexing", "");
                    editor.putString("jutichecing", "");
                    editor.putString("chexianghao", "");
                    editor.putString("shebei", "");
                    editor.putString("cmd", "");
                    editor.putString("chehao", "");
                    editor.putString("peishu", "");
                    editor.commit();

//                    edit_spinnerjuTiCheXing.getLtes_editText().setText("");
//                    edit_spinnerCheXing.getLtes_editText().setText("");
//                    edit_spinnerCheXingCheHao.getLtes_editText().setText("");
//                    edit_spinnerCheXingCheHaoSheBei.getLtes_editText().setText("");
//                    edit_spinnerCheXingCheHaoCMD.getLtes_editText().setText("");
                }
            }
        });


        //点击确定时，获取...
        view.findViewById(R.id.vercodehome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //判断wifi是不是连接了设备
                boolean wfstate = isWifi(HomeActivity.this);
                System.out.println( wfstate + "------------------" );

                String wifiresult = getSSID();
                System.out.println( wifiresult + "------------------" );
                if( wifiresult.indexOf( "SHGZ" ) < 0 ){
                    DialogSettings.style = STYLE_IOS;
                    DialogSettings.use_blur = true;
                    DialogSettings.blur_alpha = 200;
                    SelectDialog.show(HomeActivity.this, "WIFI设置", "未检测到连接设备，去设置WIFI", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent();
                            i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(i);
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    return;
                }


                //从json数据中获取用户名 密码 ip
                if (edit_spinnerCheXingCheHaoCMD.getText().toString().length() > 0 && fl_cmd.getVisibility() == View.VISIBLE) {
                    cmdValue = edit_spinnerCheXingCheHaoCMD.getText().toString();
                } else {
                    cmdValue = " ";
                }


                //分两种情况  1。记住信息 情况下：chexingValue这些值是没有的，需要重新赋值（赋记住的值）  2。现场勾选的信息，然后点击确定按钮
                //为了兼顾两种情况，则把chexingValue等的值全部重新赋值，
                chexingValue = edit_spinnerCheXing.getText().toString();
                jutiChexingValue = edit_spinnerjuTiCheXing.getText().toString();

                //车厢号要考虑NA情况
                if (edit_spinnerCheXingCheHao.getText().toString().equals("NA")) {
                    chexianghaoValue = " ";
                } else {
                    chexianghaoValue = edit_spinnerCheXingCheHao.getText().toString();
                    System.out.println("带测试" + chexianghaoValue);
                }
                shebeiValue = edit_spinnerCheXingCheHaoSheBei.getText().toString();

                List<String> jutichexingchehaoshebeicmdpswIp =
                        GetInfoForMultiList.jiDongChengCheXingCheHaoSheBeiCMDipUserPsw(
                                jsonRe, JsonRootBean15.class,
                                chexingValue, jutiChexingValue, chexianghaoValue, shebeiValue, cmdValue);
                System.out.println("值：" + chexianghaoValue);
                dataIpPswUser.clear();
                dataIpPswUser.addAll(jutichexingchehaoshebeicmdpswIp);

                Toast.makeText(HomeActivity.this, dataIpPswUser.toString(), Toast.LENGTH_LONG).show();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                //获取当前时间
                Date date = new Date(System.currentTimeMillis());
                String timeva = simpleDateFormat.format(date);
                System.out.println("当前时间：" + timeva);
                //生成一级和二级文件夹的名字 保存在sharepreference
                boolean flagNum = false;
                if (chexianghaoValue == " ") {
                    chexianghaoValue = "NA";
                } else {
                    if (chexianghaoValue.equals("") || chexianghaoValue.equals("A") || chexianghaoValue.equals("B")
                            || chexianghaoValue.equals("C")) {
                    } else {//chexianghaoValue=1,2,3,4,5.....
                        if (Integer.parseInt(chexianghaoValue) < 10) {
                            chexianghaoValue = "0" + chexianghaoValue;
                            flagNum = true;
                        }
                    }
                }

                String dirname1 = "";
                SharedPreferences sharedPreferences = getSharedPreferences("filedirname",
                        Activity.MODE_PRIVATE);
                String timeHis = sharedPreferences.getString("historyTime", "");
                String jutichexingHis = sharedPreferences.getString("historyjutichexing", "");
                String chehaoHis = sharedPreferences.getString("historychehao", "");
                String historychexianghao = sharedPreferences.getString("historychexianghao", "");
                String historypeishu = sharedPreferences.getString("historypeishu", "");

                String chexinaghao = "";
                if (flagNum) {
                    chexinaghao = "0" + edit_spinnerCheXingCheHao.getText().toString().trim();
                } else {
                    chexinaghao = edit_spinnerCheXingCheHao.getText().toString().trim();
                }
                boolean histime = false;

                if (jutichexingHis.equals(edit_spinnerjuTiCheXing.getText().toString().trim())
                        && chehaoHis.equals(editext_chehao.getText().toString().trim())
                        && historychexianghao.equals(chexinaghao)
                        && historypeishu.equals(editext_peishu.getText().toString().trim())) {
                    //车型车号车相号，配属一样，则接下来判断是否存的有时间
                    if (timeHis.length() > 5) {//存的有时间
                        //用历史的时间
                        histime = true;
                        dirname1 = chexingValue + "_" + jutiChexingValue + "_" + timeHis + "_" + chexinaghao + "_" + editext_peishu.getText().toString().trim();
                    } else {
                        //直接用最新的时间
                        dirname1 = chexingValue + "_" + jutiChexingValue + "_" + timeva + "_" + chexianghaoValue + "_" + editext_peishu.getText().toString().trim();
                    }
                } else {
                    //车型 或 车号等 有一个不一样，则新建文件夹，且时间也是最新的
                    dirname1 = chexingValue + "_" + jutiChexingValue + "_" + timeva + "_" + chexianghaoValue + "_" + editext_peishu.getText().toString().trim();
                }


                //先检查信息是否填写完毕完整
                String chehao = editext_chehao.getText().toString().trim();
                String peishu = editext_peishu.getText().toString().trim();
                if (chehao.length() != 0
                        && peishu.length() != 0 && dataIpPswUser.toString().length() > 7) {

                    String chehaoNow = editext_chehao.getText().toString().trim();
                    SharedPreferences pref = HomeActivity.this.getSharedPreferences("filedirname", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("filedirnamevalue", dirname1);//一级
                    editor.putString("shebeinamevalue", shebeiValue);//二级
                    if (!histime) {//bu是用的历史时间
                        System.out.println("布尔测试");
                        editor.putString("historyTime", timeva);//历史时间
                    }
                    editor.putString("historyjutichexing", jutiChexingValue);//上次具体车型
                    editor.putString("historychehao", chehaoNow);//上次车号
                    editor.putString("historychexianghao", chexianghaoValue);//上次车箱号
                    editor.putString("historypeishu", peishu);//上次配属用户
                    editor.commit();


                    String can[] = new String[4];
                    can[0] = _host;
                    can[1] = _port;
                    can[2] = _user;
                    can[3] = _pass;
                    LogTask task = new LogTask(HomeActivity.this);
                    task.execute(can);

                } else {
                    DialogSettings.style = STYLE_IOS;
                    DialogSettings.use_blur = true;
                    DialogSettings.blur_alpha = 200;
                    MessageDialog.show(HomeActivity.this,
                            "无法登陆", "请检查是否连接无线工装WIFI以及信息填写是否完整无误", "知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                }


            }
        });


    }


    private void registerPermission() {
        //动态获取定位权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);

        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
        }

        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toPic();
            } else {
//                Toast.makeText(this, "权限拒绝", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    //处理登录的异步函数
    class LogTask extends AsyncTask<String, Void, Boolean> {
        Context mContext;

        public LogTask(Context ctx) {
            mContext = ctx;
        }

        protected Boolean doInBackground(String... Params) {

            if (haveCheck) {
                return checkLogin(8001);

            } else {
                return nocheckLogin();
            }

        }

        protected void onPostExecute(Boolean flag) {
            if (flag) {
//                Toast tot = Toast.makeText(
//                        mContext,
//                        "登录成功",
//                        Toast.LENGTH_LONG);
//                tot.show();
                Intent intent = new Intent(HomeActivity.this, selectFileActivity.class);
                //准备进入选择界面并且准备好参数
                intent.putExtra("host", _host);
                intent.putExtra("user", _user);
                intent.putExtra("pass", _pass);
                intent.putExtra("port", Integer.parseInt(_port));
                startActivity(intent);
            } else {
                new android.app.AlertDialog.Builder(mContext)
                        .setTitle("无法连接")
                        .setMessage("请检查是否连接无线工装WIFI以及信息填写是否无误！")
                        .setPositiveButton("确定", null)
                        .show();

            }
        }


    }


    //校验 只有edrm模块需要校验  其他模块直接登陆即可。
    public boolean nocheckLogin() {
        String host1 = host.getText().toString();
        String user1 = name.getText().toString();
        String pass1 = pass.getText().toString();
        int port1 = Integer.parseInt(port.getText().toString());
        boolean flag = false;
        FTPManager manager = new FTPManager();
        try {
            flag = manager.connect(host1, port1, user1, pass1);
            if (!flag) return flag;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //最后不管是否连接上了 都关闭一下
            try {
                manager.closeFTP();
            } catch (Exception e1) {

            }
            return flag;
        }

    }


    //校验 只有edrm模块需要校验  其他模块直接登陆即可。
    public boolean checkLogin(int socketPort) {

        String host1 = _host;
        String user1 = _user;
        String pass1 = _pass;
        int port1 = Integer.parseInt(_port);

        boolean flag = false;
        FTPManager manager = new FTPManager();
        try {
            // 创建Socket对象 & 指定服务端的IP 及 端口号
//            socket = new Socket("10.0.1.5", 8001);
            socket = new Socket(host1, socketPort);

            // 判断客户端和服务器是否连接成功
            System.out.println("连接" + socket.isConnected());
            if (socket.isConnected()) {
                // 步骤1：从Socket 获得输出流对象OutputStream
                // 该对象作用：发送数据
                outputStream = socket.getOutputStream();

                // 步骤2：写入需要发送的数据到输出流对象中
                outputStream.write(("Request").getBytes("US-ASCII"));
                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞

                // 步骤3：发送数据到服务端
                outputStream.flush();
                System.out.println("发request:" + Arrays.toString(("Request").getBytes("US-ASCII")));

                System.out.println("发");

//                                接收

                // 步骤1：创建输入流对象InputStream
                is = socket.getInputStream();

                // 步骤2：创建输入流读取器对象 并传入输入流对象
                // 该对象作用：获取服务器返回的数据
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);

//                                List<char> list = new ArrayList<>();
                char[] ch = new char[40];
                String strs = "";//Response
                String strs1 = "";//Response后的字符

                for (int i = 1; i < 41; i++) {
                    ch[i - 1] = (char) br.read();
                    if (i < 9) {
                        strs += ch[i - 1];

                    }
                    if (i > 8) {
                        strs1 += ch[i - 1];

                    }
                    System.out.println("接收br第" + i + ":" + ch[i - 1]);
                }


                System.out.println("数组：" + Arrays.toString(ch));
                System.out.println("str前：" + strs);
                System.out.println("str后：" + strs1);

                //////////////////////////////第二次发//////////////////////////////////////
                char[] ch2 = new char[40];
                String strs2 = "";
                if (strs.equals("Response")) {
                    System.out.println("能收到Response");

                    //对Response后的字符转换成十六进制字节数组
                    if (strs1 == null || strs1.trim().equals("")) {
                        return false;
                    }

                    byte[] bytes16 = new byte[strs1.length() / 2];
                    for (int i = 0; i < strs1.length() / 2; i++) {
                        String subStr = strs1.substring(i * 2, i * 2 + 2);
                        bytes16[i] = (byte) Integer.parseInt(subStr, 16);
                    }

                    System.out.println("bytes16[i]:" + Arrays.toString(bytes16));


                    //加密。。。。。。

                    String strTo = "5531129003a313b176c539d223da4182";
                    byte[] bytess16 = new byte[strTo.length() / 2];
                    for (int i = 0; i < strTo.length() / 2; i++) {
                        String subStr = strTo.substring(i * 2, i * 2 + 2);
                        bytess16[i] = (byte) Integer.parseInt(subStr, 16);
                    }
                    System.out.println("strToEny:" + Arrays.toString(bytess16));
                    //先根据字节数组生成 AES密钥对象
                    // 再使用 AES密钥对象 加密数据。

                    try {
                        byte[] byteEny = HomeActivity.encrypt(bytess16, bytes16);
                        System.out.println("byteEny:" + Arrays.toString(byteEny));

                        String strEny = HomeActivity.byte2hex(byteEny);
                        System.out.println("把字节数组转换成16进值字符串:" + strEny);

//                                        把字节数组转换成16进值字符串
//                                        String strEny = byteEny.toString();
                        int[] intEny = new int[byteEny.length];

                        for (int i = 0; i < byteEny.length; i++) {
                            if (byteEny[i] < 0) {
                                intEny[i] = 256 + byteEny[i];
                            } else {
                                intEny[i] = byteEny[i];

                            }
                        }
                        System.out.println("intEny:" + Arrays.toString(intEny));
                        String strjia = "";
                        for (int i = 0; i < intEny.length; i++) {
                            strjia += (char) intEny[i];
                        }


                        System.out.println("strjia:" + strjia);

                        String sendstrpsw = "Accredit" + strEny;
                        byte[] byteEnys = sendstrpsw.getBytes("US-ASCII");

                        outputStream = socket.getOutputStream();
                        outputStream.write(byteEnys);
                        outputStream.flush();


                        System.out.println("发jiami");


//                                接收
                        is = socket.getInputStream();
                        isr = new InputStreamReader(is);
                        br = new BufferedReader(isr);
                        System.out.println("收jiami");
                        String strOk = "";
                        for (int i = 1; i < 25; i++) {
                            char jiami = (char) br.read();
                            strOk += jiami;
                            System.out.println("////：" + jiami);
                        }
                        System.out.println("加密后收到数据：" + strOk);

                        if (strOk.indexOf("ResponseAccreditOK") == 0) {
                            System.out.println("接受数据正常.....");


                        }

                    } catch (Exception e) {

                    }

                    System.out.println("字节数组：" + Arrays.toString(bytes16));


                    outputStream = socket.getOutputStream();
                    outputStream.write(("WriteFaultToFile").getBytes("US-ASCII"));
                    outputStream.flush();

                    System.out.println("发WriteFaultToFile");


//                                接收
                    is = socket.getInputStream();
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    String str22 = "";
                    for (int i = 1; i < 41; i++) {
                        ch2[i - 1] = (char) br.read();
                        str22 += ch2[i - 1];

                        System.out.println("接收br第" + i + ":" + ch2[i - 1]);
                    }
                    System.out.println("WaiteForWrite打印：" + str22);
                    if (str22.indexOf("WaiteForWrite") == 0 || str22.indexOf("WaiteForWrite_e") == 0) {
                        System.out.println("str22.indexOf(\"WaiteForWrite\")==0成立.....");


                        try {
//                            flag = manager.connect("10.0.1.5", 21, "CSR", "12345678");
                            flag = manager.connect(host1, port1, user1, pass1);
                            if (!flag) return false;
                            System.out.println("我的了flag：" + flag);


                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //最后不管是否连接上了 都关闭一下
                            try {
                                manager.closeFTP();
                            } catch (Exception e1) {

                            }
                        }


                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //最后不管是否连接上了 都关闭一下
            try {
                manager.closeFTP();
            } catch (Exception e1) {

            }
            return flag;
        }
    }


    public static byte[] encrypt(byte[] plainBytes, byte[] key) throws Exception {

        // 获取 AES 密码器
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");


        int blockSize = cipher.getBlockSize();
        int length = plainBytes.length;
        //计算需填充长度
        if (length % blockSize != 0) {
            length = length + (blockSize - (length % blockSize));
        }
        byte[] plaintext = new byte[length];
        //填充
        System.arraycopy(plainBytes, 0, plaintext, 0, plainBytes.length);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");


        // 初始化密码器（加密模型）
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        // 加密数据, 返回密文
        return cipher.doFinal(plaintext);
    }


    // /** 字节数组转成16进制字符串 **/
    public static String byte2hex(byte[] b) { // 一个字节的数，
        StringBuffer sb = new StringBuffer(b.length * 2);
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            // 整数转成十六进制表示
            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase(); // 转成da写
    }


    //检查权限问题
    private void inspectPermission() {
        // 如果SDK版本大于或等于23才需要检查权限，否则直接拨弹出图库
        if (Build.VERSION.SDK_INT >= 23) {
            // 检查权限是否允许
            if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "没有权限");
                // 没有权限，考虑是否需要解释为什么需要这个权限
                /*申请权限的解释，该方法在用户上一次已经拒绝过你的这个权限申请时使用。
                 * 也就是说，用户已经拒绝一次了，你又弹个授权框，你需要给用户一个解释，为什么要授权*/
                if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Log.e("TAG", "没有权限,用户上次已经拒绝该权限，解释为什么需要这个权限");
                    // Show an expanation to the user *asynchronously* -- don't block this thread
                    //waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Toast.makeText(HomeActivity.this, "需要权限才能上传图片哦", Toast.LENGTH_SHORT).show();

                } else {
                    Log.e("TAG", "没有权限，申请权限");
                    // 申请权限
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CALL_PHONE);
                }
            } else {
                // 有权限，执行相关操作
                toPic();
            }
            //当是6.0以下版本时直接执行弹出拍照图库窗口
        } else {
            toPic();
        }
    }


    Uri photoUri;

    //去图库的方法
    private void toPic() {
        // 写一个去图库选图的Intent
//        Intent intent1 = new Intent(Intent.ACTION_PICK);
//        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                "image/*");

        // 写一个打开系统拍照程序的intent

        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                System.currentTimeMillis() + ".jpg");
        cameraPath = file.getAbsolutePath();
        photoUri = Uri.fromFile(file);

//        intent2.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);


        /**
         * 指定拍照存储路径
         * 7.0 及其以上使用FileProvider替换'file://'访问
         */
        if (Build.VERSION.SDK_INT >= 24) {
            //这里的BuildConfig，需要是程序包下BuildConfig。
            intent2.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file));
            intent2.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent2.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        startActivityForResult(intent2, 100);
    }

    //拿到图片的路径
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        promptDialog.showLoading("加载中...", false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                promptDialog.dismiss();
                haveDismiss = true;
            }
        }, 5000);

        if (resultCode == HomeActivity.this.RESULT_OK && requestCode == 100) {
            // 作为头像的图片在设备上的本地路径是什么（/sdcard/XXX/XXXX.jpg）
            String filePath = "";
            if (data != null) {
                // 图片是用户从图库选择得到的
                // uri代表用户选取的图片在MediaStroe中存储的位置
                Uri uri = data.getData();
                try {
                    Bitmap bm = getBitmapFormUri(HomeActivity.this, uri);
                    if (bm != null) {
                        String img = imgToBase64(filePath, bm, "256");
                        Log.i("hxl", "img========================================" + img);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 利用ContentResolver查找uri所对应图片在设备上的真实路径
                Cursor cursor = HomeActivity.this.getContentResolver().query(uri,
                        new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                cursor.moveToNext();
                filePath = cursor.getString(0);
                Log.i("hxl", "filePath========" + filePath);
                imgFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                Log.i("hxl", "imgFileName========" + imgFileName);
                cursor.close();
            } else {
                // 图片是用户拍照得到的
                Uri uriImageData;
                Bitmap bitmap = null;
                //判断data是否是null，为空的话就用备用uri代替
                Uri uri = null;
                if (data != null && data.getData() != null) {
                    uri = data.getData();
                }
                if (uri == null) {
                    if (photoUri != null) {
                        uri = photoUri;

                    }
                }
                Log.i("hxl", "uri========================================" + uri);
                if (uri != null) {
                    Log.i("hxl", "uri========================================" + uri);
                    try {
                        // 利用ContentResolver查找uri所对应图片在设备上的真实路径1111

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //5.1可用
                Log.i("hxl", "cameraPath========================================" + cameraPath);
                if (cameraPath != null) {
                    imgFileName = cameraPath.substring(cameraPath.lastIndexOf("/") + 1);
                    Log.i("hxl", "imgFileName========" + imgFileName);
                    try {


                        bitmap = getBitmapFormUri(HomeActivity.this, uri);
                        System.out.println("大小2：" + bitmap.getAllocationByteCount());
//                       ;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (bitmap != null) {


                        img64 = imgToBase64(filePath, bitmap, "256");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                              token = AuthService.getAuth();
                                getToken = true;
                            }
                        }).start();
                        System.out.println("token值：" + token);
                        System.out.println("大小：" + img64.getBytes().length);
                        RequestBody formBody = new FormBody.Builder()
                                .add("image", img64)
                                .build();

                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("https://aip.baidubce.com/rest/2.0/ocr/v1/numbers?access_token=" + token)
                                .addHeader("content-type", "application/x-www-form-urlencoded")
                                // .post(RequestBody.create(MEDIA_TYPE_TEXT, postBody))
                                .post(formBody)
                                // 表单提交
                                .build();
                        //                                    Response response = client.newCall(request).execute();
                        if (getToken){
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogSettings.style = STYLE_IOS;
                                        DialogSettings.use_blur = true;
                                        DialogSettings.blur_alpha = 200;
                                        MessageDialog.show(HomeActivity.this,
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
                                String json = response.body().string();
                                System.out.println("OCR数据：" + json);
                                String post = JSON.parseObject(json).getString("postBody");
                                JsonRootBean jr = JSON.parseObject(json, JsonRootBean.class);
                                String ocrNum = "";
                                for (int i = 0; i < jr.getWords_result().size(); i++) {
                                    ocrNum += jr.getWords_result().get(i).getWords();
                                }
                                System.out.println("得到的识别数字：" + ocrNum);

                                final String finalOcrNum = ocrNum;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        editext_chehao.setText(finalOcrNum);
                                        if (!haveDismiss) {
                                            promptDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });}


                    }
                }
            }

        }
    }


    /**
     * @param imgPath
     * @param bitmap
     * @param imgFormat 图片格式
     * @return
     */
    public static String imgToBase64(String imgPath, Bitmap bitmap, String imgFormat) {
        if (imgPath != null && imgPath.length() > 0) {
            bitmap = readBitmap(imgPath);
        }
        Log.i("hxl", "bitmap==" + bitmap);
        if (bitmap == null) {
            Log.i("hxl", "bitmap is null----------------");
        } else {
            ByteArrayOutputStream out = null;
            try {
                out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                out.flush();
                out.close();

                byte[] imgBytes = out.toByteArray();
                return Base64.encodeToString(imgBytes, Base64.DEFAULT);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                return null;
            } finally {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    private static Bitmap readBitmap(String imgPath) {
        try {
            Log.i("hxl", "imgPath===============" + imgPath);
            return BitmapFactory.decodeFile(imgPath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }

    }


    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws
            FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        System.out.println("大小3（压缩前）：" + bitmap.getAllocationByteCount());

        return compressImage(bitmap);//再进行质量压缩
    }


    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 50;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences pref = HomeActivity.this.getSharedPreferences("tab", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("tabnum", 0);
        editor.commit();
        if (promptDialog.onBackPressed())
            super.onBackPressed();
    }
}

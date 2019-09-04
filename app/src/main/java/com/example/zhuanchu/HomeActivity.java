package com.example.zhuanchu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.example.zhuanchu.bean.javaBean.JsonRootBean;
import com.example.zhuanchu.bean.pojo15.JsonRootBean15;
import com.example.zhuanchu.service.AESCpher;
import com.example.zhuanchu.service.AuthService;
import com.example.zhuanchu.service.GetInfoForMultiList;
import com.githang.statusbar.StatusBarCompat;
import com.google.android.flexbox.FlexboxLayout;
import com.scottyab.aescrypt.AESCrypt;

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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import me.leefeng.promptlibrary.PromptDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Route(path = "/app/home")
public class HomeActivity extends AppCompatActivity {
    private List<String> dataCheXing = new ArrayList<>();
    private List<String> datajutiCheXing = new ArrayList<>();
    private List<String> dataCheHao = new ArrayList<>();
    private List<String> dataCMD = new ArrayList<>();
    private List<String> dataShebei = new ArrayList<>();
    private List<String> dataIpPswUser = new ArrayList<>();

    private String chexingValue = "";
    private String jutiChexingValue = "";
    private String chehaoValue = "";
    private String peishuValue = "";
    private String chexianghaoValue = "";
    private String cmdValue = " ";
    private String shebeiValue = "";

    private String[] beforeStr = new String[]{"", ""};
    private String[] beforeStr2 = new String[]{"", ""};
    private String[] beforeStr3 = new String[]{"", ""};
    private String[] beforeStr4 = new String[]{"", ""};
    private String[] beforeStr5 = new String[]{"", ""};

    private WifiManager wifiManager;
    private int mProgress = 0;
    private RingProgressBar roundProgressBar;
    private Socket socket;
    OutputStream outputStream;
    InputStream is;
    private String _host = "10.0.1.5";
    private String _port = "21";
    private String _user = "CSR";
    private String _pass = "12345678";
    private String cameraPath;
    private String imgFileName;
    private Boolean rememberInfo = false;

    protected Handler handler;

    // 输入流读取器对象
    InputStreamReader isr;
    BufferedReader br;
    private Handler mMainHandler;

    // 接收服务器发送过来的消息
    String response;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Button login;
    private Button cancel;
    public Switch switch1;
    public static EditText host, port, name, pass;
    public static boolean haveCheck = true;
    public ImageButton imbtnOcr;
    public ImageButton imbtnVoiceCheHao;
    public ImageButton imbtnVoicePeiShu;

    public EditText editext_chehao;
    public EditText editext_peishu;
    public NiceSpinner edit_spinnerCheXing;
    public NiceSpinner edit_spinnerjuTiCheXing;
    public NiceSpinner edit_spinnerCheXingCheHao;
    public NiceSpinner edit_spinnerCheXingCheHaoSheBei;
    public NiceSpinner edit_spinnerCheXingCheHaoCMD;
    public FlexboxLayout fl_cmd;


    public CheckBox cb_remember;
    private static final int REQUEST_CALL_PHONE = 100;
    public String img64;
    private PromptDialog promptDialog;
    private boolean haveDismiss = false;
    private String jsonRe = "";

    List<String> chexingList = new LinkedList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        imbtnOcr = (ImageButton) findViewById(R.id.imagebtnCamera);

        editext_chehao = (EditText) findViewById(R.id.editext_chehao);
        editext_peishu = (EditText) findViewById(R.id.et_peishu);
        edit_spinnerCheXing = findViewById(R.id.edit_spinnerCheXing);
        edit_spinnerjuTiCheXing = findViewById(R.id.edit_spinnerjuTiCheXing);
        edit_spinnerCheXingCheHao = findViewById(R.id.edit_spinnerCheXingCheHao);
        edit_spinnerCheXingCheHaoSheBei = findViewById(R.id.edit_spinnerCheXingCheHaoSheBei);
        edit_spinnerCheXingCheHaoCMD = findViewById(R.id.edit_spinnerCheXingCheHaoCMD);

        fl_cmd = (FlexboxLayout) findViewById(R.id.fl_cmd);
        cb_remember = (CheckBox) findViewById(R.id.remember_info);
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
//            chexingList.add("请选择");
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
//            chexingList.add("请选择");
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                jsonRe = getJson();


                //该段代码用来获取下拉列表数据并将数据进行展示。
//                List<String> jidongcheng = GetInfoForMultiList.jiDongCheng(jsonRe, JsonRootBean15.class);
//                for (int i = 0; i < jidongcheng.size(); i++) {
//                    dataCheXing.add(jidongcheng.get(i));
//                }
//                init();//下拉框

            }
        }).start();
        spinnerMethod();


        //跳转
        initSwich();


        ImageView image = findViewById(R.id.systembar).findViewById(R.id.downImg);
        image.setImageResource(R.drawable.down_c);
        TextView textView = findViewById(R.id.systembar).findViewById(R.id.downText);
        textView.setTextColor(Color.parseColor("#35ae5d"));


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        wifiManager.startScan();

        List<ScanResult> scanResults = wifiManager.getScanResults();
        String reulst = "";

//        Spinner spinner = findViewById(R.id.wifilist);

        final JSONArray jsonArray = new JSONArray();

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        JSONObject jobject = new JSONObject();
        try {
            jobject.put("ssid", "请选择设备");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonArray.put(jobject);
        dataAdapter.add("请选择设备");

        if (scanResults != null && scanResults.size() > 0) {
            for (int i = 0; i < scanResults.size(); i++) {
                ScanResult scanResult = scanResults.get(i);
                if (!scanResult.SSID.isEmpty()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("ssid", scanResult.SSID);
                        jsonObject.put("capabilities", scanResult.capabilities);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dataAdapter.add(scanResult.SSID);
                    jsonArray.put(jsonObject);
                }
            }
        }

//        spinner.setAdapter( dataAdapter );

        TextView text = findViewById(R.id.testtext);
        text.setText(reulst);


        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorfocus), true);
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


    private void initSwich() {
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
                            new android.app.AlertDialog.Builder(HomeActivity.this)
                                    .setTitle("信息填写不完整")
                                    .setMessage("请继续填写信息")
                                    .setPositiveButton("确定", null)
                                    .show();
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
                            new android.app.AlertDialog.Builder(HomeActivity.this)
                                    .setTitle("信息填写不完整")
                                    .setMessage("请继续填写信息")
                                    .setPositiveButton("确定", null)
                                    .show();
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


        findViewById(R.id.homeback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ARouter.getInstance().build("/app/auth").navigation();
                startActivity(new Intent(HomeActivity.this, AuthActivity.class));
            }
        });


        //点击确定时，获取...
        findViewById(R.id.vercodehome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                }
                shebeiValue = edit_spinnerCheXingCheHaoSheBei.getText().toString();

                List<String> jutichexingchehaoshebeicmdpswIp = GetInfoForMultiList.jiDongChengCheXingCheHaoSheBeiCMDipUserPsw(jsonRe, JsonRootBean15.class,
                        chexingValue, jutiChexingValue, chexianghaoValue, shebeiValue, cmdValue);
                System.out.println("值：" + chexianghaoValue);
                dataIpPswUser.clear();
                dataIpPswUser.addAll(jutichexingchehaoshebeicmdpswIp);

                new android.app.AlertDialog.Builder(HomeActivity.this)
                        .setTitle("ip,user,psw")
                        .setMessage(dataIpPswUser.toString())
                        .setPositiveButton("确定", null)
                        .show();


                String can[] = new String[4];
                can[0] = _host;
                can[1] = _port;
                can[2] = _user;
                can[3] = _pass;
                LogTask task = new LogTask(HomeActivity.this);
                task.execute(can);
                //ARouter.getInstance().build("/app/down").navigation();
            }
        });

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
    }

    public WifiConfiguration createWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.allowedAuthAlgorithms.clear();
        configuration.allowedGroupCiphers.clear();
        configuration.allowedKeyManagement.clear();
        configuration.allowedPairwiseCiphers.clear();
        configuration.allowedProtocols.clear();
        configuration.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.isExsits(SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        switch (Type) {
            case 1://不加密
                configuration.wepKeys[0] = "";
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                configuration.wepTxKeyIndex = 0;
                configuration.priority = 20000;
                break;
            case 2://wep加密
                configuration.hiddenSSID = true;
                configuration.wepKeys[0] = "\"" + Password + "\"";
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                break;
            case 3: //wpa加密

                configuration.preSharedKey = "\"" + Password + "\"";
                configuration.hiddenSSID = true;
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                configuration.status = WifiConfiguration.Status.ENABLED;
                break;
        }
        return configuration;
    }

    private WifiConfiguration isExsits(String SSID) {
        if (wifiManager != null) {
            List<WifiConfiguration> existingConfigs = wifiManager
                    .getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    public List<ScanResult> getWifiList() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        List<ScanResult> scanWifiList = wifiManager.getScanResults();
        List<ScanResult> wifiList = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                //Log.e(ConstantUtils.TAG, "搜索的wifi-ssid:" + scanResult.SSID);
                System.out.println(scanResult.SSID);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult);
                    }
                }
            }
            TextView textView = findViewById(R.id.testtext);
            textView.setText(scanWifiList.size() + "88888");
        } else {
            TextView textView = findViewById(R.id.testtext);
            textView.setText("没有搜索到wifi");
            //Log.e(ConstantUtils.TAG, "没有搜索到wifi");
            System.out.println("没有搜索到wifi");
        }
        return wifiList;
    }

    private void registerPermission() {
        //动态获取定位权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);

        } else {
            getWifiList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            getWifiList();
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
//                new android.app.AlertDialog.Builder(mContext)
//                        .setTitle("无法连接")
//                        .setMessage("请检查是否连接无线工装WIFI以及信息填写是否无误！")
//                        .setPositiveButton("确定", null)
//                        .show();

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

                                String token = AuthService.getAuth();


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
                                try {
                                    Response response = client.newCall(request).execute();
                                    if (response.isSuccessful()) {
                                        String json = response.body().string();
                                        System.out.println("测试OCR：" + json);
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
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        }).start();

                        Log.i("hxl", "img========================================" + img64);
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    public com.google.android.gms.appindexing.Action getIndexApiAction() {
//        com.google.android.gms.appindexing.Thing object = new com.google.android.gms.appindexing.Thing.Builder()
//                .setName("Main Page") // TODO: Define a title for the content shown.
//                // TODO: Make sure this auto-generated URL is correct.
//                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
//                .build();
//        return new com.google.android.gms.appindexing.Action.Builder(com.google.android.gms.appindexing.Action.TYPE_VIEW)
//                .setObject(object)
//                .setActionStatus(com.google.android.gms.appindexing.Action.STATUS_TYPE_COMPLETED)
//                .build();
//    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    public void onBackPressed() {
        if (promptDialog.onBackPressed())
            super.onBackPressed();
    }
}

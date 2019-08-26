package com.example.zhuanchu;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.example.zhuanchu.bean.javaBean.JsonRootBean;
import com.example.zhuanchu.service.AuthService;
import com.example.zhuanchu.service.WifiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import cn.lognteng.editspinner.lteditspinner.DataTest;
import cn.lognteng.editspinner.lteditspinner.LTEditSpinner;
import me.leefeng.promptlibrary.PromptDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Route(path = "/app/home")
public class HomeActivity extends AppCompatActivity {
    private String[] data = {"ERM", "TCU", "Ora1nge", "Watermelon",
            "Pear", "Grape", "Pineapple", "Strawberry", "Cherry", "Mango"};
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
    private static final int REQUEST_CALL_PHONE = 100;
    public String img64;
    private PromptDialog promptDialog;
    private boolean haveDismiss = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        imbtnOcr = (ImageButton) findViewById(R.id.imagebtnCamera);
        imbtnVoiceCheHao = (ImageButton) findViewById(R.id.imagebtnVoice);
        imbtnVoicePeiShu = (ImageButton) findViewById(R.id.voicePeiShu);
        editext_chehao = (EditText) findViewById(R.id.editext_chehao);
        promptDialog = new PromptDialog(this);
//        promptDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);
        imbtnOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editext_chehao.setText("");
                inspectPermission();
            }
        });
        imbtnVoicePeiShu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        init();//下拉框
        findViewById(R.id.homeback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/vercode").navigation();
            }
        });

        findViewById(R.id.vercode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


        /*
         * 点击wifi事件
         * */

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            private WifiConfiguration conf = new WifiConfiguration();
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
//                System.out.println( dataAdapter.getItem( position ) );
//                if( position > 0 ){
//                    final EditText editText = new EditText(HomeActivity.this);
//                    editText.setHint("请输入wifi密码");
//                    AlertDialog.Builder dialog = new AlertDialog.Builder( HomeActivity.this );
//                    dialog.setView(editText);
//                    dialog.setTitle( dataAdapter.getItem( position ) );
//
//                    String wifiName = null;
//                    wifiName = dataAdapter.getItem( position );
//
//                    String enc = null;
//
//                    try {
//                        enc = jsonArray.getJSONObject(position).getString("capabilities");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    final String finalWifiName = wifiName;
//                    final String finalEnc = enc;
//                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            // 1、注意热点和密码均包含引号，此处需要需要转义引号
//                            String ssid = finalWifiName;
//                            String psd = editText.getText().toString();
//
//                            System.out.println( finalEnc );
//
//                            int type = 1;
//                            if( finalEnc.indexOf( "WEP" ) >= 0 ){
//                                type = 2;
//                            }
//                            if( finalEnc.indexOf( "WPA" ) >= 0 ){
//                                type = 3;
//                            }
//
//                            System.out.println( ssid + "--------" );
//                            System.out.println( psd + "--------" );
//                            System.out.println( type + "--------" );
//
//                            WifiConfiguration wifiConfiguration = createWifiInfo(ssid, psd, type);
//
//                            int networkId = wifiManager.addNetwork(wifiConfiguration);
//                            boolean enable = wifiManager.enableNetwork( networkId, true );
//
//                            System.out.println( networkId + "--------" );
//
//                            System.out.println( enable + "--------" );
//
//                            if( enable ){
//                                Toast.makeText(getApplicationContext(), "连接"+ ssid +"成功", Toast.LENGTH_LONG).show();
//                            }else{
//                                Toast.makeText(getApplicationContext(), "连接"+ ssid +"失败", Toast.LENGTH_LONG).show();
//                            }
//
//                        }
//                    });
//                    dialog.setNegativeButton("取消", null);
//                    dialog.show();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

    }

    private void init() {
        List<DataTest> list = new ArrayList<DataTest>();
        for (int i = 0; i < data.length; i++) {
            list.add(new DataTest(i, data[i]));
        }

        LTEditSpinner<DataTest> ltEditSpinner = (LTEditSpinner) findViewById(R.id.edit_spinnerCheXing);
        ltEditSpinner.initData(list, new LTEditSpinner.OnESItemClickListener<DataTest>() {
            @Override
            public void onItemClick(DataTest item) {
                Toast.makeText(HomeActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "权限拒绝", Toast.LENGTH_SHORT).show();
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
                Toast tot = Toast.makeText(
                        mContext,
                        "登录成功",
                        Toast.LENGTH_LONG);
                tot.show();
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
        if (Build.VERSION.SDK_INT>=24){
            //这里的BuildConfig，需要是程序包下BuildConfig。
            intent2.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this.getApplicationContext(), BuildConfig.APPLICATION_ID+".provider",file));
            intent2.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }else{
            intent2.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }




        startActivityForResult(intent2, 100);
        System.out.println(
                "拍照测试2"
        );
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
                                                if (!haveDismiss){
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

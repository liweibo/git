package com.example.zhuanchu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.zhuanchu.adapter.UploadAdapter;
import com.example.zhuanchu.adapter.WifiAdapter;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Route( path = "/app/wifi" )
public class WifiActivity extends AppCompatActivity {

    private JSONArray jsonArray = new JSONArray();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi);

//        ARouter.openDebug();
//        ARouter.init(getApplication());

        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/upload").navigation();
            }
        });

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        wifiManager.startScan();

        List<ScanResult> scanResults = wifiManager.getScanResults();
        String reulst = "";

        if ( scanResults != null && scanResults.size() >0 ) {
            for(int i = 0;i<scanResults.size();i++){
                ScanResult scanResult = scanResults.get(i);
                if( !scanResult.SSID.isEmpty() && scanResult.SSID.indexOf("SHG") >= 0 ){
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("ssid", scanResult.SSID);
                        jsonObject.put("capabilities", scanResult.capabilities);
                        jsonObject.put("check", false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put( jsonObject );
                }
            }
        }


        for (int i = 0;i < jsonArray.length(); i++){
            try {
                WifiInfo info = wifiManager.getConnectionInfo();
                String name = info.getSSID().replace("\"", "");
                if( jsonArray.getJSONObject(i).getString("ssid").equals(name) ){
                    jsonArray.getJSONObject(i).put("check", true );
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //System.out.println( info.getSSID() );

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.wifiarrays);
        //RecyclerView recyclerView = findViewById(R.id.wifiarrays);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        recyclerView.setLayoutManager(flexboxLayoutManager);

        final WifiAdapter wifiAdapter = new WifiAdapter(this, jsonArray);


        recyclerView.setAdapter(wifiAdapter);

        wifiAdapter.setOnItemClickListener(new WifiAdapter.OnItemClickListener() {
            @Override
            public void tClick(int i) {

                WifiInfo info = wifiManager.getConnectionInfo();
                String name = info.getSSID().replace("\"", "");
                try {
                    if( jsonArray.getJSONObject(i).getString("ssid").equals(name) ){
                        ARouter.getInstance().build("/app/home").navigation();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println( i );
                final EditText editText = new EditText(WifiActivity.this);
                editText.setHint("请输入wifi密码");
                AlertDialog.Builder dialog = new AlertDialog.Builder( WifiActivity.this );
                dialog.setView(editText);
                try {
                    dialog.setTitle( jsonArray.getJSONObject(i).getString("ssid") );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String wifiName = null;
                try {
                    wifiName = jsonArray.getJSONObject(i).getString("ssid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String enc = null;

                try {
                    enc = jsonArray.getJSONObject(i).getString("capabilities");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                final String finalWifiName = wifiName;
                final String finalEnc = enc;
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 1、注意热点和密码均包含引号，此处需要需要转义引号
                        String ssid = finalWifiName;
                        String psd = editText.getText().toString();

                        System.out.println( finalEnc );

                        int type = 1;
                        if( finalEnc.indexOf( "WEP" ) >= 0 ){
                            type = 2;
                        }
                        if( finalEnc.indexOf( "WPA" ) >= 0 ){
                            type = 3;
                        }

                        System.out.println( ssid + "--------" );
                        System.out.println( psd + "--------" );
                        System.out.println( type + "--------" );

                        WifiConfiguration wifiConfiguration = createWifiInfo(ssid, psd, type);

                        int networkId = wifiManager.addNetwork(wifiConfiguration);
                        boolean enable = wifiManager.enableNetwork( networkId, true );

                        System.out.println( networkId + "--------" );

                        System.out.println( enable + "--------" );

                        if( enable ){
                            for (int i = 0;i < jsonArray.length(); i++){
                                try {
                                    if( jsonArray.getJSONObject(i).getString("ssid").equals(ssid) ){
                                        jsonArray.getJSONObject(i).put("check", true );
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            wifiAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "连接"+ ssid +"成功", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "连接"+ ssid +"失败", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
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
        if(tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        switch (Type) {
            case 1://不加密
                configuration.wepKeys[0] = "";
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                configuration.wepTxKeyIndex = 0;
                configuration.priority= 20000;
                break;
            case 2://wep加密
                configuration.hiddenSSID = true;
                configuration.wepKeys[0] = "\"" + Password +"\"";
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
        return  configuration;
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
                System.out.println( scanResult.SSID );
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult);
                    }
                }
            }
            TextView textView = findViewById(R.id.testtext);
            textView.setText( scanWifiList.size() + "88888" );
        }else {
            TextView textView = findViewById(R.id.testtext);
            textView.setText("没有搜索到wifi");
            //Log.e(ConstantUtils.TAG, "没有搜索到wifi");
            System.out.println( "没有搜索到wifi" );
        }
        return wifiList;
    }

    private void registerPermission(){
        //动态获取定位权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
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
                Intent intent = new Intent(WifiActivity.this, selectFileActivity.class);
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

//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 1) {
//            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                System.out.println("申请权限成功");
//            } else {
//                System.out.println("申请失败");
//            }
//        }
//    }

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
}

package com.example.zhuanchu;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhuanchu.adapter.MyAdapter;
import com.example.zhuanchu.bean.DownloadindBean;
import com.example.zhuanchu.service.AlertUtils;
import com.example.zhuanchu.service.DirDownTask;
import com.example.zhuanchu.service.FtpUtils;
import com.example.zhuanchu.service.RefreshListUtil;
import com.example.zhuanchu.service.fileUtils;
import com.githang.statusbar.StatusBarCompat;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.leefeng.promptlibrary.PromptDialog;

public class selectFileActivity extends AppCompatActivity implements OnClickListener {
    ExecutorService fixedThreadPool = null;
    boolean canGiveValue = true;
    int downloadingFileCounts = 0;
    int currentDowncount = 0;
    int totalDowncount = 0;
    MyApplication globalVar = null;
    int countValue;
    String downloadingFileName;
    String host, user, pass;
    int port;
    private boolean flagBtn = true;
    CopyOnWriteArrayList<String> countExe = new CopyOnWriteArrayList<>();//计算当前线程池已下载的文件个数（成功 失败都算）
    CopyOnWriteArrayList<String> countExeSuccess = new CopyOnWriteArrayList<>();//计算当前线程池已下载成功的文件个数（仅有成功的）
    String currentParent = "";
    public ListView lv;
    private MyAdapter mAdapter;
    private List<FtpUtils.wxhFile> list;
    public static List<FtpUtils.wxhFile> currentFiles = null;
    public static List<FtpUtils.wxhFile> movingList = null;
    public static List<FtpUtils.ItemFresh> itemFreshList = new ArrayList<>();

    public int globalCountValue = 0;
    private int checkNum; // 记录选中的条目数量
    private TextView tv_show;// 用于显示选中的条目数量
    private TextView tv_file_folder;
    public TextView itemTextView;
    private TextView tv_result;//用来显示具体选择的条目
    private List<Integer> checkList = new ArrayList<>();
    public static CheckBox checkbox_all;
    Button bt;
    public Button bttest;
    public int dirSize = 0;
    boolean downSucOrFail;
    int countDown = 0;
    String downString = "";
    String parentPath = null;
    public static String dirpath;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private PromptDialog promptDialog;
    ItemChooseData itemChooseData = null;

    boolean gloflag = false;
    FTPClient ftpClient;
    String portStr;
    String serverPath;
    String mypathname;
    String localPath;

    List<String> backDir;
    public TextView tvtotal = null;
    public TextView tvcurrent = null;
    public LinearLayout ll_jindu = null;

    Map<String, DownloadindBean> downloadingMap = new ConcurrentHashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ftpClient = new FTPClient();
        globalVar = (MyApplication) getApplication();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_select_file);
        promptDialog = new PromptDialog(this);
        verifyStoragePermissions(this);
        Intent intent = getIntent();
        //接收数据
        host = intent.getStringExtra("host");
        user = intent.getStringExtra("user");
        pass = intent.getStringExtra("pass");
        port = intent.getIntExtra("port", 21);

        tvtotal = (TextView) findViewById(R.id.tv_total);
        tvcurrent = (TextView) findViewById(R.id.tv_current);
        ll_jindu = findViewById(R.id.ll_jindu);


        bt = (Button) findViewById(R.id.parent);
        bt.setOnClickListener(selectFileActivity.this);
        backDir = new ArrayList<>();

        itemChooseData = new ItemChooseData();
        /* 实例化各个控件 */
        lv = (ListView) findViewById(R.id.lv);


        list = new ArrayList<FtpUtils.wxhFile>();
        checkbox_all = (CheckBox) findViewById(R.id.checkbox_all);
        bttest = (Button) findViewById(R.id.bttest);
        // 为Adapter准备数据
        initDate();

        setClick();
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("文件下载");
        actionBar.setDisplayHomeAsUpEnabled(true);

        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorfocus), true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //关闭线程池
            if (flagBtn) {
                if (fixedThreadPool != null) {
                    System.out.println("------指令指令");
                    fixedThreadPool.shutdownNow();
                }
            } else {
                Toast.makeText(selectFileActivity.this, "文件正在下载，请稍后点击！",
                        Toast.LENGTH_LONG).show();
                return false;
            }

            finish();
            SharedPreferences pref = selectFileActivity.this.getSharedPreferences("tab", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("tabnum", 0);
            editor.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //关闭线程池
        if (flagBtn) {
            if (fixedThreadPool != null) {
                System.out.println("------指令指令");
                fixedThreadPool.shutdownNow();
            }
        } else {
            Toast.makeText(selectFileActivity.this, "文件正在下载，请稍后点击！",
                    Toast.LENGTH_LONG).show();
            return;
        }


        ItemChooseData.clearFileName();
        ItemChooseData.clearFilePath();
        globalVar.hashmap.clear();
        clearItemrefresh();
        checkbox_all.setChecked(false);
        MyAdapter.clearBoolList();

        tvtotal.setText(0 + "");
        tvcurrent.setText(0 + "");
        System.out.println("按下了back键   onBackPressed()");
        if (promptDialog.onBackPressed())
            super.onBackPressed();

    }

    boolean threaddownload(final String myfilename, final int posRef, String remotePath, FTPClient ftpClient,
                           long singleFileSize) throws IOException {
        String[] strdir = remotePath.split("/");

        String mylocaldirpath = new FtpUtils(selectFileActivity.this).crSDFile(strdir);
        File localFile = new File(mylocaldirpath + "/" + myfilename);
        System.out.println("localFile：" + localFile);
        long localSize = 0;
        boolean newFile = localFile.createNewFile();

        final byte[] b = new byte[4096];
        double currentSize = 0;//当前已下载的大小

        double process = 0;//进度值
        double step;
        // 进度
        //当文件特别小时 做如下处理
        if (singleFileSize < 100) {
            step = singleFileSize * 0.01;
        } else {
            step = singleFileSize / 100;
        }

        DownloadindBean downloadindBean = new DownloadindBean();
//        downloadindBean.setFileName(myfilename);

        //判断按钮是否可点击
        if (countExe.size() == downloadingFileCounts) {
            flagBtn = true;
        } else {
            flagBtn = false;
        }

        localSize = localFile.length();
        if (localFile.exists() && (localSize >= singleFileSize)) {//表示本地已存在,且是完整的文件
            downloadindBean.setCurrent(100);
            downloadingMap.put(myfilename, downloadindBean);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(selectFileActivity.this, "请勿重复下载", Toast.LENGTH_LONG).show();
                }
            });
            countExe.add("t");
            countExeSuccess.add("t");
            if (countExe.size() == downloadingFileCounts) {
                flagBtn = true;
            } else {
                flagBtn = false;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tvcurrent.setText(countExeSuccess.size() + "");
                }
            });

            return true;
        } else {//表示本地不存在或者存在但是不完整，存在不完整就需要断点续传
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setRestartOffset(localSize);
            currentSize = localSize;
            OutputStream out = new FileOutputStream(localFile, true);
            InputStream input = ftpClient.retrieveFileStream(remotePath + myfilename);

            int length = 0;

            while (((length = input.read(b)) != -1)) {
                out.write(b, 0, length);//写文件
                currentSize = currentSize + length;
                if (currentSize / step != process) {
                    process = currentSize / step;//下载的百分比
                    int finalPro = (int) process;
                    downloadindBean.setCurrent(finalPro);
                    downloadingMap.put(myfilename, downloadindBean);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            mAdapter.notifyDataSetChanged();
                            //下载过程中 listview会不断刷新进度控件，如果不做listview局部刷新
                            //会一直全局刷新整个listview的所有组件，会造成大量内存消耗，时间过长
                            //会导致APP卡死，且全局刷新过程中，点击listview中组件是没有反应的
                            // mAdapter.notifyDataSetChanged();就是全局刷新，慎用。
                            //以下为  局部刷新 只刷新正在下载的且可见的item
                            RefreshListUtil.updateSingle(posRef, lv, movingList, downloadingMap);
                        }
                    });
                }


            }
            System.out.println("文件下载input为-1时失败-----");


            out.flush();
            out.close();
            input.close();

            // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
            if (ftpClient.completePendingCommand()) {
                countExe.add("t");
                countExeSuccess.add("t");
                if (countExe.size() == downloadingFileCounts) {
                    flagBtn = true;
                } else {
                    flagBtn = false;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tvcurrent.setText(countExeSuccess.size() + "");
                    }
                });

                return true;
            } else {
                System.out.println("文件下载失败-----");
                //流失败时 是否需要走下面这段代码-------------
                countExe.add("t");
                countExeSuccess.add("t");
                if (countExe.size() == downloadingFileCounts) {
                    flagBtn = true;
                } else {
                    flagBtn = false;
                }
                //---------------------------------------------------
                return false;
            }

        }

    }


    public void setClick() {

        //全选
        checkbox_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (currentFiles != null) {
                    ItemChooseData.clearFilePath();
                    if (isChecked) {
                        globalVar.setCheck(1);
                        // 遍历list的长度，将MyAdapter中的map值全部设为true
                        for (int i = 0; i < currentFiles.size(); i++) {
                            MyAdapter.getIsSelected().put(i, true);
                        }
                        System.out.println("点全选时，文件总个数：" + currentFiles.size());

                        mAdapter.notifyDataSetChanged();

                    } else {
                        globalVar.setCheck(0);

                        for (int i = 0; i < currentFiles.size(); i++) {
                            MyAdapter.getIsSelected().put(i, false);

                        }
                        System.out.println("点非全选时，文件总个数：" + currentFiles.size());
                        mAdapter.notifyDataSetChanged();
                    }

                }
            }

        });

//下载
        bttest.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (FtpUtils.isFastClick(5000)) {
                    Toast.makeText(selectFileActivity.this, "点击过于频繁", Toast.LENGTH_SHORT).show();
                    return;
                }
                //需要下载的文件个数，当点击gettask backtask时需要置为0 点击下载按钮时才赋值（如下）
                downloadingFileCounts = ItemChooseData.getFilePath().size();

                System.out.println("点击下载时，待下载的路径:" + ItemChooseData.getFilePath().toString());
                System.out.println(
                        "点击下载时，待下载的路径总数：" + ItemChooseData.getFilePath().size()
                );
                //当有任务正在下载时，不能点击再次下载按钮
                if (flagBtn == false) {
                    Toast.makeText(selectFileActivity.this, "文件正在下载，请稍后点击！",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                //当可以再次点击下载时，首先应该把countExe清空。
                countExe.clear();
                countExeSuccess.clear();
                currentDowncount = 0;
                tvcurrent.setText(0 + "");
                totalDowncount = ItemChooseData.getFilePath().size();

                tvtotal.setText(totalDowncount + "");


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = getSharedPreferences("mypath",
                                Activity.MODE_PRIVATE);
                        final String myremotePath = sharedPreferences.getString("pathname", "");
                        final FTPClient ftpClient2 = FtpUtils.connectServer(host, 21, user, pass, "");
                        final FTPClient ftpClient3 = FtpUtils.connectServer(host, 21, user, pass, "");
                        final List<FTPClient> ftpList = new ArrayList();

                        ftpList.add(ftpClient2);
                        ftpList.add(ftpClient3);

                        try {
                            ftpClient2.changeWorkingDirectory(myremotePath);
                            final FTPFile[] files = ftpClient2.listFiles();
//                            Map<String, Long> filesMap = new ConcurrentHashMap<>();
                            final List<String> lsRe = new ArrayList<>();

                            if (canGiveValue) {
                                System.out.println("赋值测试===" + canGiveValue);
                                //该if是为了控制 在同一个task（gettask,backtask）页面
                                // 多次下载不同的文件时，不需要重复为DownloadindBean赋初值
                                canGiveValue = false;
                                for (int i = 0; i < files.length; i++) {
                                    //先给hashmap赋初值，很重要 不然listview会出现进度错乱
                                    DownloadindBean beaning = new DownloadindBean();
                                    beaning.setCurrent(0);
                                    downloadingMap.put(files[i].getName(), beaning);
                                }

                            }
                            System.out.println("打印远程list路径：" + ItemChooseData.getFilePath().toString());

                            if (ItemChooseData.getFilePath().size() > 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ll_jindu.setVisibility(View.VISIBLE);
                                    }
                                });
                                //下载
                                String portStr = new Integer(port).toString();
                                String can[] = new String[4];
                                can[0] = host;
                                can[1] = portStr;
                                can[2] = user;
                                can[3] = pass;
                                downString = "";

//                    DownTask task = new DownTask(selectFileActivity.this, ItemChooseData.getFilePath(),
//                            ItemChooseData.getFileName());
//                    task.execute(can);


                                fixedThreadPool = Executors.newFixedThreadPool(2);

                                int posRefresh = -2;
                                for (int i = 0; i < ItemChooseData.getFileName().size(); i++) {

                                    //-------------------------
                                    String searPos = ItemChooseData.getFileName().get(i);
                                    for (int j = 0; j < movingList.size(); j++) {
                                        if (searPos.equals(movingList.get(j).filename)) {
                                            posRefresh = j;
                                        }
                                    }
                                    //-------------------------
                                    String index = "";
                                    synchronized (ItemChooseData.getFileName().get(i)) {
                                        index = ItemChooseData.getFileName().get(i);
                                    }

                                    final int finalPosRefresh = posRefresh;
                                    final String finalIndex = index;
                                    fixedThreadPool.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                System.out.println("下载的路径：" + finalIndex);

                                                FTPClient getListFtp = null;
                                                synchronized (ftpList) {
                                                    if (ftpList.size() > 0) {
                                                        getListFtp = ftpList.get(0);
                                                        ftpList.remove(0);
                                                    }

                                                }
                                                //当外界的网络断开或者不好时，应该做如下处理-----------------
                                                if (getListFtp == null) {
//                                                    Toast.makeText(selectFileActivity.this, "网络连接不佳！", Toast.LENGTH_LONG).show();
//                                                    flagBtn = true;
                                                    return;
                                                }
                                                //------------------------------------------------
                                                getListFtp.changeWorkingDirectory(myremotePath);
                                                FTPFile[] fileGetSize = getListFtp.listFiles();
                                                long singleFileSize = 0;
                                                for (int j = 0; j < fileGetSize.length; j++) {
                                                    if (fileGetSize[j].getName().equals(finalIndex)) {
                                                        singleFileSize = fileGetSize[j].getSize();
                                                    }
                                                }

                                                threaddownload(finalIndex, finalPosRefresh, myremotePath, getListFtp, singleFileSize);
                                                ftpList.add(getListFtp);


                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                }


//下载完 要把保存“成功”字样的全局变量清空

                                globalVar.hashmap.clear();
                            } else {
                                Looper.prepare();
                                Toast tot = Toast.makeText(
                                        selectFileActivity.this,
                                        "请勾选需要下载的文件！",
                                        Toast.LENGTH_LONG);
                                tot.show();
                                Looper.loop();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (currentFiles.get(arg2).isFile) {
                    //点击的是文件  则勾选上。再点，则不勾选
                    //取代在适配器中监听单个item中的checkbox的变化


                } else {
                    //是文件夹
                    if (flagBtn) {
                        whileDir(arg2);
                    } else {
                        Toast.makeText(selectFileActivity.this, "文件正在下载，请稍后点击！",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                }
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //如果item是文件夹 才进行下一步处理。不是文件夹 不做任何操作
                boolean click = false;
                FtpUtils.wxhFile wxhFile = currentFiles.get(i);
                if (wxhFile.isFile) {
                    click = false;
                } else {
                    ll_jindu.setVisibility(View.GONE);
                    //是文件夹
                    final String dirDownloadPath = wxhFile.filePath;//所需下载的文件夹的 远程路径
                    AlertDialog.Builder builder = new AlertDialog.Builder(selectFileActivity.this);
                    builder.setTitle("文件夹下载");
                    builder.setMessage("是否下载该文件夹下所有文件？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String portStr = new Integer(port).toString();
                            String can[] = new String[4];
                            can[0] = host;
                            can[1] = portStr;
                            can[2] = user;
                            can[3] = pass;

                            DirDownTask dirtask = new DirDownTask(selectFileActivity.this, dirDownloadPath,
                                    Environment.getExternalStorageDirectory().getPath());
                            dirtask.execute(can);


                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();

                    System.out.println("文件下对应的路径：" + dirDownloadPath);
                    click = true;
                }
                return click;
            }
        });

    }


    public void whileDir(int arg2) {

        String can[] = new String[5];
        can[0] = host;
        can[1] = new Integer(port).toString();
        can[2] = user;
        can[3] = pass;
        can[4] = currentFiles.get(arg2).filePath + "/";
        //在点击进入新文件夹之前记住现在的父亲路径是谁
        GetTask task = new GetTask(selectFileActivity.this);
        task.execute(can);

//        itemChooseData.getFilePath().clear();
//        itemChooseData.getFileName().clear();
        ItemChooseData.clearFilePath();
        ItemChooseData.clearFileName();
        globalVar.hashmap.clear();
        clearItemrefresh();
        MyAdapter.clearBoolList();
        checkbox_all.setChecked(false);//跳到下级目录，自动设为全不选

        tvtotal.setText(0 + "");
        tvcurrent.setText(0 + "");

        for (int i = 0; i < currentFiles.size(); i++) {
            if (!MyAdapter.isSelected.get(i)) {
                checkbox_all.setChecked(false);
            }
        }
    }

    public void clearItemrefresh() {//把下载成功与否的数组还原到原始值
        for (int i = 0; i < itemFreshList.size(); i++) {
            itemFreshList.get(i).setSucfail("");
        }
    }

    @Override
    public void onClick(View view) {
        if (flagBtn == false) {
            Toast.makeText(selectFileActivity.this, "文件正在下载，请稍后点击！",
                    Toast.LENGTH_LONG).show();
            return;
        }

//        if (currentParent.equals("") && currentFiles == null) {
        if (backDir.size() - 2 < 0) {
            AlertUtils.alertNoListener(selectFileActivity.this, "已经是顶级目录！");
        } else {
            String portStr = new Integer(port).toString();
            String can[] = new String[5];
            can[0] = host;
            can[1] = portStr;
            can[2] = user;
            can[3] = pass;
//            can[4] = currentParent;


            can[4] = backDir.get(backDir.size() - 2);


            GetTaskBackDir task = new GetTaskBackDir(this);
            task.execute(can);
            ItemChooseData.clearFileName();
            ItemChooseData.clearFilePath();
            globalVar.hashmap.clear();
            clearItemrefresh();
            MyAdapter.clearBoolList();

            tvtotal.setText(0 + "");
            tvcurrent.setText(0 + "");
//            itemChooseData.getFilePath().clear();
//            itemChooseData.getFileName().clear();

            checkbox_all.setChecked(false);//跳到上级目录，自动设为全不选
            for (int i = 0; i < currentFiles.size(); i++) {
                if (!MyAdapter.isSelected.get(i)) {
                    checkbox_all.setChecked(false);
                }
            }


//            currentFiles = null;
            backDir.remove(backDir.size() - 1);

        }

    }

    // 初始化数据
    private void initDate() {
        String portStr = new Integer(port).toString();
        String can[] = new String[5];
        can[0] = host;
        can[1] = portStr;
        can[2] = user;
        can[3] = pass;
        can[4] = "/";
        GetTask task = new GetTask(selectFileActivity.this);
        task.execute(can);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    class GetTask extends AsyncTask<String, Void, List<FtpUtils.wxhFile>> {
        Context mContext;
        boolean sucornot = false;

        public GetTask(Context ctx) {
            mContext = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_jindu.setVisibility(View.GONE);

        }

        protected List<FtpUtils.wxhFile> doInBackground(String... Params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    promptDialog.showLoading("加载中...");
                }
            });
            canGiveValue = true;
            downloadingFileCounts = 0;
            String host = Params[0];
            String portStr = Params[1];
            String user = Params[2];
            String pass = Params[3];
            String path = Params[4];
            FtpUtils util = new FtpUtils(selectFileActivity.this);
            List<FtpUtils.wxhFile> list = null;
            int port = 21;
            port = Integer.parseInt(portStr);
            try {
                sucornot = util.connectServerTask(host, port, user, pass, "");
                System.out.println("连接测试：" + sucornot);

                FTPFile[] listedDirectories = null;

                list = util.getFileList(path);
                System.out.println("select中path值：" + path);
                downloadingMap.clear();
                for (int i = 0; i < list.size(); i++) {
                    //先给hashmap赋初值，很重要 不然listview会出现进度错乱
                    DownloadindBean beaning = new DownloadindBean();
                    beaning.setCurrent(0);
                    downloadingMap.put(list.get(i).filename, beaning);
                }


                for (int i = 0; i < list.size(); i++) {
                    itemFreshList.add(new FtpUtils.ItemFresh(i, ""));
                }

                //-----1-----首先得出本地完整存在的文件名，以及该文件名在整个listview中的position
                //远程
                List<FTPFile> taskFtpFiles = util.getOriginFileList(path, FtpUtils.ftpClient);
                //path为当前所点击文件夹的完整路径名称，从而进行本地文件夹的创建（存在则不创建）
                String[] strdir = path.split("/");
                String mylocaldirpath = new FtpUtils(selectFileActivity.this).crSDFileTask(strdir);

                System.out.println("本地的文件路径测试：" + mylocaldirpath);
                File localFile = new File(mylocaldirpath);
                File[] taskfiles = localFile.listFiles();

                List<String> posHaveDown = new ArrayList<>();
                //对比本地文件与远程文件的name，size是否一样，是则记录在posHaveDown中 在后面--2--中再找position
                if (taskfiles != null && taskfiles.length > 0) {
                    for (int j = 0; j < taskfiles.length; j++) {
                        if (taskfiles[j].isFile()) {
                            for (int k = 0; k < taskFtpFiles.size(); k++) {
                                if (taskfiles[j].getName().equals(taskFtpFiles.get(k).getName()) &&
                                        taskfiles[j].length() >= taskFtpFiles.get(k).getSize()) {
                                    posHaveDown.add(taskfiles[j].getName());
                                }
                            }
                        }
                    }
                }
                //--------1-------------------------------------------------------------


                if (list.size() > 0) {
                    backDir.add(path);//每次点击文件夹进入下级目录时，都记录父路径
                }
                //附带父亲文件夹的路径进去
                list.add(new FtpUtils.wxhFile(path, "", 2, "", ""));


                List<FtpUtils.wxhFile> listFile = new ArrayList<>();
                List<FtpUtils.wxhFile> listDir = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isParent) {
                        parentPath = list.get(i).filePath;
                        list.remove(i);
                        System.out.println("parent打印：" + parentPath);
                    }
                }
                System.out.println("刚开始的个数：" + list.size());
                List<FtpUtils.wxhFile> removeFileIndex = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isFile) {//是文件
                        listFile.add(list.get(i));
                        removeFileIndex.add(list.get(i));//记住待删除的文件对象。
                    }
                }
                System.out.println("文件个数：" + listFile.size());
                for (int i = 0; i < removeFileIndex.size(); i++) {
                    list.remove(removeFileIndex.get(i));//删除所有的文件
                }
                dirSize = list.size();//文件夹的个数
                System.out.println("文件夹个数：" + list.size());
                list.addAll(listFile);//文件夹与文件集合的拼接
                System.out.println("拼接后个数：" + list.size());


                //-----2-----在这里才添加“已下载”
                if (posHaveDown.size() > 0 && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        for (int j = 0; j < posHaveDown.size(); j++) {
                            if (posHaveDown.get(j).equals(list.get(i).filename)) {
                                System.out.println("测试最终位置结果：" + i);
                                itemFreshList.get(i).setSucfail("已下载");
                            }
                        }
                    }
                }

                //------2---------------------------------------------------------------

                for (int i = 0; i < list.size(); i++) {
                    System.out.println("大小：" + list.get(i).filesize + "--" + "时间：" + list.get(i).filetime);
                }
            } catch (Exception e) {
                System.out.println("开启连接出错");
                e.printStackTrace();
            } finally {
                try {
                    util.closeServer();
                } catch (Exception e) {
                    e.printStackTrace();

                }
                return list;
            }
        }

        protected void onPostExecute(List<FtpUtils.wxhFile> list) {
            SharedPreferences pref = selectFileActivity.this.getSharedPreferences("mypath", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("pathname", parentPath);
            editor.commit();
            System.out.println("这个父亲文件夹为：" + parentPath);
            System.out.println("处理结果函数内部");

            if (!sucornot) {
                Toast.makeText(selectFileActivity.this, "网络连接不佳！", Toast.LENGTH_LONG).show();
                promptDialog.dismiss();
                return;
            }
            if (list == null || list.size() == 0) {
                promptDialog.dismiss();
                AlertUtils.alertNoListener(selectFileActivity.this, "该文件夹为空文件夹！");
            } else {
                //更新文件list
                currentFiles = list;
                movingList = list;

                System.out.println("刚开始的父路径：" + list.get(0).filePath);
                //调用刷新来显示我们的列表
//                inflateListView(list,parentPath);


//                for (int i = 0; i < movingList.size(); i++) {
//                    System.out.println("遍历：" + movingList.get(i).filename);
//                    itemFreshList.add(new FtpUtils.ItemFresh(i, ""));
//                }


                // 实例化自定义的MyAdapter
                mAdapter = new MyAdapter(movingList, itemFreshList, selectFileActivity.this,
                        globalCountValue, globalVar, downloadingMap);
                // 绑定Adapter
                lv.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                new MyAdapter(movingList, itemFreshList, selectFileActivity.this,
                        globalCountValue, globalVar, downloadingMap) {
                };
                if (fixedThreadPool != null) {
                    System.out.println("--11----指令指令");
                    fixedThreadPool.shutdownNow();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        promptDialog.dismiss();

                    }
                });
            }

        }
    }


    class GetTaskBackDir extends AsyncTask<String, Void, List<FtpUtils.wxhFile>> {
        Context mContext;
        boolean sucornot = false;

        public GetTaskBackDir(Context ctx) {
            mContext = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_jindu.setVisibility(View.GONE);

        }

        protected List<FtpUtils.wxhFile> doInBackground(String... Params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    promptDialog.showLoading("加载中...");


                }
            });
            canGiveValue = true;
            downloadingFileCounts = 0;
            String host = Params[0];
            String portStr = Params[1];
            String user = Params[2];
            String pass = Params[3];
            String path = Params[4];
            FtpUtils util = new FtpUtils(selectFileActivity.this);
            List<FtpUtils.wxhFile> list = null;
            int port = 21;
            port = Integer.parseInt(portStr);
            try {
//                util.connectServer(host, port, user, pass, "");
                sucornot = util.connectServerTask(host, port, user, pass, "");

                list = util.getFileList(path);

                downloadingMap.clear();

                for (int i = 0; i < list.size(); i++) {
                    //先给hashmap赋初值，很重要 不然listview会出现进度错乱
                    DownloadindBean beaning = new DownloadindBean();
                    beaning.setCurrent(0);
                    downloadingMap.put(list.get(i).filename, beaning);
                }

                for (int i = 0; i < list.size(); i++) {
                    itemFreshList.add(new FtpUtils.ItemFresh(i, ""));
                }

                //-----1-----首先得出本地完整存在的文件名，以及该文件名在整个listview中的position
                //远程
                List<FTPFile> taskFtpFiles = util.getOriginFileList(path, FtpUtils.ftpClient);
                //path为当前所点击文件夹的完整路径名称，从而进行本地文件夹的创建（存在则不创建）
                String[] strdir = path.split("/");
                String mylocaldirpath = new FtpUtils(selectFileActivity.this).crSDFileTask(strdir);
                System.out.println("本地的文件路径测试：" + mylocaldirpath);
                File localFile = new File(mylocaldirpath);
                File[] taskfiles = localFile.listFiles();

                List<String> posHaveDown = new ArrayList<>();
                //对比本地文件与远程文件的name，size是否一样，是则记录在posHaveDown中 在后面--2--中再找position
                for (int j = 0; j < taskfiles.length; j++) {
                    if (taskfiles[j].isFile()) {
                        for (int k = 0; k < taskFtpFiles.size(); k++) {
                            if (taskfiles[j].getName().equals(taskFtpFiles.get(k).getName()) &&
                                    taskfiles[j].length() >= taskFtpFiles.get(k).getSize()) {
                                posHaveDown.add(taskfiles[j].getName());
                            }
                        }
                    }
                }
                //--------1-------------------------------------------------------------


                //附带父亲文件夹的路径进去
                list.add(new FtpUtils.wxhFile(path, "", 2, "", ""));


                List<FtpUtils.wxhFile> listFile = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isParent) {
                        parentPath = list.get(i).filePath;
                        list.remove(i);
                    }
                }
                System.out.println("刚开始的个数：" + list.size());
                List<FtpUtils.wxhFile> removeFileIndex = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isFile) {//是文件
                        listFile.add(list.get(i));
                        removeFileIndex.add(list.get(i));//记住待删除的文件对象。
                    }
                }
                System.out.println("文件个数：" + listFile.size());


                for (int i = 0; i < removeFileIndex.size(); i++) {
                    list.remove(removeFileIndex.get(i));//删除所有的文件
                }

                System.out.println("文件夹个数：" + list.size());
                list.addAll(listFile);//文件夹与文件集合的拼接
                System.out.println("拼接后个数：" + list.size());

                //-----2-----在这里才添加“已下载”
                if (posHaveDown.size() > 0 && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        for (int j = 0; j < posHaveDown.size(); j++) {
                            if (posHaveDown.get(j).equals(list.get(i).filename)) {
                                System.out.println("测试最终位置结果：" + i);
                                itemFreshList.get(i).setSucfail("已下载");
                            }
                        }
                    }
                }
                //------2---------------------------------------------------------------
            } catch (Exception e) {
                System.out.println("开启连接出错");
                e.printStackTrace();
            } finally {
                try {
                    util.closeServer();
                } catch (Exception e) {
                    e.printStackTrace();

                }
                return list;
            }
        }

        protected void onPostExecute(List<FtpUtils.wxhFile> list) {
            SharedPreferences pref = selectFileActivity.this.getSharedPreferences("mypath", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("pathname", parentPath);
            editor.commit();
            System.out.println("这个父亲文件夹为：" + parentPath);
            System.out.println("处理结果函数内部");
            if (!sucornot) {
                Toast.makeText(selectFileActivity.this, "网络连接不佳！", Toast.LENGTH_LONG).show();
                promptDialog.dismiss();
                return;
            }
            if (list == null || list.size() == 0) {
                promptDialog.dismiss();

                AlertUtils.alertNoListener(selectFileActivity.this, "该文件夹为空文件夹！");


            } else {
                System.out.println("找到了！！");
                //更新文件list
                currentFiles = list;
                movingList = list;
                System.out.println("刚开始的父路径：" + list.get(0).filePath);
                //调用刷新来显示我们的列表
//                inflateListView(list,parentPath);

                // 实例化自定义的MyAdapter
                mAdapter = new MyAdapter(list, itemFreshList, selectFileActivity.
                        this, globalCountValue, globalVar, downloadingMap);
                // 绑定Adapter
                lv.setAdapter(mAdapter);
                if (fixedThreadPool != null) {
                    System.out.println("--11----指令指令");
                    fixedThreadPool.shutdownNow();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        promptDialog.dismiss();
                    }
                });
            }

        }
    }

    private void setDialogText(View v) {
        if (v instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) v;
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                setDialogText(child);
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setTextSize(5);
        }
    }

    class DownTask extends AsyncTask<String, Long, Boolean> {

        int whereInt = 0;//listview中正在下载文件的index位置。

        Context mContext;
        ProgressDialog pdialog;
        List<String> downloadPathList;
        List<String> downnameList;

        long totalFileSize = 0;//获取所选文件的总大小
        double currentSize = 0;//当前已下载的大小

        public DownTask(Context ctx, List<String> pathList,
                        List<String> filenameList) {
            mContext = ctx;
            downloadPathList = pathList;//下载的路径list
            downnameList = filenameList;//下载的文件名list
        }

        protected void onPreExecute() {
            pdialog = new ProgressDialog(mContext);
//            View v = pdialog.getWindow().getDecorView();
//            setDialogText(v);

            pdialog.setTitle("文件正在下载");
            pdialog.setMessage("敬请等待...");
            pdialog.setProgressDrawable(getResources().getDrawable(R.drawable.myprogressbarstyle));
            pdialog.setCancelable(false);
            pdialog.setMax(100);
            pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pdialog.setIndeterminate(false);
            pdialog.show();

        }

        protected void onProgressUpdate(Long... values) {
            pdialog.setTitle("文件：" + downloadingFileName);

            pdialog.setMessage("正在下载：" + fileUtils.setSize((long) currentSize) + "/" + fileUtils.setSize((long) totalFileSize));//显示 文件当前已下载大小 以及总大小
            pdialog.show();
            long a = Long.valueOf(values[0].toString());
            int value = (int) a;
            pdialog.setProgress(value);
        }

        @Override
        protected Boolean doInBackground(String... Params) {
            host = Params[0];
            portStr = Params[1];
            user = Params[2];
            pass = Params[3];

            SharedPreferences sharedPreferences = getSharedPreferences("mypath",
                    Activity.MODE_PRIVATE);
            mypathname = sharedPreferences.getString("pathname", "");

            System.out.println(
                    "pathname:" + mypathname);
            //判断是否是测试
            String[] strdir = mypathname.split("/");

            dirpath = new FtpUtils(selectFileActivity.this).crSDFile(strdir);
            localPath = dirpath;
            boolean textb = false;

            if (itemChooseData.getFilePath().size() > 0) {
                ftpClient.setDataTimeout(6000);//设置连接超时时间
                ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
                ftpClient.setAutodetectUTF8(true);//中文文件夹中的数据没法下载 需要该设置。
                try {
                    ftpClient.connect(host, port);
                    gloflag = ftpClient.login(user, pass);
                    textb = download(downnameList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return textb;
        }

        boolean download(List<String> dnameList) {
            try {
                if (!gloflag) return gloflag;

                totalFileSize = getFilesSize(mypathname, ftpClient, dnameList);//获取所选文件的总大小

                System.out.println("文件总大小：" + totalFileSize);
                double process = 0;//进度值
                double step;

                String pathChange = localPath;

                //mypathname 为用户点击的文件夹 的完整ftp路径。
                //以下两句代码 可以避免文件名带有空格时不能下载问题，我们一般使用
//                ftpClient.listFiles(mypathname);来替代下面两句的作用，但是无法避免文件名
//                带有空格时不能下载问题
                ftpClient.changeWorkingDirectory(mypathname);
                FTPFile[] files = ftpClient.listFiles();
                int dots = 0;
                for (int k = 0; k < files.length; k++) {
                    System.out.println("文件名" + k + ":" + files[k].getName());
                    if (files[k].getName().equals("..") || files[k].getName().equals(".")) {
                        dots++;
                    }
                }
                if (files.length - dots == 0) {
                    return false;
                }
//dnameList表示勾选的文件名称列表
                for (int i = dots; i < files.length; i++) {
                    if (files[i].isFile() && dnameList.contains(files[i].getName())) {
                        File localFile = new File(localPath + "/" + files[i].getName());
                        System.out.println("localFile：" + localFile);
                        long localSize = 0;

                        //要下载的文件在本地已存在

                        if (localFile.exists()) {
//                            File file = new File(pathChange);
//                            file.delete();
                            localSize = localFile.length();
                            if (localSize >= totalFileSize) {//表示本地已存在完整的文件
                                currentSize = totalFileSize;
                                currentDowncount++;
                                //记得设置标签为 成功！
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        tvcurrent.setText("已下载文件：" + currentDowncount + "个");
//                                        itemTextView.setText("成功");
//                                    }
//                                });
                                continue;
                            }
                            //本地已存在 但是不完整
                            currentSize = localSize;
//                            boolean newFile = localFile.createNewFile();
//                            System.out.println("newFile：" + newFile);
                            // 进度

                            //当文件特别小时 做如下处理
                            if (totalFileSize < 100) {
                                step = totalFileSize * 0.01;
                            } else {
                                step = totalFileSize / 100;
                                System.out.println("step的值：" + step);
                            }

//                ftpClient.enterLocalActiveMode();
                            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                            ftpClient.setRestartOffset(localSize);
                            OutputStream out = new FileOutputStream(localFile, true);
                            InputStream input = ftpClient.retrieveFileStream(mypathname + files[i].getName());
                            byte[] b = new byte[4096];
                            int length = 0;
                            while ((length = input.read(b)) != -1) {
                                out.write(b, 0, length);//写文件
                                currentSize = currentSize + length;
                                System.out.println("当前已下载的大小：" + currentSize);

                                if (currentSize / step != process) {
                                    process = currentSize / step;//下载的百分比
                                    countValue = (int) process;//全局的下载进度

                                    publishProgress((long) process);

//                            downloadingFileName = dnameList.get(i - dots);//全局的下载filename
                                    downloadingFileName = files[i].getName();//全局的下载filename


                                    for (int j = 0; j < movingList.size(); j++) {
                                        System.out.println("文件名查找：" + movingList.get(j).filename);
                                        if (movingList.get(j).filename.equals(downloadingFileName)) {
                                            whereInt = j;
                                            System.out.println("标志成功 失败的位置值：" + whereInt);
                                            break;
                                        }
                                    }
//
//                            System.out.println("正在下载的文件在list的全局position：" + whereInt);
//
                                    if (whereInt >= lv.getFirstVisiblePosition() &&
                                            whereInt <= lv.getLastVisiblePosition()) {
                                        int positionInListView = whereInt - lv.getFirstVisiblePosition();

                                        System.out.println("展示成功失败的TextView的位置值：" + positionInListView);
                                        itemTextView = (TextView) lv.getChildAt(positionInListView)//展示成功 失败的文本
                                                .findViewById(R.id.item_tv_sucfail);

                                        if (countValue > 100) {
                                            countValue = 100;
                                            globalCountValue = countValue;
                                        }

                                    }


                                }
//                    }
                            }
                            out.flush();
                            out.close();
                            input.close();

                            // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
                            if (ftpClient.completePendingCommand()) {
                                //统计已下载的文件个数 并显示
                                ++currentDowncount;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvcurrent.setText("已下载文件：" + currentDowncount + "个");
                                        itemTextView.setText("成功");
                                    }
                                });
                                //item显示下载是否成功
                                itemFreshList.get(whereInt).setSucfail("成功");


                            } else {
                                //单个文件下载流失败
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        itemTextView.setText("失败");//列表中可见的item可直接设置 值
                                    }
                                });
                                itemFreshList.get(whereInt).setSucfail("失败");//用于列表中不可见和可见
                                return false;
                            }
                            continue;

                            //本地完全不存在 某文件

                        } else {

                            boolean newFile = localFile.createNewFile();
                            System.out.println("newFile：" + newFile);
                            // 进度

                            //当文件特别小时 做如下处理
                            if (totalFileSize < 100) {
                                step = totalFileSize * 0.01;
                            } else {
                                step = totalFileSize / 100;
                                System.out.println("step的值：" + step);
                            }

//                ftpClient.enterLocalActiveMode();
                            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                            ftpClient.setRestartOffset(localSize);
                            OutputStream out = new FileOutputStream(localFile, true);
                            InputStream input = ftpClient.retrieveFileStream(mypathname + files[i].getName());
                            byte[] b = new byte[4096];
                            int length = 0;
                            while ((length = input.read(b)) != -1) {
                                out.write(b, 0, length);//写文件
                                currentSize = currentSize + length;
                                System.out.println("当前已下载的大小：" + currentSize);

                                if (currentSize / step != process) {
                                    process = currentSize / step;//下载的百分比
                                    countValue = (int) process;//全局的下载进度

                                    publishProgress((long) process);

//                            downloadingFileName = dnameList.get(i - dots);//全局的下载filename
                                    downloadingFileName = files[i].getName();//全局的下载filename


                                    for (int j = 0; j < movingList.size(); j++) {
                                        System.out.println("文件名查找：" + movingList.get(j).filename);
                                        if (movingList.get(j).filename.equals(downloadingFileName)) {
                                            whereInt = j;
                                            System.out.println("标志成功 失败的位置值：" + whereInt);
                                            break;
                                        }
                                    }
//
//                            System.out.println("正在下载的文件在list的全局position：" + whereInt);
//
                                    if (whereInt >= lv.getFirstVisiblePosition() &&
                                            whereInt <= lv.getLastVisiblePosition()) {
                                        int positionInListView = whereInt - lv.getFirstVisiblePosition();

                                        System.out.println("展示成功失败的TextView的位置值：" + positionInListView);
                                        itemTextView = (TextView) lv.getChildAt(positionInListView)//展示成功 失败的文本
                                                .findViewById(R.id.item_tv_sucfail);

                                        if (countValue > 100) {
                                            countValue = 100;
                                            globalCountValue = countValue;
                                        }

                                    }


                                }
//                    }
                            }
                            out.flush();
                            out.close();
                            input.close();

                            // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
                            if (ftpClient.completePendingCommand()) {
                                //统计已下载的文件个数 并显示
                                ++currentDowncount;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvcurrent.setText("已下载文件：" + currentDowncount + "个");
                                        itemTextView.setText("成功");
                                    }
                                });
                                //item显示下载是否成功
                                itemFreshList.get(whereInt).setSucfail("成功");


                            } else {
                                //单个文件下载流失败
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        itemTextView.setText("失败");//列表中可见的item可直接设置 值
                                    }
                                });
                                itemFreshList.get(whereInt).setSucfail("失败");//用于列表中不可见和可见
                                return false;
                            }
                            continue;

                        }


                    }
                }
                //for之后进行的操作


            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                return gloflag;
            }
        }

        protected void onPostExecute(Boolean flag) {
//            if (flag) {
//                //item显示下载是否成功
//                itemFreshList.get(whereInt).setSucfail("成功");
//                System.out.println("添加是否成功-" + "位置：" + itemFreshList.get(whereInt).pos +
//                        "--值：" + itemFreshList.get(whereInt).sucfail);
//                globalVar.setSucFail(whereInt, "成功");
//                countDown++;
//                downSucOrFail = true;
//                currentDowncount++;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tvcurrent.setText("已下载文件：" + currentDowncount + "个");
//                    }
//                });
//            }
//            else {
//                globalVar.setSucFail(whereInt, "失败");
//                itemFreshList.get(whereInt).setSucfail("失败");
//            }

            if (ftpClient != null) {
                // 登出FTP
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ftpClient.logout();
                            ftpClient.disconnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            pdialog.dismiss();
        }
    }

    //所选择的文件列表的文件总大小

    public long getFilesSize(String path, FTPClient ftpClient, List<String> dnameList) throws IOException {
        long totalSize = 0;
        ftpClient.changeWorkingDirectory(path);
        FTPFile[] files = ftpClient.listFiles();
        int dots = 0;
        for (int k = 0; k < files.length; k++) {
            if (files[k].getName().equals("..") || files[k].getName().equals(".")) {
                dots++;
            }
        }
        if (files.length - dots == 0) {
            return 0;
        }
        for (int i = dots; i < files.length; i++) {
            if (files[i].isFile() && dnameList.contains(files[i].getName())) {
                System.out.println("选择的文件并计算长度：" + files[i].getName());
                long serverSize = files[i].getSize(); // 获取远程文件的长度
                totalSize += serverSize;
            }

        }
        return totalSize;
    }


    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ItemChooseData.clearFilePath();
        ItemChooseData.clearFileName();
        globalVar.hashmap.clear();
        clearItemrefresh();
        MyAdapter.clearBoolList();

        tvtotal.setText(0 + "");
        tvcurrent.setText(0 + "");
        checkbox_all.setChecked(false);//跳到下级目录，自动设为全不选
        //关闭线程池
        if (flagBtn) {
            if (fixedThreadPool != null) {
                System.out.println("------指令指令");
                fixedThreadPool.shutdownNow();
            }
        }

        System.out.println("selectactivity退出了。");
    }
}

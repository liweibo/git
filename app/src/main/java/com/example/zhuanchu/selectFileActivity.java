package com.example.zhuanchu;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.zhuanchu.adapter.MyAdapter;
import com.example.zhuanchu.service.FtpUtils;
import com.githang.statusbar.StatusBarCompat;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SelectableChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.leefeng.promptlibrary.PromptDialog;

public class selectFileActivity extends AppCompatActivity implements OnClickListener {
    int countValue;
    String downloadingFileName;
    String host, user, pass;
    int port;
    String currentParent = "";
    private ListView lv;
    private MyAdapter mAdapter;
    private List<FtpUtils.wxhFile> list;
    public static List<FtpUtils.wxhFile> currentFiles = null;
    public static List<FtpUtils.wxhFile> movingList = null;

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

    FTPClient ftpClient;
    String portStr;
    String serverPath;
    String mypathname;
    String localPath;

    List<String> backDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        promptDialog = new PromptDialog(this);
        verifyStoragePermissions(this);
        Intent intent = getIntent();
        //接收数据
        host = intent.getStringExtra("host");
        user = intent.getStringExtra("user");
        pass = intent.getStringExtra("pass");
        port = intent.getIntExtra("port", 21);

        bt = (Button) findViewById(R.id.parent);
        bt.setOnClickListener(selectFileActivity.this);
        backDir = new ArrayList<>();


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
        super.onBackPressed();
        ItemChooseData.getFilePath().clear();
        ItemChooseData.getFileName().clear();
        System.out.println("按下了back键   onBackPressed()");
        if (promptDialog.onBackPressed())
            super.onBackPressed();
    }

    public void setClick() {


        //全选
        checkbox_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (currentFiles!=null){

                if (isChecked) {
                    // 遍历list的长度，将MyAdapter中的map值全部设为true
                    for (int i = 0; i < currentFiles.size(); i++) {
                        MyAdapter.getIsSelected().put(i, true);
                        System.out.println("11111" + MyAdapter.getIsSelected().get(i));
                    }
                    mAdapter.notifyDataSetChanged();

                } else {
                    for (int i = 0; i < currentFiles.size(); i++) {
                        MyAdapter.getIsSelected().put(i, false);
                    }
                    mAdapter.notifyDataSetChanged();
                }

                }
            }

        });
//下载
        bttest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ItemChooseData.getFilePath().size() > 0) {
                    List<Integer> listIndex = ItemChooseData.getIndexArr();
                    for (int i = 0; i < listIndex.size(); i++) {
//                        String filename = currentFiles.get(listIndex.get(i)).filename;

//                        String filePath = currentFiles.get(listIndex.get(i)).filePath;
//                        ItemChooseData.addFilePath(filePath);
//                        System.out.println("测试文件名：" + filename);
//                        System.out.println("测试文件路径：" + filePath);
                    }
                    System.out.println("测试文件路径汇总：" + ItemChooseData.getFilePath().toString());

//[tffs/start.txt, tffs/EventFas, tffs/Fault, tffs/mvb_confm.dat, tffs/EDRM_CONF.xml]

                    //下载
                    String portStr = new Integer(port).toString();
                    String can[] = new String[4];
                    can[0] = host;
                    can[1] = portStr;
                    can[2] = user;
                    can[3] = pass;

                    //循环下载
                    for (int i = 0; i < ItemChooseData.getFilePath().size(); i++) {
                        downString = "";
                        DownTask task = new DownTask(selectFileActivity.this, ItemChooseData.getFilePath().get(i),
                                ItemChooseData.getFileName().get(i), i
                        );
//                        ItemChooseData.listFilename.get(i).replace(ItemChooseData.listFilename.get(i),
//                                ItemChooseData.listFilename.get(i)+" "+countValue+"%");
                        System.out.println("下载：：" + ItemChooseData.getFilePath().get(i));
                        System.out.println("下载121：：" + ItemChooseData.getFilePath());

                        task.execute(can);


                    }


                } else {
                    Toast tot = Toast.makeText(
                            selectFileActivity.this,
                            "请勾选需要下载的文件！",
                            Toast.LENGTH_LONG);
                    tot.show();
                }

            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (currentFiles.get(arg2).isFile) {
                    //点击的是文件  则勾选上。再点，则不勾选
                } else {
                    //是文件夹
                    whileDir(arg2);
                }
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
        checkbox_all.setChecked(false);//跳到下级目录，自动设为全不选
        ItemChooseData.getFilePath().clear();
        ItemChooseData.getFileName().clear();
        for (int i = 0; i < currentFiles.size(); i++) {
            if (!MyAdapter.isSelected.get(i)) {
                checkbox_all.setChecked(false);
            }
        }
    }


    @Override
    public void onClick(View view) {
//        if (currentParent.equals("") && currentFiles == null) {
        if (backDir.size() - 2 < 0) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("已经是顶级目录！")
                    .setPositiveButton("确定", null)
                    .show();
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
            ItemChooseData.getFilePath().clear();
            ItemChooseData.getFileName().clear();

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
        GetTask task = new GetTask(this);
        task.execute(can);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    class GetTask extends AsyncTask<String, Void, List<FtpUtils.wxhFile>> {
        Context mContext;

        public GetTask(Context ctx) {
            mContext = ctx;
        }

        protected List<FtpUtils.wxhFile> doInBackground(String... Params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    promptDialog.showLoading("加载中...");
                }
            });


            String host = Params[0];
            String portStr = Params[1];
            String user = Params[2];
            String pass = Params[3];
            String path = Params[4];
            FtpUtils util = new FtpUtils();
            List<FtpUtils.wxhFile> list = null;
            int port = 21;
            port = Integer.parseInt(portStr);
            try {
                util.connectServer(host, port, user, pass, "");
                list = util.getFileList(path);
                if (list.size() > 0) {
                    backDir.add(path);//每次点击文件夹进入下级目录时，都记录父路径
                }
                //附带父亲文件夹的路径进去
                list.add(new FtpUtils.wxhFile(path, "", 2));


                System.out.println("backDir:" + backDir.toString());
                List<FtpUtils.wxhFile> listFile = new ArrayList<>();
                List<FtpUtils.wxhFile> listDir = new ArrayList<>();

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

                dirSize = list.size();//文件夹的个数

                System.out.println("文件夹个数：" + list.size());
                list.addAll(listFile);//文件夹与文件集合的拼接
                System.out.println("拼接后个数：" + list.size());

            } catch (Exception e) {
                System.out.println("开启连接出错");
                e.printStackTrace();
            } finally {
                try {
                    util.closeServer();
                } catch (Exception e) {
                    e.printStackTrace();
                    ;
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
            if (list == null || list.size() == 0) {
                System.out.println("空文件夹或者访问出错");
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("该文件夹为空文件夹！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                promptDialog.dismiss();


                            }
                        })
                        .show();

            } else {
                System.out.println("找到了！！");
                //更新文件list
                currentFiles = list;

                movingList = list;

                System.out.println("刚开始的父路径：" + list.get(0).filePath);
                //调用刷新来显示我们的列表
//                inflateListView(list,parentPath);

                for (int i = 0; i < movingList.size(); i++) {
                    System.out.println("遍历：" + movingList.get(i).filename);
                }


                // 实例化自定义的MyAdapter
                mAdapter = new MyAdapter(movingList, selectFileActivity.this);
                // 绑定Adapter
                lv.setAdapter(mAdapter);

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

        public GetTaskBackDir(Context ctx) {
            mContext = ctx;
        }

        protected List<FtpUtils.wxhFile> doInBackground(String... Params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    promptDialog.showLoading("加载中...");


                }
            });

            String host = Params[0];
            String portStr = Params[1];
            String user = Params[2];
            String pass = Params[3];
            String path = Params[4];
            FtpUtils util = new FtpUtils();
            List<FtpUtils.wxhFile> list = null;
            int port = 21;
            port = Integer.parseInt(portStr);
            try {
                util.connectServer(host, port, user, pass, "");
                list = util.getFileList(path);

                //附带父亲文件夹的路径进去
                list.add(new FtpUtils.wxhFile(path, "", 2));


                System.out.println("backDir:" + backDir.toString());
                List<FtpUtils.wxhFile> listFile = new ArrayList<>();
                List<FtpUtils.wxhFile> listDir = new ArrayList<>();

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

            } catch (Exception e) {
                System.out.println("开启连接出错");
                e.printStackTrace();
            } finally {
                try {
                    util.closeServer();
                } catch (Exception e) {
                    e.printStackTrace();
                    ;
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
            if (list == null || list.size() == 0) {
                System.out.println("空文件夹或者访问出错");
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("该文件夹为空文件夹！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                promptDialog.dismiss();


                            }
                        })
                        .show();

            } else {
                System.out.println("找到了！！");
                //更新文件list
                currentFiles = list;
                movingList = list;
                System.out.println("刚开始的父路径：" + list.get(0).filePath);
                //调用刷新来显示我们的列表
//                inflateListView(list,parentPath);

                // 实例化自定义的MyAdapter
                mAdapter = new MyAdapter(list, selectFileActivity.this);
                // 绑定Adapter
                lv.setAdapter(mAdapter);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        promptDialog.dismiss();
                    }
                });
            }

        }
    }


    class DownTask extends AsyncTask<String, Long, Boolean> {
        Context mContext;
        ProgressDialog pdialog;
        String downloadPath;
        String downname;
        int myIndex;

        public DownTask(Context ctx, String path, String filename, int indexWhich) {
            mContext = ctx;
            downloadPath = path;
            downname = filename;
            myIndex = indexWhich;


        }

        protected void onPreExecute() {
            pdialog = new ProgressDialog(mContext);
            pdialog.setTitle("文件正在下载");
            pdialog.setMessage("敬请等待...");
            pdialog.setProgressDrawable(getResources().getDrawable(R.drawable.myprogressbarstyle));
            pdialog.setCancelable(false);
            pdialog.setMax(100);
            pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pdialog.setIndeterminate(false);
//            pdialog.show();
            if (countValue == 100) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemTextView.setVisibility(View.VISIBLE);
                        itemTextView.setText("成功");
                        itemTextView.setTextColor(Color.rgb(0, 152, 253));
                    }
                });
            }
        }

        protected void onProgressUpdate(Long... values) {
            pdialog.setTitle("文件:" + downloadingFileName);
            pdialog.show();
            long a = Long.valueOf(values[0].toString());
            int value = (int) a;
//            System.out.println("进度：" + value);
            pdialog.setProgress(value);
            if (countValue > 100) {
                countValue = 100;
            }
            if (countValue == 100) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemTextView.setVisibility(View.VISIBLE);
                        itemTextView.setText("成功");
                        itemTextView.setTextColor(Color.rgb(0, 152, 253));
                    }
                });
            }
//            tv_jindu.setText("文件" + downloadingFileName + "下载进度：" + countValue + "%" + downString
//            );


        }

        @Override
        protected Boolean doInBackground(String... Params) {
            ftpClient = new FTPClient();
            host = Params[0];
            portStr = Params[1];
            user = Params[2];
            pass = Params[3];


            SharedPreferences sharedPreferences = getSharedPreferences("mypath",
                    Activity.MODE_PRIVATE);
            mypathname = sharedPreferences.getString("pathname", "");
            String[] strdir = mypathname.split("/");
            crSDFile(strdir);
            localPath = dirpath;

            boolean textb = false;

            if (ItemChooseData.getFilePath().size() > 0) {
                System.out.println("xxxxx");
                textb = download(downloadPath, downname);

            }


            return textb;


        }

        boolean download(String myserverPath, String dname) {
            boolean flag = false;
            int port = Integer.parseInt(portStr);
            try {
                ftpClient.setDataTimeout(6000);//设置连接超时时间
                ftpClient.setControlEncoding("utf-8");
                ftpClient.connect(host, port);

                flag = ftpClient.login(user, pass);
                if (!flag) return flag;
                FTPFile[] files = ftpClient.listFiles(myserverPath);
                if (files.length == 0) {
                    return false;
                }


                String ss = files[0].getName();
                String newstr = ss.replace(mypathname, "");
                newstr = newstr.replace("/", "");
                System.out.println("newstr" + newstr);
                localPath = localPath + newstr;


                System.out.println("我的localPath：" + localPath);
                // 接着判断下载的文件是否能断点下载
                long serverSize = files[0].getSize(); // 获取远程文件的长度
                System.out.println("文件长度：" + serverSize);

                File localFile = new File(localPath);
                System.out.println("localFile：" + localFile);
                long localSize = 0;

                if (localFile.exists()) {
                    System.out.println("localFile.exists()进来了");
                    File file = new File(localPath);
                    file.delete();

                }
                boolean newFile = localFile.createNewFile();
                System.out.println("newFile：" + newFile);
                // 进度
                long step = serverSize / 100;
                long process = 0;
                long currentSize = 0;
                // 开始准备下载文件
                ftpClient.enterLocalActiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                OutputStream out = new FileOutputStream(localFile, true);
                ftpClient.setRestartOffset(localSize);
                InputStream input = ftpClient.retrieveFileStream(myserverPath);
                byte[] b = new byte[1024];
                int length = 0;
                while ((length = input.read(b)) != -1) {
                    out.write(b, 0, length);//写文件
                    currentSize = currentSize + length;
                    if (currentSize / step != process) {
                        process = currentSize / step;//下载的百分比

//                        System.out.println("文件"+myserverPath+"已下载："+process);
                        System.out.println("文件" + dname + "已下载：" + (int) process);
                        countValue = (int) process;//全局的下载进度
                        downloadingFileName = dname;//全局的下载filename

                        //把某个正在下载的文件名  进度 实时传给进度组件，文件名可以得出在勾选列表中的正在下载的文件位置，
//                        再把文件位置传给listview  用来展示列表中某个位置进度组件的可见性。

                        int whereInt = 0;//listview中正在下载文件的index位置。
                        for (int i = 0; i < movingList.size(); i++) {
                            System.out.println("文件名查找：" + movingList.get(i).filename);

                            if (movingList.get(i).filename == downloadingFileName) {
//                                if (dirSize > 0) {
//                                    whereInt = i + dirSize;
//                                } else {
                                whereInt = i;
                                break;
//                                }
                            }
                        }

                        System.out.println("文件名123：" + whereInt);

                        if (whereInt >= lv.getFirstVisiblePosition() &&
                                whereInt <= lv.getLastVisiblePosition()) {
                            int positionInListView = whereInt - lv.getFirstVisiblePosition();
                            System.out.println("positionInListView:" + positionInListView);
                            final RingProgressBar item = (RingProgressBar) lv.getChildAt(positionInListView)
                                    .findViewById(R.id.ringProgressBarAdapter);

                            itemTextView = (TextView) lv.getChildAt(positionInListView)//展示成功 失败的文本
                                    .findViewById(R.id.item_tv_sucfail);

                            if (countValue > 100) {
                                countValue = 100;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    item.setVisibility(View.VISIBLE);
                                    if (countValue == 100) {
                                        itemTextView.setVisibility(View.VISIBLE);
                                        itemTextView.setText("成功");
                                        itemTextView.setTextColor(Color.rgb(0, 152, 253));
                                    }
                                }
                            });


                            item.setProgress(countValue);

                        }


                        publishProgress(process);
                    }
                }
                out.flush();
                out.close();
                input.close();
                // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
                if (ftpClient.completePendingCommand()) {
                    return true;
                } else {
                    return false;
                }


            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());
                System.out.println("Date获取当前日期时间:" + simpleDateFormat.format(date));
                ItemChooseData.addDownloadSucOrFail(downloadingFileName + "   " + simpleDateFormat.format(date), flag);//记录下载的文件名及是否下载成功。

                return flag;
            }
        }

        protected void onPostExecute(Boolean flag) {

            if (flag && countValue >= 100) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemTextView.setVisibility(View.VISIBLE);
                        itemTextView.setText("成功");
                        itemTextView.setTextColor(Color.rgb(0, 152, 253));
                    }
                });

                countDown++;
                downSucOrFail = true;

                Toast tot = Toast.makeText(
                        mContext,
                        downloadingFileName + "下载成功!",
                        Toast.LENGTH_SHORT);
                tot.show();
            } else if (!flag) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemTextView.setVisibility(View.VISIBLE);
                        itemTextView.setText("失败");
                        itemTextView.setTextColor(Color.rgb(255, 0, 0));
                    }
                });

                downSucOrFail = false;
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage(downloadingFileName + "抱歉，下载过程中出现错误，请重新尝试")
                        .setPositiveButton("确定", null)
                        .show();

            } else if (countValue == 100) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemTextView.setVisibility(View.VISIBLE);
                        itemTextView.setText("成功");
                        itemTextView.setTextColor(Color.rgb(0, 152, 253));
                    }
                });
                Toast tot = Toast.makeText(
                        mContext,
                        downloadingFileName + "下载成功!",
                        Toast.LENGTH_SHORT);
                tot.show();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemTextView.setVisibility(View.VISIBLE);
                        itemTextView.setText("失败");
                        itemTextView.setTextColor(Color.rgb(255, 0, 0));
                    }
                });
            }
            pdialog.dismiss();
        }
    }


    public static void crSDFile(String... folder) {

        int length = folder.length;
        String genFolder = Environment.getExternalStorageDirectory().getPath().toString() +
                File.separator + "CRRC" + File.separator;
        File file, file2, file3;
        file2 = new File(genFolder);
        if (!file2.exists()) {
            file2.mkdir();
        }


        String genFolder1 = genFolder +
                File.separator + "DOWNLOAD" + File.separator;
        file3 = new File(genFolder1);
        if (!file3.exists()) {
            file3.mkdir();
        }


        String str = genFolder1;
        for (int i = 0; i < length; i++) {

            str = str + folder[i] + "/";
            file = new File(str);

            if (!file.exists()) {
                file.mkdir();

            }

        }

        System.out.println("路径：" + str);
        dirpath = str;

    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}

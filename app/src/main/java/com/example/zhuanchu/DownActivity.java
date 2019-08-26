package com.example.zhuanchu;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Route(path = "/app/down")
public class DownActivity extends AppCompatActivity {

    private static String host;
    private String user;
    private String pass;
    private static int port;
    private Button choose, download;
    private TextView tv;
    public static String dirpath;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    FTPClient ftpClient;
    String portStr;
    String serverPath;
    String mypathname;
    String localPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.down);

        findViewById(R.id.homeback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/home").navigation();
            }
        });

        /*
         * 导航修改的内容
         */
        findViewById(R.id.systembar).findViewById(R.id.toSystem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/system").navigation();
            }
        });

        findViewById(R.id.systembar).findViewById(R.id.toDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/home").navigation();
            }
        });

        findViewById(R.id.systembar).findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/upload").navigation();
            }
        });

        findViewById(R.id.systembar).findViewById(R.id.toPack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/app/pack").navigation();
            }
        });

        ImageView image = findViewById(R.id.systembar).findViewById(R.id.downImg);
        image.setImageResource(R.drawable.down_c);
        TextView textView = findViewById(R.id.systembar).findViewById(R.id.downText);
        textView.setTextColor( Color.parseColor("#35ae5d")  );
    }

    class DownTask extends AsyncTask<String, Long, Boolean> {
        Context mContext;
        ProgressDialog pdialog;
        String downloadPath;

        public DownTask(Context ctx, String path) {
            mContext = ctx;
            downloadPath = path;
        }

        protected void onPreExecute() {
            pdialog = new ProgressDialog(mContext);
            pdialog.setTitle("任务正在执行中");
            pdialog.setMessage("正在下载中，敬请等待...");
            pdialog.setCancelable(false);
            pdialog.setMax(100);
            pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pdialog.setIndeterminate(false);
            pdialog.show();
        }

        protected void onProgressUpdate(Long... values) {
            long a = Long.valueOf(values[0].toString());
            int value = (int) a;
            pdialog.setProgress(value);
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
            DownActivity.crSDFile(strdir);
            localPath = dirpath;

            boolean textb = false;

            if (ItemChooseData.getFilePath().size() > 0) {
                System.out.println("xxxxx");
                textb = download(downloadPath);

            }
            return textb;


        }

        boolean download(String myserverPath) {
            boolean flag = false;
            int port = Integer.parseInt(portStr);
            try {
                ftpClient.setDataTimeout(6000);//设置连接超时时间
                ftpClient.setControlEncoding("utf-8");
                System.out.println("登录前");

                ftpClient.connect(host, port);

                flag = ftpClient.login(user, pass);
                System.out.println("登录后");
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
                return flag;
            }
        }

        protected void onPostExecute(Boolean flag) {
            if (flag) {
                Toast tot = Toast.makeText(
                        mContext,
                        "文件下载成功,保存在本地存储目录下的1ftpData文件夹",
                        Toast.LENGTH_LONG);
                tot.show();
            } else {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("抱歉，下载过程中出现错误，请重新尝试")
                        .setPositiveButton("确定", null)
                        .show();

            }
            pdialog.dismiss();
        }
    }


    public static void crSDFile(String... folder) {

        int length = folder.length;
        String genFolder = Environment.getExternalStorageDirectory().getPath().toString() +
                File.separator + "EDRMdata" + File.separator;

        File file, file2;
        file2 = new File(genFolder);
        if (!file2.exists()) {
            file2.mkdir();
        }

        String str = genFolder;
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

    void testDown(){
        String portStr = new Integer(port).toString();
        String can[] = new String[5];
        can[0] = host;
        can[1] = portStr;
        can[2] = user;
        can[3] = pass;
        //循环下载
        List<Boolean> booList = new ArrayList<>();
        for (int i = 0; i < ItemChooseData.getFilePath().size(); i++) {
            DownTask task = new DownTask(this, ItemChooseData.getFilePath().get(i));
            task.execute(can);
        }

    }

}

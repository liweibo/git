package com.example.zhuanchu.service;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Zip4jConstants;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.zhuanchu.HomeActivity;
import com.example.zhuanchu.adapter.VerticalAdapter;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import devlight.io.library.ntb.NavigationTabBar;


public class CompressOperate_zip4j {
    private static ZipFile zipFile;
    private static ZipParameters zipParameters;
    private static int result = 0; //状态返回值

    private static final String TAG = "CompressOperate_zip4j";
    private static  SqlHelper sqlHelper = null;
    private static SQLiteDatabase sqLiteDatabase = null;
    private static final int COMPLETED = 0;
//    private static Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            System.out.println( 8888 );
//        }
//    };


    /**
     *  zip4j压缩
     * @param filePath 要压缩的文件路径（可文件，可目录）
     * @param zipFilePath zip生成的文件路径
     * @param password  密码
     * @return 状态返回值
     */
    public static int compressZip4j(final String filePath, String zipFilePath, String password, final ProgressDialog pdialog, final Context context, final String filename) {

        sqlHelper = new SqlHelper(context);
        sqLiteDatabase = sqlHelper.getWritableDatabase();

        File sourceFile = new File(filePath);
        File zipFile_ = new File(zipFilePath);
//        try {
//
//        } catch (ZipException e) {
//            Log.e(TAG, "compressZip4j: 异常：" + e);
//            result = -1;
//            return result;
//        }

        try {
            zipFile = new ZipFile(zipFile_);
        } catch (ZipException e) {
            e.printStackTrace();
        }
        try {
            zipFile.setFileNameCharset("GBK"); //设置编码格式（支持中文）
        } catch (ZipException e) {
            e.printStackTrace();
        }

        zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); //压缩方式
        zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); // 压缩级别
        if (password != null && password != "") {   //是否要加密(加密会影响压缩速度)
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD); // 加密方式
            zipParameters.setPassword(password.toCharArray());
        }

        final ProgressMonitor progressMonitor = zipFile.getProgressMonitor();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {

                //System.out.println( progressMonitor.getPercentDone() );
            }
        }, 10, 500);// 设定指定的时间time,此处为2000毫秒



//            if (zipFile_.isDirectory()) {
//                String sourceFileName = checkString(sourceFile.getName()); //文件校验
//                zipFilePath = zipFilePath + "/" + sourceFileName + ".zip";
//                Log.i(TAG, "保存压缩文件的路径(zipFilePath)：" + zipFilePath);
//                compressZip4j(filePath,zipFilePath,password);
//            }
        if (sourceFile.isDirectory()) {
            //  File[] files = sourceFile.listFiles();
            //  ArrayList<File> arrayList = new ArrayList<File>();
            //  Collections.addAll(arrayList, files);
            zipFile.setRunInThread(true);
            try {
                zipFile.addFolder(sourceFile, zipParameters);
            } catch (ZipException e) {
                e.printStackTrace();
            }
        } else {
            zipFile.setRunInThread(true);
            try {
                zipFile.addFile(sourceFile, zipParameters);
            } catch (ZipException e) {
                e.printStackTrace();
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {



                boolean qidong = true;
                Cursor cursor = sqLiteDatabase.query("package", null, "name='"+ filename +"'", null, null, null, null);

                while (qidong){

                    System.out.println("Percentage done: " + progressMonitor.getPercentDone());
                    System.out.println("Current file: " + progressMonitor.getFileName());
                    pdialog.setProgress(progressMonitor.getPercentDone());
                    if( ( progressMonitor.getPercentDone() >= 99 && progressMonitor.getFileName() != null ) || progressMonitor.getFileName() == null ){
                        qidong = false;
                        pdialog.dismiss();

                        if( cursor.getCount() == 0 ){
                            sqLiteDatabase.execSQL("insert into package(name) values('"+ filename +"')");
                        }

                        Looper.prepare();
                        Toast.makeText(context.getApplicationContext(), "打包成功", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                    Log.i(TAG, "compressZip4j: 压缩成功");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

        //pdialog.dismiss();




        return result;
    }

    /**
     * 校验提取出的原文件名字是否带格式
     * @param sourceFileName 要压缩的文件名
     * @return
     */
    private static String checkString(String sourceFileName){
        if (sourceFileName.indexOf(".") > 0){
            sourceFileName = sourceFileName.substring(0,sourceFileName.length() - 4);
            Log.i(TAG, "checkString: 校验过的sourceFileName是：" + sourceFileName);
        }
        return sourceFileName;
    }

    /**
     *  zip4j解压
     * @param zipFilePath 待解压的zip文件（目录）路径
     * @param filePath 解压到的保存路径
     * @param password  密码
     * @return 状态返回值
     */
    public int uncompressZip4j(String zipFilePath, String filePath, String password) {
        File zipFile_ = new File(zipFilePath);
        File sourceFile = new File(filePath);

        try {
            zipFile = new ZipFile(zipFile_);
            zipFile.setFileNameCharset("GBK");  //设置编码格式（支持中文）
            if (!zipFile.isValidZipFile()){     //检查输入的zip文件是否是有效的zip文件
                throw new ZipException("压缩文件不合法,可能被损坏.");
            }
            if (sourceFile.isDirectory() && !sourceFile.exists()) {
                sourceFile.mkdir();
            }
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(filePath); //解压
            Log.i(TAG, "uncompressZip4j: 解压成功");

        } catch (ZipException e) {
            Log.e(TAG, "uncompressZip4j: 异常："+ e);
            result = -1;
            return result;
        }
        return result;
    }
}

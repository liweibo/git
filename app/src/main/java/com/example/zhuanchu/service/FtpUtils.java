package com.example.zhuanchu.service;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.test.espresso.core.deps.guava.io.CountingOutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class FtpUtils {
    public static FTPClient ftpClient;
    private String strencoding;

    static String host;
    static String user;
    static String pass;
    static int port;
    static String portStr;
    static Context mContext;
    static long dirFileTotalSize = 0;
    static int fileCount = 0;

    /**
     * FTP根目录.
     */
    public static final String REMOTE_PATH = "/";

    /**
     * FTP当前目录.
     */
    private static String currentPath = "";

    /**
     * init ftp servere
     */
    public FtpUtils(Context context) {
        this.mContext = context;
    }


    public static FTPClient connectServer(String ip, int port, String userName, String userPwd, String path) {
        ftpClient = new FTPClient();
        ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
        ftpClient.setAutodetectUTF8(true);//中文文件夹中的数据没法下载 需要该设置。
        try {
            // 连接
            ftpClient.connect(ip, port);
            // 登录
            ftpClient.login(userName, userPwd);
            ftpClient.setControlEncoding("UTF-8");
            if (path != null && path.length() > 0) {
                // 跳转到指定目录
                ftpClient.changeWorkingDirectory(path);

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ftpClient;
    }


    //gettask  backtask中使用
    public static boolean connectServerTask(String ip, int port, String userName, String userPwd, String path) {

        try {
            ftpClient = new FTPClient();
            ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
            ftpClient.setAutodetectUTF8(true);//中文文件夹中的数据没法下载 需要该设置。
            // 连接
            System.out.println("---来了-11--");
            ftpClient.connect(ip, port);
            System.out.println("---来了-22--");

            // 登录
            ftpClient.login(userName, userPwd);
            ftpClient.setControlEncoding("UTF-8");
            if (path != null && path.length() > 0) {
                // 跳转到指定目录
                ftpClient.changeWorkingDirectory(path);
            }
        } catch (Exception e) {
            System.out.println("连接异常打印");
//            e.printStackTrace();
            return false;
        }
        System.out.println("finnal连接异常打印");
        return true;


    }

    /**
     * @throws IOException function:关闭连接
     */
    public static void closeServer() {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param path
     * @return function:读取指定目录下的文件名
     * @throws IOException
     */
    public List<wxhFile> getFileList(String path) throws ParseException {
        List<wxhFile> fileLists = new ArrayList<wxhFile>();
        List<wxhFile> fileListsNoSpace = new ArrayList<wxhFile>();
        // 获得指定目录下所有文件名
        FTPFile[] ftpFiles = null;
        ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
        FTPFile[] listedDirectories = null;

        System.out.println("path的值：" + path);
        //path的值为当前点击的文件夹的完整路径。
        if (path.contains(" ")) {
            try {//以下几句代码的作用是为了避免该问题：当文件夹命名中带有空格无法进入该文件夹目录。
                ftpClient.cwd(path);
                listedDirectories = ftpClient.listFiles();
                ftpClient.cdup();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int dotsSpace = 0;
            for (int i = 0; i < listedDirectories.length; i++) {
                if (listedDirectories[i].getName().equals(".") || listedDirectories[i].getName().equals("..")) {
                    dotsSpace++;
                }
            }
            for (int i = dotsSpace; listedDirectories != null && i < listedDirectories.length; i++) {
                FTPFile file = listedDirectories[i];
                System.out.println("打印测试：" + file.getName());
                if (file.isFile()) {
                    FtpUtils.wxhFile a = new FtpUtils.wxhFile(path + file.getName(), file.getName(),
                            1, fileUtils.setTimeStamp(file.getTimestamp().getTime()), fileUtils.setSize(file.getSize()));
                    fileLists.add(a);
                } else if (file.isDirectory()) {
                    String name = file.getName();
                    if (name.equals(".") || name.equals("..")) continue;
                    FtpUtils.wxhFile a = new FtpUtils.wxhFile(path + file.getName(), file.getName(),
                            0, "", "");
                    fileLists.add(a);
                }
            }
            fileListsNoSpace.clear();
            fileListsNoSpace.addAll(fileLists);

        } else {

            try {
//            ftpClient.enterLocalActiveMode();
//            ftpClient.enterLocalPassiveMode();
                // 更改FTP目录
                ftpClient.changeWorkingDirectory(path);
                // 得到FTP当前目录下所有文件
                ftpFiles = ftpClient.listFiles();
//                ftpFiles = ftpClient.listFiles(path);
                System.out.println(ftpFiles.length + "长度");
            } catch (Exception e) {

                e.printStackTrace();
            }
            for (int i = 0; ftpFiles != null && i < ftpFiles.length; i++) {
                FTPFile file = ftpFiles[i];
                if (file.isFile()) {
                    System.out.println("文件夹下面的文件=====" + file.getName());
                    wxhFile a = new wxhFile(path + file.getName(), file.getName(),
                            1, fileUtils.setTimeStamp(file.getTimestamp().getTime()), fileUtils.setSize(file.getSize()));
                    fileListsNoSpace.add(a);
                } else if (file.isDirectory()) {
                    String name = file.getName();
                    if (name.equals(".") || name.equals("..")) continue;
                    System.out.println("文件夹名称为=====" + name);
                    wxhFile a = new wxhFile(path + file.getName(), file.getName(), 0
                            , "", "");
                    fileListsNoSpace.add(a);
                }
            }
        }


        return fileListsNoSpace;
    }

    //获取当前页面文件或文件夹的信息
    public List<FTPFile> getOriginFileList(String path, FTPClient ftpClient) {
        List<FTPFile> fileLists = new ArrayList<>();
        ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
        FTPFile[] listedDirectories = null;
        //path的值为当前点击的文件夹的完整路径。
        if (path.contains(" ")) {
            try {//以下几句代码的作用是为了避免该问题：当文件夹命名中带有空格无法进入该文件夹目录。
                ftpClient.cwd(path);
                listedDirectories = ftpClient.listFiles();
                ftpClient.cdup();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int i = 0; listedDirectories != null && i < listedDirectories.length; i++) {
                if (listedDirectories[i].getName().equals(".") || listedDirectories[i].getName().equals("..")) {
                } else {
                    FTPFile file = listedDirectories[i];
                    fileLists.add(file);
                }
            }


        } else {
            try {
                listedDirectories = ftpClient.listFiles(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int i = 0; listedDirectories != null && i < listedDirectories.length; i++) {
                if (listedDirectories[i].getName().equals(".") || listedDirectories[i].getName().equals("..")) {
                } else {
                    FTPFile file = listedDirectories[i];
                    fileLists.add(file);
                }
            }
        }

        return fileLists;
    }

    public List<wxhFile> firstgetFileList() throws ParseException {
        List<wxhFile> fileLists = new ArrayList<wxhFile>();
        // 获得指定目录下所有文件名
        FTPFile[] ftpFiles = null;
        try {
            ftpFiles = ftpClient.listFiles();//区别在这里，没有参数。
            System.out.println(ftpFiles.length + "长度");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; ftpFiles != null && i < ftpFiles.length; i++) {
            FTPFile file = ftpFiles[i];
            if (file.isFile()) {
                System.out.println("文件夹下面的文件first=====" + file.getName());
                wxhFile a = new wxhFile("" + file.getName(), file.getName(), 1, fileUtils.setTimeStamp(file.getTimestamp().getTime())
                        , fileUtils.setSize(file.getSize()));
                fileLists.add(a);
            } else if (file.isDirectory()) {
                String name = file.getName();
                if (name.equals(".") || name.equals("..")) continue;
                System.out.println("文件夹名称为first=====" + name);
                wxhFile a = new wxhFile("" + file.getName(), file.getName(), 0, "", "");
                fileLists.add(a);
            }
        }

        return fileLists;
    }

    /**
     * @param fileName
     * @return function:从服务器上读取指定的文件
     * @throws ParseException
     * @throws IOException
     */
    public String readFile(String fileName) throws ParseException {
        InputStream ins = null;
        StringBuilder builder = null;
        try {
            // 从服务器上读取指定的文件
            ins = ftpClient.retrieveFileStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins, strencoding));
            String line;
            builder = new StringBuilder(150);
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                builder.append(line);
            }
            reader.close();
            if (ins != null) {
                ins.close();
            }
            // 主动调用一次getReply()把接下来的226消费掉. 这样做是可以解决这个返回null问题
            ftpClient.getReply();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * @param fileName function:删除文件
     */
    public void deleteFile(String fileName) {
        try {
            ftpClient.deleteFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class wxhFile {
        public String filePath;
        public boolean isFile;
        public String filename;
        public String filetime;
        public String filesize;
        public boolean isParent = false;

        public wxhFile(String path, String name, int flag, String time, String size) {
            if (flag == 0) isFile = false;
            else if (flag == 1) isFile = true;
            else if (flag == 2) {
                isParent = true;
            }
            filePath = path;
            filename = name;
            filesize = size;
            filetime = time;
        }

        boolean ifFile() {
            return isFile;
        }

        String getFileName() {
            return filename;
        }
    }

    public static class ItemFresh {
        public int pos;
        public String sucfail;

        public int getPos() {
            return pos;
        }

        public String getSucfail() {
            return sucfail;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public void setSucfail(String sucfail) {
            this.sucfail = sucfail;
        }

        public ItemFresh(int pos, String sucfail) {
            this.pos = pos;
            this.sucfail = sucfail;
        }

    }

    private static long lastClickTime;

    public static boolean isFastClick(long ClickIntervalTime) {
        long ClickingTime = System.currentTimeMillis();
        if (ClickingTime - lastClickTime < ClickIntervalTime) {
            return true;
        }
        lastClickTime = ClickingTime;
        return false;
    }


    /**
     * 下载整个目录
     *
     * @param remotePath FTP目录
     * @param localPath  本地目录
     * @return Result 成功下载的文件数量
     * @throws IOException
     */
    public static void downloadFolder(String remotePath, String localPath) throws IOException {

        // 初始化FTP当前目录
        currentPath = remotePath;

        //以下两句代码结合 ftpClient.retrieveFile 可以避免文件名带有空格的问题
        // 更改FTP目录
        ftpClient.changeWorkingDirectory(remotePath);
        // 得到FTP当前目录下所有文件
        FTPFile[] ftpFiles = ftpClient.listFiles();

        //在本地创建对应文件夹目录
        localPath = localPath + "/" + remotePath.substring(remotePath.lastIndexOf("/"));
        System.out.println("remotePath.substring:" + remotePath.substring(remotePath.lastIndexOf("/")));
        System.out.println("文件夹：localPath:" + localPath);
        File localFolder = new File(localPath);
        if (!localFolder.exists()) {
            localFolder.mkdirs();
        }
        // 循环遍历
        for (FTPFile ftpFile : ftpFiles) {
            if (!ftpFile.getName().equals("..")
                    && !ftpFile.getName().equals(".")) {
                if (ftpFile.isDirectory()) {
                    //下载文件夹
                    downloadFolder(currentPath + "/" + ftpFile.getName(), localPath);
                } else if (ftpFile.isFile()) {
                    // 下载单个文件
//                    boolean flag = downloadSingle(new File(localPath + "/" + ftpFile.getName()), ftpFile);
                    new DirDownTask(mContext, "", "").
                            download(new File(localPath + "/" + ftpFile.getName()),
                                    ftpFile, ftpClient);

                }
            }
        }
    }

    public static void setZero() {
        dirFileTotalSize = 0;
    }

    public static long getRealSize() {
        return dirFileTotalSize;
    }

    public static void setfileCountZero() {
        fileCount = 0;
    }

    public static int getfileCount() {
        return fileCount;
    }

    //计算某文件夹下所有的文件的总大小
    public static void calculateDirFileTotalSize(String remotePath) throws IOException {

        // 初始化FTP当前目录
        currentPath = remotePath;
        //以下两句代码结合 ftpClient.retrieveFile 可以避免文件名带有空格的问题
        // 更改FTP目录
        ftpClient.changeWorkingDirectory(remotePath);
        // 得到FTP当前目录下所有文件
        FTPFile[] ftpFiles = ftpClient.listFiles();

        // 循环遍历
        for (FTPFile ftpFile : ftpFiles) {
            if (!ftpFile.getName().equals("..")
                    && !ftpFile.getName().equals(".")) {
                if (ftpFile.isDirectory()) {
                    System.out.println("总大小--文件夹：" + ftpFile.getName());
                    //下载文件夹
                    calculateDirFileTotalSize(currentPath + "/" + ftpFile.getName());
                } else if (ftpFile.isFile()) {
                    System.out.println("总大小--文件：" + ftpFile.getName());

                    //计算当前文件的大小 并 累加
                    dirFileTotalSize += ftpFile.getSize();
                    fileCount++;
                }
            }
        }
    }


    /**
     * 下载单个文件
     *
     * @param localFile 本地目录
     * @param ftpFile   FTP文件
     * @return true下载成功, false下载失败
     * @throws IOException
     */
    public static boolean downloadSingle(File localFile, FTPFile ftpFile) throws IOException {
        boolean flag;
        // 创建输出流
        OutputStream outputStream = new FileOutputStream(localFile);
        CountingOutputStream cos = new CountingOutputStream(outputStream);

        // 下载单个文件
        flag = ftpClient.retrieveFile(ftpFile.getName(), cos);//这种方式下载可以避免文件名中带有空格问题
        // 关闭文件流
        outputStream.close();
        return flag;
    }


    public static String crSDFile(String... folder) {

        int length = folder.length;
        String genFolder8 = Environment.getExternalStorageDirectory().getPath() +
                File.separator + "CRRC";
        File file, file2, file3, file4, file8, filejidongcheng;
        file8 = new File(genFolder8);
        if (!file8.exists()) {
            file8.mkdir();
        }

        String genFolder = genFolder8 +
                File.separator + "DOWNLOAD";
        file2 = new File(genFolder);
        if (!file2.exists()) {
            file2.mkdir();
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("filedirname",
                Activity.MODE_PRIVATE);
        String jidongcheng = sharedPreferences.getString("chexing", "");//机车产品线  ..文件夹创建
        if (jidongcheng.equals("机车")) {
            jidongcheng = "机车产品线";
        } else if (jidongcheng.equals("动车")) {
            jidongcheng = "动车产品线";

        } else if (jidongcheng.equals("城轨")) {
            jidongcheng = "城轨产品线";
        }
        String dir1 = sharedPreferences.getString("filedirnamevalue", "");
        String dir2 = sharedPreferences.getString("shebeinamevalue", "");
        System.out.println("一级：" + dir1);
        System.out.println("二级：" + dir2);
////        机车产品线 等文件夹创建
//        String genFolderJidongcheng = genFolder +
//                File.separator + jidongcheng + File.separator;
//        filejidongcheng = new File(genFolderJidongcheng);
//        if (!filejidongcheng.exists()) {
//            filejidongcheng.mkdir();
//        }

        String genFolder1 = genFolder +
                File.separator + dir1;
        file3 = new File(genFolder1);
        if (!file3.exists()) {
            file3.mkdir();
        }

        String genFolder2 = genFolder1 +
                File.separator + dir2 + File.separator;
        file4 = new File(genFolder2);
        if (!file4.exists()) {
            file4.mkdir();
        }


        String str = genFolder2;
        for (int i = 0; i < length; i++) {

            str = str + folder[i] + "/";
            file = new File(str);

            if (!file.exists()) {
                file.mkdir();

            }

        }

        System.out.println("111路径：" + str);
        return str;

    }


    public static String crSDFileTask(String... folder) {

        int length = folder.length;
        String genFolder8 = Environment.getExternalStorageDirectory().getPath().toString() +
                File.separator + "CRRC";
        File file, file2, file3, file4, file8, filejidongcheng;
        file8 = new File(genFolder8);
        if (!file8.exists()) {
            file8.mkdir();
        }

        String genFolder = genFolder8 +
                File.separator + "DOWNLOAD";
        file2 = new File(genFolder);
        if (!file2.exists()) {
            file2.mkdir();
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("filedirname",
                Activity.MODE_PRIVATE);

        String dir1 = sharedPreferences.getString("filedirnamevalue", "");
        String dir2 = sharedPreferences.getString("shebeinamevalue", "");
        System.out.println("一级：" + dir1);
        System.out.println("二级：" + dir2);

        String genFolder1 = genFolder +
                File.separator + dir1;
        file3 = new File(genFolder1);
        String genFolder2 = "";
        if (file3.exists()) {
            genFolder2 = genFolder1 +
                    File.separator + dir2 + File.separator;
        } else {
            return genFolder;
        }


        file4 = new File(genFolder2);
        String str = "";
        if (file4.exists()) {
            str = genFolder2;
        } else {
            return genFolder1;
        }


        for (int i = 0; i < length; i++) {

            str = str + folder[i] + "/";
            file = new File(str);
            if (file.exists()) {
                continue;
            } else {
                return str;
            }

        }

        System.out.println("task中路径：" + str);
        return str;

    }

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

    public List<String> getRemoteFileSize(List<FTPFile> taskFtpFiles) {
        List<String> remotSize = new ArrayList<>();
        for (int i = 0; i < taskFtpFiles.size(); i++) {
            if (taskFtpFiles.get(i).isFile()) {

            } else if (taskFtpFiles.get(i).isDirectory()) {

            }
        }
        return remotSize;
    }

}
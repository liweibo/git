package com.example.zhuanchu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemChooseData {
    private static List<Integer> listindex = new ArrayList<>();
    private static List<String> listFilePath = new ArrayList<>();
    public static List<String> listFilename = new ArrayList<>();
    public static List<Map<String, Boolean>> fileDownloadSucOrFail = new ArrayList<>();
    public static List<String> fileDownloadSucOrFailName = new ArrayList<>();
    public static List<Boolean> fileDownloadSucOrFailTrueOrfalse = new ArrayList<>();

    public static void addDownloadSucOrFail(String file, boolean sucOrFail) {//记录文件是否下载成功
        fileDownloadSucOrFailName.add(file);
        fileDownloadSucOrFailTrueOrfalse.add(sucOrFail);
    }

    public static List<Boolean> getDownloadSucOrFailIsTrueOrFalse() {
        return fileDownloadSucOrFailTrueOrfalse;
    }

    public static List<String> getDownloadSucOrFailNamew() {
        return fileDownloadSucOrFailName;
    }
//    public static void removeDownloadSucOrFail(String file, boolean sucOrFail) {//删除记录的文件是否下载成功值
//        getDownloadSucOrFail().clear();
//    }


    public static void removeIndex(int i) {
        if (listindex.size() > 0 && listindex.size() > i) {
            listindex.remove(i);
        }
    }

    public static void addIndex(int i) {
        listindex.add(i);
    }

    public static List<Integer> getIndexArr() {
        return listindex;
    }

    public static void addFilePath(String filepath) {
        listFilePath.add(filepath);

    }

    public static void addFileName(String filename) {
        listFilename.add(filename);

    }

    public static List<String> getFileName() {
        return listFilename;
    }

    public static void removeFileName(int i) {
        if (getFileName().size() > 0) {
            //不能按序号直接删，应根据序号先找出currentFiles中对应的文件的name
            // ，再删除该路径
            String s1 = selectFileActivity.currentFiles.get(i).filename;
            System.out.println("对应name：" + s1);
            int x1 = getFileName().indexOf(s1);
            System.out.println("搜索序号name：" + x1);
            getFileName().remove(x1);
        }
    }


    public static List<String> getFilePath() {
        return listFilePath;
    }

    public static void clearFilePath() {
        listFilePath.clear();
    }

    public static void removeFilePath(int i) {
        if (getFilePath().size() > 0) {
            //不能按序号直接删，应根据序号先找出currentFiles中对应的文件的路径，再删除该路径
            String s1 = selectFileActivity.currentFiles.get(i).filePath;
            System.out.println("对应：" + s1);
            int x1 = getFilePath().indexOf(s1);
            System.out.println("搜索序号：" + x1);
            getFilePath().remove(x1);
        }
    }
}

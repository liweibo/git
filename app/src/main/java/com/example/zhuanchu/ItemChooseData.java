package com.example.zhuanchu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemChooseData {
    public static List<String> listFilePath = new ArrayList<>();
    public static List<String> listFilename = new ArrayList<>();
    public static List<String> fileDownloadSucOrFailName = new ArrayList<>();
    public static List<Boolean> fileDownloadSucOrFailTrueOrfalse = new ArrayList<>();

    public static synchronized  void addDownloadSucOrFail(String file, boolean sucOrFail) {//记录文件是否下载成功
        fileDownloadSucOrFailName.add(file);
        fileDownloadSucOrFailTrueOrfalse.add(sucOrFail);
    }


    public static synchronized void addFileName(String filename) {
        listFilename.add(filename);
    }

    public static  List<String> getFileName() {//去重
        List<String> newList = new ArrayList<String>();
        for (String cd : listFilename) {
            if (!newList.contains(cd)) {
                newList.add(cd);
            }
        }
        listFilename.clear();
        listFilename.addAll(newList);
        return newList;
    }

    public static synchronized void removeFileName(int i) {
        if (listFilename.size() > 0) {
            //不能按序号直接删，应根据序号先找出currentFiles中对应的文件的name
            // ，再删除该路径
            String s1 = selectFileActivity.currentFiles.get(i).filename;

            int x1 = listFilename.indexOf(s1);//这里容易出错
            System.out.println("搜索序号name：" + x1);
            if (x1 != -1) {
//                getFileName().remove(x1);
                Iterator<String> iterator = listFilename.iterator();
                while (iterator.hasNext()) {
                    String item = iterator.next();
                    if (item.equals(s1)) {
                        iterator.remove();
                    }
                }

            }

        }
    }


    public static synchronized void addFilePath(String filepath) {
        listFilePath.add(filepath);
//        System.out.println("添加下载路径：" + listFilePath.toString());
    }

    public static synchronized List<String> getFilePath() {//去重  异步处理
        List<String> newList = new ArrayList<String>();
        for (String cd : listFilePath) {
            if (!newList.contains(cd)) {
                newList.add(cd);
            }
        }
        listFilePath.clear();
        listFilePath.addAll(newList);
        System.out.println("listFilePath变了没:" + listFilePath.toString());
        return newList;
    }


    public static synchronized void removeFilePath(int i) {
        if (listFilePath.size() > 0) {
            //不能按序号直接删，应根据序号先找出currentFiles中对应的文件的路径，再删除该路径
            String s1 = selectFileActivity.currentFiles.get(i).filePath;
            System.out.println("需要删除的某下载路径：" + s1 + "--对应的列表项位置：" + i);
            System.out.println("已勾选过的所有下载路径：" + listFilePath.toString());
            int x1 = listFilePath.indexOf(s1);//当x1为-1时 代表 s1不在listFilePath中
            System.out.println("需删除下载路径在勾选过的下载路径的第几个：" + x1);
            if (x1 != -1) {
                Iterator<String> iterator = listFilePath.iterator();
                while (iterator.hasNext()) {
                    String item = iterator.next();
                    if (item.equals(s1)) {
                        System.out.println(item + "========" + s1);
                        iterator.remove();
                    }
                }
            }
            System.out.println("remove过后的下载路径列表：" + listFilePath);
        }
    }
    public static synchronized void clearFilePath(){
        listFilePath.clear();
    }
    public static synchronized void clearFileName(){
        listFilename.clear();
    }

}

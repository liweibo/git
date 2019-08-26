package com.example.zhuanchu.service;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import android.util.Log;

import java.io.File;
import java.io.IOException;


public class CompressOperate_zip4j {
    private static ZipFile zipFile;
    private static ZipParameters zipParameters;
    private static int result = 0; //状态返回值

    private static final String TAG = "CompressOperate_zip4j";

    /**
     *  zip4j压缩
     * @param filePath 要压缩的文件路径（可文件，可目录）
     * @param zipFilePath zip生成的文件路径
     * @param password  密码
     * @return 状态返回值
     */
    public static int compressZip4j(String filePath, String zipFilePath, String password) {
        File sourceFile = new File(filePath);
        File zipFile_ = new File(zipFilePath);
        try {
            zipFile = new ZipFile(zipFile_);
            zipFile.setFileNameCharset("GBK"); //设置编码格式（支持中文）

            zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); //压缩方式
            zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); // 压缩级别
            if (password != null && password != "") {   //是否要加密(加密会影响压缩速度)
                zipParameters.setEncryptFiles(true);
                zipParameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD); // 加密方式
                zipParameters.setPassword(password.toCharArray());
            }

            if (zipFile_.isDirectory()) {
                String sourceFileName = checkString(sourceFile.getName()); //文件校验
                zipFilePath = zipFilePath + "/" + sourceFileName + ".zip";
                Log.i(TAG, "保存压缩文件的路径(zipFilePath)：" + zipFilePath);
                compressZip4j(filePath,zipFilePath,password);
            }
            if (sourceFile.isDirectory()) {
                //  File[] files = sourceFile.listFiles();
                //  ArrayList<File> arrayList = new ArrayList<File>();
                //  Collections.addAll(arrayList, files);
                zipFile.addFolder(sourceFile, zipParameters);
            } else {
                zipFile.addFile(sourceFile, zipParameters);
            }
            Log.i(TAG, "compressZip4j: 压缩成功");
        } catch (ZipException e) {
            Log.e(TAG, "compressZip4j: 异常：" + e);
            result = -1;
            return result;
        }
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

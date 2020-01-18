package com.example.zhuanchu.bean;

public class DownloadindBean {
    public int current;//下载进度
    public String fileName;//当前下载的文件名称
    public String downloaded;//文件是否已下载完成

    public int getCurrent() {
        return current;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(String downloaded) {
        this.downloaded = downloaded;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

package com.example.yangchengyu.materialdesign.entity;

/**
 * Created by YangChengyu on 2017/4/8.
 */

public class TaskInfo {
    private boolean isOnDownloading;
    private String taskID;
    private String fileName;
    private String url;
    private long fileLength = 0;
    private long finished = 0;
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public boolean isOnDownloading() {
        return isOnDownloading;
    }

    public void setOnDownloading(boolean onDownloading) {
        isOnDownloading = onDownloading;
    }

    public int getProgress() {
        if (fileLength == 0) {
            return 0;
        } else {
            return ((int) (100 * finished / fileLength));
        }
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

}

package com.example.yangchengyu.materialdesign.entity;

/**
 * Created by YangChengyu on 2017/4/8.
 */

public class DBDownloadInfo {
    private String taskId;
    private String URL;
    private String fileName;
    private String filePath;
    private int begin;
    private int end;
    private long finished;
    private long length;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public DBDownloadInfo(String taskId, String URL, String fileName, int begin, int end, int finished, int length) {
        this.taskId = taskId;
        this.URL = URL;
        this.fileName = fileName;
        this.begin = begin;
        this.end = end;
        this.finished = finished;
        this.length = length;
    }

    public DBDownloadInfo() {

    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }


    @Override
    public String toString() {
        return "taskID=" + taskId + ";url=" + URL + ";filePath=" + filePath + ";fileName=" + fileName + ";length=" + length + ";finished=" + finished;
    }
}

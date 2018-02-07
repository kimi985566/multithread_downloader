package com.example.yangchengyu.materialdesign.downloadHelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.yangchengyu.materialdesign.dbHelper.DataKeeper;
import com.example.yangchengyu.materialdesign.dbHelper.FileHelper;
import com.example.yangchengyu.materialdesign.entity.DBDownloadInfo;
import com.example.yangchengyu.materialdesign.entity.TaskInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by YangChengyu on 2017/4/3.
 */

public class DownloadManager {
    private Context mContext;
    private ArrayList<Downloader> mTaskList = new ArrayList<Downloader>();
    private final int MAX_DOWNLOADING_TASK = 5; // 最大同时下载数
    private Downloader.DownLoadSuccess mDownLoadSuccess = null;
    private boolean isSupportBreakpoint = false;
    private ThreadPoolExecutor pool;
    private SharedPreferences mSharedPreferences;
    private DownloadListener mDownloadListener;
    private DBDownloadInfo mDBDownloadInfo;

    public DownloadManager() {

    }

    public DownloadManager(Context context) {
        this.mContext = context;
        init(context);
    }

    private void init(Context context) {
        pool = new ThreadPoolExecutor(
                MAX_DOWNLOADING_TASK, MAX_DOWNLOADING_TASK, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2000));

        mDownLoadSuccess = new Downloader.DownLoadSuccess() {
            @Override
            public void onTaskSeccess(String TaskID) {
                int taskSize = mTaskList.size();
                for (int i = 0; i < taskSize; i++) {
                    Downloader deletedownloader = mTaskList.get(i);
                    if (deletedownloader.getTaskID().equals(TaskID)) {
                        mTaskList.remove(deletedownloader);
                        return;
                    }
                }
            }
        };
        mSharedPreferences = mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        recoverData(mContext);
    }


    private void recoverData(Context context) {
        stopAllTask();
        mTaskList = new ArrayList<>();
        DataKeeper datakeeper = new DataKeeper(context);
        ArrayList<DBDownloadInfo> sqlDownloadInfoList = null;
        sqlDownloadInfoList = datakeeper.getAllDownLoadInfo();

        if (sqlDownloadInfoList.size() > 0) {
            int listSize = sqlDownloadInfoList.size();
            for (int i = 0; i < listSize; i++) {
                DBDownloadInfo sqlDownLoadInfo = sqlDownloadInfoList.get(i);
                Downloader sqlDownLoader = new Downloader(context, sqlDownLoadInfo, pool, isSupportBreakpoint, false);
                sqlDownLoader.setDownLodSuccesslistener(mDownLoadSuccess);
                sqlDownLoader.setDownLoadListener("public", mDownloadListener);
                mTaskList.add(sqlDownLoader);
            }
        }
    }

    public void setSupportBreakpoint(boolean isSupportBreakpoint) {
        if ((!this.isSupportBreakpoint) && isSupportBreakpoint) {
            int taskSize = mTaskList.size();
            for (int i = 0; i < taskSize; i++) {
                Downloader downloader = mTaskList.get(i);
                downloader.setSupportBreakpoint(true);
            }
        }
        this.isSupportBreakpoint = isSupportBreakpoint;
    }

    public int addTask(String TaskID, String url, String fileName) {
        return addTask(TaskID, url, fileName, null);
    }

    public int addTask(String TaskID, String url, String fileName, String filepath) {
        if (TaskID == null) {
            TaskID = fileName;
        }
        int state = getAttachmentState(TaskID, fileName, filepath);
        if (state != 1) {
            return state;
        }
        DBDownloadInfo downloadinfo = new DBDownloadInfo();
        downloadinfo.setFinished(0);
        downloadinfo.setLength(0);
        downloadinfo.setTaskId(TaskID);
        downloadinfo.setFileName(fileName);
        downloadinfo.setURL(url);
        if (filepath == null) {
            downloadinfo.setFilePath(FileHelper.getFileDefaultPath() + "/(" + FileHelper.filterIDChars(TaskID) + ")" + fileName);
        } else {
            downloadinfo.setFilePath(filepath);
        }
        Downloader taskDownLoader = new Downloader(mContext, downloadinfo, pool, isSupportBreakpoint, true);
        taskDownLoader.setDownLodSuccesslistener(mDownLoadSuccess);
        if (isSupportBreakpoint) {
            taskDownLoader.setSupportBreakpoint(true);
        } else {
            taskDownLoader.setSupportBreakpoint(false);
        }
        taskDownLoader.start();
        taskDownLoader.setDownLoadListener("public", mDownloadListener);
        mTaskList.add(taskDownLoader);
        return 1;
    }

    private int getAttachmentState(String TaskID, String fileName, String filepath) {
        int taskSize = mTaskList.size();
        for (int i = 0; i < taskSize; i++) {
            Downloader downloader = mTaskList.get(i);
            if (downloader.getTaskID().equals(TaskID)) {
                return 0;
            }
        }
        File file = null;
        if (filepath == null) {
            file = new File(FileHelper.getFileDefaultPath() + "/(" + FileHelper.filterIDChars(TaskID) + ")" + fileName);
            if (file.exists()) {
                return -1;
            }
        } else {
            file = new File(filepath);
            if (file.exists()) {
                return -1;
            }
        }
        return 1;
    }

    public ArrayList<TaskInfo> getAllTask() {
        ArrayList<TaskInfo> taskInfolist = new ArrayList<TaskInfo>();
        int listSize = mTaskList.size();
        for (int i = 0; i < listSize; i++) {
            Downloader deletedownloader = mTaskList.get(i);
            DBDownloadInfo sqldownloadinfo = deletedownloader.getSQLDownLoadInfo();
            TaskInfo taskinfo = new TaskInfo();
            taskinfo.setFileName(sqldownloadinfo.getFileName());
            taskinfo.setOnDownloading(deletedownloader.isDownLoading());
            taskinfo.setTaskID(sqldownloadinfo.getTaskId());
            taskinfo.setFileLength(sqldownloadinfo.getLength());
            taskinfo.setFinished(sqldownloadinfo.getFinished());
            taskInfolist.add(taskinfo);
        }
        return taskInfolist;
    }

    public void startTask(String taskID) {
        int listSize = mTaskList.size();
        for (int i = 0; i < listSize; i++) {
            Downloader deletedownloader = mTaskList.get(i);
            if (deletedownloader.getTaskID().equals(taskID)) {
                deletedownloader.start();
                break;
            }
        }
    }

    public void stopTask(String taskID) {
        int listSize = mTaskList.size();
        for (int i = 0; i < listSize; i++) {
            Downloader deletedownloader = mTaskList.get(i);
            if (deletedownloader.getTaskID().equals(taskID)) {
                deletedownloader.stop();
                break;
            }
        }
    }

    public void startAllTask() {
        int listSize = mTaskList.size();
        for (int i = 0; i < listSize; i++) {
            Downloader deletedownloader = mTaskList.get(i);
            deletedownloader.start();
        }
    }

    public void stopAllTask() {
        int listSize = mTaskList.size();
        for (int i = 0; i < listSize; i++) {
            Downloader deletedownloader = mTaskList.get(i);
            deletedownloader.stop();
        }
    }

    private Downloader getDownloader(String taskID) {
        for (int i = 0; i < mTaskList.size(); i++) {
            Downloader downloader = mTaskList.get(i);
            if (taskID != null && taskID.equals(downloader.getTaskID())) {
                return downloader;
            }
        }
        return null;
    }

    public void setAllTaskListener(DownloadListener listener) {
        mDownloadListener = listener;
        int listSize = mTaskList.size();
        for (int i = 0; i < listSize; i++) {
            Downloader deletedownloader = mTaskList.get(i);
            deletedownloader.setDownLoadListener("public", listener);
        }
    }
}

package com.example.yangchengyu.materialdesign.downloadHelper;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.example.yangchengyu.materialdesign.dbHelper.DataKeeper;
import com.example.yangchengyu.materialdesign.dbHelper.FileHelper;
import com.example.yangchengyu.materialdesign.entity.DBDownloadInfo;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by YangChengyu on 2017/4/1.
 */

public class Downloader {
    private static final int TASK_START = 0;
    private static final int TASK_STOP = 1;
    private static final int TASK_PROGRESS = 2;
    private static final int TASK_ERROR = 3;
    private static final int TASK_SUCCESS = 4;

    private DataKeeper mDataKeeper;
    private HashMap<String, DownloadListener> mListenerHashMap;
    private DownLoadSuccess mDownLoadSuccess;
    public DBDownloadInfo mDBDownloadInfo;
    private long fileLength = 0;//文件总大小
    private long finished = 0;//已经下载的文件的大小
    private int downloadtimes = 0;//当前尝试请求的次数
    private int maxdownloadtimes = 3;//失败重新请求次数
    private boolean ondownload = false;//当前下载状态
    private DownLoadThread downLoadThread;
    private ThreadPoolExecutor pool;//线程池
    private boolean isSupportBreakpoint = false;//标识服务器是否支持断点续传
    private final String TEMP_FILEPATH = FileHelper.getTempDirPath();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == TASK_START) { //开始下载
                startNotice();
            } else if (msg.what == TASK_STOP) { //停止下载
                stopNotice();
            } else if (msg.what == TASK_PROGRESS) { //改变进程
                onProgressNotice();
            } else if (msg.what == TASK_ERROR) { //下载出错
                errorNotice();
            } else if (msg.what == TASK_SUCCESS) { //下载完成
                successNotice();
            }
        }
    };

    public Downloader() {

    }

    public Downloader(Context context, DBDownloadInfo dbDownloadInfo, ThreadPoolExecutor threadPoolExecutor, boolean isSupportBreakpoint, boolean isNewTask) {
        this.mDBDownloadInfo = dbDownloadInfo;
        this.pool = threadPoolExecutor;
        this.isSupportBreakpoint = isSupportBreakpoint;
        this.fileLength = dbDownloadInfo.getLength();
        this.finished = dbDownloadInfo.getFinished();
        this.mDataKeeper = new DataKeeper(context);
        this.mListenerHashMap = new HashMap<>();

        if (isNewTask) {
            saveDownloadInfo();
        }
    }

    public String getTaskID() {
        return mDBDownloadInfo.getTaskId();
    }

    public void start() {
        if (downLoadThread == null) {
            downloadtimes = 0;
            ondownload = true;
            handler.sendEmptyMessage(TASK_START);
            downLoadThread = new DownLoadThread();
            pool.execute(downLoadThread);
        }
    }

    public void stop() {
        if (downLoadThread != null) {
            ondownload = false;
            downLoadThread.stopDownLoad();
            pool.remove(downLoadThread);
            downLoadThread = null;
        }
    }

    public void setDownLoadListener(String key, DownloadListener listener) {
        if (listener == null) {
            removeDownLoadListener(key);
        } else {
            mListenerHashMap.put(key, listener);
        }
    }

    public void removeDownLoadListener(String key) {
        if (mListenerHashMap.containsKey(key)) {
            mListenerHashMap.remove(key);
        }
    }

    public void setDownLodSuccesslistener(DownLoadSuccess downloadsuccess) {
        this.mDownLoadSuccess = downloadsuccess;
    }

    public void destroy() {
        if (downLoadThread != null) {
            downLoadThread.stopDownLoad();
            downLoadThread = null;
        }
        mDataKeeper.deleteDownLoadInfo(mDBDownloadInfo.getTaskId());
        File downloadFile = new File(TEMP_FILEPATH + "/(" + FileHelper.filterIDChars(mDBDownloadInfo.getTaskId()) + ")" + getFileName(mDBDownloadInfo.getURL()));
        if (downloadFile.exists()) {
            downloadFile.delete();
        }
    }

    /**
     * (保存下载信息至数据库)
     */
    private void saveDownloadInfo() {
        if (isSupportBreakpoint) {
            mDBDownloadInfo.setFinished(finished);
            mDataKeeper.saveDownloadInfo(mDBDownloadInfo);
        }
    }

    /**
     * (通知监听器，任务已开始下载)
     */
    private void startNotice() {
        if (!mListenerHashMap.isEmpty()) {
            Collection<DownloadListener> c = mListenerHashMap.values();
            Iterator<DownloadListener> it = c.iterator();
            while (it.hasNext()) {
                DownloadListener listener = it.next();
                listener.onStart(getSQLDownLoadInfo());
            }
        }
    }

    /**
     * (通知监听器，当前任务进度)
     */
    private void onProgressNotice() {
        if (!mListenerHashMap.isEmpty()) {
            Collection<DownloadListener> c = mListenerHashMap.values();
            Iterator<DownloadListener> it = c.iterator();
            while (it.hasNext()) {
                DownloadListener listener = it.next();
                listener.onProgress(getSQLDownLoadInfo(), isSupportBreakpoint);
            }
        }
    }

    /**
     * (通知监听器，当前任务已停止)
     */
    private void stopNotice() {
        if (!isSupportBreakpoint) {
            finished = 0;
        }
        if (!mListenerHashMap.isEmpty()) {
            Collection<DownloadListener> c = mListenerHashMap.values();
            Iterator<DownloadListener> it = c.iterator();
            while (it.hasNext()) {
                DownloadListener listener = it.next();
                listener.onStop(getSQLDownLoadInfo(), isSupportBreakpoint);
            }
        }
    }

    /**
     * (通知监听器，当前任务异常，并进入停止状态)
     */
    private void errorNotice() {
        if (!mListenerHashMap.isEmpty()) {
            Collection<DownloadListener> c = mListenerHashMap.values();
            Iterator<DownloadListener> it = c.iterator();
            while (it.hasNext()) {
                DownloadListener listener = it.next();
                listener.onError(getSQLDownLoadInfo());
            }
        }
    }

    /**
     * (通知监听器，当前任务成功执行完毕)
     */
    private void successNotice() {
        if (!mListenerHashMap.isEmpty()) {
            Collection<DownloadListener> c = mListenerHashMap.values();
            Iterator<DownloadListener> it = c.iterator();
            while (it.hasNext()) {
                DownloadListener listener = it.next();
                listener.onSuccess(getSQLDownLoadInfo());
            }
        }
        if (mDownLoadSuccess != null) {
            mDownLoadSuccess.onTaskSeccess(mDBDownloadInfo.getTaskId());
        }
    }

    /**
     * 类功能描述：该接口用于在任务执行完之后通知下载管理器,以便下载管理器将已完成的任务移出任务列表
     */
    public interface DownLoadSuccess {
        void onTaskSeccess(String TaskID);
    }

    /**
     * 当前任务进行的状态
     */
    public boolean isDownLoading() {
        return ondownload;
    }

    /**
     * (获取当前任务对象)
     *
     * @return
     */
    public DBDownloadInfo getSQLDownLoadInfo() {
        mDBDownloadInfo.setFinished(finished);
        return mDBDownloadInfo;
    }

    /**
     * (设置是否支持断点续传)
     *
     */
    public void setSupportBreakpoint(boolean isSupportBreakpoint) {
        this.isSupportBreakpoint = isSupportBreakpoint;
    }

    class DownLoadThread extends Thread {
        private boolean isdownloading;
        private URL url;
        private RandomAccessFile localFile;
        private HttpURLConnection urlConn;
        private InputStream inputStream;
        private int progress = -1;

        public DownLoadThread() {
            isdownloading = true;
        }

        @Override
        public void run() {
            while (downloadtimes < maxdownloadtimes) { //做3次请求的尝试
                try {
                    if (finished == fileLength
                            && fileLength > 0) {
                        ondownload = false;
                        Message msg = new Message();
                        msg.what = TASK_PROGRESS;
                        msg.arg1 = 100;
                        handler.sendMessage(msg);
                        downloadtimes = maxdownloadtimes;
                        downLoadThread = null;
                        return;
                    }
                    url = new URL(mDBDownloadInfo.getURL());
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setConnectTimeout(5000);
                    urlConn.setReadTimeout(10000);
                    if (fileLength < 1) {//第一次下载，初始化
                        openConnention();
                    } else {
                        File file = new File(TEMP_FILEPATH + "/(" + FileHelper.filterIDChars(mDBDownloadInfo.getTaskId()) + ")" + getFileName(mDBDownloadInfo.getURL()));
                        localFile = new RandomAccessFile(file, "rwd");
                        localFile.seek(finished);
                        urlConn.setRequestProperty("Range", "bytes=" + finished + "-");
                    }
                    inputStream = urlConn.getInputStream();
                    byte[] buffer = new byte[1024 * 1024];
                    int length = 0;
                    while ((length = inputStream.read(buffer)) != -1 && isdownloading) {
                        localFile.write(buffer, 0, length);
                        finished += length;
                        int nowProgress = (int) ((100 * finished) / fileLength);
                        if (nowProgress > progress) {
                            progress = nowProgress;
                            handler.sendEmptyMessage(TASK_PROGRESS);
                        }
                    }
                    //下载完了
                    if (finished == fileLength) {
                        handler.sendEmptyMessage(TASK_SUCCESS);
                        mDataKeeper.deleteDownLoadInfo(mDBDownloadInfo.getTaskId());
                        downLoadThread = null;
                        ondownload = false;
                    }
                    downloadtimes = maxdownloadtimes;
                } catch (Exception e) {
                    if (isdownloading) {
                        if (isSupportBreakpoint) {
                            downloadtimes++;
                            if (downloadtimes >= maxdownloadtimes) {
                                if (fileLength > 0) {
                                    saveDownloadInfo();
                                }
                                pool.remove(downLoadThread);
                                downLoadThread = null;
                                ondownload = false;
                                handler.sendEmptyMessage(TASK_ERROR);
                            }
                        } else {
                            finished = 0;
                            downloadtimes = maxdownloadtimes;
                            ondownload = false;
                            downLoadThread = null;
                            handler.sendEmptyMessage(TASK_ERROR);
                        }

                    } else {
                        downloadtimes = maxdownloadtimes;
                    }
                    e.printStackTrace();
                } finally {
                    try {
                        if (urlConn != null) {
                            urlConn.disconnect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (localFile != null) {
                            localFile.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        public void stopDownLoad() {
            isdownloading = false;
            downloadtimes = maxdownloadtimes;
            if (fileLength > 0) {
                saveDownloadInfo();
            }
            handler.sendEmptyMessage(TASK_STOP);
        }

        private void openConnention() throws Exception {
            long urlfilesize = urlConn.getContentLength();
            if (urlfilesize > 0) {
                isFolderExist();
                localFile = new RandomAccessFile(TEMP_FILEPATH + "/(" + FileHelper.filterIDChars(mDBDownloadInfo.getTaskId()) + ")" + getFileName(mDBDownloadInfo.getURL()), "rwd");
                localFile.setLength(urlfilesize);
                mDBDownloadInfo.setLength(urlfilesize);
                fileLength = urlfilesize;
                if (isdownloading) {
                    saveDownloadInfo();
                }
            }
        }

    }

    private boolean isFolderExist() {
        boolean result = false;
        try {
            String filepath = TEMP_FILEPATH;
            File file = new File(filepath);
            if (!file.exists()) {
                if (file.mkdirs()) {
                    result = true;
                }
            } else {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getFileName(String url) {
        int index = url.lastIndexOf("/") + 1;
        return url.substring(index);
    }
}


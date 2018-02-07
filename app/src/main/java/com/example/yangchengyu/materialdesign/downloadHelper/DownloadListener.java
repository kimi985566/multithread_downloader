package com.example.yangchengyu.materialdesign.downloadHelper;

import com.example.yangchengyu.materialdesign.entity.DBDownloadInfo;

/**
 * Created by YangChengyu on 2017/4/2.
 */

public interface DownloadListener {
    /**
     * (开始下载文件)
     *
     * @param dbDownloadInfo 下载任务对象
     */
    void onStart(DBDownloadInfo dbDownloadInfo);

    /**
     * (文件下载进度情况)
     *
     * @param dbDownloadInfo      下载任务对象
     * @param isSupportBreakpoint 服务器是否支持断点续传
     */
    void onProgress(DBDownloadInfo dbDownloadInfo, boolean isSupportBreakpoint);

    /**
     * (停止下载完毕)
     *
     * @param dbDownloadInfo      下载任务对象
     * @param isSupportBreakpoint 服务器是否支持断点续传
     */
    void onStop(DBDownloadInfo dbDownloadInfo, boolean isSupportBreakpoint);

    /**
     * (文件下载失败)
     *
     * @param dbDownloadInfo 下载任务对象
     */
    void onError(DBDownloadInfo dbDownloadInfo);


    /**
     * (文件下载成功)
     *
     * @param dbDownloadInfo 下载任务对象
     */
    void onSuccess(DBDownloadInfo dbDownloadInfo);
}

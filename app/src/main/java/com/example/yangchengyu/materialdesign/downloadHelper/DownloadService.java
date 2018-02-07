package com.example.yangchengyu.materialdesign.downloadHelper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by YangChengyu on 2017/4/9.
 */

public class DownloadService extends Service {
    private static DownloadManager sDownloadManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static DownloadManager getDownloadManager() {
        return sDownloadManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sDownloadManager = new DownloadManager(DownloadService.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放downLoadManager
        sDownloadManager.stopAllTask();
        sDownloadManager = null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (sDownloadManager == null) {
            sDownloadManager = new DownloadManager(DownloadService.this);
        }
    }
}

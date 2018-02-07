package com.example.yangchengyu.materialdesign.UI;

import android.app.Application;
import android.content.Intent;

import com.example.yangchengyu.materialdesign.downloadHelper.DownloadService;

public class DLApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        this.startService(new Intent(this, DownloadService.class));
    }

}

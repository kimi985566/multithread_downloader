package com.example.yangchengyu.materialdesign.dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.yangchengyu.materialdesign.entity.DBDownloadInfo;

import java.util.ArrayList;

/**
 * Created by YangChengyu on 2017/4/1.
 */

public class DataKeeper {
    private dbhelper mDbhelper;
    private SQLiteDatabase mDatabase;
    private int doSaveTimes = 0;

    public DataKeeper(Context context) {
        this.mDbhelper = new dbhelper(context);
    }

    public void saveDownloadInfo(DBDownloadInfo downloadInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("fileName", downloadInfo.getFileName());
        contentValues.put("filePath", downloadInfo.getFilePath());
        contentValues.put("taskID", downloadInfo.getTaskId());
        contentValues.put("url", downloadInfo.getURL());
        contentValues.put("length", downloadInfo.getLength());
        contentValues.put("Begin", downloadInfo.getBegin());
        contentValues.put("end", downloadInfo.getEnd());
        contentValues.put("finished", downloadInfo.getFinished());
        Cursor cursor = null;
        try {
            mDatabase = mDbhelper.getWritableDatabase();
            cursor = mDatabase.rawQuery(
                    "SELECT * from " + dbhelper.Table_Name
                            + " WHERE taskID = ? ", new String[]{downloadInfo.getTaskId()});
            if (cursor.moveToNext()) {
                mDatabase.update(dbhelper.Table_Name, contentValues, " taskID = ? ", new String[]{downloadInfo.getTaskId()});
            } else {
                mDatabase.insert(dbhelper.Table_Name, null, contentValues);
            }
            cursor.close();
            mDatabase.close();
        } catch (Exception e) {
            doSaveTimes++;
            if (doSaveTimes < 5) { //最多只做5次数据保存，降低数据保存失败率
                saveDownloadInfo(downloadInfo);
            } else {
                doSaveTimes = 0;
            }
            if (cursor != null) {
                cursor.close();
            }
            if (mDatabase != null) {
                mDatabase.close();
            }
        }
        doSaveTimes = 0;
    }

    public ArrayList<DBDownloadInfo> getAllDownLoadInfo() {
        ArrayList<DBDownloadInfo> downloadinfoList = new ArrayList<DBDownloadInfo>();
        mDatabase = mDbhelper.getWritableDatabase();
        Cursor cursor = mDatabase.rawQuery(
                "SELECT * from " + dbhelper.Table_Name, null);
        while (cursor.moveToNext()) {
            DBDownloadInfo downloadinfo = new DBDownloadInfo();
            downloadinfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            downloadinfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            downloadinfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadinfo.setLength(cursor.getInt(cursor.getColumnIndex("length")));
            downloadinfo.setURL(cursor.getString(cursor.getColumnIndex("url")));
            downloadinfo.setTaskId(cursor.getString(cursor.getColumnIndex("taskID")));
            downloadinfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            downloadinfoList.add(downloadinfo);
        }
        cursor.close();
        mDatabase.close();
        return downloadinfoList;

    }

    public void deleteDownLoadInfo(String taskID) {
        mDatabase = mDbhelper.getWritableDatabase();
        mDatabase.delete(dbhelper.Table_Name, "taskID = ? ", new String[]{taskID});
        mDatabase.close();
    }

    public void deleteAllDownLoadInfo() {
        mDatabase = mDbhelper.getWritableDatabase();
        mDatabase.delete(dbhelper.Table_Name, null, null);
        mDbhelper.close();
    }

}

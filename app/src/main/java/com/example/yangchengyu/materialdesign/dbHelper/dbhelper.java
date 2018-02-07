package com.example.yangchengyu.materialdesign.dbHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by YangChengyu on 2017/4/1.
 */

public class dbhelper extends SQLiteOpenHelper {


    public static final String DataBaseName = "download";
    public static final int VERSION = 1;
    public static final String Table_Name = "download_file";

    public dbhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DataBaseName, null, VERSION);
    }

    public dbhelper(Context context) {
        super(context, DataBaseName, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String create_table = "CREATE TABLE IF NOT EXISTS " + Table_Name + "("
                + "id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"
                + "taskID VARCHAR,"
                + "url VARCHAR,"
                + "filePath VARCHAR,"
                + "fileName VARCHAR,"
                + "begin INTEGER,"
                + "end INTEGER,"
                + "finished INTEGER,"
                + "length INTEGER"
                + ")";

        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

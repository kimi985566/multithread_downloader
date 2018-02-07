package com.example.yangchengyu.materialdesign.dbHelper;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileHelper {
    private static String userID = "YANG";
    private static String baseFilePath = Environment.getExternalStorageDirectory().toString() + "/multidownload";
    private static String dowloadFilePath = baseFilePath + "/" + userID + "/FILETEMP";
    private static String tempDirPath = baseFilePath + "/" + userID + "/TEMPDir";

    private static String[] wrongChars = {
            "/", "\\", "*", "?", "<", ">", "\"", "|"};

    public void newFile(File f) {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFileDefaultPath() {
        return dowloadFilePath;
    }

    public static String getTempDirPath() {
        return tempDirPath;
    }

    public static void newDirFile(File f) {
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public static double getSize(List<String> willupload) {
        return (double) getSizeUnitByte(willupload) / (1024 * 1024);
    }

    public static long getSizeUnitByte(List<String> willupload) {
        long allfilesize = 0;
        for (int i = 0; i < willupload.size(); i++) {
            File newfile = new File(willupload.get(i));
            if (newfile.exists() && newfile.isFile()) {
                allfilesize = allfilesize + newfile.length();
            }
        }
        return allfilesize;
    }

    public static String filterIDChars(String attID) {
        if (attID != null) {
            for (int i = 0; i < wrongChars.length; i++) {
                String c = wrongChars[i];
                if (attID.contains(c)) {
                    attID = attID.replaceAll(c, "");
                }
            }
        }
        return attID;
    }

}

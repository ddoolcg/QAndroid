package com.lcg.mylibrary.utils;

import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件工具
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2015-5-14 下午6:49:33
 */
public class FileUtils {
    private static AssetManager manager;

    /**
     * 保存string到文件fileName
     */
    public static void save(String fileName, String string, boolean append) {
        try {
            File file = new File(Environment
                    .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).getPath()
                    + "/" + fileName);
            if (append && file.lastModified() < System.currentTimeMillis() - 3600000)
                append = false;
            FileWriter w = new FileWriter(file, append);
            w.write("\n--------------------------\n");
            w.write(string);
            w.flush();
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String path = "C:\\Users\\mengchun\\Desktop\\新建文件夹";
        File file = new File(path);
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File file2 : listFiles) {
                String name = file2.getName();
                File file3 = new File(path, name.replace(" 拷贝", ""));
                boolean b = file2.renameTo(file3);
                System.out.println("重命名：" + name + " " + b);
            }
        }
    }
}
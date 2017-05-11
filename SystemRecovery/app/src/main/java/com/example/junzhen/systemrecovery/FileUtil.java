package com.example.junzhen.systemrecovery;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by junzhen on 2015/10/20.
 *
 * 文件工具类
 *
 */
public class FileUtil {



    /**
     * 开始消息提示常量
     * */
    public static final int startDownloadMeg = 10;

    /**
     * 更新消息提示常量
     * */
    public static final int updateDownloadMeg = 20;

    /**
     * 完成消息提示常量
     * */
    public static final int endDownloadMeg = 30;

    /**
     * 取消下载
     * */
    public static final int cancleDownloadMeg = 40;
    public static final int timeout = 50;
    public static final int fileRight = 60;
    public static final int fileWrong = 70;
    public static final int fileNotExist =  80;

    /**
     * 检验SDcard状态
     * @return boolean
     */
    public static boolean checkSDCard()
    {
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            return true;
        }else{
            return false;
        }
    }
    /**
     * 保存文件文件到目录
     * @param context
     * @return  文件保存的目录
     */
    public static String setMkdir(Context context)
    {
        String filePath;
        if(checkSDCard())
        {
            filePath = Environment.getExternalStorageDirectory()+ File.separator+"tsing_recovery";
        }else{
            filePath = context.getCacheDir().getAbsolutePath()+File.separator+"tsing_recovery";
        }
        File file = new File(filePath);
        if(!file.exists())
        {
            boolean b = file.mkdirs();
            Log.v("file", "文件不存在  创建文件    " + b);
        }else{
            Log.v("file", "文件存在");
        }
        return filePath;
    }
    /**
     * 得到文件的名称
     * @return
     * @throws IOException
     */
    public static  String getFileName(String url)
    {
        String name= null;
        try {
            name = url.substring(url.lastIndexOf("/")+1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }




}

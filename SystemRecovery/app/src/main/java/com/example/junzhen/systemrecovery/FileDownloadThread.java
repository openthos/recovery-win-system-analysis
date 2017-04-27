package com.example.junzhen.systemrecovery;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by junzhen on 2015/10/20.
 */

public class FileDownloadThread extends Thread {
    private static final String TAG = "FileDownloadThread";
    /**
     * 缓冲区
     */
    private static final int BUFF_SIZE = 1024;
    /**
     * 需要下载的URL
     */
    private URL url;
    /**
     * 缓存的FIle
     */
    private File file;
    /**
     * 完成
     */
    private boolean finished = false;
    /**
     * 已经下载多少
     */
    private int downloadSize = 0;
    private boolean flag = false;
    private boolean timeout = false;

    /***
     * @param url  下载的URL
     * @param file 下载的文件
     */
    public FileDownloadThread(URL url, File file) {
        this.url = url;
        this.file = file;
        this.flag = false;
        Log.e(TAG, toString());
    }


    @Override
    public void run() {
        //Root权限
        if (interrupted())
            this.flag = true;


        BufferedInputStream bis = null;
        RandomAccessFile fos = null;
        byte[] buf = new byte[BUFF_SIZE];
        URLConnection conn = null;
        try {
            //下载权限

            conn = url.openConnection();

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            conn.setAllowUserInteraction(true);
                /*if(!flag)
                    conn.*/
            fos = new RandomAccessFile(file, "rwd");//读写
            //设置本地文件的长度和下载文件相同
            fos.setLength(file.length());
            bis = new BufferedInputStream(conn.getInputStream(), BUFF_SIZE);

            while (!flag) {
                    /*if (this.isInterrupted()) {
                        flag = true;
                    }*/
                conn.getConnectTimeout();
                int len = bis.read(buf, 0, BUFF_SIZE);
                if (len == -1)   //下载完成
                {
                    break;
                }
                fos.write(buf, 0, len);
                int count = downloadSize + len;//这里需要考虑下，怎样处理好
                if (count > file.length()) {    //如果下载多了，则减去多余部分
                    Log.e(TAG, "当前" + this.getName() + "下载多了!!!");
                    downloadSize = (int) file.length();
                    this.finished = true;
                } else {
                    downloadSize += len;
                }
            }
            this.finished = true;  //当前阶段下载完成
            Log.e(TAG, "当前" + this.getName() + "下载完成");
            bis.close();  //关闭流
            fos.close();
        }catch (SocketTimeoutException ste){
            timeout = true;
            Log.e(TAG,"time out");
        }
        catch (Exception e) {

            Log.e(TAG, "download error Exception " + e.getMessage());
            e.printStackTrace();
        }
        super.run();
            /*if (this.isInterrupted()) {
                flag = true;
            }*/
    }


    /**
     * 是否完成当前下载
     *
     * @return
     */
    public boolean isFinished() {
        return finished;
    }


    public boolean isTimeOut() {return timeout;}
    /**
     * 已经下载多少
     *
     * @return
     */
    public int getDownloadSize() {
        return downloadSize;
    }

    @Override
    public String toString() {
        return "FileDownloadThread [url=" + url + ", file=" + file
                + ", finished=" + finished + ", downloadSize=" + downloadSize + "]";
    }
}

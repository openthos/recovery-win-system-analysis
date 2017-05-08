package com.example.junzhen.systemrecovery;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by junzhen on 2015/10/20.
 */

public class updateUIThread extends Thread {
    private static final String TAG = "MultiThreadDownload";
    /***
     * 文件大小
     */
    private int fileSize;
    /**
     * 已经下载多少
     */
    private int downloadSize;
    /**
     * 文件的url,线程编号，文件名称
     */
    private String UrlStr, ThreadNo, fileName;
    /***
     * 保存的路径
     */
    private String savePath;
    /**
     * 下载的百分比
     */
    private long downloadPercent = 0;
    /**
     * 下载的 平均速度
     */
    private int downloadSpeed = 0;
    /**
     * 下载用的时间
     */
    private int usedTime = 0;
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 当前时间
     */
    private long curTime;
    /**
     * 是否已经下载完成
     */
    private boolean completed = false;
    private Handler handler;
    private boolean finished;
    private boolean flag;

    /**
     * 下载的构造函数
     *
     * @param url      请求下载的URL
     * @param savePath 保存的路径
     * @param fileName 保存的名字
     */
    public updateUIThread(Handler handler, String url, String savePath, String fileName) {
        this.handler = handler;
        this.UrlStr = url;
        this.savePath = savePath;
        this.fileName = fileName;
        this.finished = false;
        this.flag = true;

        Log.e(TAG, toString());
    }

    @Override
    public void run() {

        try {
            URL url = new URL(UrlStr);
            URLConnection conn = (URLConnection)url.openConnection();

/*
            conn.setRequestProperty("User-Agent", " Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
*/

            fileSize = conn.getContentLength();
            sendMsg(FileUtil.startDownloadMeg);
            Log.e(TAG, "文件Size:" + fileSize);

            File file = new File(savePath + fileName);
            FileDownloadThread fdt = new FileDownloadThread(url, file);

            fdt.setName("downloadThread");
            fdt.start();

            startTime = System.currentTimeMillis();


            while (!finished) {

                 downloadSize = 0;
                finished = true;
                downloadSize += fdt.getDownloadSize();
                if (!fdt.isFinished()) {
                    finished = false;
                }
                if (fdt.isTimeOut()) {
                    finished = true;
                }
                if (interrupted()) {
                    fdt.interrupt();
                    finished = true;
                    flag = false;
                    break;
                }
                downloadPercent = (downloadSize * 100) / fileSize;
                curTime = System.currentTimeMillis();
               // System.out.println("curTime = " + curTime + " downloadSize = " + downloadSize + " usedTime " + (int) ((curTime - startTime) / 1000));
                usedTime = (int) ((curTime - startTime) / 1000);

                if (usedTime == 0) {
                    usedTime = 1;
                }
                downloadSpeed = (downloadSize / usedTime) / 1024;
                //sleep(1000);/*1秒钟刷新一次界面*/
                sendMsg(FileUtil.updateDownloadMeg);
            }
            if (fdt.isTimeOut()) {
                sendMsg(FileUtil.timeout);
            } else if (flag) {
                completed = true;
                Log.e(TAG, "ok");

                sendMsg(FileUtil.endDownloadMeg);
            } else
                sendMsg(FileUtil.cancleDownloadMeg);

        } catch (Exception e) {
            Log.e(TAG, "multi file error Exception :" + e.getMessage());
            e.printStackTrace();
        }
        super.run();

    }


    /**
     * 得到文件的大小
     *
     * @return
     */
    public int getFileSize() {
        return this.fileSize;
    }

    /**
     * 得到已经下载的数量
     *
     * @return
     */
    public int getDownloadSize() {
        return this.downloadSize;
    }

    /**
     * 获取下载百分比
     *
     * @return
     */
    public long getDownloadPercent() {
        return this.downloadPercent;
    }

    /**
     * 获取下载速度
     *
     * @return
     */
    public int getDownloadSpeed() {
        return this.downloadSpeed;
    }

    /**
     * 分块下载完成的标志
     *
     * @return
     */
    public boolean isCompleted() {
        return this.completed;
    }

    @Override
    public String toString() {
        return "MultiThreadDownload [threadNum=" + ", fileSize="
                + fileSize + ", UrlStr=" + UrlStr + ", ThreadNo=" + ThreadNo
                + ", fileName=" + fileName + ", savePath=" + savePath + "]";
    }


    private void sendMsg(int what) {
        Message msg = new Message();
        msg.what = what;
        handler.sendMessage(msg);
    }


}

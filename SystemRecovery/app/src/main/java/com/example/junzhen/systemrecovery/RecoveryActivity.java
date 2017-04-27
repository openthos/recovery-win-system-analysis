/*
package com.example.junzhen.systemrecovery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RecoveryActivity extends AppCompatActivity {


    MainActivity mainActivity = new MainActivity();



    private static final int OUTPUT_BUFFER_SIZE = 1024;


    */
/**
     * 底部四个按钮
     *//*

    */
/**
     * 用于对Fragment进行管理
     *//*

    public String wimfile7,wimfile10;
    private String fromonline_win7_home = "/storage/emulated/legacy/tsing_recovery/online_win7_home.wim";
    private String fromonline_win7_professional = "/storage/emulated/legacy/tsing_recovery/online_win7_professional.wim";
    private String fromonline_win7_business = "/storage/emulated/legacy/tsing_recovery/online_win7_business.wim";
    private String fromonline_win8 = "/storage/emulated/legacy/tsing_recovery/online_win8.wim";
    private String fromonline_win10 = "/storage/emulated/legacy/tsing_recovery/online_win10.wim";



    private File file;
    List<BigInteger> image_size;
    List<BigInteger> disk_size;
    List<String> section_detail;

    private boolean isRight = false;

    private String[] data;


    private int pos = -1;

    private String chooseid = "";
    private String choose_section = "";
    private  String url_win7_home,url_win7_professional,url_win7_business,url_win8,url_win10;

    private CheckIntegrity checkIntegrity = null;
    private updateUIThread mUpdateUIThread = null;

    private String win7_sha1_stardard,win10_sha1_stardard;

    ProgressDialog checkprogressDialog;
    ProgressDialog recoveryprogressDialog;
    ProgressDialog downloadprogressDialog;
    public int i,j;


    public void download_dialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RecoveryActivity.this);
        builder.setTitle("提示");
        builder.setMessage("没有找到系统恢复文件，请点击\"下载\"按钮开始下载官方文件！");
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                download();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        builder.create();
        builder.show();
    }



    public void download() {
        if (isNetworkAvailable()) {
            // TODO Auto-generated method stub
            downloadprogressDialog = new ProgressDialog(RecoveryActivity.this);
            downloadprogressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

            if (mUpdateUIThread == null) {
                mUpdateUIThread = newmyThread();
                mUpdateUIThread.start();
            } else {
                mUpdateUIThread = null;
                mUpdateUIThread = newmyThread();
                mUpdateUIThread.start();
            }

        } else {
            Toast.makeText(getApplication(), "网络未连接", Toast.LENGTH_LONG).show();
        }
    }



    public boolean isNetworkAvailable() {
        Context context = getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Toast.makeText(context, "网络异常", Toast.LENGTH_LONG).show();
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null) {
                // 判断当前网络状态是否为连接状态
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                } else {
                    Toast.makeText(context, "网络未连接", Toast.LENGTH_LONG).show();
                    return false;
                }

            }
        }
        return false;
    }



    public void dialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(RecoveryActivity.this);
        builder.setTitle("警告");
        builder.setMessage("点击'确认'按钮，恢复系统默认安装在Windows系统盘，原有系统数据将会丢失");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                decompress();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        builder.create();
        builder.show();
    }




    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FileUtil.startDownloadMeg:
                    downloadprogressDialog.setMax(mUpdateUIThread.getFileSize());
                    downloadprogressDialog.setMessage("正在下载，请耐心等待……");
                    downloadprogressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    downloadprogressDialog.show();
                    downloadprogressDialog.setCancelable(false);
                    downloadprogressDialog.onStart();
                    break;
                case FileUtil.updateDownloadMeg:
                    if (!mUpdateUIThread.isCompleted())   //下载
                    {
                        downloadprogressDialog.setProgress(mUpdateUIThread.getDownloadSize());
                    } else {
                    }
                    break;
                case FileUtil.endDownloadMeg:
                    Toast.makeText(RecoveryActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    downloadprogressDialog.dismiss();
                    */
/*apk安装界面跳转*//*

                    //跳转到选择系统界面
                    break;
                case FileUtil.cancleDownloadMeg:
                    Toast.makeText(RecoveryActivity.this, "取消下载", Toast.LENGTH_SHORT).show();
                    exec("rm /storage/emulated/legacy/tsing_recovery/test.wim");
                    downloadprogressDialog.dismiss();

                    break;
                case FileUtil.timeout:
                    Toast.makeText(RecoveryActivity.this, "连接超时，请检查网络或者站点是否正常", Toast.LENGTH_SHORT).show();
                    exec("rm /storage/emulated/legacy/tsing_recovery/test.wim");
                    break;
                case FileUtil.fileNotExist:
                    Toast.makeText(getApplication(), "文件不存在", Toast.LENGTH_LONG).show();
                    checkprogressDialog.dismiss();
                    break;
                case FileUtil.fileRight:
                    Toast.makeText(getApplication(), "文件SHA1检验正确", Toast.LENGTH_LONG).show();
                    checkprogressDialog.dismiss();
                    mainActivity.recovery.setVisibility(View.VISIBLE);
                    mainActivity.listview_section.setVisibility(View.VISIBLE);
                    section_select();

                    break;
                case FileUtil.fileWrong:
                    Toast.makeText(getApplication(), "文件SHA1检验失败，请重新下载", Toast.LENGTH_LONG).show();
                    checkprogressDialog.dismiss();
                    // create_wim.setEnabled(true);
                    //download.setEnabled(true);

                    break;
            }
            super.handleMessage(msg);
        }
    };

    public updateUIThread newmyThread() {

        if (j==1){
            mUpdateUIThread = new updateUIThread(handler, url_win7_home, FileUtil.setMkdir(this) + File.separator, "online_win7_home.wim");
        }else if (j==2){
            mUpdateUIThread = new updateUIThread(handler, url_win7_professional, FileUtil.setMkdir(this) + File.separator, "online_win7_professional.wim");
        }else if (j==3){
            mUpdateUIThread = new updateUIThread(handler, url_win7_business, FileUtil.setMkdir(this) + File.separator, "online_win7_business.wim");
        }else if (j==4){
            mUpdateUIThread = new updateUIThread(handler, url_win8, FileUtil.setMkdir(this) + File.separator, "online_win8.wim");
        }else if (j==5){
            mUpdateUIThread = new updateUIThread(handler, url_win10, FileUtil.setMkdir(this) + File.separator, "online_win10.wim");
        }
        return mUpdateUIThread;
    }


    public CheckIntegrity newcheckThread() {
        //Toast.makeText(getApplication(), wimfile, Toast.LENGTH_LONG).show();
        if (i==1){
            checkIntegrity = new CheckIntegrity(handler, wimfile7, win7_sha1_stardard);
        }else if (i==2){
            checkIntegrity = new CheckIntegrity(handler, wimfile10, win10_sha1_stardard);
        }
        return checkIntegrity;
    }


    public boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }



    public void checkintergrity()                                                                                                                                                          {
        checkprogressDialog = new ProgressDialog(RecoveryActivity.this);
        if (checkIntegrity == null) {
            checkIntegrity = newcheckThread();
            checkprogressDialog.setMessage("检验文件完整性过程时间较长，大约1分钟左右，请耐心等待…………");
            checkprogressDialog.setCancelable(false);
            checkprogressDialog.show();
            checkIntegrity.start();
        } else {
            checkIntegrity = null;
            checkIntegrity = newcheckThread();
            checkprogressDialog.setMessage("检验文件完整性过程时间较长，大约1分钟左右，请耐心等待…………");
            checkprogressDialog.setCancelable(false);
            checkprogressDialog.show();
            checkIntegrity.start();
        }
    }






    public void getConfig() {
        FileUtil.setMkdir(getApplicationContext());
        String configname = "/storage/emulated/legacy/tsing_recovery/recovery.config";
        file = new File(configname);
        if (!file.exists()) {
            FileWriter fw;
            BufferedWriter bw;
            try {
                StringBuffer config = new StringBuffer("");
                //config.append(sourcefile.getText().toString() + "\n");
                win7_sha1_stardard = "65281e401c1849a2348e773796abea06585fd527";
                win10_sha1_stardard = "c1cf3da775b2023b9c3cb020abaa25d466bcd584";
                wimfile7 = "/storage/emulated/legacy/tsing_recovery/window7.wim";
                wimfile10 = "/storage/emulated/legacy/tsing_recovery/window10.wim";

                url_win7_home = "http://dldir1.qq.com/qqfile/qq/QQ7.7/16096/QQ7.7.exe";
                url_win7_professional = "http://dldir1.qq.com/qqfile/qq/QQ7.7/16096/QQ7.7.exe";
                url_win7_business =  "http://dldir1.qq.com/qqfile/qq/QQ7.7/16096/QQ7.7.exe";
                url_win8 = "http://dldir1.qq.com/qqfile/qq/QQ7.7/16096/QQ7.7.exe";
                url_win10 = "http://dldir1.qq.com/qqfile/qq/QQ7.7/16096/QQ7.7.exe";

                config.append(win7_sha1_stardard + "\n");
                config.append(win10_sha1_stardard + "\n");
                config.append(wimfile7 + "\n");
                config.append(wimfile10 + "\n");
                config.append(url_win7_home);
                config.append(url_win7_professional);
                config.append(url_win7_business);
                config.append(url_win8);
                config.append(url_win10);
                fw = new FileWriter(file);//
                // 创建FileWriter对象，用来写入字符流
                bw = new BufferedWriter(fw); // 将缓冲对文件的输出
                //String myreadline = datetime + "[]" + str;


                bw.write(config + "\n"); // 写入文件

                bw.newLine();
                bw.flush(); // 刷新该流的缓冲
                bw.close();
                fw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            //target = "/dev/block/sda4";

        } else {
            FileReader fr;
            BufferedReader br;
            try {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                StringBuffer temp = new StringBuffer("");
                String inline;
                while ((inline = br.readLine()) != null) {
                    temp.append(inline + "\n");
                }
                String[] x = temp.toString().split("\n");
                win7_sha1_stardard = x[0];
                win10_sha1_stardard= x[1];

                wimfile7 = x[2];
                wimfile10=x[3];
                url_win7_home = x[4];
                url_win7_professional = x[5];
                url_win7_business = x[6];
                url_win8 = x[7];
                url_win10 = x[8];
                br.close();
                fr.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public String exec(String cmd) {
        try {
            if (cmd != null)

            {

                Runtime rt = Runtime.getRuntime();
                Process process = rt.exec("su");//Root权限
                //Process process = rt.exec("sh");//模拟器测试权限
                DataOutputStream dos = new DataOutputStream(process.getOutputStream());
                dos.writeBytes(cmd + "\n");
                dos.flush();
                dos.writeBytes("exit\n");
                dos.flush();
                InputStream myin = process.getInputStream();
                InputStreamReader is = new InputStreamReader(myin);
                */
/*******************
                 buffer单行模式
                 ******************//*

                */
/*BufferedReader ibr = new BufferedReader(is);
                String inline;
                StringBuffer sb = new StringBuffer("");
                while ((inline = ibr.readLine()) != null) {
                    System.out.println(inline);
                    sb.append(inline+"\n");
                }*//*



                char[] buffer = new char[OUTPUT_BUFFER_SIZE];
                int bytes_read = is.read(buffer);
                StringBuffer aOutputBuffer = new StringBuffer();
                while (bytes_read > 0) {
                    //info.setText(aOutputBuffer.toString());
                    aOutputBuffer.append(buffer, 0, bytes_read);

                    bytes_read = is.read(buffer);
                }
                return aOutputBuffer.toString();
            } else {
                System.out.println("退出");
                return "请输入正确的命令";
            }
        } catch (IOException e) {
            e.printStackTrace();

            return "操作异常";
        }
    }



    public void section_select() {

        String section_cmd = "fdisk -l /dev/block/sda";
        String info = exec(section_cmd);
        String[] section_info = info.split("\n");

        section_detail = new ArrayList<>();
        disk_size = new ArrayList<>();

        for (int i = 0; i < section_info.length; i++) {
            BigInteger begin, end;
            if (section_info[i].contains("Number") && section_info[i].contains("Start") && section_info[i].contains("End")) {
                for (int j = i + 1; j < section_info.length; j++) {
                    String[] temp = section_info[j].split("\\s+");
                    section_detail.add(temp[1] + "  " + temp[4]);
                    begin = new BigInteger(temp[2]);
                    end = new BigInteger(temp[3]);
                    disk_size.add(end.subtract(begin).pow(512));
                }
                break;
            }
        }
        //final AlertDialog.Builder section_select = new AlertDialog.Builder(RecoveryActivity.this);
        //section_select.setTitle("请选择分区");


        int num = section_detail.size();
        data = new String[num];
        for (int i = 0; i < num; i++) {
            switch (i) {
                case 0:
                    data[i] = section_detail.get(i) + "  " + "MSR分区";
                    break;
                case 1:
                    data[i] = section_detail.get(i) + "  " + "EFI分区";
                    break;
                case 2:
                    data[i] = section_detail.get(i) + "  " + "Microsoft预留分区";
                    break;
                default:
                    data[i] = section_detail.get(i);
            }

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(RecoveryActivity.this, R.layout.listview_item1, data);

        mainActivity.listview_section.setAdapter(adapter);
        mainActivity.listview_section.setBackgroundColor(Color.LTGRAY);
        mainActivity.listview_section.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position != 1 && position != 2) {
                    if (disk_size.get(position).compareTo(image_size.get(Integer.valueOf(chooseid) - 1)) > 0) {
                        if (pos != -1) {
                            View v = parent.getChildAt(pos);
                            v.setBackgroundColor(Color.LTGRAY);
                        }
                        pos = position;
                        choose_section = String.valueOf(position + 1);
                        TextView tv = (TextView) view.findViewById(R.id.itemText1);
                        tv.setTextColor(Color.WHITE);
                        view.setBackgroundResource(R.color.blue);

                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(RecoveryActivity.this);
                        builder.setTitle("警告");
                        builder.setMessage("该分区磁盘空间不足，请选择其他分区");
                        builder.setNeutralButton("确认", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        });
                        builder.create();
                        builder.show();
                    }
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(RecoveryActivity.this);
                    builder.setTitle("警告");
                    builder.setMessage("该分区为系统自带分区，请选择序号3以后的分区");
                    builder.setNeutralButton("确认", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });
                    builder.create();
                    builder.show();
                }
            }
        });
    }




    class MyTask extends AsyncTask<String, String, String> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            //Log.i(TAG, "onPreExecute() called");

            //info.setText("loading...");
            recoveryprogressDialog = new ProgressDialog(RecoveryActivity.this);
            recoveryprogressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            recoveryprogressDialog.setMessage("正在恢复Windows系统，请耐心等待！");
            recoveryprogressDialog.setCancelable(false);
            recoveryprogressDialog.show();
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            //Log.i(TAG, "doInBackground(Params... params) called");

            if (params != null) {
                if (params[0].equals("check")) {
                    return "false";

                } else {
                    try {
                        Runtime rt = Runtime.getRuntime();
                        Process process = rt.exec("su");//Root权限
                        //Process process = rt.exec("sh");//模拟器测试权限
                        DataOutputStream dos = new DataOutputStream(process.getOutputStream());
                        //dos.writeBytes(params[0] + " " + params[1] + " " + params[2] + "\n");
                        dos.writeBytes(params[0] + "\n");
                        dos.flush();
                        //process.waitFor();
                        dos.writeBytes(params[1] + "\n");
                        dos.flush();
                        dos.writeBytes("exit\n");
                        dos.flush();
                        InputStream myin = process.getInputStream();
                        InputStreamReader is = new InputStreamReader(myin);
                        */
/*******************
                         buffer单行模式
                         ******************//*

                        BufferedReader ibr = new BufferedReader(is);
                        String inline;
                        while ((inline = ibr.readLine()) != null) {
                            System.out.println(inline);

                            //调用publishProgress公布进度,最后onProgressUpdate方法将被执行
                            //if (params[0].contains("capture")) {
                            if (inline.contains("%")) {
                                String[] ratio = inline.split("%");
                                String[] temp = ratio[0].split("\\(");

                                publishProgress(temp[temp.length - 1]);

                            }
                        }
                        return "系统恢复成功";
                    } catch (IOException e) {
                        e.printStackTrace();

                        return "操作异常";
                    }

                }


            } else {
                System.out.println("退出");
                return "请输入正确的命令";
            }
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(String... progresses) {
            //progressBar.setProgress(Integer.parseInt(progresses[0]));
            //info.setText(progresses[0] + "%");
            recoveryprogressDialog.setProgress(Integer.parseInt(progresses[0]));
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            recoveryprogressDialog.dismiss();
            if (result.equals("系统恢复成功"))
               reboot();
            if (result.equals("false"))
                isRight = false;
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
        }
    }



    public void decompress() {


        String dir = "/dev/block/sda" + choose_section;
        String cmd1 = "mkntfs -f " + dir;
        String cmd2 = "wimlib-imagex apply " + wimfile7 + " " + dir;
        String cmd3 = "wimlib-imagex apply " + wimfile10 + " " + dir;

        String cmd4 = "wimlib-imagex apply " + fromonline_win7_home + " " + dir;
        String cmd5 = "wimlib-imagex apply " + fromonline_win7_professional + " " + dir;
        String cmd6 = "wimlib-imagex apply " + fromonline_win7_business + " " + dir;
        String cmd7 = "wimlib-imagex apply " + fromonline_win8 + " " + dir;
        String cmd8 = "wimlib-imagex apply " + fromonline_win10 + " " + dir;
        */
/*Toast.makeText(getApplication(), cmd2, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplication(), cmd1, Toast.LENGTH_LONG).show();*//*

        MyTask myTask = new MyTask();

        if (i!=0 && j==0) {
            if (i == 1) {
                myTask.execute(cmd1, cmd2);
            } else if (i == 2) {
                myTask.execute(cmd1, cmd3);
            }
        }else if (i==0 && j!=0){
            if (j==1){
                myTask.execute(cmd1, cmd4);
            } else if (j==2){
                myTask.execute(cmd1, cmd5);
            } else if (j==3){
                myTask.execute(cmd1, cmd6);
            } else if (j==4){
                myTask.execute(cmd1, cmd7);
            } else if (j==5){
                myTask.execute(cmd1, cmd8);
            }
        }
    }



    public void reboot() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RecoveryActivity.this);
        //create_wim.setEnabled(true);

        builder.setTitle("重新启动");
        builder.setMessage("系统已经恢复成功！");
        builder.setPositiveButton("立即重启", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                exec("reboot");

            }
        });
        builder.setNegativeButton("稍后重启", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                // imageselect();
            }
        });
        builder.create();
        builder.show();
    }
}
*/

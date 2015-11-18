package com.example.junzhen.systemrecovery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.security.NoSuchAlgorithmException;

public class RecoveryActivity extends AppCompatActivity {

    private static final int OUTPUT_BUFFER_SIZE = 1024;
    private int imagecount;
    private String sha1_stardard;
    private Button create_wim;
    private Button cancle_wim;
    private Button cancel_download;
    private EditText source;
    //private TextView info;
    //private ProgressBar progressBar;
    private Button download;

    private Button save;
    private Button cancel;
    private Button bt;
    private EditText chooseimageid;

    private static String src;
    public int downLoadFileSize;
    public int fileSize;
    private TextView wiminfo;
    private Button cancle_bt;
    private LinearLayout chooselayout;
    //private TextView wancheng;
    private File file;
    private Button help;

    ProgressDialog progressDialog;

    private updateUIThread mUpdateUIThread = null;
    private CheckIntegrity checkIntegrity = null;
    //private FileDownloadThread fdt = null;
    /*private String url = "http://cdimage.ubuntu.com/ubuntukylin/releases/14.04.2/release/ubuntukylin-14.04.2-desktop-amd64.iso";*/
    private String url;
    private String wimfile;
    private String target;
    private boolean isRight = false;
    private boolean remove = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        //info = (TextView) findViewById(R.id.return_info);
        //info.setTextSize(20);
        progressDialog = new ProgressDialog(RecoveryActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        create_wim = (Button) findViewById(R.id.creat_wim);
       // progressBar = (ProgressBar) findViewById(R.id.progress);
        download = (Button) findViewById(R.id.dowmload);
       /* save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);*/
        cancel_download = (Button) findViewById(R.id.cancel_wim);
        cancel_download.setEnabled(false);
        chooseimageid = (EditText) findViewById(R.id.chooseimageid);
        //wancheng = (TextView) findViewById(R.id.wancheng);
        help = (Button) findViewById(R.id.help);
        View.OnClickListener helplistener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                helpdialog();

            }
        };
        View.OnClickListener createlistener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                getConfig();
                Toast.makeText(getApplication(), "检验文件完整性过程时间较长，大约1分钟左右，请耐心等待…………", Toast.LENGTH_LONG).show();
                create_wim.setEnabled(false);
                download.setEnabled(false);
                chooseimageid.setEnabled(false);
                if(checkIntegrity == null)
                {
                    checkIntegrity = newcheckThread();
                    checkIntegrity.start();
                }else {
                    checkIntegrity = null;
                    checkIntegrity = newcheckThread();
                    checkIntegrity.start();
                }
                /*MyTask task = new MyTask();
                task.execute("check");*/
                /*try {
                    task.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                //typeselect();
            }
        };
        View.OnClickListener downloadlistener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    // TODO Auto-generated method stub
                    getConfig();
                    if (!fileIsExists(wimfile)) {
                        if (mUpdateUIThread == null) {
                            mUpdateUIThread = newmyThread();
                            mUpdateUIThread.start();
                        } else {
                            mUpdateUIThread = null;
                            mUpdateUIThread = newmyThread();
                            mUpdateUIThread.start();
                        }
                        create_wim.setEnabled(false);
                        download.setEnabled(false);
                        cancel_download.setEnabled(true);
                    }else {
                        if(removefile()){
                            if (mUpdateUIThread == null) {
                                mUpdateUIThread = newmyThread();
                                mUpdateUIThread.start();
                            } else {
                                mUpdateUIThread = null;
                                mUpdateUIThread = newmyThread();
                                mUpdateUIThread.start();
                            }
                            create_wim.setEnabled(false);
                            download.setEnabled(false);
                            cancel_download.setEnabled(true);
                        }
                    }
                }else {
                    Toast.makeText(getApplication(), "网络未连接", Toast.LENGTH_LONG).show();
                }
            }
        };
        View.OnClickListener canceldownloadlistener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                if (mUpdateUIThread != null) {
                    mUpdateUIThread.interrupt();
                    //fdt.interrupt();
                    //mUpdateUIThread = null;
                }

            }
        };

        help.setOnClickListener(helplistener);
        download.setOnClickListener(downloadlistener);
        create_wim.setOnClickListener(createlistener);
        cancel_download.setOnClickListener(canceldownloadlistener);
        getConfig();
        wiminfo = (TextView) findViewById(R.id.wiminfo);
        chooselayout = (LinearLayout) findViewById(R.id.chooselayout);
        //String path = "/storage/emulated/legacy/tsing_recovery/windows.wim";
        imagecount = 0;
        setWiminfo();
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                               .setAction("Action", null).show();
                                       //Intent intent = new Intent(RecoveryActivity.this, MainActivity.class);
                                       //startActivity(intent);

                                   }
                               }
        );*/
    }

    private void setWiminfo(){
        String str;
        if (fileIsExists(wimfile)) {
            str = exec("wimlib-imagex-32 info " + wimfile + "\n");
            wiminfo.setTextColor(Color.BLACK);
            wiminfo.setText(str);
            /*String[] info = str.split("\n");
            for(int i=0;i<info.length;i++)
            {
                String number;
                if(info[i].contains("Image Count"))
                {
                    number = info[i].split(":")[1];
                    String[] numbers = number.split(" ");
                    int[] nums = new int[numbers.length];
                    for(int j=0;j<numbers.length;j++)
                    {
                        nums[j] = Integer.parseInt(numbers[j]);
                        if(nums[j]>0)
                            imagecount = nums[j];
                    }
                    wiminfo.setText(imagecount);
                }
            }*/

        } else {
            str = "没有相关ESD文件，请点击下方的自动下载按钮！！";
            wiminfo.setTextColor(Color.RED);
            wiminfo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //bt.setEnabled(false);
            chooselayout.setVisibility(View.GONE);
            wiminfo.setText(str);
        }

    }
    private void helpdialog() {
        final AlertDialog builder = new AlertDialog.Builder(RecoveryActivity.this).create();
        builder.setTitle("帮助");
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.help, null);

        Button confirm = (Button) view.findViewById(R.id.confirm);
        builder.setView(view);
        builder.show();
        View.OnClickListener confirmlistener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub

                builder.dismiss();
            }
        };

        confirm.setOnClickListener(confirmlistener);
    }

    public updateUIThread newmyThread() {
        mUpdateUIThread = new updateUIThread(handler, url, FileUtil.setMkdir(this) + File.separator, "test.wim");
        return mUpdateUIThread;
    }
    public CheckIntegrity newcheckThread() {
        Toast.makeText(getApplication(), wimfile, Toast.LENGTH_LONG).show();
        checkIntegrity = new CheckIntegrity(handler,wimfile,sha1_stardard);
        return checkIntegrity;
    }

    private void getConfig() {
        String configname = "/storage/emulated/legacy/tsing_recovery/recovery.config";
        file = new File(configname);
        if (!file.exists()) {
            FileWriter fw;
            BufferedWriter bw;
            try {
                StringBuffer config = new StringBuffer("");
                //config.append(sourcefile.getText().toString() + "\n");
                sha1_stardard = "f32dffc2186e7b4b247efb3409e4065bb2fb4a20";
                wimfile = "/storage/emulated/legacy/tsing_recovery/window.wim";
                url = "http://dldir1.qq.com/qqfile/qq/QQ7.7/16096/QQ7.7.exe";
                config.append(sha1_stardard+"\n");
                config.append(wimfile + "\n");
                config.append(url);
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
                sha1_stardard = x[0];
                wimfile = x[1];
                url = x[2];
                br.close();
                fr.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*public boolean check() {
        this.file = new File(wimfile);
        if (file.exists()) {
            try {
                String str = getFileSHA(this.file);
                Toast.makeText(getApplication(), str, Toast.LENGTH_LONG).show();
                if (sha1_stardard.equals(str)) {
                    Toast.makeText(getApplication(), "文件SHA1检验正确", Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    Toast.makeText(getApplication(), "文件SHA1检验失败，请重新下载", Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            Toast.makeText(getApplication(), "文件不存在", Toast.LENGTH_LONG).show();
            return false;
        }

    }*/
    /*public FileDownloadThread newfdt() {
        try {
            URL url = new URL(this.url);
            URLConnection conn = null;

            conn = url.openConnection();

            fileSize = conn.getContentLength();

            this.file = new File(FileUtil.setMkdir(this) + File.separator + "test.wim");
            fdt = new FileDownloadThread(url, this.file);
            return fdt;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/

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

    public boolean removefile() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RecoveryActivity.this);
        builder.setTitle("警告");
        builder.setMessage("点击'确定'按钮，原有的ESD文件将被删除！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(wimfile!=null)
                    exec("rm "+wimfile);
                Toast.makeText(getApplication(),"删除成功",Toast.LENGTH_LONG).show();
                remove = true;

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                remove = false;

            }
        });
        builder.create();
        builder.show();
        return remove;
    }
    public void dialog() {
        chooseimageid = (EditText) findViewById(R.id.chooseimageid);
        final AlertDialog.Builder builder = new AlertDialog.Builder(RecoveryActivity.this);
        final String src = chooseimageid.getText().toString();
        create_wim.setEnabled(true);
        download.setEnabled(true);
        builder.setTitle("警告");
        builder.setMessage("点击'确认'按钮，恢复系统默认安装在Windows系统盘，原有系统数据将会丢失");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                /*src = source.getText().toString();
                //src = "";
                decompress(src);*/

                decompress(src);
                create_wim.setEnabled(false);
                download.setEnabled(false);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                chooseimageid.setEnabled(true);
            }
        });
        builder.create();
        builder.show();
    }

    public void typeselect() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RecoveryActivity.this);
        create_wim.setEnabled(true);
        download.setEnabled(true);
        builder.setTitle("选择用户模式");
        builder.setMessage("\"开发人员模式\"提供ESD文件的详细信息，供开发人员选择");
        builder.setPositiveButton("普通用户模式", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                decompress("1");
                create_wim.setEnabled(false);
                download.setEnabled(false);

            }
        });
        builder.setNegativeButton("开发人员模式", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
               // imageselect();
            }
        });
        builder.create();
        builder.show();
    }

    public void reboot() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RecoveryActivity.this);
        create_wim.setEnabled(true);
        download.setEnabled(true);
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
    /*public void imageselect() {
        final AlertDialog builder = new AlertDialog.Builder(RecoveryActivity.this).create();
        String str;
        create_wim.setEnabled(true);
        download.setEnabled(true);
        builder.setTitle("选择启动镜像号");
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.choose, null);
        chooselayout = (LinearLayout) view.findViewById(R.id.chooselayout);
        bt = (Button) view.findViewById(R.id.chooseimage);
        cancle_bt = (Button) view.findViewById(R.id.cancelimage);
        chooseimageid = (EditText) view.findViewById(R.id.chooseimageid);
        wiminfo = (TextView) view.findViewById(R.id.wiminfo);
        //String path = "/storage/emulated/legacy/tsing_recovery/windows.wim";
        if (fileIsExists(wimfile)) {
            str = exec("wimlib-imagex-32 info " + wimfile + "\n");
            wiminfo.setTextColor(Color.BLACK);
        } else {
            str = "没有相关ESD文件，请点击下方的自动下载按钮！！";
            wiminfo.setTextColor(Color.RED);
            bt.setEnabled(false);
            chooselayout.setVisibility(View.GONE);
        }
        wiminfo.setText(str);
        builder.setView(view);
        builder.show();
        View.OnClickListener imagelistener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                src = chooseimageid.getText().toString();
                if (src.isEmpty()) {
                    Toast.makeText(getApplication(), "请选择镜像号", Toast.LENGTH_LONG).show();
                } else {
                    dialog(src);
                    builder.dismiss();
                }
            }
        };
        View.OnClickListener cancleimagelistener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                builder.dismiss();
            }
        };
        bt.setOnClickListener(imagelistener);
        cancle_bt.setOnClickListener(cancleimagelistener);
    }*/

    /*private void initViews() {

        mTabBtnWeixin = (LinearLayout) findViewById(R.id.id_tab_bottom_weixin);
        mTabBtnFrd = (LinearLayout) findViewById(R.id.id_tab_bottom_friend);


        mTabBtnWeixin.setOnClickListener(this);
        mTabBtnFrd.setOnClickListener(this);
    }*/

    /*@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tab_bottom_weixin:
                setTabSelection(0);
                break;
            case R.id.id_tab_bottom_friend:
                setTabSelection(1);
                break;

            default:
                break;
        }
    }*/

    /**
     * 根据传入的index参数来设置选中的tab页。
     */
    @SuppressLint("NewApi")
    /*private void setTabSelection(int index) {
        // 重置按钮
        resetBtn();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                mTabBtnWeixin.findViewById(R.id.id_tab_bottom_weixin).setBackgroundColor(Color.GREEN);
                save.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                cancel_download.setVisibility(View.VISIBLE);
                create_wim.setVisibility(View.VISIBLE);
                download.setVisibility(View.VISIBLE);
                help.setVisibility(View.VISIBLE);
                info.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                wancheng.setVisibility(View.GONE);
               *//* if (mainTab == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mainTab = new MainTab();
                    transaction.add(R.id.id_content, mainTab);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(mainTab);
                }*//*
                break;
            case 1:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                mTabBtnFrd.findViewById(R.id.id_tab_bottom_friend).setBackgroundColor(Color.GREEN);
                save.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                cancel_download.setVisibility(View.GONE);
                create_wim.setVisibility(View.GONE);
                help.setVisibility(View.GONE);
                download.setVisibility(View.GONE);
                info.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                wancheng.setVisibility(View.GONE);
                *//*if (configTab == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    configTab = new ConfigTab();
                    transaction.add(R.id.id_content, configTab);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(configTab);
                }*//*
                break;
        }
        transaction.commit();
    }*/

    /**
     * 清除掉所有的选中状态。
     */
    /*private void resetBtn() {

        mTabBtnWeixin.findViewById(R.id.id_tab_bottom_weixin).setBackgroundColor(Color.LTGRAY);
        mTabBtnFrd.findViewById(R.id.id_tab_bottom_friend).setBackgroundColor(Color.LTGRAY);
    }*/

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    /*@SuppressLint("NewApi")
    private void hideFragments(FragmentTransaction transaction) {
        if (mainTab != null) {
            transaction.hide(mainTab);
        }
        if (configTab != null) {
            transaction.hide(configTab);
        }
    }*/


    class MyTask extends AsyncTask<String, String, String> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            //Log.i(TAG, "onPreExecute() called");

            //info.setText("loading...");
            progressDialog.show();
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            //Log.i(TAG, "doInBackground(Params... params) called");

            if (params != null) {
                if (params[0].equals("check")) {
                    /*if(check()) {
                        typeselect();
                        return "true";
                    }
                    else
                        return "false";*/
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
                        /*******************
                         buffer单行模式
                         ******************/
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

                       /* }
                        if (params[0].contains("apply")) {
                            if (inline.contains("%")) {
                                String[] ratio = inline.split("%");
                                String[] temp = ratio[0].split("\\(");

                                publishProgress("0", temp[temp.length - 1]);


                            }
                        } else if (params[0].contains("wget")) {
                            if (inline.contains("%")) {
                                String[] ratio = inline.split("%");
                                String[] temp = ratio[0].split(" ");
                                //File file();
                                publishProgress("1", inline);


                            } else {
                                publishProgress("hello", "test");
                            }*/


                            /******************
                             buffer全获取方式
                             *****************/
                    /*char[] buffer = new char[OUTPUT_BUFFER_SIZE];
                    int bytes_read = is.read(buffer);
                    StringBuffer aOutputBuffer = new StringBuffer();
                    while (bytes_read > 0) {
                        //info.setText(aOutputBuffer.toString());
                        aOutputBuffer.append(buffer, 0, bytes_read);

                        bytes_read = is.read(buffer);
                    }
                    /****************
                    获取错误输出流信息
                    ****************/
                    /*
                    InputStream stderr = process.getErrorStream();
                    InputStreamReader isr = new InputStreamReader(stderr);
                    BufferedReader br = new BufferedReader(isr);
                    String line = null;
                    System.out.println("<ERROR>");
                    while ((line = br.readLine()) != null)
                        System.out.println(line);
                    System.out.println("</ERROR>");
                    */
                        }
                        return "系统恢复成功";
                    }catch(IOException e){
                        e.printStackTrace();

                        return "操作异常";
                    }

                }



            }
            else{
                System.out.println("退出");
                return "请输入正确的命令";
            }
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(String... progresses) {
            //progressBar.setProgress(Integer.parseInt(progresses[0]));
            //info.setText(progresses[0] + "%");
            progressDialog.setProgress(Integer.parseInt(progresses[0]));
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            if(result.equals("系统恢复成功"))
                reboot();
            if(result.equals("false"))
                isRight = false;
            create_wim.setEnabled(true);
            download.setEnabled(true);
            chooseimageid.setEnabled(true);
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            //Log.i(TAG, "onCancelled() called");
            //info.setText("cancelled");
            //progressBar.setProgress(0);
            /*create_wim.setEnabled(true);
            cancel_wim.setEnabled(false);*/
        }
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
                /*******************
                 buffer单行模式
                 ******************/
                /*BufferedReader ibr = new BufferedReader(is);
                String inline;
                StringBuffer sb = new StringBuffer("");
                while ((inline = ibr.readLine()) != null) {
                    System.out.println(inline);
                    sb.append(inline+"\n");
                }*/


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

    public void decompress(String src) {

        Toast.makeText(getApplication(), "展开" + src, Toast.LENGTH_LONG).show();
        String dir = "";
        String info = exec("blkid");
        String[] target = info.split("\n");
        for (int i = 0; i < target.length; i++) {
            if (target[i].contains("ntfs")) {
                String cmd = "blockdev --getsz " + target[i].split(":")[0];
                String sda = exec(cmd);
                if (Integer.parseInt(sda.split("\n")[0]) / 2 > 5000000) {
                    dir = target[i].split(":")[0];
                    break;
                }
            }
        }

        String cmd1 = "mkntfs -f " + dir;
        String cmd2 = "wimlib-imagex-32 apply " + wimfile + " " + src + " " + dir;
        Toast.makeText(getApplication(), cmd2, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplication(), cmd1, Toast.LENGTH_LONG).show();
        MyTask myTask = new MyTask();
        myTask.execute(cmd1, cmd2);

    }

    /**
     * get file md5
     *
     * @param file
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
   /* private static String getFileSHA(File file) throws NoSuchAlgorithmException, IOException {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        digest = MessageDigest.getInstance("SHA-1");
        in = new FileInputStream(file);
        while ((len = in.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        in.close();
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }*/

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FileUtil.startDownloadMeg:

                    //progressBar.setMax(mUpdateUIThread.getFileSize());   //开始
                    progressDialog.setMax(mUpdateUIThread.getFileSize());
                    progressDialog.show();
                    progressDialog.onStart();
                    break;
                case FileUtil.updateDownloadMeg:
                    if (!mUpdateUIThread.isCompleted())   //下载
                    {
                        //Log.e(TAG, "已下载：" + mUpdateUIThread.getDownloadSize());
                        //progressBar.setProgress(mUpdateUIThread.getDownloadSize());
                        progressDialog.setProgress(mUpdateUIThread.getDownloadSize());
                        //.setText("下载速度：" + mUpdateUIThread.getDownloadSpeed() + "k/秒"/*       下载百分比" + mUpdateUIThread.getDownloadPercent() + "%"*/);
                    } else {
                        //info.setText("下载完成");
                    }
                    break;
                case FileUtil.endDownloadMeg:
                    Toast.makeText(RecoveryActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
				/*apk安装界面跳转*/
                   /* String filename = FileUtil.getFileName(url);
                    String str = "/tsing_recovery/" + filename;
                    String fileName = Environment.getExternalStorageDirectory() + str;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
                    startActivity(intent);*//*
                    //exec("mv /storage/emulated/legacy/tsing_recovery/" + filename + " /system/test/test.wim");*/
                    //info.setText("下载完成");
                    //.setProgress(0);
                    create_wim.setEnabled(true);
                    download.setEnabled(true);
                    cancel_download.setEnabled(false);
                    break;
                case FileUtil.cancleDownloadMeg:
                    Toast.makeText(RecoveryActivity.this, "取消下载", Toast.LENGTH_SHORT).show();
                    exec("rm /storage/emulated/legacy/tsing_recovery/test.wim");
                    //info.setText("下载取消");
                    //progressBar.setProgress(0);
                    create_wim.setEnabled(true);
                    download.setEnabled(true);
                    cancel_download.setEnabled(false);
                    progressDialog.dismiss();

                    break;
                case FileUtil.timeout:
                    Toast.makeText(RecoveryActivity.this, "连接超时，请检查网络或者站点是否正常", Toast.LENGTH_SHORT).show();
                    exec("rm /storage/emulated/legacy/tsing_recovery/test.wim");
                    //.setText("连接超时");
                    //progressBar.setProgress(0);
                    create_wim.setEnabled(true);
                    download.setEnabled(true);
                    cancel_download.setEnabled(false);
                    break;
                case FileUtil.fileNotExist:
                    Toast.makeText(getApplication(), "文件不存在", Toast.LENGTH_LONG).show();
                    create_wim.setEnabled(true);
                    download.setEnabled(true);
                    chooseimageid.setEnabled(true);
                    break;
                case FileUtil.fileRight:
                    Toast.makeText(getApplication(), "文件SHA1检验正确", Toast.LENGTH_LONG).show();
                    dialog();

                    break;
                case FileUtil.fileWrong:
                    Toast.makeText(getApplication(), "文件SHA1检验失败，请重新下载", Toast.LENGTH_LONG).show();
                    create_wim.setEnabled(true);
                    download.setEnabled(true);
                    chooseimageid.setEnabled(true);
                    break;
            }
            super.handleMessage(msg);
        }
    };

}

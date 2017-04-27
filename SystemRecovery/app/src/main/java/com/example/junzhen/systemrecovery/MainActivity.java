package com.example.junzhen.systemrecovery;


import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {

    // private ConfigTab configTab;
    private MainTab mainTab;
    private static final int OUTPUT_BUFFER_SIZE = 1024;


    private String wimfile7,wimfile8,wimfile10;



    private File file;

    private LinearLayout welcome;

    List<String> index;
    List<String> name;
    List<BigInteger> image_size;
    List<BigInteger> disk_size;
    List<String> section_detail;

    private boolean isRight = false;

    private String[] data;
    private String[] listviewdata;
    private ListView listview;
    private ListView listview_section;
    List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();

    private SimpleAdapter listItemAdapter;
    private ArrayList<HashMap<String, Object>> listItems;

    private int pos = -1;
    private int pos_sys = -1;

    private String chooseid = "";
    private String choose_section = "";
    private  String url_win7_home,url_win8,url_win10;

    private CheckIntegrity checkIntegrity = null;
    // private CheckIntegrity checkIntegrity10 = null;
    private updateUIThread mUpdateUIThread = null;

    private String win7_sha1_stardard,win8_sha1_stardard,win10_sha1_stardard;

    ProgressDialog checkprogressDialog;
    ProgressDialog recoveryprogressDialog;
    ProgressDialog downloadprogressDialog;

    private Button recovery,helpMessagge;

    private Button offline_win7,offline_win8, offline_win10,online_win7_home,online_win8,online_win10;


    public int i,j;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.Theme_MyCustomTheme);
        super.onCreate(savedInstanceState);
        //2016.4.13wimlib-imagex i
        getConfig();
        setContentView(R.layout.final_layout);

       /* offline = (ImageButton) findViewById(R.id.offline);
        online = (ImageButton) findViewById(R.id.online);*/

        offline_win7 = (Button)findViewById(R.id.offline_win7);
        offline_win8 = (Button) findViewById(R.id.offline_win8);
        offline_win10=(Button)findViewById(R.id.offline_win10);

        online_win7_home = (Button)findViewById(R.id.online_win7);
        online_win8 = (Button)findViewById(R.id.online_win8);
        online_win10 = (Button) findViewById(R.id.online_win10);



        recovery = (Button) findViewById(R.id.recovery);
        helpMessagge=(Button) findViewById(R.id.helpMessage);
        listview_section = (ListView) findViewById(R.id.listView2);

        recovery.setVisibility(View.GONE);
        listview_section.setVisibility(View.GONE);

        View.OnClickListener offline_win7_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (fileIsExists(wimfile7)) {
                    //setWiminfo();
                    i=1;
                    checkintergrity();

                } else {
                    //弹出下载对话框
                    Toast.makeText(getApplication(), "您的电脑磁盘没找到系统布置文件，请使用网络云盘", Toast.LENGTH_LONG).show();
                }
            }


        };
        offline_win7.setOnClickListener(offline_win7_listener);


        View.OnClickListener offline_win8_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (fileIsExists(wimfile8)) {
                    //setWiminfo();
                    i=2;
                    checkintergrity();

                } else {
                    //弹出下载对话框
                    Toast.makeText(getApplication(), "您的电脑磁盘没找到系统布置文件，请使用网络云盘", Toast.LENGTH_LONG).show();
                }
            }


        };
        offline_win8.setOnClickListener(offline_win8_listener);


        View.OnClickListener offline_win10_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (fileIsExists(wimfile10)) {
                    //setWiminfo();
                    i=3;
                    checkintergrity();

                } else {
                    //弹出下载对话框
                    Toast.makeText(getApplication(), "您的电脑磁盘没找到系统布置文件，请使用网络云盘", Toast.LENGTH_LONG).show();
                }
            }
        };
        offline_win10.setOnClickListener(offline_win10_listener);



        View.OnClickListener online_win7_home_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (fileIsExists(wimfile7)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("云盘系统部署工具已经存在，是否重新下载！");
                    builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            exec("rm /storage/emulated/legacy/tsing_recovery/win7_home.wim");
                            j=1;
                            download_dialog();
                        }
                    });
                    builder.setNegativeButton("取消",null);
                    builder.create();
                    builder.show();
                } else {
                    //弹出下载对话框
                    j=1;
                    download_dialog();
                }
            }
        };
        online_win7_home.setOnClickListener(online_win7_home_listener);


        View.OnClickListener online_win8_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (fileIsExists(wimfile8)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("云盘系统部署工具已经存在，是否重新下载！");
                    builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            exec("rm /storage/emulated/legacy/tsing_recovery/win8.wim");
                            j=2;
                            download_dialog();
                        }
                    });
                    builder.setNegativeButton("取消",null);
                    builder.create();
                    builder.show();
                } else {
                    //弹出下载对话框
                    j=2;
                    download_dialog();
                }
            }
        };
        online_win8.setOnClickListener(online_win8_listener);





        View.OnClickListener online_win10_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (fileIsExists(wimfile10)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("云盘系统部署工具已经存在，是否重新下载！");
                    builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            exec("rm /storage/emulated/legacy/tsing_recovery/win10.wim");
                            j=3;
                            download_dialog();
                        }
                    });
                    builder.setNegativeButton("取消",null);
                    builder.create();
                    builder.show();
                } else {
                    //弹出下载对话框
                    j=3;
                    download_dialog();
                }
            }
        };
        online_win10.setOnClickListener(online_win10_listener);


        View.OnClickListener helpMessaggelistener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("系统恢复流程");
                builder.setMessage("1.如果本地有windows系统的镜像，则可以选择本地镜像，在右侧的分区列表中选择您想要恢复到哪个分区，然后就可以将现在的系统恢复到windows系统了；\n" +
                        "2.如果本地没有windows系统的镜像，您可以通过云盘下载我们官方的镜像，同样在分区列表选择分区，即可开始恢复windows系统。");
                builder.setNeutralButton("确定", null);
                builder.create();
                builder.show();

            }
        };
        helpMessagge.setOnClickListener(helpMessaggelistener);




        View.OnClickListener recoverylistener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog();
            }
        };
        recovery.setOnClickListener(recoverylistener);
        getConfig();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }



    private void setWiminfo() {
        String str;
        index = new ArrayList<>();
        name = new ArrayList<>();
        image_size = new ArrayList<>();
        //Toast.makeText(getApplication(), wimfile, Toast.LENGTH_LONG).show();

        if (listems.isEmpty()) {

            //展示windows系统列表
            str = exec("wimlib-imagex info " + wimfile7 + "\n");
            String[] info = str.split("\n");

            for (int i = 0; i < info.length; i++) {
                String t_index;
                String t_name;
                String t_size;
                if (info[i].contains("Index")) {
                    if (info[i].split(":")[0].contains("Boot"))
                        continue;
                    t_index = info[i].split(":")[1];
                    index.add(t_index);
                }
                if (info[i].contains("Name")) {
                    t_name = info[i].split(":")[1];
                    name.add(t_name);
                }
                if (info[i].contains("Total Bytes")) {
                    t_size = info[i].split(":")[1];
                    Toast.makeText(getApplication(), t_size, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplication(), t_size.trim(), Toast.LENGTH_LONG).show();
                    image_size.add(new BigInteger(t_size.trim()));
                }
            }
            int num = index.size();
            listviewdata = new String[num];
            for (int i = 0; i < num; i++) {
                HashMap<String, Object> pMap = new HashMap<String, Object>();
                pMap.put("name", name.get(i));
                pMap.put("picture", R.drawable.win10);
                listems.add(pMap);
                //listviewdata[i] = index.get(i)+"——"+name.get(i);
            }
        }
        listview.setVisibility(View.VISIBLE);
        set_listview();

    }

    public void set_listview() {
        SimpleAdapter adapter = new SimpleAdapter(this, listems, R.layout.listview_item, new String[]{"picture", "name"}, new int[]{R.id.itemImage, R.id.itemText});

        listview.setAdapter(adapter);
        listview.setBackgroundColor(Color.LTGRAY);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (pos_sys != -1) {
                    View v = parent.getChildAt(pos_sys);
                    v.setBackgroundColor(Color.LTGRAY);
                }
                pos_sys = position;

                chooseid = String.valueOf(position + 1);
                TextView tv = (TextView) view.findViewById(R.id.itemText);
                tv.setTextColor(Color.WHITE);
                view.setBackgroundResource(R.color.blue);
                //Toast.makeText(getApplication(), chooseid, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void download_dialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
            downloadprogressDialog = new ProgressDialog(MainActivity.this);
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

    private void checkintergrity()                                                                                                                                                          {
        checkprogressDialog = new ProgressDialog(MainActivity.this);
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


    public updateUIThread newmyThread() {


      /*  mUpdateUIThread = new updateUIThread(handler, url_win7_home, FileUtil.setMkdir(this) + File.separator, "online_win7_home.wim");*/
        if (j==1){
            mUpdateUIThread = new updateUIThread(handler, url_win7_home, FileUtil.setMkdir(this) + File.separator, "win7_home.wim");
        }else if (j==2){
            mUpdateUIThread = new updateUIThread(handler, url_win8, FileUtil.setMkdir(this) + File.separator, "win8.wim");
        }else if (j==3){
            mUpdateUIThread = new updateUIThread(handler, url_win10, FileUtil.setMkdir(this) + File.separator, "win10.wim");
        }
        return mUpdateUIThread;
    }



    public CheckIntegrity newcheckThread() {
        //Toast.makeText(getApplication(), wimfile, Toast.LENGTH_LONG).show();

       /* checkIntegrity = new CheckIntegrity(handler, wimfile7, win7_sha1_stardard);*/
       // if (i!=0&&j==0) {
            if (i == 1) {
                checkIntegrity = new CheckIntegrity(handler, wimfile7, win7_sha1_stardard);
            } else if (i == 2) {
                checkIntegrity = new CheckIntegrity(handler, wimfile8, win8_sha1_stardard);
            }else if (i == 3) {
                checkIntegrity = new CheckIntegrity(handler, wimfile10, win10_sha1_stardard);
            }
        return checkIntegrity;
    }


    private void getConfig() {
        FileUtil.setMkdir(getApplicationContext());
        String configname = "/storage/emulated/legacy/tsing_recovery/recovery.config";
        file = new File(configname);
        if (!file.exists()) {
            FileWriter fw;
            BufferedWriter bw;
            try {
                StringBuffer config = new StringBuffer("");
                //config.append(sourcefile.getText().toString() + "\n");
                win7_sha1_stardard = "0237435ae2a14078510136201ebb4e67e7425e81";
                win8_sha1_stardard = "58589cf5b04fd17524ff8ef9e0c54cc73ea3f382";
                win10_sha1_stardard = "9b0a40b1aabf6948b7b7bf83ec0679c479016242";
                wimfile7 = "/storage/emulated/legacy/tsing_recovery/win7_home.wim";
                wimfile8 = "/storage/emulated/legacy/tsing_recovery/win8.wim";
                wimfile10 = "/storage/emulated/legacy/tsing_recovery/win10.wim";

                url_win7_home = "http://192.168.0.129:8080/AppStoreServer/Resource/Software/9/9.apk";
                url_win8 = "http://dldir1.qq.com/qqfile/qq/QQ7.7/16096/QQ7.7.exe";
                url_win10 = " http://192.168.0.81:8080/win10.wim";

                config.append(win7_sha1_stardard + "\n");
                config.append(win8_sha1_stardard + "\n");
                config.append(win10_sha1_stardard + "\n");
                config.append(wimfile7 + "\n");
                config.append(wimfile8 + "\n");
                config.append(wimfile10 + "\n");
                config.append(url_win7_home + "\n");
                config.append(url_win8 + "\n");
                config.append(url_win10 + "\n");
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
                win8_sha1_stardard = x[1];
                win10_sha1_stardard= x[2];
                wimfile7 = x[3];
                wimfile8 = x[4];
                wimfile10=x[5];
                url_win7_home = x[6];
                url_win8 = x[7];
                url_win10 = x[8];
                br.close();
                fr.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void section_select() {

        String section_cmd = "fdisk -l /dev/block/sda";
        String info = exec(section_cmd);
        String[] section_info = info.split("\n");

        section_detail = new ArrayList<>();
        disk_size = new ArrayList<>();

        for (int i = 0; i < section_info.length; i++) {
            BigInteger begin, end;
            if (/*section_info[i].contains("Number") &&*/ section_info[i].contains("Start") && section_info[i].contains("End")) {
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.listview_item1, data);

        listview_section.setAdapter(adapter);
        listview_section.setBackgroundColor(Color.LTGRAY);
        listview_section.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /* if (position != 0 && position != 1 && position != 2) {
                    if (disk_size.get(position).compareTo(image_size.get(Integer.valueOf(chooseid) - 1)) > 0) {
                        if (pos != -1) {
                            View v = parent.getChildAt(pos);
                            v.setBackgroundColor(Color.LTGRAY);
                        }*/
                        pos = position;
                        choose_section = String.valueOf(position + 1);
                        TextView tv = (TextView) view.findViewById(R.id.itemText1);
                        tv.setTextColor(Color.WHITE);
                        view.setBackgroundResource(R.color.blue);

                 /*   } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                }*/
            }
        });
    }

    class MyTask extends AsyncTask<String, String, String> {

        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            //Log.i(TAG, "onPreExecute() called");

            //info.setText("loading...");
            recoveryprogressDialog = new ProgressDialog(MainActivity.this);
            recoveryprogressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            recoveryprogressDialog.setMessage("正在恢复Windows系统，请耐心等待！");
            recoveryprogressDialog.setCancelable(false);
            recoveryprogressDialog.show();
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {

            Log.v("Window","doInBackground");
            //Log.i(TAG, "doInBackground(Params... params) called");

            if (params != null) {
                if (params[0].equals("check")) {
                    return "false";

                } else {
                    try {

                        Log.v("Window","1");
                        Runtime rt = Runtime.getRuntime();
                        Log.v("Window","2");
                        Process process = rt.exec("su");//Root权限
                        //Process process = rt.exec("sh");//模拟器测试权限
                        Log.v("Window","3");
                        DataOutputStream dos = new DataOutputStream(process.getOutputStream());
                        //dos.writeBytes(params[0] + " " + params[1] + " " + params[2] + "\n");

                        Log.v("Window","4");
                        dos.writeBytes(params[0] + "\n");
                        Log.v("Window","5");
                        dos.flush();
                        //process.waitFor();
                        Log.v("Window","6");
                        dos.writeBytes(params[1] + "\n");
                        Log.v("Window","7");
                        dos.flush();
                        Log.v("Window","8");
                        dos.writeBytes("exit\n");
                        Log.v("Window","9");
                        dos.flush();
                        InputStream myin = process.getInputStream();
                        InputStreamReader is = new InputStreamReader(myin);

                        Log.v("Window","10");
                        BufferedReader ibr = new BufferedReader(is);
                        String inline;
                        while ((inline = ibr.readLine()) != null) {
                            System.out.println(inline);

                            //调用publishProgress公布进度,最后onProgressUpdate方法将被执行
                            //if (params[0].contains("capture")) {
                            if (inline.contains("%")) {

                                Log.v("Window","11");
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
        String cmd2 = "wimlib-imagex apply " + wimfile7 + " " + "4 "+ dir;
        String cmd3 = "wimlib-imagex apply " + wimfile8 + " " + dir;
        String cmd4 = "wimlib-imagex apply " + wimfile10 + " " + dir;

        /*Toast.makeText(getApplication(), cmd2, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplication(), cmd1, Toast.LENGTH_LONG).show();*/
        MyTask myTask = new MyTask();



       // myTask.execute(cmd1, cmd2);

            if (i == 1) {

                Log.v("Window","i=1");
                myTask.execute(cmd1, cmd2);
            } else if (i == 2) {
                Log.v("Window","i=2");
                myTask.execute(cmd1, cmd3);
            } else if (i==3){
                Log.v("Window","i=3");
                myTask.execute(cmd1, cmd4);
            }


    }

    public void reboot() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                    Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    downloadprogressDialog.dismiss();
                    /*apk安装界面跳转*/
                    //跳转到选择系统界面
                    break;
                case FileUtil.cancleDownloadMeg:
                    Toast.makeText(MainActivity.this, "取消下载", Toast.LENGTH_SHORT).show();
                    exec("rm /storage/emulated/legacy/tsing_recovery/test.wim");
                    downloadprogressDialog.dismiss();

                    break;
                case FileUtil.timeout:
                    Toast.makeText(MainActivity.this, "连接超时，请检查网络或者站点是否正常", Toast.LENGTH_SHORT).show();
                    exec("rm /storage/emulated/legacy/tsing_recovery/test.wim");
                    break;
                case FileUtil.fileNotExist:
                    Toast.makeText(getApplication(), "文件不存在", Toast.LENGTH_LONG).show();
                    checkprogressDialog.dismiss();
                    break;
                case FileUtil.fileRight:
                    Toast.makeText(getApplication(), "文件SHA1检验正确", Toast.LENGTH_LONG).show();
                    checkprogressDialog.dismiss();

                    //listview.setVisibility(View.GONE);
                   /*2016.04.13
                    back_system.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.GONE);
                    listview_section.setVisibility(View.VISIBLE);
                    system.setVisibility(View.GONE);
                    partition.setVisibility(View.VISIBLE);*/
                    recovery.setVisibility(View.VISIBLE);
                    listview_section.setVisibility(View.VISIBLE);
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

    /*public class ListAdapter extends BaseAdapter {

        ArrayList<ButtonView> arrayList = null;
        LayoutInflater inflater;
        View view;
        ButtonLayoutHolder buttonLayoutHolder;
        LinearLayout buttonLayout = null;
        TextView buttonText = null;
        ImageView buttonImage = null;

        private int selectedPosition = -1;// 选中的位置

        public ListAdapter(ArrayList<ButtonView> buttonListView) {
            // TODO Auto-generated constructor stub
            arrayList = buttonListView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_item, null, false);
            buttonLayoutHolder = (ButtonLayoutHolder) view.getTag();

            if (buttonLayoutHolder == null) {
                buttonLayoutHolder = new ButtonLayoutHolder();
                buttonLayoutHolder.buttonLayout = (LinearLayout) view
                        .findViewById(R.id.LinearLayout);
                buttonLayoutHolder.textView = (TextView) view
                        .findViewById(R.id.itemText);
                buttonLayoutHolder.imageView = (ImageView) view
                        .findViewById(R.id.itemImage);
                view.setTag(buttonLayoutHolder);
            }
            buttonLayout = buttonLayoutHolder.buttonLayout;
            buttonText = buttonLayoutHolder.textView;
            buttonImage = buttonLayoutHolder.imageView;
            if (selectedPosition == position) {
                buttonText.setSelected(true);
                buttonText.setPressed(true);
                buttonLayout.setBackgroundColor(Color.RED);
            } else {
                buttonText.setSelected(false);
                buttonText.setPressed(false);
                buttonLayout.setBackgroundColor(Color.TRANSPARENT);

            }

            buttonText.setTextColor(Color.WHITE);
            buttonText.setText(arrayList.get(position).textViewId);

            return view;

        }

    }

    ;

    class ButtonView {
        int textViewId;

        ButtonView(int tId) {
            textViewId = tId;
        }
    }

    class ButtonLayoutHolder {
        LinearLayout buttonLayout;
        TextView textView;
        ImageView imageView;
    }*/
}

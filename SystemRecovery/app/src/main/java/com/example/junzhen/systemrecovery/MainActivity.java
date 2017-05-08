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
import android.widget.ImageButton;
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

    private String efi_win ;

    private File file;

    private LinearLayout welcome;

    List<String> index;
    List<String> name;
    List<BigInteger> image_size;
    List<BigInteger> disk_size;
    List<String> section_detail;
    List<String> deny_part;

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
    private String choose_partition = "";
    private String choose_efi = "";
    private String url_win7_home,url_win8,url_win10,url_efi;

    private String downloads_rm ;

    private CheckIntegrity checkIntegrity = null;
    // private CheckIntegrity checkIntegrity10 = null;
    private updateUIThread mUpdateUIThread = null;
    private updateUIThread efiUpdateUIThread = null;

    private String win7_sha1_stardard,win8_sha1_stardard,win10_sha1_stardard;

    ProgressDialog checkprogressDialog;
    ProgressDialog recoveryprogressDialog;
    ProgressDialog downloadprogressDialog;

    private ImageButton offline, online;

    private Button recovery,helpMessagge;

    private Button offline_win7,offline_win8, offline_win10,online_win7_home,online_win8,online_win10;

    public int i,j;

    private PartitionsAdapter mAdapter;

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

        offline_win10=(Button)findViewById(R.id.offline_win10);
        online_win10 = (Button) findViewById(R.id.online_win10);

        recovery = (Button) findViewById(R.id.recovery);
        helpMessagge=(Button) findViewById(R.id.helpMessage);
        listview_section = (ListView) findViewById(R.id.listView2);

        recovery.setVisibility(View.GONE);
        listview_section.setVisibility(View.GONE);

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
                            exec(downloads_rm);
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
                if (choose_partition.equals("")){
                    return;
                }

                    for (int k=0; k < deny_part.size(); k++){
                        if (choose_partition.equals(deny_part.get(k))){
                            return;
                        }
                    }

                dialog();
            }
        };
        recovery.setOnClickListener(recoverylistener);

        getConfig();
        downloads_rm = "rm -f " + wimfile10 + "  " + efi_win ;

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
        SimpleAdapter adapter = new SimpleAdapter(this, listems, R.layout.listview_item , new String[]{"picture", "name"}, new int[]{R.id.itemImage, R.id.itemText});

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
        builder.setMessage("点击\"下载\"按钮开始下载官方系统恢复文件");
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


/*
            if (efiUpdateUIThread == null) {
                efiUpdateUIThread = efiDownloadThread();
                efiUpdateUIThread.start();
            } else {
                efiUpdateUIThread = null;
                efiUpdateUIThread = efiDownloadThread();
                efiUpdateUIThread.start();
            }


            if (mUpdateUIThread == null) {
                mUpdateUIThread = wimDownloadThread();
                mUpdateUIThread.start();
            } else {
                mUpdateUIThread = null;
                mUpdateUIThread = wimDownloadThread();
                mUpdateUIThread.start();
            }
	    */

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

        mUpdateUIThread = new updateUIThread(handler, url_win10, FileUtil.setMkdir(this) + File.separator, "windows.wim");
        return mUpdateUIThread;
    }


    /*  mUpdateUIThread = new updateUIThread(handler, url_win7_home, FileUtil.setMkdir(this) + File.separator, "online_win7_home.wim");*/

    /*

    public updateUIThread wimDownloadThread() {
        Log.v("WinRec", "Downloading windows.wim");
        mUpdateUIThread = new updateUIThread(handler, url_win10, FileUtil.setMkdir(this) + File.separator, "windows.wim");
        return mUpdateUIThread;
    }

    public updateUIThread efiDownloadThread() {
        Log.v("WinRec", "Downloading efi.tar");
        efiUpdateUIThread = new updateUIThread(handler, url_efi, FileUtil.setMkdir(this) + File.separator, "efi.tar");
        return efiUpdateUIThread;
    }
    */

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
                win10_sha1_stardard = "9e37983f063d68ca97465a16ea870600bea79cc0";
                wimfile10 = "/storage/emulated/legacy/tsing_recovery/windows.wim";
                efi_win = "/storage/emulated/legacy/tsing_recovery/efi.tar" ;
               // url_win10 = "http://192.168.0.185/dl/windows.wim";
                url_win10 = "http://dldir1.qq.com/qqfile/qq/TIM1.0.5/20303/TIM1.0.5.exe";
                url_efi = "http://192.168.0.185/dl/efi.tar";

                config.append(win10_sha1_stardard + "\n");
                config.append(wimfile10 + "\n");
                config.append(efi_win + "\n");
                config.append(url_win10 + "\n");
                config.append(url_efi + "\n");
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


                String[] x = temp.toString().split("\\n+");
                win10_sha1_stardard= x[0];
                wimfile10=x[1];
                efi_win = x[2];
                url_win10 = x[3];
                url_efi = x[4];

                br.close();
                fr.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.v("WinRec", win10_sha1_stardard + "\n" + wimfile10 + "\n" + efi_win + "\n" + url_win10 + "\n" +url_efi );

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
                /*
                 buffer单行模式
		 */

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

    private boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }


    private void section_select() {

        String section_cmd = "fdisk -l /dev/block/sd[a-z]";
        String block_dev = "/dev/block/";
        String info = exec(section_cmd);
        String[] section_info = info.trim().split("\\n+");

        section_detail = new ArrayList<>();
        disk_size = new ArrayList<>();

        deny_part = new ArrayList<>();

        for (int i = 0; i < section_info.length; i++) {
            BigInteger Start, End, Size;
            String tmp_sp = "  ";
            String[] fdisk_line = section_info[i].trim().split("\\s+");

            Log.v("WinRec", "section_info:" + fdisk_line[0] + "  " +fdisk_line[1] + " " + fdisk_line[2] );
            if (fdisk_line.length < 3){
                continue;
            }

            if (fdisk_line[1].startsWith("/dev/block/sd")&&isNumeric(fdisk_line[2])) {
                BigInteger tmp_size;
                Size = new BigInteger(fdisk_line[2],10);
                tmp_size = Size.multiply(BigInteger.valueOf(512)).divide(BigInteger.valueOf(1048576)); //1024*1024 = 1048576

                block_dev = fdisk_line[1].substring(0, fdisk_line[1].length() - 1);
                //section_detail.add(fdisk_line[1].substring(0, fdisk_line[1].length() - 1) + tmp_sp + tmp_size +"M");

            }else if ( isNumeric(fdisk_line[1]) && isNumeric(fdisk_line[2]) ){
                //section_detail.add(fdisk_line[0] + tmp_sp + fdisk_line[1] + tmp_sp + fdisk_line[2] + tmp_sp + fdisk_line[3] + tmp_sp + fdisk_line[5] + " ");
                if (fdisk_line[5].startsWith("EFI")) {
                    choose_efi = block_dev + fdisk_line[0];
                }

                Start = new BigInteger(fdisk_line[1],10);
                End = new BigInteger(fdisk_line[2],10);

                //if partition < 5G , deny it .
                if ( End.subtract(Start).multiply(BigInteger.valueOf(512)).divide(BigInteger.valueOf(1048576)).compareTo(BigInteger.valueOf(5120)) < 0){
                    deny_part.add(block_dev + fdisk_line[0]);
                }

                //Log.v("WinRec", "this partition size: " +  End.subtract(Start).multiply(BigInteger.valueOf(512)).divide(BigInteger.valueOf(1048576)) + "M" );

                section_detail.add(block_dev + section_info[i].trim());
            }
        }
        Log.v("WinRec", "EFI partition: " + choose_efi);

/*
        for (int i = 0; i < section_info.length; i++) {
            BigInteger begin, end;
            if ( section_info[i].contains("Start") && section_info[i].contains("End")) {
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
*/

        //final AlertDialog.Builder section_select = new AlertDialog.Builder(RecoveryActivity.this);
        //section_select.setTitle("请选择分区");
/*
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
        */

        //final ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.listview_item1, data);
        ArrayList<String> datas = new ArrayList<>();
        ArrayList<String> deny_datas = new ArrayList<>();
        /*
        for (int k = 0; k < data.length; k++) {
            datas.add(data[k]);
        }*/

        for (int k=0; k < section_detail.size(); k++){
            datas.add(section_detail.get(k));
        }

        for (int k=0; k < deny_part.size(); k++){
            deny_datas.add(deny_part.get(k));
        }


        mAdapter = new PartitionsAdapter(this, datas, deny_datas);
        listview_section.setAdapter(mAdapter);
        listview_section.setBackgroundColor(Color.WHITE);
        listview_section.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                /*
                pos = position;
                choose_section = String.valueOf(position + 1);
                */

                //TextView tv = (TextView) view.findViewById(R.id.itemText1);
                //tv.setTextColor(Color.WHITE);
                //view.setBackgroundResource(R.color.gray);

                String get_text =  ((TextView) view).getText().toString();
                choose_partition = get_text.trim().substring(0,15);
                Log.v("WinRec", "Partition: " + choose_partition);

                mAdapter.setmSelectPos(position, choose_partition);
                mAdapter.notifyDataSetChanged();

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

                        dos.writeBytes(params[2] + "\n");
                        dos.flush();

                        dos.writeBytes("exit\n");
                        dos.flush();

                        InputStream myin = process.getInputStream();
                        InputStreamReader is = new InputStreamReader(myin);

                        Log.v("Window","over");
                        BufferedReader ibr = new BufferedReader(is);
                        String inline;
                        while ((inline = ibr.readLine()) != null) {
                            System.out.println(inline);

                            //调用publishProgress公布进度,最后onProgressUpdate方法将被执行
                            //if (params[0].contains("capture")) 
                            if (inline.contains("%")) {

                                Log.v("Window","progress");
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

        //String dir = "/dev/block/sdb" + choose_section;
	    //String efi_dev="/dev/block/sdb2";
	    String efi_dir="/data/local/tmp/efi/";

        String dir = choose_partition;
        String efi_dev = choose_efi;

        String cmd1 = "mkntfs -f " + dir;
        String cmd_wim = "wimlib-imagex apply " + wimfile10 + " " + dir;

	    //String efi_rec= "mkdir -p " + efi_dir + " ; mount -t vfat " + efi_dev + " " + efi_dir + " ; cp -rf " + efi_win + " " + efi_dir + "EFI/ ; umount " + efi_dir ;
	    String efi_rec= "mkdir -p " + efi_dir + " ; mount -t vfat " + efi_dev + " " + efi_dir + " ; tar -xf " + efi_win + " -C " + efi_dir + "EFI/ ; umount " + efi_dir ;

        /*Toast.makeText(getApplication(), cmd2, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplication(), cmd1, Toast.LENGTH_LONG).show();*/
        MyTask myTask = new MyTask();

       // myTask.execute(cmd1, cmd2);

    	myTask.execute(cmd1, cmd_wim, efi_rec);

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
                exec("sync; reboot -f");

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
                    exec(downloads_rm);
                    downloadprogressDialog.dismiss();

                    break;
                case FileUtil.timeout:
                    Toast.makeText(MainActivity.this, "连接超时，请检查网络或者站点是否正常", Toast.LENGTH_SHORT).show();
                    exec(downloads_rm);
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

package com.example.junzhen.systemrecovery;


import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/*
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
*/

public class MainActivity extends Activity{

    // private ConfigTab configTab;
    private MainTab mainTab;
    private static final int OUTPUT_BUFFER_SIZE = 1024;

    private String wimfile10;

    private String efi_win ;

    private File file;

    private LinearLayout welcome;

    List<String> index;
    List<String> name;
    List<BigInteger> image_size;
    List<BigInteger> disk_size;
    List<String> section_detail;
    List<String> deny_part;

    private long deny_size = 5120; //5G

    private ArrayList<Disk> mDisks = new ArrayList<>();

    private boolean isRight = false;

    private String[] data;
    private String[] listviewdata;
    private ListView listview;
    private ListView listview_section;

    private TextView tvInfo;

    private LinearLayout main; //for disk parition info

    List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();

    private SimpleAdapter listItemAdapter;
    private ArrayList<HashMap<String, Object>> listItems;

    private int pos = -1;
    private int pos_sys = -1;

    private String chooseid = "";
    private String choose_section = "";
    private String choose_partition = "";
    private String choose_efi = "";
    private String efi_dir="/data/local/tmp/efi/";
    private String url_win10,url_efi;

    private String downloads_rm ;
    private ProgressDialog mProgressDialog;


    private CheckIntegrity checkIntegrity = null;
    // private CheckIntegrity checkIntegrity10 = null;
    private updateUIThread mUpdateUIThread = null;
    private updateUIThread efiUpdateUIThread = null;

    private String win10_sha1_stardard;

    ProgressDialog checkprogressDialog;
    ProgressDialog recoveryprogressDialog;
    ProgressDialog downloadprogressDialog;

    private ImageButton offline, online;

    private Button recovery,helpMessagge;
    private TextView rec_tv;

    private Button  offline_win10, online_win10, efi_backup;

    public int i, j;
    private int notice_flag = 0;

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

        efi_backup = (Button) findViewById(R.id.backup_efi);

        main = (LinearLayout) findViewById(R.id.targetdisk);

        helpMessagge=(Button) findViewById(R.id.helpMessage);

        recovery = (Button) findViewById(R.id.recovery);
        rec_tv = (TextView) findViewById(R.id.rec_tv);
        listview_section = (ListView) findViewById(R.id.listView2);

        //notice info
        setOffNextStep();

        listview_section.setVisibility(View.GONE);

        View.OnClickListener offline_win10_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (fileIsExists(wimfile10)) {
                    //setWiminfo();
                    i=3;
                    // checkintergrity();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("请选择Windows恢复分区，并按“下一步”。");
                    builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            choose_partition = "";
                            setOnNextStep(3);
                        }
                    });
                    builder.setNegativeButton("取消",null);
                    builder.create();
                    builder.show();

                } else {
                    Toast.makeText(getApplication(), "没有找到系统镜像WIM文件，请先使用云盘镜像下载。", Toast.LENGTH_LONG).show();
                }
            }
        };
        offline_win10.setOnClickListener(offline_win10_listener);

        View.OnClickListener online_win10_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setOffNextStep();

                if (fileIsExists(wimfile10)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("云盘系统镜像已经存在，是否重新下载？");
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


        View.OnClickListener efi_backup_listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                setOffNextStep();
                if (fileIsExists(efi_win)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("检测到Windows efi已经备份，是否重新备份？");
                    builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            backup_windows_efi();
                        }
                    });
                    builder.setNegativeButton("取消",null);
                    builder.create();
                    builder.show();
                } else {
                    //备份
                    backup_windows_efi();
                }

            }
        };
        efi_backup.setOnClickListener(efi_backup_listener);

        View.OnClickListener helpMessaggelistener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("系统恢复流程");
                builder.setMessage( "1.如果本地没有windows系统的wim镜像，需要先通过云盘下载我们官方的镜像，在磁盘分区列表选择恢复分区，即可开始恢复windows系统.\n\n" +
                                "2.如果本地已有windows系统的wim镜像，则可以直接在磁盘分区列表中选择想要恢复的分区进行系统恢复.\n" );

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
                    rec_tv.setText("请先选择Windows恢复分区。");
                    return;
                }

                    for (int k=0; k < deny_part.size(); k++){
                        if (choose_partition.equals(deny_part.get(k))){
                            rec_tv.setText("选择的分区不正确！");
                            return;
                        }
                    }

                checkintergrity();

               // dialog();
            }
        };
        recovery.setOnClickListener(recoverylistener);

       //getConfig();
        //downloads_rm = "rm -f " + wimfile10 + "  " + efi_win ;
        downloads_rm = "rm -f " + wimfile10 ;

        //just show disk list
        section_select();

        /*
        for (Disk d : mDisks) {
            for (Volume v : d.getVolumes()) {
                Log.i("diskinfo",  v.getBlock() + "," + v.getSize() + "," + v.getSectorStart() + "," + v.getSectorEnd() + "," +v.getfDisk() + v.getInfo());
            }
        }
        */

        for (Disk d : mDisks) {
            LinearLayout space = new LinearLayout(this);
            space.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            space.setLayoutParams(spaceParams);
            space.setGravity(Gravity.BOTTOM);
            space.setPadding(0,30,0,0);

            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.storage);
            iv.setPadding(5,0,0,0);

            TextView tv = new TextView(this);
            tv.setText(d.getBlock() + "  Size: "+ d.getSize() + "GB");
            tv.setPadding(5, 0, 0, 3);
            tv.setTextSize(20);
            tv.setTextColor(Color.BLACK);

            space.addView(iv);
            space.addView(tv);

            main.addView(space);
            main.addView(getDiskView(d));
        }

        View mLine = new View(this);
        mLine.setBackgroundColor(Color.LTGRAY);
        mLine.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));

        tvInfo = new TextView(this);
        tvInfo.setPadding(20, 10, 0, 0);
        tvInfo.setTextSize(20);
        tvInfo.setTextColor(Color.BLACK);

        main.addView(mLine);
        main.addView(tvInfo);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private void setOnNextStep(int flag){
        notice_flag = flag;
        recovery.setVisibility(View.VISIBLE);
        rec_tv.setVisibility(View.VISIBLE);
    }

    private void setOffNextStep(){
        recovery.setVisibility(View.GONE);
        rec_tv.setVisibility(View.GONE);
    }

    private void setWiminfo() {
        String str;
        index = new ArrayList<>();
        name = new ArrayList<>();
        image_size = new ArrayList<>();
        //Toast.makeText(getApplication(), wimfile, Toast.LENGTH_LONG).show();

        if (listems.isEmpty()) {

            //展示windows系统列表
            str = exec("wimlib-imagex info " + wimfile10 + "\n");
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

    public void backup_windows_efi(){
        /*
        String section_cmd = "fdisk -l /dev/block/sd[a-z]";
        String block_dev = "/dev/block/";
        String info = exec(section_cmd);
        String[] section_info = info.trim().split("\\n+");

        for (int i = 0; i < section_info.length; i++) {
            String[] fdisk_line = section_info[i].trim().split("\\s+");
            if (fdisk_line.length < 3){
                continue;
            }

            //get disk dev info
            if (fdisk_line[1].startsWith("/dev/block/sd")&&isNumeric(fdisk_line[2])) {
                block_dev = fdisk_line[1].substring(0, fdisk_line[1].length() - 1);

                //get disk partition info
            }else if (isNumeric(fdisk_line[0]) && isNumeric(fdisk_line[1]) && isNumeric(fdisk_line[2]) ){

                if (fdisk_line.length > 5 && fdisk_line[5].startsWith("EFI")) {
                    choose_efi = block_dev + fdisk_line[0];
                    break;
                }
            }
        }
        Log.v("WinRec", "Detected EFI partition: " + choose_efi);
        */

        final EditText inputServer = new EditText(this);
        inputServer.setText(choose_efi);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("EFI分区").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer);
        builder.setMessage("检测的EFI分区是否正确？");

        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("正确", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                choose_efi = inputServer.getText().toString();
                Log.v("WinRec", "Get EFI partition: " + choose_efi);

                String regEx = "/dev/block/sd[a-z][0-9]";
                Pattern pattern = Pattern.compile(regEx);
                if(pattern.matcher(choose_efi.trim()).matches()){

                    String cmd_efi = "mkdir -p " + efi_dir + " ; mount -t vfat " + choose_efi + " " + efi_dir + " ; tar -cf " + efi_win + " Microsoft/ -C " + efi_dir + "EFI/ ; umount " + efi_dir ;
                    Log.v("WinRec", "EFI partition backup: " + cmd_efi);
                    exec(cmd_efi);

                    Toast.makeText(getApplication(), "EFI分区备份成功！", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplication(), "EFI分区不正确，备份失败！", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.show();

    }


    public void download_dialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提示");
        builder.setMessage("开始下载系统恢复文件。");
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
               // download();
                download_file(url_win10, wimfile10.substring(0, wimfile10.lastIndexOf("/")));
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

    public void download_file(String fileUrl, String fileDir) {
        if (isNetworkAvailable()) {

            mProgressDialog = new ProgressDialog(MainActivity.this);
           // mProgressDialog.setTitle("Downloading...");
            mProgressDialog.setMessage("正在下载...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressNumberFormat("%1dMB/%2dMB");

            final DownloadTask downloadTask = new DownloadTask(MainActivity.this);

            /*
            mProgressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "暂停", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadTask.pauseDownload();
                }
            });
            */
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadTask.cancelDownload();

                }
            });
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancelDownload();

                }
            });

            downloadTask.execute(fileUrl, fileDir);

        }else{
            Toast.makeText(getApplication(), "网络未连接", Toast.LENGTH_LONG).show();
        }

    }

    public void dialog() {

        if (fileIsExists(efi_win)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示");
            builder.setMessage("检测到Windows EFI已经备份，是否需要进行Windows EFI恢复？");
            builder.setNeutralButton("需要", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                    final EditText inputServer = new EditText(MainActivity.this);
                    inputServer.setText(choose_efi);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("EFI分区").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer);
                    builder.setMessage("检测的EFI分区是否正确？");

                    builder.setNegativeButton("不恢复EFI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog2(0);
                        }
                    });
                    builder.setPositiveButton("正确", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            choose_efi = inputServer.getText().toString();
                            Log.v("WinRec", "Get EFI partition: " + choose_efi);

                            String regEx = "/dev/block/sd[a-z][0-9]";
                            Pattern pattern = Pattern.compile(regEx);
                            if(pattern.matcher(choose_efi.trim()).matches()){
                                dialog2(1);
                            }else {
                                dialog2(0);
                            }
                        }
                    });
                    builder.show();

                }
            });
            builder.setNegativeButton("不需要", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog2(0);            }
            });
            builder.create();
            builder.show();
        } else {
            dialog2(0);
        }
    }

    public void dialog2(final int flag) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("警告");
            builder.setMessage("系统将恢复到分区:" + choose_partition + "，原有数据将会丢失！");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    decompress(flag);

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

    private void checkintergrity() {

        checkprogressDialog = new ProgressDialog(MainActivity.this);
        if (checkIntegrity == null) {
            checkIntegrity = newcheckThread();
            checkprogressDialog.setMessage("正在检验WIM文件完整性，请耐心等待…………");
            checkprogressDialog.setCancelable(false);
            checkprogressDialog.show();
            checkIntegrity.start();
        } else {
            checkIntegrity = null;
            checkIntegrity = newcheckThread();
            checkprogressDialog.setMessage("正在检验WIM文件完整性，请耐心等待…………");
            checkprogressDialog.setCancelable(false);
            checkprogressDialog.show();
            checkIntegrity.start();
        }
    }

    public updateUIThread newmyThread() {

        mUpdateUIThread = new updateUIThread(handler, url_win10, FileUtil.setMkdir(this) + File.separator, "windows.wim");
        return mUpdateUIThread;
    }


    public CheckIntegrity newcheckThread() {
        //Toast.makeText(getApplication(), wimfile, Toast.LENGTH_LONG).show();

       /* checkIntegrity = new CheckIntegrity(handler, wimfile7, win7_sha1_stardard);*/

        checkIntegrity = new CheckIntegrity(handler, wimfile10, win10_sha1_stardard);
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

                url_win10 = "http://192.168.0.185/dl/windows.wim";
                //url_win10 = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
                //url_efi = "http://192.168.0.185/dl/efi.tar";
                url_efi = " ";

                config.append(win10_sha1_stardard + "\n");
                config.append(wimfile10 + "\n");
                config.append(efi_win + "\n");
                config.append(url_win10 + "\n");
                config.append(url_efi + "\n");

                fw = new FileWriter(file);
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




    private View getDiskView(Disk d) {

        int viewHeight = 80;

        LinearLayout content = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, viewHeight + 30);
        content.setLayoutParams(layoutParams);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setBackground(getDrawable(R.drawable.disk_drawable));

        //content.setWeightSum(1);
        //content.setPadding(20, 20, 20, 20);

        for (Volume vol : d.getVolumes()) {

            float w = (float)0.1 + ((float)(vol.getSectorEnd() - vol.getSectorStart())) / d.getSectorNum();

            Log.i("getDiskView", "currect W: " + w);
            LinearLayout view = new LinearLayout(this);
            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(0, viewHeight, w);
            view.setLayoutParams(viewParams);

            TextView tv = new TextView(this);
            tv.setText(vol.getBlock() + "\nSize: "+ vol.getSize() + "MB");
            tv.setPadding(5, 5, 0, 0);
            tv.setTextSize(15);
            tv.setTextColor(Color.BLACK);

            // < 5G can not click
            if ( vol.getSize() < deny_size){
                view.setClickable(false);
                view.setBackground(getDrawable(R.drawable.gray_drawable));
            }else {
                view.setClickable(true);
                view.setBackground(getmDrawable());
            }

            view.addView(tv);
            view.setTag(vol);
            view.setOnClickListener(listener);

            content.addView(view);
        }

        return content;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Volume vol = (Volume) v.getTag();

            tvInfo.setVisibility(View.VISIBLE);
            tvInfo.setText(vol.getfDisk() + vol.getInfo());

            if ( vol.getSize() > deny_size){
                choose_partition = vol.getfDisk() + vol.getBlock();
                Log.v("WinRec", "Partition: " + choose_partition);

                rec_tv.setText("已选择分区： " + choose_partition);

            }else{
                choose_partition="";
                rec_tv.setText("");
            }

        }
    };

    private int[] colors = {R.drawable.green_drawable, R.drawable.red_drawable, R.drawable.blue_drawable, R.drawable.yellow_drawable};
    private int colorIndex = 0;
    private Drawable getmDrawable() {
        return getDrawable(colors[colorIndex++ % 4]);
    }

    public List<String> getBlockDev() {
        List<String> fileList = new ArrayList<String>();

        String filePath = "/sys/class/block/";

        String regEx = "sd[a-z]";
        Pattern pattern = Pattern.compile(regEx);

        //获得该路径文件夹下所有的文件
        File mfile = new File(filePath);
        File[] files = mfile.listFiles();

        //将所有的文件存入ArrayList中
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if(pattern.matcher(file.getName()).matches()){
                Log.d("getFileList","regEx files: " +  file.getPath());

                File file_r = new File(file.getPath() + "/removable");
                try{
                    FileInputStream in = new FileInputStream(file_r);
                    byte byt[] = new byte[128];
                    int len = in.read(byt);

                    String getStr = new String(byt);
                    if (getStr.startsWith("0")){
                        fileList.add(file.getName());
                    }
                    in.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }

        Log.d("getFileList","match files: " +  fileList);
        return fileList;
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

        long sectorSize = 512 ;

        section_detail = new ArrayList<>();
        disk_size = new ArrayList<>();

        deny_part = new ArrayList<>();
        Disk tmpDisk = null ;

        for (int i = 0; i < section_info.length; i++) {
            BigInteger Start, End, Size;
            String tmp_sp = "  ";
            String[] fdisk_line = section_info[i].trim().split("\\s+");

            //Log.v("WinRec", "section_info:" + fdisk_line[0] + "  " +fdisk_line[1] + " " + fdisk_line[2] );
            if (fdisk_line.length <= 3){
                continue;
            }

            //get disk dev info
            if (fdisk_line[1].startsWith("/dev/block/sd") && isNumeric(fdisk_line[2]) && fdisk_line[3].startsWith("sectors")) {
                Size = new BigInteger(fdisk_line[2],10);
                BigInteger tmp_size = Size.multiply(BigInteger.valueOf(512)).divide(BigInteger.valueOf(1073741824)); //1024*1024 = 1048576 ,1024*1024*1024 = 1073741824 -> GB

                block_dev = fdisk_line[1].substring(0, fdisk_line[1].length() - 1);
                //section_detail.add(fdisk_line[1].substring(0, fdisk_line[1].length() - 1) + tmp_sp + tmp_size +"M");

                tmpDisk = new Disk();
                tmpDisk.setBlock(block_dev);
                tmpDisk.setSize(tmp_size.longValue());
                tmpDisk.setSectorNum(Long.parseLong(fdisk_line[2]));
                tmpDisk.setSectorSize(sectorSize);

                Log.v("WinRec", "add disk: " +  block_dev );
                mDisks.add(tmpDisk);

             //   mDisks.add(d);

            //get volume partition info
            }else if (isNumeric(fdisk_line[0]) && isNumeric(fdisk_line[1]) && isNumeric(fdisk_line[2]) ){
                //section_detail.add(fdisk_line[0] + tmp_sp + fdisk_line[1] + tmp_sp + fdisk_line[2] + tmp_sp + fdisk_line[3] + tmp_sp + fdisk_line[5] + " ");
                if (fdisk_line.length > 5 && fdisk_line[5].startsWith("EFI")) {
                    choose_efi = block_dev + fdisk_line[0];
                }

                Start = new BigInteger(fdisk_line[1],10);
                End = new BigInteger(fdisk_line[2],10);

                BigInteger tmp_size = End.subtract(Start).multiply(BigInteger.valueOf(512)).divide(BigInteger.valueOf(1048576));

                //if partition < 5G , deny it .
                if ( tmp_size.compareTo(BigInteger.valueOf(5120)) < 0){
                    deny_part.add(block_dev + fdisk_line[0]);
                }

                //Log.v("WinRec", "this partition size: " +  End.subtract(Start).multiply(BigInteger.valueOf(512)).divide(BigInteger.valueOf(1048576)) + "M" );

                Volume v = new Volume();
                v.setfDisk(block_dev);
                v.setBlock(fdisk_line[0]);
                v.setSize(tmp_size.longValue());
                v.setSectorStart(Long.parseLong(fdisk_line[1]));
                v.setSectorEnd(Long.parseLong(fdisk_line[2]));
                v.setInfo(section_info[i].trim());

                Log.v("WinRec", "add volume: " +  fdisk_line[0]);
                tmpDisk.getVolumes().add(v);

                section_detail.add(block_dev + section_info[i].trim());
            }
        }
        Log.v("WinRec", "EFI partition: " + choose_efi);


        //final ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.listview_item1, data);
        ArrayList<String> datas = new ArrayList<>();
        ArrayList<String> deny_datas = new ArrayList<>();
        /*
        for (int k = 0; k < data.length; k++) {
            datas.add(data[k]);
        }*/


        //disk partition info
        /*
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
        */
    }

    private class DownloadTask extends AsyncTask<String, Long, Integer>{
        public static final int TYPE_SUCCESS = 0;
        public static final int TYPE_FAILED = 1;
        public static final int TYPE_PAUSED = 2;
        public static final int TYPE_CANCELED = 3;

        private boolean isCanceled = false;
        private boolean isPaused = false;
        private long lastProgress;

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context){
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... params) {

            HttpURLConnection urlConn = null;
            InputStream inputStream = null;

            InputStream is = null;
            RandomAccessFile savedFile = null;
            File file = null;
            try {
                long downloadedLength = 0;
                String downloadUrl = params[0];
                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                //String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                String directory = params[1];
                file = new File(directory + "/" + fileName);
                Log.d("DownloadTask: ","file path: " + file.getPath() );

                if (file.exists()){
                    downloadedLength = file.length();
                }

                long contentLength = getContentLength(downloadUrl);
                //long contentLength = urlConn.getContentLength();
                Log.d("DownloadTask: ","getContentLength: " + (contentLength / (1024*1024)) + "M" );

                if (contentLength == 0){
                    return TYPE_FAILED;
                }else if(contentLength == downloadedLength){
                    return TYPE_SUCCESS;
                }

                URL url = new URL(downloadUrl);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setConnectTimeout(5000);
                urlConn.setReadTimeout(5000);
                urlConn.connect();

                /*
                urlConn.setRequestProperty("Range", "bytes=" + downloadedLength + "-");
                urlConn.setUseCaches(false);
                */

                /*
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        //断点下载
                        .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                        .url(downloadUrl)
                        .build();
                Response response = client.newCall(request).execute();
                */

                int response = urlConn.getResponseCode();
                if(response == HttpURLConnection.HTTP_OK){
                    inputStream = urlConn.getInputStream();
                    is = new BufferedInputStream(inputStream);

                    //is = response.body().byteStream();
                    savedFile = new RandomAccessFile(file, "rw");
                    //savedFile.seek(downloadedLength); //跳过已下载的字节

                    byte[] b = new byte[1024];
                    long total = 0;
                    int len;

                    while ((len = is.read(b)) != -1){
                        if(isCanceled){
                            return TYPE_CANCELED;
                        }else if(isPaused){
                            return TYPE_PAUSED;
                        }else{
                            total += (long) len;
                            savedFile.write(b, 0, len);

                            //下载百分比
                            //int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                            //Log.d("DownloadTask: ","progress total: " + total );

                            //publishProgress(progress);
                            publishProgress((total + downloadedLength), contentLength);
                        }
                    }
                    //response.body().close();
                    return TYPE_SUCCESS;
                }

            } catch (Exception e){
                e.printStackTrace();
            } finally {
                try {
                    if (urlConn != null) {
                        urlConn.disconnect();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (is != null) {
                        is.close();
                    }

                    /*
                    if (is != null){
                        is.close();
                    }
                    */
                    if (savedFile != null){
                        savedFile.close();
                    }
                    if (isCanceled && file != null){
                        file.delete();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            return TYPE_FAILED;

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }


        @Override
        protected void onProgressUpdate(Long... progress) {
            super.onProgressUpdate(progress);

            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgress((int)(progress[0]/(1024*1024)));
            mProgressDialog.setMax((int)(progress[1]/(1024*1024)));

            //Log.d("DownloadTask: ","progress update: " + progress[0] );
        }

        @Override
        protected void onPostExecute(Integer status) {
            mWakeLock.release();
            mProgressDialog.dismiss();

            switch(status){
                case TYPE_SUCCESS:
                    Toast.makeText(context,"Download Success!", Toast.LENGTH_SHORT).show();
                    break;
                case TYPE_FAILED:
                    Toast.makeText(context,"Download Failed!", Toast.LENGTH_SHORT).show();
                    break;
                case TYPE_PAUSED:
                    Toast.makeText(context,"Download Paused!", Toast.LENGTH_SHORT).show();
                    break;
                case TYPE_CANCELED:
                    Toast.makeText(context,"Download Canceled!", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }

        }

        private long getContentLength(String downloadUrl) throws IOException{
            HttpURLConnection connection = null;
            long contenLength = 0;
            try{
                URL url = new URL(downloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    contenLength = connection.getContentLength();

                    if(contenLength < 0){
                        contenLength = Long.parseLong(connection.getHeaderField("Content-Length"));
                    }

                    Log.d("DownloadTask: ","getResponseCode: " + connection.getResponseCode() + ", getContentLength: " + contenLength);
                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
            }
            return contenLength;

            /*
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(downloadUrl)
                    .build();

            Response response = client.newCall(request).execute();
            if(response != null && response.isSuccessful()){
                long contenLength = response.body().contentLength();
                response.body().close();
                return contenLength;
            }
            return 0;
            */

        }

        public void pauseDownload(){
            isPaused = true;
        }

        public void cancelDownload(){
            isCanceled = true;
        }

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
                            //System.out.println(inline);

                            //调用publishProgress公布进度,最后onProgressUpdate方法将被执行
                            //if (params[0].contains("capture")) 
                            if (inline.contains("%")) {

                                //Log.v("Window","progress");
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
            if (result.equals("系统恢复成功")) {
                setOffNextStep();
                reboot();
            }

            if (result.equals("false")) {
                isRight = false;
            }
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
        }
    }

    public void writeFileDate(String fileName, InputStream message, int filesize){
        try{
            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = new byte[filesize];
            message.read(bytes);
            fout.write(bytes);
            fout.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void decompress(int flag) {

        String wimFile = "/data/data/" + getPackageName() + "/wimlib-imagex";

        if(!fileIsExists(wimFile)) {
            try {
                InputStream is = getResources().getAssets().open("bin/wimlib-imagex");
                FileOutputStream fos = new FileOutputStream(new File(wimFile));

                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }

                fos.flush();
                is.close();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File elfFile = new File(wimFile);
        boolean b = elfFile.setExecutable(true, true);

        //String dir = "/dev/block/sdb" + choose_section;
	    //String efi_dev="/dev/block/sdb2";

        String dir = choose_partition;
        String efi_dev = choose_efi;

        String cmd1 = "mkntfs -f " + dir;
        String cmd_wim = wimFile + "  apply " + wimfile10 + " " + dir;

	    //String efi_rec= "mkdir -p " + efi_dir + " ; mount -t vfat " + efi_dev + " " + efi_dir + " ; cp -rf " + efi_win + " " + efi_dir + "EFI/ ; umount " + efi_dir ;
	    String efi_rec= "mkdir -p " + efi_dir + " ; mount -t vfat " + efi_dev + " " + efi_dir + " ; tar -xf " + efi_win + " -C " + efi_dir + "EFI/ ; umount " + efi_dir ;

        /*Toast.makeText(getApplication(), cmd2, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplication(), cmd1, Toast.LENGTH_LONG).show();*/
        MyTask myTask = new MyTask();

       // myTask.execute(cmd1, cmd2);
        if (flag == 1) {
            Log.v("WinRec", "Rec include efi");
            myTask.execute(cmd1, cmd_wim, efi_rec);
        }else{
            Log.v("WinRec", "Rec no efi");
            myTask.execute(cmd1, cmd_wim, "");
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

                    //recovery.setVisibility(View.VISIBLE);
                    //listview_section.setVisibility(View.VISIBLE);
                    //section_select();
                    dialog();

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


}

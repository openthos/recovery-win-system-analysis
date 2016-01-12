package com.example.junzhen.systemrecovery;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

;


public class MainActivity extends Activity   {

   // private ConfigTab configTab;
    private MainTab mainTab;
    private static final int OUTPUT_BUFFER_SIZE = 1024;


    /**
     * 底部四个按钮
     */
    private LinearLayout mTabBtnWeixin;
    private LinearLayout mTabBtnFrd;
    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;

    private Button begin;
    private Button system;
    private Button partition;


    private String wimfile;

    private File file;

    private TextView welcome;

    List<String> index;
    List<String> name;
    List<String> section_detail;

    private boolean isRight = false;

    private String[] data;
    private String[] listviewdata;
    private ListView listview;
    private ListView listview_section;
    List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();

    private SimpleAdapter listItemAdapter;
    private ArrayList<HashMap<String, Object>>   listItems;


    private String chooseid;
    private String choose_section;
    private String url;
    private CheckIntegrity checkIntegrity = null;
    private String sha1_stardard;

    ProgressDialog checkprogressDialog;
    ProgressDialog recoveryprogressDialog;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_MyCustomTheme);
        super.onCreate(savedInstanceState);

        //initViews();
        //fragmentManager = getFragmentManager();
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        /*getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,      // 注意顺序
                R.layout.title);*/
        //打开APP的界面，“继续”按钮
        begin = (Button) findViewById(R.id.begin);
        system = (Button) findViewById(R.id.system);
        partition = (Button) findViewById(R.id.partition);
        listview = (ListView) findViewById(R.id.list_view);
        listview_section = (ListView) findViewById(R.id.list_view_section);
        welcome = (TextView) findViewById(R.id.welcome);

        system.setVisibility(View.GONE);
        partition.setVisibility(View.GONE);
        listview.setVisibility(View.GONE);
        listview_section.setVisibility(View.GONE);
        OnClickListener beginlistener = new OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                /*Intent intent = new Intent(MainActivity.this,RecoveryActivity.class);
                startActivity(intent);*/
                setWiminfo();
                begin.setVisibility(View.GONE);
                welcome.setVisibility(View.GONE);
                system.setVisibility(View.VISIBLE);
            }
        };
        begin.setOnClickListener(beginlistener);

        //打开系统选择界面，“继续”按钮

        OnClickListener systemlistener = new OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                checkintergrity();
                system.setVisibility(View.GONE);
            }
        };
        system.setOnClickListener(systemlistener);

        //打开分区选择界面，“继续”按钮

        OnClickListener partitionlistener = new OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                dialog();
            }
        };
        partition.setOnClickListener(partitionlistener);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setWiminfo(){
        String str;
        getConfig();
        index = new ArrayList<>();
        name = new ArrayList<>();
        //Toast.makeText(getApplication(), wimfile, Toast.LENGTH_LONG).show();
        if (fileIsExists(wimfile)) {
            listview.setVisibility(View.VISIBLE);
            //展示windows系统列表
            str = exec("wimlib-imagex info " + wimfile + "\n");
            String[] info = str.split("\n");

            for(int i=0;i<info.length;i++)
            {
                String t_index;
                String t_name;
                if(info[i].contains("Index"))
                {
                    if(info[i].split(":")[0].contains("Boot"))
                        continue;
                    t_index = info[i].split(":")[1];
                    index.add(t_index);
                }
                if(info[i].contains("Name"))
                {
                    t_name = info[i].split(":")[1];
                    name.add(t_name);
                }
            }
            int num = index.size();
            listviewdata = new String[num];
            for(int i=0;i<num;i++){
                HashMap<String, Object> pMap=new HashMap<String,Object>();
                pMap.put("name", name.get(i));
                pMap.put("picture", R.drawable.win10);
                listems.add(pMap);
                //listviewdata[i] = index.get(i)+"——"+name.get(i);
            }
            SimpleAdapter adapter=new SimpleAdapter(this,listems,R.layout.listview_item, new String[]{"picture","name"}, new int[]{R.id.itemImage,R.id.itemText});
            //ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,listviewdata);
            //int [] resIds={R.drawable.win10,R.drawable.win8,R.drawable.win7};
            //listview.setAdapter(new ListViewAdapter(listviewdata,resIds));

            listview.setAdapter(adapter);
            listview.setBackgroundColor(Color.LTGRAY);
            listview.setSelected(true);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    chooseid = String.valueOf(position+1);
                    Toast.makeText(getApplication(), chooseid, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            //弹出下载对话框
            /*str = "没有相关ESD文件，请点击下方的自动下载按钮！！";
            listview.setVisibility(View.GONE);
            tip.setVisibility(View.GONE);
            wiminfo.setTextColor(Color.RED);
            wiminfo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //bt.setEnabled(false);
//            chooselayout.setVisibility(View.GONE);
            wiminfo.setText(str);*/
        }

    }
    public void dialog() {
        //chooseimageid = (EditText) findViewById(R.id.chooseimageid);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //final String src = chooseimageid.getText().toString();
        //create_wim.setEnabled(true);
        builder.setTitle("警告");
        builder.setMessage("点击'确认'按钮，恢复系统默认安装在Windows系统盘，原有系统数据将会丢失");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                decompress();
                //create_wim.setEnabled(false);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //chooseimageid.setEnabled(true);
            }
        });
        builder.create();
        builder.show();
    }
    private void checkintergrity(){
        checkprogressDialog = new ProgressDialog(MainActivity.this);

        //Toast.makeText(getApplication(), "", Toast.LENGTH_LONG).show();
        //create_wim.setEnabled(false);
        //download.setEnabled(true);
        //chooseimageid.setEnabled(false);
        if(checkIntegrity == null)
        {
            checkIntegrity = newcheckThread();
            checkprogressDialog.setMessage("检验文件完整性过程时间较长，大约1分钟左右，请耐心等待…………");
            checkprogressDialog.setCancelable(false);
            checkprogressDialog.show();
            checkIntegrity.start();
        }else {
            checkIntegrity = null;
            checkIntegrity = newcheckThread();
            checkprogressDialog.setMessage("检验文件完整性过程时间较长，大约1分钟左右，请耐心等待…………");
            checkprogressDialog.setCancelable(false);
            checkprogressDialog.show();
            checkIntegrity.start();
        }
    }

    public CheckIntegrity newcheckThread() {
        Toast.makeText(getApplication(), wimfile, Toast.LENGTH_LONG).show();
        checkIntegrity = new CheckIntegrity(handler,wimfile,sha1_stardard);
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
    private void section_select(){
        String section_cmd = "fdisk -l /dev/block/sda";
        String info = exec(section_cmd);
        String[] section_info = info.split("\n");
        section_detail = new ArrayList<>();
        for(int i=0;i<section_info.length;i++){
            if(section_info[i].contains("Number") && section_info[i].contains("Start") && section_info[i].contains("End")){
                for(int j=i+1;j<section_info.length;j++){
                    String[] temp = section_info[j].split("\\s+");
                    section_detail.add(temp[1]+"  "+temp[4]);
                }
                break;
            }
        }
        //final AlertDialog.Builder section_select = new AlertDialog.Builder(RecoveryActivity.this);
        //section_select.setTitle("请选择分区");


        int num  = section_detail.size();
        data = new String[num];
        for(int i=0;i<num;i++) {
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,data);

        listview_section.setAdapter(adapter);
        listview_section.setBackgroundColor(Color.GREEN);
        listview_section.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                choose_section = String.valueOf(position + 1);
                Toast.makeText(getApplication(), choose_section, Toast.LENGTH_LONG).show();
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
            recoveryprogressDialog.setProgress(Integer.parseInt(progresses[0]));
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            recoveryprogressDialog.dismiss();
            if(result.equals("系统恢复成功"))
                reboot();
            if(result.equals("false"))
                isRight = false;
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
        }
    }
    public void decompress() {


        String dir = "/dev/block/sda"+choose_section;

        String cmd1 = "mkntfs -f " + dir;
        String cmd2 = "wimlib-imagex apply " + wimfile + " " + chooseid + " " + dir;
        Toast.makeText(getApplication(), cmd2, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplication(), cmd1, Toast.LENGTH_LONG).show();
        MyTask myTask = new MyTask();
        myTask.execute(cmd1, cmd2);
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

                    //progressBar.setMax(mUpdateUIThread.getFileSize());   //开始
                    /*downloadprogressDialog.setMax(mUpdateUIThread.getFileSize());
                    downloadprogressDialog.setMessage("正在下载，请耐心等待……");
                    downloadprogressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    downloadprogressDialog.show();
                    downloadprogressDialog.setCancelable(false);
                    downloadprogressDialog.onStart();*/
                    break;
                case FileUtil.updateDownloadMeg:
                    /*if (!mUpdateUIThread.isCompleted())   //下载
                    {
                        //Log.e(TAG, "已下载：" + mUpdateUIThread.getDownloadSize());
                        //progressBar.setProgress(mUpdateUIThread.getDownloadSize());
                        downloadprogressDialog.setProgress(mUpdateUIThread.getDownloadSize());
                        //.setText("下载速度：" + mUpdateUIThread.getDownloadSpeed() + "k/秒"*//*       下载百分比" + mUpdateUIThread.getDownloadPercent() + "%"*//*);
                    } else {
                        //info.setText("下载完成");
                    }*/
                    break;
                case FileUtil.endDownloadMeg:
                    /*Toast.makeText(RecoveryActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    downloadprogressDialog.dismiss();*/
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
                    //create_wim.setEnabled(true);

                    //.setEnabled(true);
                    /*cancel_download.setEnabled(false);*/
                    break;
                case FileUtil.cancleDownloadMeg:
                    /*Toast.makeText(RecoveryActivity.this, "取消下载", Toast.LENGTH_SHORT).show();
                    exec("rm /storage/emulated/legacy/tsing_recovery/test.wim");
                    //info.setText("下载取消");
                    //progressBar.setProgress(0);
                    //create_wim.setEnabled(true);
                    download.setEnabled(true);
                    cancel_download.setEnabled(false);
                    downloadprogressDialog.dismiss();*/

                    break;
                case FileUtil.timeout:
                    /*Toast.makeText(RecoveryActivity.this, "连接超时，请检查网络或者站点是否正常", Toast.LENGTH_SHORT).show();
                    exec("rm /storage/emulated/legacy/tsing_recovery/test.wim");
                    //.setText("连接超时");
                    //progressBar.setProgress(0);
                    //create_wim.setEnabled(true);
                    download.setEnabled(true);
                    cancel_download.setEnabled(false);*/
                    break;
                case FileUtil.fileNotExist:
                    Toast.makeText(getApplication(), "文件不存在", Toast.LENGTH_LONG).show();
                    checkprogressDialog.dismiss();
                    //create_wim.setEnabled(true);
                    //download.setEnabled(true);
                    break;
                case FileUtil.fileRight:
                    Toast.makeText(getApplication(), "文件SHA1检验正确", Toast.LENGTH_LONG).show();
                    checkprogressDialog.dismiss();

                    //listview.setVisibility(View.GONE);
                    listview.setVisibility(View.GONE);
                    listview_section.setVisibility(View.VISIBLE);
                    system.setVisibility(View.GONE);
                    partition.setVisibility(View.VISIBLE);
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

    public class ListViewAdapter extends BaseAdapter {
        View [] itemViews;

        public ListViewAdapter( String [] itemTexts,
                               int [] itemImageRes){

            for (int i=0; i<itemViews.length; ++i){
                itemViews[i] = makeItemView( itemTexts[i],
                        itemImageRes[i]);
            }
        }

        public int getCount()   {
            return itemViews.length;
        }

        public View getItem(int position)   {
            return itemViews[position];
        }

        public long getItemId(int position) {
            return position;
        }

        private View makeItemView( String strText, int resId) {
            LayoutInflater inflater = (LayoutInflater)MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 使用View的对象itemView与R.layout.item关联
            View itemView = inflater.inflate(R.layout.listview_item, null);

            // 通过findViewById()方法实例R.layout.item内各组件

            TextView text = (TextView)itemView.findViewById(R.id.itemText);
            text.setText(strText);
            ImageView image = (ImageView)itemView.findViewById(R.id.itemImage);
            image.setImageResource(resId);

            return itemView;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                return itemViews[position];
            return convertView;
        }
    }
}

/*
package com.example.junzhen.systemrecovery;

*/
/**
 * Created by junzhen on 2015/10/9.
 *//*



import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

@SuppressLint("NewApi")
public class ConfigTab extends Fragment {
    private Button create;
    private EditText command;
    private EditText sourcefile;
    private EditText wimfile;
    private EditText url;
    private TextView jindu;
    private File file;
    private ProgressBar cmd_progress;
    private Button save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.config_tab, container, false);
        create = (Button) view.findViewById(R.id.create);
        command = (EditText) view.findViewById(R.id.command);
        //sourcefile = (EditText) view.findViewById(R.id.sourcefile);
        wimfile = (EditText) view.findViewById(R.id.wimfile);
        jindu = (TextView) view.findViewById(R.id.jindu);
        cmd_progress = (ProgressBar) view.findViewById(R.id.cmd_progress);
        save = (Button) getActivity().findViewById(R.id.save);
        url = (EditText) view.findViewById(R.id.url);
        View.OnClickListener createlistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmd = command.getText().toString();
                MyTask mt = new MyTask();
                mt.execute(cmd);
            }
        };
        View.OnClickListener savelistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfig(file);
                Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_LONG).show();
            }
        };
        create.setOnClickListener(createlistener);
        save.setOnClickListener(savelistener);
        setConfig();
        //读取或创建配置文件

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

    class MyTask extends AsyncTask<String, String, String> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            //Log.i(TAG, "onPreExecute() called");
            jindu.setText("loading...");
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            //Log.i(TAG, "doInBackground(Params... params) called");
            try {
                if (params != null)

                {

                    Runtime rt = Runtime.getRuntime();
                    Process process = rt.exec("su");//Root权限
                    //Process process = rt.exec("sh");//模拟器测试权限
                    DataOutputStream dos = new DataOutputStream(process.getOutputStream());
                    dos.writeBytes(params[0] + "\n");
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
                        if (inline.contains("%")) {
                            String[] ratio = inline.split("%");
                            String[] temp = ratio[0].split("\\(");

                            publishProgress(temp[temp.length - 1]);

                        }
                    }
                    return "创建成功";
                } else {
                    System.out.println("退出");
                    return "请输入正确的命令";
                }


            } catch (IOException e) {
                e.printStackTrace();

                return "操作异常";
            }

        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(String... progresses) {
            cmd_progress.setProgress(Integer.parseInt(progresses[0]));
            jindu.setText("进度"+progresses[0] + "%");
            //}
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            jindu.setText("命令执行完成");
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            //Log.i(TAG, "onCancelled() called");
            */
/*info.setText("cancelled");
            progressBar.setProgress(0);
            create_wim.setEnabled(true);
            cancel_wim.setEnabled(false);*//*

        }
    }
    private void setConfig() {
        String configname = "/storage/emulated/legacy/tsing_recovery/recovery.config";
        file = new File(configname);
        if (file.exists()) {
            getConfig(file);
        } else
            saveConfig(file);
    }

    private void saveConfig(File file) {
        FileWriter fw;
        BufferedWriter bw;
        try {
            StringBuffer config = new StringBuffer("");
            //config.append(sourcefile.getText().toString() + "\n");
            config.append(wimfile.getText().toString() + "\n");
            config.append(url.getText().toString());
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
    }

    private void getConfig(File file) {
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
            //sourcefile.setText(x[0]);
            wimfile.setText(x[0]);
            url.setText(x[1]);
            br.close();
            fr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

*/

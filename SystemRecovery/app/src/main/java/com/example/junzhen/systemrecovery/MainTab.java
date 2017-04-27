package com.example.junzhen.systemrecovery;

/**
 * Created by junzhen on 2015/10/9.
 */


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainTab extends Fragment {

    private TextView textView;
    private TextView filefoldnum_tv;
    private TextView imageid_tv;
    private TextView filenum_tv;
    private TextView size_tv;

    public StringBuffer imagecount;
    private StringBuffer imageid;
    private StringBuffer filenum;
    private StringBuffer filefoldnum;
    private StringBuffer size;

    private LinearLayout linearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_tab, container, false);
        /*
        textView = (TextView) view.findViewById(R.layout.main_tab);
        //linearLayout = (LinearLayout) view.findViewById(R.id.layout_info);
        imagecount = new StringBuffer();
        imageid = new StringBuffer();
        filenum = new StringBuffer();
        filefoldnum = new StringBuffer();
        size = new StringBuffer();*/
        //getInfo(imagecount, imageid, filenum, filefoldnum, size);
        //int num = Integer.parseInt(imagecount.toString());
        /*int num = 1;
        if(num == 1)
        {
            imageid_tv = (TextView) view.findViewById(R.id.imageid);
            imageid_tv.setText(imageid.toString());
            filenum_tv = (TextView) view.findViewById(R.id.filenum);
            filenum_tv.setText(filenum.toString());
            filefoldnum_tv = (TextView) view.findViewById(R.id.filefoldnum);
            filefoldnum_tv.setText(filefoldnum);
            size_tv = (TextView) view.findViewById(R.id.size);
            size_tv.setText(size.toString());
        }
        else {
            imageid_tv = (TextView) view.findViewById(R.id.imageid);
            imageid_tv.setText(imageid.toString().split("|")[0]);
            filenum_tv = (TextView) view.findViewById(R.id.filenum);
            filenum_tv.setText(filenum.toString().split("|")[0]);
            filefoldnum_tv = (TextView) view.findViewById(R.id.filefoldnum);
            filefoldnum_tv.setText(filefoldnum.toString().split("|")[0]);
            size_tv = (TextView) view.findViewById(R.id.size);
            size_tv.setText(size.toString().split("|")[0]);
        }
        */


        /*
        tableRow = new TableRow(());
        TextView image_id = new TextView(this.getContext());
        image_id.setText("hello");
        TextView file_num = new TextView(this.getContext());
        file_num.setText("hello2");
        TextView filefold_num = new TextView(this.getContext());
        filefold_num.setText("hello3");
        TextView size_num = new TextView(this.getContext());
        size_num.setText("hello4");*//*


        tableRow.addView(image_id);
        tableRow.addView(file_num);
        tableRow.addView(filefold_num);
        tableRow.addView(size_num);

        tableLayout.addView(tableRow);*/
        //int num = Integer.parseInt(imagecount.toString());

        /*if (num > 1) {
            TableRow[] tableRow = new TableRow[num];
            TextView[] image_id = new TextView[num];
            TextView[] file_num = new TextView[num];
            TextView[] filefold_num = new TextView[num];
            TextView[] size_num = new TextView[num];
            String[] imageid_str = imageid.toString().split("|");
            String[] filenum_str = imageid.toString().split("|");
            String[] filefoldnum_str = imageid.toString().split("|");
            String[] size_str = imageid.toString().split("|");
            for (int i = 0; i < num; i++) {
                tableRow[i] = new TableRow(this.getContext());
                image_id[i] = new TextView(this.getContext());
                image_id[i].setText(imageid_str[i]);
                file_num[i] = new TextView(this.getContext());
                file_num[i].setText(filenum_str[i]);
                filefold_num[i] = new TextView(this.getContext());
                filefold_num[i].setText(filefoldnum_str[i]);
                size_num[i] = new TextView(this.getContext());
                size_num[i].setText(size_str[i]);


                tableRow[i].addView(image_id[i]);
                tableRow[i].addView(file_num[i]);
                tableRow[i].addView(filefold_num[i]);
                tableRow[i].addView(size_num[i]);

                tableLayout.addView(tableRow[i]);
            }
        }
        else {
            TableRow tableRow = new TableRow(this.getContext());
            TextView image_id = new TextView(this.getContext());
            image_id.setText(imageid.toString());
            TextView file_num = new TextView(this.getContext());
            file_num.setText(filenum);
            TextView filefold_num = new TextView(this.getContext());
            filefold_num.setText(filefoldnum);
            TextView size_num = new TextView(this.getContext());
            size_num.setText(size);


            tableRow.addView(image_id);
            tableRow.addView(file_num);
            tableRow.addView(filefold_num);
            tableRow.addView(size_num);

            tableLayout.addView(tableRow);
        }
        */
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

    /*
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
    */

    /*
    public void getInfo(StringBuffer imagecount, StringBuffer imageid, StringBuffer filenum, StringBuffer filefoldnum, StringBuffer size) {
        try {
            if (fileIsExists(Environment.getRootDirectory().getAbsolutePath() + File.separator+ "windows.wim")) {
                Runtime rt = Runtime.getRuntime();
                Process process = null;//Root权限

                process = rt.exec("su");

                //Process process = rt.exec("sh");//模拟器测试权限
                DataOutputStream dos = new DataOutputStream(process.getOutputStream());

                dos.writeBytes("wimlib-imagex-32 info /system/windows.wim" + "\n");
                dos.flush();
                dos.writeBytes("exit\n");
                dos.flush();
                InputStream myin = process.getInputStream();
                InputStreamReader is = new InputStreamReader(myin);
                *//*******************
                 buffer单行模式
                 ******************//*
                BufferedReader ibr = new BufferedReader(is);
                String inline;
                StringBuffer sb = new StringBuffer();

                while ((inline = ibr.readLine()) != null) {
                    System.out.println(inline);

                    sb.append(inline + "\n");
                    //调用publishProgress公布进度,最后onProgressUpdate方法将被执行

                }
                String t = sb.toString();
                String[] lines = t.split("\n");
                StringBuffer test = new StringBuffer();
                for (int i = 0; i < lines.length; i++) {
                    if (lines[i].contains("Image Count")) {
                        imagecount.append(lines[i].split(":")[1].toString());
                        test.append(lines[i].split(":")[1].toString());
                    }

                    if (lines[i].contains("Index")) {
                        imageid.append(lines[i].split(":")[1].toString() + "|");
                        test.append(lines[i].split(":")[1].toString());
                    }
                    if (lines[i].contains("File Count")) {
                        filenum.append(lines[i].split(":")[1].toString() + "|");
                        test.append(lines[i].split(":")[1].toString());
                    }
                    if (lines[i].contains("Directory Count")) {
                        filefoldnum.append(lines[i].split(":")[1].toString() + "|");
                        test.append(lines[i].split(":")[1].toString());
                    }
                    if (lines[i].contains("Total Bytes")) {
                        size.append(lines[i].split(":")[1].toString() + "|");
                        test.append(lines[i].split(":")[1].toString());
                    }

                }
                textView.setText(t.toString());
            } else {
                linearLayout.setVisibility(View.INVISIBLE);
                textView.setTextColor(Color.RED);

                textView.setText("没有相关ESD文件，请点击下方的自动下载按钮！" + Environment.getRootDirectory() + File.separator +"test/"+ "windows.wim");
            }

            //textView.setText(sb);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    */
}


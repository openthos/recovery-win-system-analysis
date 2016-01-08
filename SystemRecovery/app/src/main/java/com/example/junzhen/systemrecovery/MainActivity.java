package com.example.junzhen.systemrecovery;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

;


public class MainActivity extends Activity   {

   // private ConfigTab configTab;
    private MainTab mainTab;


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

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initViews();
        //fragmentManager = getFragmentManager();

        begin = (Button) findViewById(R.id.begin);
        //source = (EditText) findViewById(R.id.creat_source);

        OnClickListener beginlistener = new OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this,RecoveryActivity.class);
                startActivity(intent);

            }
        };

        begin.setOnClickListener(beginlistener);


        //mUpdateUIThread = new updateUIThread(handler, url, FileUtil.setMkdir(this) + File.separator, FileUtil.getFileName(url));


        //setTabSelection(0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


}

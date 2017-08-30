package com.sxy.autoinstalldemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotoInstall(View v){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File apkFile = new File(path + "/toutiao.apk");
        new ApkInstaller(this, apkFile).installRun();
    }
}

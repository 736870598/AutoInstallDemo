package com.sxy.autoinstalldemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * 安装
 * Created by sunxiaoyu on 2017/8/29.
 */
public class ApkInstaller {

    private Context context;
    private File apkFile;

    public ApkInstaller(Context context, File apkFile) {
        this.context = context;
        this.apkFile = apkFile;
    }


    public void installRun(){
        new Thread(){
            @Override
            public void run() {
                if (!installByRoot()){
                    //智能安装
                    if (isAccessBillityOn()){
                        //跳转到安装界面
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri apkUri = Uri.fromFile(apkFile);
                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        context.startActivity(intent);
                    }else{
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        context.startActivity(intent);
                    }
                }
            }
        }.start();
    }


    /**
     * 判断当前手机是否有root权限，有的话直接静默安装
     */
    private boolean installByRoot(){
        boolean result = false;
        Process process = null;
        DataOutputStream os = null;
        BufferedReader br = null;
        StringBuffer sb = null;
        String cmd = "pm install -r " + apkFile.getAbsolutePath();

        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());

            os.writeBytes(cmd);
            os.writeBytes("\n");
            os.flush();
            os.writeBytes("exit");
            os.writeBytes("\n");
            process.waitFor();

            br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            sb = new StringBuffer();

            String line;
            while ((line = br.readLine()) != null){
                sb.append(line);
            }
            if (!sb.toString().contains("Failure")){
                result = true;
            }

        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }finally {
            try {
                if (os != null)
                    os.close();
                if (br != null)
                    br.close();
            }catch (IOException e){
                os = null;
                br = null;
                process.destroy();
            }
        }
        return result;
    }

    private boolean isAccessBillityOn(){
        String service = context.getPackageName() + "/" + ApkService.class.getCanonicalName();
        try {
            int i = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);

            if (i == 1){
                String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

                if (settingValue != null){
                    if (settingValue.toUpperCase().contains(service.toUpperCase())) {
                        return true;
                    }
                }
            }

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
}

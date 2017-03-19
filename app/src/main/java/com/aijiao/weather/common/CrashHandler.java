package com.aijiao.weather.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;

/**
 * Created by 25858 on 2017/3/19.
 */

public class CrashHandler implements UncaughtExceptionHandler {

    private static final CrashHandler crashHandler = new CrashHandler();
    private  UncaughtExceptionHandler uncaughtExceptionHandler;
    private Context context;

    /**
     * 防止被重复创建
     */
    private CrashHandler(){}

    public static CrashHandler getCrashHandler(){
        return crashHandler;
    }

    public void init(Context context){
        //获取context
        context =context.getApplicationContext();
        // 获取系统异常处理器
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 把当前实例设置为系统默认异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 这个方法是我们重写的重点，当系统出现未捕获异常时，就会调用这个方法
     *
     * @param t 出现未捕获异常的线程
     * @param e 未捕获的异常
     */
    @Override
    public void uncaughtException(Thread t, Throwable e){
        try {
            saveExceptionToFile(e);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        if (uncaughtExceptionHandler !=null){
            //如果系统有默认异常处理就使用它处理
            uncaughtExceptionHandler.uncaughtException(t,e);
        }else {
            //否则我们自行结束程序
            Process.killProcess(Process.myPid());
        }
    }

    private void saveExceptionToFile(Throwable e)throws IOException
    {
        if (FileUtils.ExistSDCard()){
            long currentTime = System.currentTimeMillis();
            String crashTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
            File file = new File(FileUtils.getAppCrashDir()+"crash" + crashTime + ".txt");
            file.createNewFile();
            try{
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                pw.println(crashTime);
                printPhoneInfo(pw);
                pw.println();
                e.printStackTrace(pw); //输出错误信息
                pw.close();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     * 输出手机信息
     */
    private void printPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App version: ");
        pw.print(pi.versionName);
        pw.print("_");
        pw.println(pi.versionCode);

        //android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);

        //cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
    }

}

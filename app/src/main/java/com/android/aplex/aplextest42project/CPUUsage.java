package com.android.aplex.aplextest42project;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CPUUsage extends AppCompatActivity {

    Button start_threads;
    TextView temperature;
    TextView CPU_usage;

    /**
     * 创建四个线程用于占用cpu资源，也就是说该程序适合用于1-4核的CPU测试
     */
    Thread thread1 = null;
    Thread thread2 = null;
    Thread thread3 = null;
    Thread thread4 = null;

    /**
     * 这个线程用于获取CPU的利用率
     */
    Thread getCPURateThread = null;

    /**
     * 用于保存当前的按键状态
     */
    boolean status = true;

    /**
     * 用于保存合成的CPU利用率的字符串，主要是用于更新显示界面的UI
     */
    String CPURateString = null;

    /**
     * 主要是因为线程不能直接进行UI界面的更新，这里使用了handler来更新界面
     */
    Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpuusage);

        setTitle("CPU Usage");

        start_threads = (Button)(findViewById(R.id.start_threads));
        temperature = (TextView)(findViewById(R.id.temperature));
        CPU_usage = (TextView)(findViewById(R.id.cpu_usage));

        temperature.setText(getTemperature());
        CPU_usage.setText("0%");

        start_threads.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /**
                 * 判断当前的测试状态，并根据当前的测试状态进行对应的操作
                 */
                if (start_threads.getText().equals("Start")) {
                    start_threads.setText("Stop");
                    status = true;

                    /**
                     * 接下来创建4个对应的线程，并启动线程
                     */
                    thread1 = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            int i = 0;
                            while (status) {
                                i++;
                            }
                            thread1 = null;
                        }
                    });
                    thread2 = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            int j = 0;
                            while (status) {
                                j ++;
                            }
                            thread2 = null;
                        }
                    });
                    thread3 = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            int k = 0;
                            while (status) {
                                k ++;
                            }
                            thread3 = null;
                        }
                    });
                    thread4 = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            int l = 0;
                            while (status) {
                                l ++;
                            }
                            thread4 = null;
                        }
                    });

                    thread1.start();
                    thread2.start();
                    thread3.start();
                    thread4.start();
                } else {
                    start_threads.setText("Start");

                    if (thread1 != null && thread2 != null
                            && thread3 != null && thread4 != null) {
                        status = false;
                    }
                }

            }
        });

        /**
         * 因为android的UI界面不能用用线程直接进行更新，当前程序使用Handler进行界面的更新
         */
        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
            if(msg.what == 0)//成功
            {
                CPU_usage.setText(CPURateString);
                temperature.setText(getTemperature());
            }
            }

        };

        /**
         * 获取CPU利用率线程，里面合成了需要显示的字符串
         */
        getCPURateThread = new Thread(new Runnable() {

            @Override
            public void run() {
            int cpuCurrentUsage = 0;
            while (true) {
                cpuCurrentUsage = (int)getProcessCpuRate();
                CPURateString = String.format("%3d  %%",
                        cpuCurrentUsage > 100 ? 100 : cpuCurrentUsage < 0 ? 0 : cpuCurrentUsage);
                try {
                    handler.sendEmptyMessage(0);//UI线程外想更新UI线程
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            }
        });
        getCPURateThread.start();
    }

    String getTemperature() {
        String ret = "";
        try {
            ret =  ShellExecute.execute("cat /sys/class/thermal/thermal_zone0/temp");
            // for android 5.1
            if (ret.length() > 2)
                ret = String.valueOf(Float.valueOf(ret)/1000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret + "  ℃";
    }

    public static float getProcessCpuRate() {

        float totalCpuTime1 = getTotalCpuTime();
        float processCpuTime1 = getAppCpuTime();
        try {
            Thread.sleep(360);
        } catch (Exception e) {
        }

        float totalCpuTime2 = getTotalCpuTime();
        float processCpuTime2 = getAppCpuTime();

        float cpuRate = 100 * (processCpuTime2 - processCpuTime1)
                / (totalCpuTime2 - totalCpuTime1);

        return cpuRate;
    }

    public static long getTotalCpuTime() { // 获取系统总CPU使用时间
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long totalCpu = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        return totalCpu;
    }

    public static long getAppCpuTime() { // 获取应用占用的CPU时间
        String[] cpuInfos = null;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);
        return appCpuTime;
    }

    private String getVersionName()
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String version = "";
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}

package com.android.aplex.wachendorffbringup;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String TAG = "AplexOS";

    TimePicker timepicker;
    Button apply;
    Button start;
    Button clean;
    TextClock currentTime;
    TextView message;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        timepicker = (TimePicker)(findViewById(R.id.timePicker));
        apply = (Button)(findViewById(R.id.apply));
        start = (Button)(findViewById(R.id.start));
        clean = (Button)(findViewById(R.id.clean));
        currentTime = (TextClock)(findViewById(R.id.currentTime));
        message = (TextView)(findViewById(R.id.message));

        SPUtils.setApplication(getApplication());
        int hour = SPUtils.getInt("hour");
        int minute = SPUtils.getInt("minute");

        if (hour == 0 && minute == 0) {
            /**
             * timepicker.setHour(0);
             * timepicker.setMinute(0);
            */
            timepicker.setCurrentHour(0);
            timepicker.setCurrentMinute(0);
        }

        timepicker.setCurrentHour(hour);
        timepicker.setCurrentMinute(minute);
        Log.i(TAG, "hour: " + hour + ", minute: " + minute);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = timepicker.getCurrentHour();
                int minute = timepicker.getCurrentMinute();

                Log.i(TAG, "hour: " + hour + ", minute: " + minute);

                SPUtils.pushInt("hour", hour);
                SPUtils.pushInt("minute", minute);

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start.getText() == "Start") {
                    start.setText("Stop");
                    SPUtils.pushString("workStatus", "Start");

                    Log.i(TAG, "workStatus: Start");

                } else {
                    start.setText("Start");
                    SPUtils.pushString("workStatus", "Stop");

                    Log.i(TAG, "workStatus: Stop");

                }
            }
        });

        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPUtils.pushInt("hour", 0);
                SPUtils.pushInt("minute", 0);
                SPUtils.getString("workStatus", "");
                SPUtils.pushInt("reboot", 0);

                timepicker.setCurrentHour(0);
                timepicker.setCurrentMinute(0);
            }
        });

        String workStatus = SPUtils.getString("workStatus");
        if (workStatus == null || workStatus.length() <= 0) {
            start.setText("Start");
            SPUtils.pushString("workStatus", "Stop");
        } else {
            if (workStatus == "Start")
                start.setText("Stop");
            else
                start.setText("Start");
        }
        Log.i(TAG, "workStatus: " + workStatus);

        Thread backend = new Thread(new Runnable() {
            @Override
            public void run() {

                boolean runApp = false;

                while (true) {

                    Date date = new Date();
                    Log.i(TAG, "data: hour: " + date.getHours() + ",minute: " + date.getMinutes() + ", workStatus: " + SPUtils.getString("workStatus"));
                    Log.i(TAG, "SP: hour: " + SPUtils.getInt("hour") + ",minute: " + SPUtils.getInt("minute") + ", workStatus: " + SPUtils.getString("workStatus"));

                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);

                    if (SPUtils.getString("workStatus").compareTo("Start") == 0) {
                        Log.i(TAG, "Thread workStatus: Start");
                        if ((date.getHours() == SPUtils.getInt("hour")) && (date.getMinutes() == SPUtils.getInt("minute"))) {
                            Log.i(TAG, "check Date is OK.");
                            if (runApp == false) {
                                Log.i(TAG, "just a time for work.");

                                try {
                                    ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
                                    manager.killBackgroundProcesses("com.example.wachendorff");

                                    Thread.sleep(1000);

                                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.example.wachendorff");
                                    if (intent != null)
                                        startActivity(intent);

                                    runApp = true;

                                } catch (Exception ex) {
                                    Log.e(TAG, ex.toString());
                                } finally {
                                    int reboot = SPUtils.getInt("reboot") + 1;
                                    SPUtils.pushInt("reboot",  reboot);
                                }

                            }
                        } else {
                            if (runApp == true)
                                runApp = false;
                        }
                    }

                    try {
                        Thread.sleep(1000 * 30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        backend.start();

        int reboot = SPUtils.getInt("reboot");
        if (reboot == 0) {
            SPUtils.pushInt("reboot", 0);
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int hour = SPUtils.getInt("hour");
            int minute = SPUtils.getInt("minute");
            String workStatus = SPUtils.getString("workStatus");
            int reboot = SPUtils.getInt("reboot");

            if (msg.what == 1) {
                message.setText("hour: " + hour + ", minute: " + minute + ", workStatus: " + workStatus + ", reboot: " + reboot);
            }

            super.handleMessage(msg);
        }
    };

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}

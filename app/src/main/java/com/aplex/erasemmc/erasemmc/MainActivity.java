package com.aplex.erasemmc.erasemmc;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cengalabs.flatui.views.FlatButton;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.showPwdBtn)
    public FlatButton mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.showPwdBtn)
    public void onClick(View v) {
        String ret = eraseMMC();
        Log.e("Erase U-Boot", ret);
        if( ret.toLowerCase().indexOf("permission") > 0 )
            Toast.makeText(MainActivity.this,"Erase U-boot failed!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this,"Erase U-boot successfully!", Toast.LENGTH_SHORT).show();
    }

    private String eraseMMC() {
        String ret = "";
        try {
            if ("6.0.1".compareToIgnoreCase(Build.VERSION.RELEASE) == 0) {
                ret += ShellExecute.execute("echo 0 > /sys/block/mmcblk3boot0/force_ro");
                ret += ShellExecute.execute("dd if=/dev/zero of=/dev/block/mmcblk3boot0 bs=1024 count=10240");
                ret += ShellExecute.execute("dd if=/dev/zero of=/dev/block/mmcblk3 bs=1024 count=10240");
                // ret += ShellExecute.execute("mmc_utils bootpart enable 7 0 /dev/block/mmcblk3");
            } else {
                ret += ShellExecute.execute("su 0 dd if=/dev/zero of=/dev/block/mmcblk3 bs=1024 count=10240");
                ret += ShellExecute.execute("echo 56 > /sys/block/mmcblk3/device/boot_config");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}

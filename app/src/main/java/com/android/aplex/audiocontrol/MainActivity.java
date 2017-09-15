package com.android.aplex.audiocontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button powerOnOff;
    Button sysSelectLow;
    Button sysSelectHigh;
    Button spkMode1Low;
    Button spkMode1High;
    Button GPIOAnt3Low;
    Button GPIOAnt3High;
    TextView message;

    String POWER_ONOFF = "POWER_ONOFF";
    String SYS_SELECT_LOW = "SYS_SELECT_LOW";
    String SYS_SELECT_HIGH = "SYS_SELECT_HIGH";
    String SPK_MODE1_LOW = "SPK_MODE1_LOW";
    String SPK_MODE1_HIGH = "SPK_MODE1_HIGH";
    String GPIO_ANT3_LOW = "GPIO_ANT3_LOW";
    String GPIO_ANT3_HIGH = "GPIO_ANT3_HIGH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Example of a call to a native method

        powerOnOff = (Button) (findViewById(R.id.powerOnOff));
        sysSelectHigh = (Button) (findViewById(R.id.sysSelectHigh));
        sysSelectLow = (Button) (findViewById(R.id.sysSelectLow));
        message = (TextView) (findViewById(R.id.message));
        spkMode1Low = (Button) (findViewById(R.id.spkMode1Low));
        spkMode1High = (Button) (findViewById(R.id.spkMode1High));
        GPIOAnt3Low = (Button) (findViewById(R.id.gpioAnt3Low));
        GPIOAnt3High = (Button) (findViewById(R.id.gpioAnt3High));

        powerOnOff.setOnClickListener(this);
        sysSelectHigh.setOnClickListener(this);
        sysSelectLow.setOnClickListener(this);
        spkMode1Low.setOnClickListener(this);
        spkMode1High.setOnClickListener(this);
        GPIOAnt3Low.setOnClickListener(this);
        GPIOAnt3High.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        StringBuilder output = new StringBuilder();
        String button_text = String.valueOf(((Button)(view)).getText());
        output.append(button_text + "\n");

        String shell_str = "";
        try {
            if (button_text.compareToIgnoreCase(POWER_ONOFF) == 0) {
                shell_str = ShellExecute.execute("echo 1xxx > /dev/mtp-850");
            } else if (button_text.compareToIgnoreCase(SYS_SELECT_LOW) == 0) {
                shell_str = ShellExecute.execute("echo x0xx > /dev/mtp-850");
            } else if (button_text.compareToIgnoreCase(SYS_SELECT_HIGH) == 0) {
                shell_str = ShellExecute.execute("echo x1xx > /dev/mtp-850");
            } else if (button_text.compareToIgnoreCase(SPK_MODE1_LOW) == 0) {
                shell_str = ShellExecute.execute("echo xx0x > /dev/mtp-850");
            } else if (button_text.compareToIgnoreCase(SPK_MODE1_HIGH) == 0) {
                shell_str = ShellExecute.execute("echo xx1x > /dev/mtp-850");
            } else if (button_text.compareToIgnoreCase(GPIO_ANT3_LOW) == 0) {
                shell_str = ShellExecute.execute("echo xxx0 > /dev/mtp-850");
            } else if (button_text.compareToIgnoreCase(GPIO_ANT3_HIGH) == 0) {
                shell_str = ShellExecute.execute("echo xxx1 > /dev/mtp-850");
            }
        } catch (IOException e) {
            e.printStackTrace();
            output.append(e.toString());
        }

        if (shell_str.contains("Permission denied")) {
            output.append(" set failed:\n ");
        } else {
            output.append(" set success:\n");
        }

        message.setText(output + shell_str + "\n\n");
    }
}

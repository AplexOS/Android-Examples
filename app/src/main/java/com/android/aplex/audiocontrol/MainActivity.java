package com.android.aplex.audiocontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button powerOnOff;
    Button sysSelectLow;
    Button sysSelectHigh;
    TextView message;

    String POWER_ONOFF = "POWER_ONOFF";
    String SYS_SELECT_LOW = "SYS_SELECT_LOW";
    String SYS_SELECT_HIGH = "SYS_SELECT_HIGH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Example of a call to a native method

        powerOnOff = (Button) (findViewById(R.id.powerOnOff));
        sysSelectHigh = (Button) (findViewById(R.id.sysSelectHigh));
        sysSelectLow = (Button) (findViewById(R.id.sysSelectLow));
        message = (TextView) (findViewById(R.id.message));

        powerOnOff.setOnClickListener(this);
        sysSelectHigh.setOnClickListener(this);
        sysSelectLow.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        StringBuilder output = new StringBuilder();
        String button_text = String.valueOf(((Button)(view)).getText());
        output.append(button_text + "\n");

        String shell_str = "";
        try {
            if (button_text.compareToIgnoreCase(POWER_ONOFF) == 0) {
                shell_str = ShellExecute.execute("echo 1xx > /dev/mtp-850");
            } else if (button_text.compareToIgnoreCase(SYS_SELECT_HIGH) == 0) {
                shell_str = ShellExecute.execute("echo x1x > /dev/mtp-850");
            } else if (button_text.compareToIgnoreCase(SYS_SELECT_LOW) == 0) {
                shell_str = ShellExecute.execute("echo x0x > /dev/mtp-850");
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

        message.append(output + shell_str + "\n\n");
    }
}

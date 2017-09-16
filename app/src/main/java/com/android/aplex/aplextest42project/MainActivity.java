package com.android.aplex.aplextest42project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button date_and_time;
    Button CPU_usage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Aplex");

        date_and_time = (Button)(findViewById(R.id.date_and_time));
        CPU_usage = (Button)(findViewById(R.id.CPU_usage));

        date_and_time.setOnClickListener(this);
        CPU_usage.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        String button_string = ((Button)(view)).getText() + "";

        if (button_string.compareToIgnoreCase("Data And Time") == 0) {
            Log.e("AplexTest42Project", "Data And Time");

            startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
        } else if (button_string.compareToIgnoreCase("CPU Usage") == 0) {
            Log.e("AplexTest42Project", "CPU Usage");

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, CPUUsage.class);
            startActivity(intent);
        }
    }
}

package com.android.aplex.aplextest42project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TimeAndDate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_and_date);

        setTitle("Time & Date");

        startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
    }
}

package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.vke.shop4stech.R;

public class AboutUsActivity extends AppCompatActivity {

    public static void start(Activity activty) {
        Intent starter = new Intent(activty, AboutUsActivity.class);
        activty.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

}

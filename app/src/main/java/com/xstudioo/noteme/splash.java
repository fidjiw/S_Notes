package com.xstudioo.noteme;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import gr.net.maroulis.library.EasySplashScreen;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(splash.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(2000)
                .withBackgroundColor(Color.parseColor("#ffffff"))
                .withHeaderText("")
                .withFooterText("")
                .withBeforeLogoText("")
                .withAfterLogoText("")
                .withLogo(R.drawable.logo);
                config.getHeaderTextView().setTextColor(Color.BLACK);


        View screen = config.create();
        setContentView(screen);
    }
}
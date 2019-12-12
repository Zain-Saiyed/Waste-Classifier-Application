package com.zain.deeplearning.recycl_ifier;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.mipmap.app_logo_front_foreground);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_splash_screen);

        Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,Login.class));
                finish();
            }
        },3000);
    }
}

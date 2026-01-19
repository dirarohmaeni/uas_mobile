package com.example.sehatin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends BaseActivity {
    private static final long SPLASH_DELAY_MS = 1200; // 1.2s, ubah sesuai keinginan

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // pakai layout splashmu

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent i = new Intent();
            i.setClassName(getPackageName(), "com.example.sehatin.WelcomeActivity");
            startActivity(i);
            finish();
        }, SPLASH_DELAY_MS);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

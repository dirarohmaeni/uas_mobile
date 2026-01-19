package com.example.sehatin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_taken_notification); // layout notification
        // tampilkan UI notifikasi card. tutup activity ini saat oke/selesai.
    }
}

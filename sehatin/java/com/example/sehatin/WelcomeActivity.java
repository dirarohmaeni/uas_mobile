package com.example.sehatin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WelcomeActivity extends BaseActivity {

    private static final int REQ_PERM_LOCATION = 1234;

    private FusedLocationProviderClient fusedLocationClient;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private ImageView ivFlagSmall;
    private TextView tvCountry;
    private Button btnRefreshLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ivFlagSmall = findViewById(R.id.iv_flag_small);
        tvCountry = findViewById(R.id.tv_country);
        btnRefreshLoc = findViewById(R.id.btn_refresh_loc);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnRefreshLoc.setOnClickListener(v -> {
            Toast.makeText(this, R.string.detecting_location, Toast.LENGTH_SHORT).show();
            refreshLocation();
        });
    }

    private void refreshLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_PERM_LOCATION
            );
            return;
        }

        CancellationTokenSource cts = new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(
                        com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        cts.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        geocodeAndGo(location.getLatitude(), location.getLongitude());
                    } else {
                        simFallbackAndGo();
                    }
                })
                .addOnFailureListener(e -> simFallbackAndGo());
    }

    private void geocodeAndGo(double lat, double lon) {
        executor.execute(() -> {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(lat, lon, 1);

                if (list != null && !list.isEmpty()) {
                    runOnUiThread(() ->
                            applyLocaleAndDelay(list.get(0).getCountryCode()));
                } else {
                    runOnUiThread(this::simFallbackAndGo);
                }
            } catch (IOException e) {
                runOnUiThread(this::simFallbackAndGo);
            }
        });
    }

    private void simFallbackAndGo() {
        try {
            TelephonyManager tm =
                    (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            if (tm != null) {
                String simC = tm.getSimCountryIso();
                if (simC != null && !simC.isEmpty()) {
                    applyLocaleAndDelay(simC.toUpperCase(Locale.US));
                    return;
                }
            }
        } catch (Exception ignored) {}

        applyLocaleAndDelay("ID");
    }

    // ================= FINAL METHOD =================
    private void applyLocaleAndDelay(String countryCode) {
        if (countryCode == null) countryCode = "ID";

        String lang;
        int flagRes;
        int countryName;

        if ("US".equalsIgnoreCase(countryCode)) {
            lang = "en";
            flagRes = R.drawable.amerika;
            countryName = R.string.country_usa;
        } else {
            lang = "id";
            flagRes = R.drawable.indonesia;
            countryName = R.string.country_indonesia;
        }

        // 1️⃣ TAMPILKAN LANGSUNG
        ivFlagSmall.setImageResource(flagRes);
        tvCountry.setText(countryName);

        // 2️⃣ SIMPAN BAHASA
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(PREF_LANG, lang)
                .apply();

        // 3️⃣ DELAY 5 DETIK → PINDAH
        ivFlagSmall.postDelayed(() -> {
            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 5000); // ⏱️ 5 detik
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERM_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            refreshLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}

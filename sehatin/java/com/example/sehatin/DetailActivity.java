package com.example.sehatin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class DetailActivity extends BaseActivity {

    // ✅ WAJIB ADA supaya AddMedicineActivity tidak error
    public static final String EXTRA_ID = "extra_medicine_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_obat);

        Button btnSimpan = findViewById(R.id.btn_simpan_detail);

        btnSimpan.setOnClickListener(v -> {
            // Kembali ke ScheduleActivity
            Intent intent = new Intent(DetailActivity.this, ScheduleActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}

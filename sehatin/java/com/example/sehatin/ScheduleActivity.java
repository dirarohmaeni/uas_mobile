package com.example.sehatin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ScheduleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // ===== BUTTON TAMBAH =====
        Button btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddMedicineActivity.class));
        });

        // ===== ITEM OBAT =====
        LinearLayout itemParacetamol = findViewById(R.id.item_paracetamol);
        LinearLayout itemVitaminC = findViewById(R.id.item_vitamin_c);
        LinearLayout itemAmoxicillin = findViewById(R.id.item_amoxicillin);

        itemParacetamol.setOnClickListener(v ->
                openDetail("Paracetamol"));

        itemVitaminC.setOnClickListener(v ->
                openDetail("Vitamin C"));

        itemAmoxicillin.setOnClickListener(v ->
                openDetail("Amoxicillin"));
    }

    private void openDetail(String medicineName) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("medicine_name", medicineName);
        startActivity(intent);
    }
}

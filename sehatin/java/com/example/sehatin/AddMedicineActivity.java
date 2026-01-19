package com.example.sehatin;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class AddMedicineActivity extends BaseActivity {

    private EditText etName, etTime, etNote;
    private Button btnSave;

    // Simpan waktu internal dalam format "HH:mm"
    private String internalTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        etName = findViewById(R.id.input_name);
        etTime = findViewById(R.id.input_time);
        etNote = findViewById(R.id.input_note);
        btnSave = findViewById(R.id.btn_save);

        // Jangan biarkan keyboard muncul pada field waktu — kita pakai TimePickerDialog
        etTime.setShowSoftInputOnFocus(false); // API 21+
        etTime.setFocusable(false);
        etTime.setClickable(true);

        // Jika ada nilai lama di EditText (mis. dari restore), coba konversi ke internal/display
        String existingRaw = etTime.getText() == null ? "" : etTime.getText().toString().trim();
        if (!existingRaw.isEmpty()) {
            // Jika format lama "0905" atau "9:05" atau already "HH:mm — Periode", handle
            String conv = convertRawToDisplay(existingRaw);
            if (conv != null) {
                // set display only; juga set internalTime
                etTime.setText(conv);
                // extract HH:mm part
                String[] parts = conv.split(" — ");
                if (parts.length > 0) {
                    internalTime = parts[0].trim();
                }
            }
        }

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // fallback ke sekarang jika internalTime kosong
                int hour, minute;
                if (internalTime != null && !internalTime.isEmpty()) {
                    String[] p = internalTime.split(":");
                    try {
                        hour = Integer.parseInt(p[0]);
                        minute = Integer.parseInt(p[1]);
                    } catch (Exception e) {
                        Calendar now = Calendar.getInstance();
                        hour = now.get(Calendar.HOUR_OF_DAY);
                        minute = now.get(Calendar.MINUTE);
                    }
                } else {
                    Calendar now = Calendar.getInstance();
                    hour = now.get(Calendar.HOUR_OF_DAY);
                    minute = now.get(Calendar.MINUTE);
                }

                TimePickerDialog dialog = new TimePickerDialog(
                        AddMedicineActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                internalTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                String period = getPeriodLabel(hourOfDay);
                                String display = internalTime + " — " + period;
                                etTime.setText(display);
                            }
                        },
                        hour,
                        minute,
                        true // 24-hour view (we show period separately)
                );
                dialog.setTitle("Pilih Jam Pengingat");
                dialog.show();
            }
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            // Ambil waktu yang akan disimpan: internalTime bila ada, atau coba konversi dari etTime text
            String timeToSave = "-";
            if (internalTime != null && !internalTime.isEmpty()) {
                timeToSave = internalTime;
            } else {
                // coba parse isi EditText (bisa "0905" atau "09:05" atau "09:05 — Pagi")
                String raw = etTime.getText() == null ? "" : etTime.getText().toString().trim();
                String parsed = convertRawToInternal(raw);
                if (parsed != null && !parsed.isEmpty()) {
                    timeToSave = parsed;
                }
            }

            String note = etNote.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Isi nama obat dulu", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = System.currentTimeMillis();
            Medicine m = new Medicine(id, name, timeToSave.equals("") ? "-" : timeToSave, note.isEmpty() ? "-" : note);
            MedicineStore.list.add(m);

            // buka detail
            Intent i = new Intent(AddMedicineActivity.this, DetailActivity.class);
            i.putExtra(DetailActivity.EXTRA_ID, id);
            startActivity(i);

            finish();
        });
    }

    /**
     * Mengubah input mentah (contoh "0905", "9:05", "09:05 — Pagi") ke internal "HH:mm".
     * Jika tidak bisa, kembalikan null.
     */
    private String convertRawToInternal(String raw) {
        if (raw == null) return null;
        raw = raw.trim();

        // Jika sudah dalam bentuk "HH:mm — Periode", ambil sebelum " — "
        if (raw.contains("—")) {
            raw = raw.split("—")[0].trim();
        }

        // Jika format "0905" atau "905"
        if (raw.matches("\\d{3,4}")) {
            if (raw.length() == 3) raw = "0" + raw;
            String hh = raw.substring(0,2);
            String mm = raw.substring(2);
            raw = hh + ":" + mm;
        }

        // Jika sudah "H:mm" atau "HH:mm"
        if (raw.matches("\\d{1,2}:\\d{2}")) {
            try {
                String[] p = raw.split(":");
                int hour = Integer.parseInt(p[0]);
                int minute = Integer.parseInt(p[1]);
                if (hour < 0 || hour > 23 || minute < 0 || minute > 59) return null;
                return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    /**
     * Mengubah input mentah ke tampilan "HH:mm — Periode". Jika gagal, kembalikan null.
     */
    private String convertRawToDisplay(String raw) {
        String internal = convertRawToInternal(raw);
        if (internal == null) return null;
        String[] p = internal.split(":");
        int hour = Integer.parseInt(p[0]);
        int minute = Integer.parseInt(p[1]);
        String period = getPeriodLabel(hour);
        return String.format(Locale.getDefault(), "%02d:%02d — %s", hour, minute, period);
    }

    /**
     * Tentukan label periode berdasarkan hourOfDay (0..23)
     */
    private String getPeriodLabel(int hourOfDay) {
        if (hourOfDay >= 4 && hourOfDay < 10) {
            return "Pagi";
        } else if (hourOfDay >= 10 && hourOfDay < 15) {
            return "Siang";
        } else if (hourOfDay >= 15 && hourOfDay < 18) {
            return "Sore";
        } else {
            return "Malam";
        }
    }
}

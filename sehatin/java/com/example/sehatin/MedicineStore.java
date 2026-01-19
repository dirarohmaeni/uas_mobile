package com.example.sehatin;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * MedicineStore sederhana: menyimpan list ke SharedPreferences (JSON)
 */
public class MedicineStore {
    public static ArrayList<Medicine> list = new ArrayList<>();

    private static final String PREFS_NAME = "med_store_prefs";
    private static final String KEY_LIST = "med_list_json";

    // === PUBLIC save yang diperlukan oleh ScheduleActivity ===
    public static void save(Context ctx) {
        if (ctx == null) return;
        JSONArray arr = new JSONArray();
        try {
            for (Medicine m : list) {
                if (m == null) continue;
                JSONObject o = new JSONObject();
                o.put("id", m.id);
                o.put("name", m.name != null ? m.name : "");
                o.put("time", m.time != null ? m.time : "-");
                o.put("note", m.note != null ? m.note : "");
                arr.put(o);
            }
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_LIST, arr.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // optional: load list dari prefs
    public static void load(Context ctx) {
        list.clear();
        if (ctx == null) return;
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String s = prefs.getString(KEY_LIST, null);
        if (s == null || s.isEmpty()) return;
        try {
            JSONArray arr = new JSONArray(s);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                long id = o.optLong("id", System.currentTimeMillis() + i);
                String name = o.optString("name", "");
                String time = o.optString("time", "-");
                String note = o.optString("note", "");
                list.add(new Medicine(id, name, time, note));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // helper tambah & save
    public static long add(Context ctx, String name, String time, String note) {
        long id = System.currentTimeMillis();
        Medicine m = new Medicine(id, name, time, note);
        list.add(m);
        save(ctx);
        return id;
    }

    // optional clear
    public static void clear(Context ctx) {
        list.clear();
        if (ctx == null) return;
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_LIST).apply();
    }
}

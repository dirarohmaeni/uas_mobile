package com.example.sehatin;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {

    protected static final String PREFS_NAME = "app_prefs";
    protected static final String PREF_LANG = "app_lang";

    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences prefs =
                newBase.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String lang = prefs.getString(
                PREF_LANG,
                Locale.getDefault().getLanguage()
        );

        Context context = LocaleHelper.setLocale(newBase, lang);
        super.attachBaseContext(context);
    }
}

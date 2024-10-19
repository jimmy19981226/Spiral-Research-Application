package com.example.templemaps;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class PrefsActivity extends PreferenceActivity {


    public static final String SPIRAL_EFFECT= "SPIRAL_EFFECT";
    public static final String SHOW_LABEL= "SHOW_LABEL";

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);


        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);

        ListPreference spiral_effect = new ListPreference(this);
        spiral_effect.setTitle(R.string.SpiralEffectTitle);
        spiral_effect.setSummary(R.string.SpiralEffectSummary);
        spiral_effect.setKey(SPIRAL_EFFECT);
        spiral_effect.setEntries(R.array.SpiralEffect);
        spiral_effect.setEntryValues(R.array.SpiralEffect_value);
        spiral_effect.setValue("static");
        screen.addPreference(spiral_effect);

        CheckBoxPreference show_label = new CheckBoxPreference(this);
        show_label.setTitle(R.string.show_label_title);
        show_label.setSummaryOn(R.string.show_label_on);
        show_label.setSummaryOff(R.string.show_label_off);
        show_label.setKey(SHOW_LABEL);
        show_label.setChecked(true);
        screen.addPreference(show_label);

        setPreferenceScreen(screen);

        //Log.d("Prefs ", "preference screen here ");

    }

    public static String getSpiralEffectPref(Context c) {
        String effect = PreferenceManager.
                getDefaultSharedPreferences(c).getString(SPIRAL_EFFECT, "static");
        return effect;
    }

    public static boolean getShowLabelPref(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(SHOW_LABEL, true);
    }
}

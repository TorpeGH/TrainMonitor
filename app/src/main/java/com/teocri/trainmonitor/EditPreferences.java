package com.teocri.trainmonitor;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

// Save preferences on DefaultSharedPreferences
public class EditPreferences extends PreferenceActivity {

    Preference[] p = new Preference[6];

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.train_data);

        for (int i = 0; i < 6; i++){
            String id = "TrainNumber" + (i+1);
            p[i] = findPreference(id);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updatePreferences();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @SuppressWarnings("deprecation")
    private void updatePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("train_data", MODE_PRIVATE);

        for (int i = 0; i < 6; i++){
            int stat = sharedPreferences.getInt("TrainStatus" + (i), -1);
            if (stat != -1){
                p[i].setSummary(DataHolder.context.getText(R.string.mainscreen_train) + sharedPreferences.getString("TrainNumber" + (i), "####"));
            }else{
                p[i].setSummary(R.string.mainscreen_not_monitored);
            }
        }
    }
}
package com.teocri.trainmonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class EditTrainSettingsActivity extends AppCompatActivity {

    TextView    textViewTrain;
    RadioGroup  radioTime;
    RadioButton radioTimeButton;
    int oldId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_train_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int ind = getIntent().getIntExtra("index", 0);

        textViewTrain = (TextView) findViewById(R.id.textViewTrain);
        textViewTrain.setText(DataHolder.context.getText(R.string.mainscreen_train) + DataHolder.getTrainNumber(ind));
        radioTime = (RadioGroup) findViewById(R.id.radio);
    }

    @Override
    protected void onStart() {
        super.onStart();
        int ind = getIntent().getIntExtra("index", 0);
        int actual_pTime = DataHolder.pTime[ind];

        RadioButton radioB;

        switch(actual_pTime){
            case 30000:
                radioB = (RadioButton) findViewById(R.id.radioButton30s);
                radioB.setChecked(true);
                break;
            case 60000:
                radioB = (RadioButton) findViewById(R.id.radioButton1m);
                radioB.setChecked(true);
                break;
            case 120000:
                radioB = (RadioButton) findViewById(R.id.radioButton2m);
                radioB.setChecked(true);
                break;
            case 15000:
            default:
                radioB = (RadioButton) findViewById(R.id.radioButton15s);
                radioB.setChecked(true);
        }
        oldId = radioB.getId();
    }

    public void SetChanges(View view) {
        int selectedId = radioTime.getCheckedRadioButtonId();

        if (selectedId == oldId){
            Toast.makeText(getApplicationContext(), R.string.toast_no_changes, Toast.LENGTH_SHORT).show();
            return;
        }

        int ind = getIntent().getIntExtra("index", 0);
        radioTimeButton = (RadioButton) findViewById(selectedId);
        int pollingValue = Integer.parseInt(radioTimeButton.getHint().toString());

        DataHolder.set_pTime(ind, pollingValue);
        ThreadHive.restartUnit(ind);

        Toast.makeText(getApplicationContext(), R.string.toast_polling_changed, Toast.LENGTH_SHORT).show();
        onOptionsItemSelected(null);
    }

    public void deleteTrain(View view) {
        int ind = getIntent().getIntExtra("index", 0);

        DataHolder.resetDataIndex(ind);

        ThreadHive.killUnit(ind);

        onOptionsItemSelected(null);
    }

    @Override
    public void onBackPressed() { onOptionsItemSelected(null); }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}

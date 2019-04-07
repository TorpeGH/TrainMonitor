package com.teocri.trainmonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    EditText trainNumber;
    RadioGroup radioTime;
    RadioButton radioTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trainNumber = (EditText) findViewById(R.id.editTextGetNumber);
        radioTime = (RadioGroup) findViewById(R.id.radio);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    @Override
    public void onBackPressed() { onOptionsItemSelected(null); }

    public void addTrain(View view) {

        if (trainNumber.getText().length() == 0){
            Toast.makeText(this, R.string.toast_insert_train, Toast.LENGTH_LONG).show();
            return;
        }

        if (isMonitored(String.valueOf(trainNumber.getText()))){
            Toast.makeText(this,
                    DataHolder.context.getText(R.string.mainscreen_train) +
                    String.valueOf(trainNumber.getText()) +
                    " " +
                    getString(R.string.is_already_monitored), Toast.LENGTH_LONG).show();
            return;
        }

        int selectedId = radioTime.getCheckedRadioButtonId();
        radioTimeButton = (RadioButton) findViewById(selectedId);
        int pollingValue = Integer.parseInt(radioTimeButton.getHint().toString());
        int ind = getIntent().getIntExtra("index", 0);

        DataHolder.setTrainNumber(ind, trainNumber.getText().toString());
        DataHolder.set_tStatus(ind, 8);
        DataHolder.set_pStatus(ind, true);
        DataHolder.set_pTime(ind, pollingValue);
        DataHolder.updateMainScreen(ind);

        ThreadHive.executeUnit(ind);

        onOptionsItemSelected(null);
    }

    private boolean isMonitored(String trainNumb) {
        for (int i = 0; i < DataHolder.TRAIN_SLOTS; i++){
            if (DataHolder.getTrainNumber(i).trim().equals(trainNumb.trim()))
                return true;
        }
        return false;
    }
}

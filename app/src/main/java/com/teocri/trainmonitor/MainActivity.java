package com.teocri.trainmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int TRAIN_SLOTS = 6;

    TextView[] trainNumbersTW    = new TextView[TRAIN_SLOTS];   //trainNumbersTextView
    TextView[] trainTimeTW       = new TextView[TRAIN_SLOTS];   //trainTimeTextView
    TextView[] trainDD           = new TextView[TRAIN_SLOTS];   //trainDepartureDestination
    LinearLayout[] linearLayouts = new LinearLayout[TRAIN_SLOTS];
    ProgressBar[] progressBars   = new ProgressBar[TRAIN_SLOTS];
    Button[] buttons             = new Button[TRAIN_SLOTS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        for (int i = 0; i < TRAIN_SLOTS; i++){
            String id = "textViewTN" + (i+1);
            int temp = getResources().getIdentifier(id, "id", getPackageName());
            trainNumbersTW[i] = (TextView)findViewById(temp);
        }

        for (int i = 0; i < TRAIN_SLOTS; i++){
            String id = "textViewTT" + (i+1);
            int temp = getResources().getIdentifier(id, "id", getPackageName());
            trainTimeTW[i] = (TextView)findViewById(temp);
        }

        for (int i = 0; i < TRAIN_SLOTS; i++){
            String id = "textViewDD" + (i+1);
            int temp = getResources().getIdentifier(id, "id", getPackageName());
            trainDD[i] = (TextView)findViewById(temp);
        }

        for (int i = 0; i < TRAIN_SLOTS; i++){
            String id = "line" + (i+1);
            int temp = getResources().getIdentifier(id, "id", getPackageName());
            linearLayouts[i] = (LinearLayout) findViewById(temp);
        }

        for (int i = 0; i < TRAIN_SLOTS; i++) {
            String id = "updateT" + (i + 1);
            int temp = getResources().getIdentifier(id, "id", getPackageName());
            buttons[i] = (Button) findViewById(temp);
        }

        for (int i = 0; i < TRAIN_SLOTS; i++) {
            String id = "progressBar" + (i + 1);
            int temp = getResources().getIdentifier(id, "id", getPackageName());
            progressBars[i] = (ProgressBar) findViewById(temp);
        }

        new DataHolder(trainNumbersTW, trainTimeTW, trainDD,  progressBars, linearLayouts, buttons, this);
        new DataReader();
        new ThreadHive(TRAIN_SLOTS);
        onReopening();
    }

    /**********_START_ACTIVITIES_**********/
    public void startEditActivity(View v, int ind) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("index", ind);
        startActivity(intent);
    }

    public void startSettingActivity(View v, int ind) {
        Intent intent = new Intent(MainActivity.this, EditTrainSettingsActivity.class);
        intent.putExtra("index", ind);
        startActivity(intent);
    }

    public void startPreferences(View v) {
        Intent intent = new Intent(MainActivity.this, EditPreferences.class);
        startActivity(intent);
    }

    /**********_MENU_**********/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_preferences:
                startPreferences(getCurrentFocus());
                return true;

            case R.id.item_refresh:
                resetToDefault();
                ThreadHive.DestroyHive();
                DataHolder.resetData();
                DataHolder.updateAllMainScreen();
                Toast.makeText(getApplicationContext(), R.string.toast_reset_done, Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**********_BUTTONS_CLICKED_**********/
    public void pollingStatus(View view) {
        Button b = (Button)view;
        int ind = getIndexLine(view);

        if (DataHolder.pStatus[ind]) {
            if(DataHolder.tStatus[ind]!=-1) {
                DataHolder.set_tStatus(ind, 0);
                ThreadHive.killUnit(ind);
            }
            DataHolder.set_pStatus(ind, false);
            b.setText(R.string.mainscreen_button_stop);

        }else{
            if(DataHolder.tStatus[ind]!=-1) {
                DataHolder.set_tStatus(ind, 8);
                ThreadHive.executeUnit(ind);
            }
            DataHolder.set_pStatus(ind, true);
            b.setText(R.string.mainscreen_button_start);
        }
        DataHolder.updateMainScreen(ind);
    }

    public void editTrain(View view) {
        int ind = getIndexLine(view);
        if (DataHolder.tStatus[ind] == -1)
            startEditActivity(view, ind);
        else
            startSettingActivity(view, ind);
    }

    /**********_ON_EVENTS_**********/
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    protected void onReopening() {
        for (int i = 0; i < ThreadHive.threadUnits.length; i++) {
            if (DataHolder.tStatus[i] != -1 && DataHolder.tStatus[i] != 0) {
                if (ThreadHive.threadUnits[i].getStatus() != AsyncTask.Status.RUNNING)
                    ThreadHive.executeUnit(i);
            }
        }
    }

    /**********_UTILITIES_**********/
    private int getIndexLine(View view) {
        String tmp = view.getResources().getResourceName(view.getId());
        int ind = Character.getNumericValue(tmp.charAt(tmp.length()-1));
        return --ind;
    }

    private void resetToDefault() {
        SharedPreferences sharedPreferences = getSharedPreferences("train_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
    }
}

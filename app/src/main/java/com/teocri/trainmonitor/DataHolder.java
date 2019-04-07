package com.teocri.trainmonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

public class DataHolder {

    public static final int TRAIN_SLOTS = 6;

    static TextView[]     trainNumberTW = new TextView[TRAIN_SLOTS];
    static TextView[]     trainTimeTW   = new TextView[TRAIN_SLOTS];
    static TextView[]     trainDD       = new TextView[TRAIN_SLOTS];
    static ProgressBar[]  progressBars  = new ProgressBar[TRAIN_SLOTS];
    static LinearLayout[] linearLayouts = new LinearLayout[TRAIN_SLOTS];
    static Button[]       buttons       = new Button[TRAIN_SLOTS];
    static int[]          tStatus       = new int[TRAIN_SLOTS];      //train status
    static Boolean[]      pStatus       = new Boolean[TRAIN_SLOTS];  //polling status
    static int[]          pTime         = new int[TRAIN_SLOTS];      //polling time
    static Context        context;

    public DataHolder(TextView[] tN, TextView[] tT, TextView[] tDD, ProgressBar[] pgBars, LinearLayout[] ll, Button[] btns, Context cntx) {
        trainNumberTW = tN;
        trainTimeTW = tT;
        trainDD = tDD;
        progressBars = pgBars;
        linearLayouts = ll;
        buttons = btns;
        context = cntx;
        loadAllStatus();
        updateAllMainScreen();
        updateAllButtonsText();
    }

    /**********_LOAD_METHODS_**********/
    public static void loadAllStatus() {
        for (int i = 0; i < tStatus.length; i++)
            load_tStatus(i);
        for (int i = 0; i < pStatus.length; i++)
            load_pStatus(i);
        for (int i = 0; i < pTime.length; i++)
            load_pTime(i);
    }

    public static void loadStatusIndex(int i) {
        load_tStatus(i);
        load_pStatus(i);
        load_pTime(i);
    }

    private static void load_tStatus(int i) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE);
        int stat = sharedPreferences.getInt("TrainStatus" + i, -1);
        tStatus[i] = stat;
    }
    private static void load_pStatus(int i) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE);
        boolean stat = sharedPreferences.getBoolean("PollingStatus" + i, true);
        pStatus[i] = stat;
    }
    private static void load_pTime(int i) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE);
        int pT = sharedPreferences.getInt("PollingTime" + i, 60000);
        pTime[i] = pT;
    }

    /**********_STATUS_SETTERS_**********/
    public static void set_tStatus(int i, int value) {
        DataHolder.tStatus[i] = value;
        updateSP_tStatus(i, value);
    }
    private static void updateSP_tStatus(int i, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("TrainStatus" + i, value);
        editor.apply();
    }

    public static void set_pStatus(int i, boolean value) {
        DataHolder.pStatus[i] = value;
        updateSP_pStatus(i, value);
    }
    private static void updateSP_pStatus(int i, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("PollingStatus" + i, value);
        editor.apply();
    }

    public static void set_pTime(int i, int value) {
        DataHolder.pTime[i] = value;
        updateSP_pTime(i, value);
    }
    private static void updateSP_pTime(int i, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("PollingTime" + i, value);
        editor.apply();
    }

    /**********_SET/GET_TRAIN_TO/FROM_SHARED_PREFERENCES_**********/
    public static String getTrainNumber(int ind) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE);
        String sNumber = sharedPreferences.getString("TrainNumber" + ind, "Not monitored");
        return sNumber;
    }

    public static void setTrainNumber(int i, String s) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("train_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TrainNumber" + i , s);
        editor.apply();
    }

    private static String getTrainSituation(int ind) {
        SharedPreferences sharedPreferences = DataHolder.context.getSharedPreferences("train_data", MODE_PRIVATE);
        String sit = sharedPreferences.getString("Situation" + ind, "");
        return sit;
    }

    private static String getTrainJourney(int ind) {
        SharedPreferences sharedPreferences = DataHolder.context.getSharedPreferences("train_data", MODE_PRIVATE);
        String sit = sharedPreferences.getString("Journey" + ind, "");
        return sit;
    }

    /**********_UPDATES_**********/
    public static void updateAllMainScreen(){
        for (int i = 0; i < TRAIN_SLOTS; i++) {
            updateMainScreen(i);
        }
    }

    public static void updateMainScreen(int i){
        loadStatusIndex(i);
        switch (tStatus[i]) {
            case 0:
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_0)));
                trainTimeTW[i]  .setText(R.string.mainscreen_unknown_delay);
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_0)));
                trainDD[i]      .setText(getTrainJourney(i));
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_0)));
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_0_layout)));
                break;
            case 1:
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_1)));
                trainTimeTW[i]  .setText(getTrainSituation(i));
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_1)));
                trainDD[i]      .setText(getTrainJourney(i));
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_1)));
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_1_layout)));
                break;
            case 2:
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_2)));
                trainTimeTW[i]  .setText(getTrainSituation(i));
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_2)));
                trainDD[i]      .setText(getTrainJourney(i));
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_2)));
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_2_layout)));
                break;
            case 3:
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_3)));
                trainTimeTW[i]  .setText(getTrainSituation(i));
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_3)));
                trainDD[i]      .setText(getTrainJourney(i));
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_3)));
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_3_layout)));
                break;
            case 4:
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_4)));
                trainTimeTW[i]  .setText(getTrainSituation(i));
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_4)));
                trainDD[i]      .setText(getTrainJourney(i));
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_4)));
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_4_layout)));
                break;
            case 6:
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_6)));
                trainTimeTW[i]  .setText(getTrainSituation(i));
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_6)));
                trainDD[i]      .setText(getTrainJourney(i));
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_6)));
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_6_layout)));
                break;
            case 8:
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_8)));
                trainTimeTW[i]  .setText(R.string.mainscreen_updating);
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_8)));
                trainDD[i]      .setText(getTrainJourney(i));
                trainDD[i]      .setTextColor(Color.parseColor(context.getString(R.string.case_8)));
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_8_layout)));
                break;
            case 99:
                trainNumberTW[i].setText(context.getText(R.string.mainscreen_train) + getTrainNumber(i));
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_99)));
                trainTimeTW[i]  .setText(getTrainSituation(i));
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_99)));
                trainDD[i]      .setText("");
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_99_layout)));
                break;
            case -1:
            default:
                trainNumberTW[i].setText(R.string.mainscreen_empty_slot);
                trainNumberTW[i].setTextColor(Color.parseColor(context.getString(R.string.case_default)));
                trainTimeTW[i]  .setText(R.string.mainscreen_not_monitored);
                trainTimeTW[i]  .setTextColor(Color.parseColor(context.getString(R.string.case_default)));
                trainDD[i]      .setText(getTrainJourney(i));
                linearLayouts[i].setBackgroundColor(Color.parseColor(context.getString(R.string.case_default_layout)));
        }
        updateButton(i);
    }

    public static void updateButton(int i) {
        if (pStatus[i])
            buttons[i].setText(R.string.mainscreen_button_start);
        else
            buttons[i].setText(R.string.mainscreen_button_stop);
    }

    public static void updateAllButtonsText() {
        for (int i = 0; i < TRAIN_SLOTS; i++){ updateButton(i); }
    }

    /**********_RESETS_**********/
    public static void resetData() {
        resetAll_tStatus();
        resetAll_pStatus();
        resetAllProgressBars();
        updateAllMainScreen();
    }

    public static void resetDataIndex(int i) {
        reset_tStatus(i);
        reset_pStatus(i);
        reset_ProgressBar(i);
        DataReader.writeJourney(i, "");
        updateMainScreen(i);
    }

    private static void resetAll_tStatus() { for (int i = 0; i < tStatus.length; i++) reset_tStatus(i); }
    private static void reset_tStatus(int i) { set_tStatus(i, -1); }

    private static void resetAll_pStatus() { for (int i = 0; i < pStatus.length; i++) reset_tStatus(i); }
    private static void reset_pStatus(int i) { set_pStatus(i, true); }

    private static void resetAllProgressBars() { for (int i = 0; i < TRAIN_SLOTS; i++) reset_ProgressBar(i); }
    private static void reset_ProgressBar(int i) { DataHolder.progressBars[i].setProgress(0); }

}

/***********_STATUS_VALUES_***********/
//-1 not monitored / empty slot
// 0 monitored but polling "stop"
// 1 not travelling
// 2 on time
// 3 less than 10 minutes late
// 4 more than 10 minutes late
// 6 arrived
// 8 updating
// 99 cant define train status

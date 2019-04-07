package com.teocri.trainmonitor;


import android.os.AsyncTask;
import android.widget.Toast;

public class ThreadHive {

    static ThreadUnit[] threadUnits;

    public ThreadHive(int hiveSize) {
        threadUnits = new ThreadUnit[hiveSize];
        buildUnits(hiveSize);
    }

    public static void buildUnits(int hiveSize) { for (int i = 0; i < hiveSize; i++) { createUnit(i); } }

    public static void createUnit(int ind) {
        if(threadUnits[ind] == null)
            threadUnits[ind] = new ThreadUnit(ind);
        else
            Toast.makeText(DataHolder.context, R.string.toast_unit + ind + R.string.toast_in_hive_yet, Toast.LENGTH_SHORT).show();
    }

    public static void executeUnit(int ind) {
        if(threadUnits[ind] != null)
            threadUnits[ind].executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            Toast.makeText(DataHolder.context, R.string.toast_unit + ind + R.string.toast_cannot_be_execute, Toast.LENGTH_SHORT).show();
    }

    public static void restartUnit(int ind) {
        killUnit(ind);
        executeUnit(ind);
    }

    public static void killUnit(int ind) {
        if (threadUnits[ind] != null) {
            threadUnits[ind].cancel(true);
            threadUnits[ind] = null;
            createUnit(ind);
        }else
            Toast.makeText(DataHolder.context, R.string.toast_unit + ind + R.string.toast_dead_yet, Toast.LENGTH_SHORT).show();
    }

    public static void DestroyHive() {
        for (int i = 0; i < threadUnits.length; i++) { killUnit(i); }
    }
}

package com.teocri.trainmonitor;


import android.os.AsyncTask;

import java.io.IOException;


public class ThreadUnit extends AsyncTask<Void, Integer, Void> {

    int ind;
    int j;

    public ThreadUnit(int i) {
        ind = i;
        j = 0;
    }

    @Override
    protected void onPreExecute() {
        DataHolder.progressBars[ind].setProgress(0);
        DataHolder.progressBars[ind].setMax(DataHolder.pTime[ind]);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            DataReader.connect(ind, DataHolder.getTrainNumber(ind));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for ( j = 0; j < DataHolder.pTime[ind] && !isCancelled(); j += 1000) {
            publishProgress(j);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!isCancelled()) {
            publishProgress(j + 1000);
            try {
                DataReader.setJourney(ind);
                DataReader.setSituation(ind);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        DataHolder.progressBars[ind].setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        DataHolder.updateMainScreen(ind);
        ThreadHive.restartUnit(ind);
    }

    @Override
    protected void onCancelled() {
        DataHolder.progressBars[this.ind].setProgress(0);
    }
}
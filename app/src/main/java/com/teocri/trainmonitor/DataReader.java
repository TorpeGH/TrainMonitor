package com.teocri.trainmonitor;


import android.content.SharedPreferences;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

public class DataReader {

    static URL               url;
    static HttpURLConnection hUC;
    static InputStream       inStream;
    static BufferedReader[]  br = new BufferedReader[6];
    static Boolean           trainExistence = true;


    public DataReader() {}

    /**********_SETTERS/GETTERS_**********/
    public static void setJourney(int ind) throws IOException {
        String journey = "";
        trainExistence = true;
        if (readLinesTo(ind, "<!-- ORIGINE -->") == 0) { return; } skipLine(ind);
        if (readCharsTo(ind, '>') == 0) { return; }

        String departure = copyCharsTo(ind, "</h2>");
        if (departure.length() == 0){
            trainExistence = false;
            return;
        }

        if (readLinesTo(ind, "<!-- DESTINAZIONE -->") == 0) { return; } skipLine(ind);
        if (readCharsTo(ind, '>') == 0) { return; }

        String destination = copyCharsTo(ind, "</h2>");
        if (destination.length() == 0){
            trainExistence = false;
            return;
        }

        journey = departure + "\n" + destination;
        writeJourney(ind, journey);
    }

    public static void setSituation(int ind) throws IOException {
        if (readLinesTo(ind, "<!-- SITUAZIONE -->") == 0) {
            br[ind].close();
            return;
        }
        String tmp_sit = "";
        String ret_sit = "";
        tmp_sit = copyLinesTo(ind, "<br>");
        ret_sit = analiseSituation(ind, tmp_sit);
        writeSituation(ind, ret_sit);
        br[ind].close();
    }

    private static String analiseSituation(int ind, String sit) {
        String tmp = sit;
        if (trainExistence == false){
            DataHolder.set_tStatus(ind, 99);
            return String.valueOf(DataHolder.context.getText(R.string.dont_exist));
        }
        if (tmp.contains("arrived")) {
            DataHolder.set_tStatus(ind, 6);
            return String.valueOf(DataHolder.context.getText(R.string.arrived));
        }
        if (tmp.contains("not left")) {
            DataHolder.set_tStatus(ind, 1);
            return String.valueOf(DataHolder.context.getText(R.string.not_travelling));
        }
        if (tmp.contains("on time")) {
            DataHolder.set_tStatus(ind, 2);
            return String.valueOf(DataHolder.context.getText(R.string.on_time));
        }
        if (tmp.contains("delay")){
            int i = Integer.parseInt(tmp.replaceAll("\\D", ""));
            if (i < 10)
                DataHolder.set_tStatus(ind, 3);
            else
                DataHolder.set_tStatus(ind, 4);
            return i + String.valueOf(DataHolder.context.getText(R.string.mintes_delay));
        }
        DataHolder.set_tStatus(ind, 99);
        return String.valueOf(DataHolder.context.getText(R.string.not_found));
    }

    public static void writeSituation(int ind, String sit) {
        SharedPreferences sharedPreferences = DataHolder.context.getSharedPreferences("train_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Situation" + ind , sit);
        editor.apply();
    }

    public static void resetSituation(int ind) {
        SharedPreferences sharedPreferences = DataHolder.context.getSharedPreferences("train_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Situation" + ind).commit();
    }

    public static void writeJourney(int ind, String journey) {
        SharedPreferences sharedPreferences = DataHolder.context.getSharedPreferences("train_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Journey" + ind , journey);
        editor.apply();
    }

    public static void resetJourney(int ind) {
        SharedPreferences sharedPreferences = DataHolder.context.getSharedPreferences("train_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Journey" + ind).commit();
    }

    /**********_CONNECTION_**********/
    public static void connect(int ind, String tNumb) throws IOException {
        url = new URL("http://mobile.viaggiatreno.it/vt_pax_internet/mobile/numero?lang=EN&numeroTreno=" + tNumb);
        hUC = (HttpURLConnection) url.openConnection();
        inStream = new BufferedInputStream(hUC.getInputStream());
        br[ind] = new BufferedReader(new InputStreamReader(inStream));
    }

    /**********_UTILITIES_**********/
    private static void skipLine(int ind) throws IOException { br[ind].readLine(); }

    private static int readLinesTo(int ind, String s) throws IOException {
        String sRead = "";
        while (!sRead.contains(s)) {
            sRead = br[ind].readLine();
            if (sRead == null){
                return 0;
            }
        }return 1;
        //1 = found
        //0 = EOF
    }

    private static int readCharsTo(int ind, Character c) throws IOException {
        Character cRead = '#';
        while (cRead != c) {
            cRead = (char)br[ind].read();
            if (cRead == null){
                return 0;
            }
        }return 1;
        //1 = found
        //0 = EOF
    }

    private static String copyLinesTo(int ind, String s) throws IOException {
        String sRead = "";
        String sup = "";
        while (!sup.contains(s) && !sup.equals(null) ) {
            if (sup.length() > 0)
                sRead += sup;
            sup = br[ind].readLine();
        }
        return sRead;
    }

    private static String copyCharsTo(int ind, String s) throws IOException {
        String cRead = "";
        String sup = "";
        while (!sup.contains(s) && !sup.equals(null) ) {
            if (sup.length() > 0)
                cRead = sup;
            sup += (char)br[ind].read();
        }
        return cRead.substring(0, cRead.length() - (s.length() -1));
    }
}

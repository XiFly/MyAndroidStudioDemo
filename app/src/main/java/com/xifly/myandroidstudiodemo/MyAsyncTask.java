package com.xifly.myandroidstudiodemo;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by XiFly on 2016/7/29/0029.
 */
public class MyAsyncTask extends AsyncTask<Void,Void,Void>{

    private final static String XiFly = "xifly";

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(XiFly,"doInBackground");
        publishProgress();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(XiFly,"onPreExecute");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d(XiFly,"onPostExecute");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Log.d(XiFly,"onProgressUpdate");
    }
}

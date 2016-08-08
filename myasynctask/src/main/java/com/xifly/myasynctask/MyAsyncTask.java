package com.xifly.myasynctask;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by XiFly on 2016/7/29/0029.
 */
public class MyAsyncTask extends AsyncTask<Void,Void,Void>{

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("xifly","doInBackground");
        publishProgress();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("xifly","onPreExecute");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("xifly","onPostExecute");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Log.d("xifly","onProgressUpdate");
    }
}

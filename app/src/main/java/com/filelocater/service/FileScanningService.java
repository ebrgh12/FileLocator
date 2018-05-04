package com.filelocater.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.filelocater.model.FileModel;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 23-04-2018.
 */

public class FileScanningService extends Service {

    Callbacks callbacks = null;
    private final IBinder mBinder = new LocalBinder();
    List<Long> fileSize = new ArrayList<Long>();
    AsyncDoc asyncDoc;

    @Override
    public void onCreate() {
        super.onCreate();
        asyncDoc = (AsyncDoc) new AsyncDoc().execute();
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void stopScanning() {
        asyncDoc.cancel(true);
    }

    public void startScanning() {
        asyncDoc = (AsyncDoc) new AsyncDoc().execute();
    }

    public class AsyncDoc extends AsyncTask<URL, Integer, List<FileModel>> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
            int current = values[0];
            int total = values[1];
            float percentage = 100 * (float)current / (float)total;
            if(callbacks != null){
                callbacks.scanningPercentage(percentage);
            }
        }

        @Override
        protected List<FileModel> doInBackground(URL... strings) {
            Log.d("data","data");
            List<String> fileList = new ArrayList<String>();
            List<FileModel> fileModels = new ArrayList<FileModel>();
            File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Download") ;
            file.mkdir();
            File list[] = file.listFiles();
            for( int i=0; i< list.length; i++)
            {
                fileList.add(list[i].getName() );
                fileSize.add(list[i].length());
                fileModels.add(new FileModel(list[i].getName(), list[i].length()));
                publishProgress(i, list.length);

                //This just to increase the user experience of showing the
                //File scanning percentage.
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return fileModels;
        }

        @Override
        protected void onPostExecute(List<FileModel> fileModels) {
            super.onPostExecute(fileModels);
            if(callbacks != null){
                callbacks.updateClient(fileModels);
                callbacks.scanningPercentage(100.00f);
            }
            stopSelf();
        }
    }

    public class LocalBinder extends Binder {
        public FileScanningService getServiceInstance(){
            return FileScanningService.this;
        }
    }

    public void setInstance(Object object){
        callbacks = (Callbacks) object;
    }

    public interface Callbacks{
        public void updateClient(List<FileModel> data);
        public void scanningPercentage(Float value);
    }

}
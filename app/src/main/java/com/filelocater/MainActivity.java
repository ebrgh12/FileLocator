package com.filelocater;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.filelocater.adapter.FileListAdapter;
import com.filelocater.model.FileModel;
import com.filelocater.model.FrequentModel;
import com.filelocater.service.FileScanningService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends PermissionActivity implements FileScanningService.Callbacks{

    TextView fileScanningPercent, averageFileSize, mostFrequentDoc,
            stopFileScanning, shareResult;
    RecyclerView fileList;
    Float percent;

    Context context;
    FileScanningService fileScanningService;
    List<FileModel> documentFiles = new ArrayList<FileModel>();
    List<Long> fileSize = new ArrayList<Long>();
    FileListAdapter fileListAdapter;

    Integer count = 0;
    NotificationCompat.Builder notificationBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        fileScanningPercent = findViewById(R.id.file_scanning_percent);
        averageFileSize = findViewById(R.id.avg_file_size);
        mostFrequentDoc = findViewById(R.id.file_extension);
        stopFileScanning = findViewById(R.id.stop_service);
        shareResult = findViewById(R.id.share_result);

        fileList = findViewById(R.id.file_list);
        fileList.setLayoutManager(new LinearLayoutManager(this));

        requestPermissionAction();

        stopFileScanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count == 0){
                    count = 1;
                    stopFileScanning.setText("Start Scanning");
                    fileScanningService.stopScanning();
                }else {
                    count = 0;
                    stopFileScanning.setText("Stop File Scanning");
                    fileScanningService.startScanning();
                }
            }
        });

        shareResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(percent == 100.0){
                    try {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "File Scan statistics");
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("\nAverage File Size\n");
                        stringBuilder.append(averageFileSize.getText().toString());
                        stringBuilder.append("\nMost Frequent File Extension\n");
                        stringBuilder.append(mostFrequentDoc.getText().toString());
                        String finalString = stringBuilder.toString();
                        intent.putExtra(Intent.EXTRA_TEXT, finalString);
                        startActivity(Intent.createChooser(intent, "Select Sharing option"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    }else {
                    commonToast("File Scanning in progress please wait.");
                }
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        if(requestCode == 1){
            startService();
        }
    }

    public void startService(){
        Intent intentNotify = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intentNotify, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setContentTitle("File Scanning")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Scanning Files From External Storage.")
                .setTicker("File Scanning");
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0, builder.build());

        Intent intent = new Intent(this, FileScanningService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopService(){
        stopService(new Intent(this, FileScanningService.class));
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            FileScanningService.LocalBinder localBinder = (FileScanningService.LocalBinder) iBinder;
            fileScanningService = localBinder.getServiceInstance();
            fileScanningService.setInstance(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }

    @Override
    public void updateClient(List<FileModel> data) {
        count = 1;
        stopFileScanning.setText("Start Scanning");

        documentFiles.clear();
        fileSize.clear();
        List<FileModel> temp = new ArrayList<FileModel>();
        temp.addAll(data);
        Integer doc = 0, docx = 0, pdf = 0;
        int i=0;
        while (i<temp.size()){
            if(temp.get(i).getFileName().toLowerCase().contains(".pdf") ||
                    temp.get(i).getFileName().toLowerCase().contains(".docx") ||
                    temp.get(i).getFileName().toLowerCase().contains(".doc")){
                if(temp.get(i).getFileName().toLowerCase().contains(".pdf")){
                    pdf = pdf + 1;
                }else if(temp.get(i).getFileName().toLowerCase().contains(".docx")){
                    docx = docx + 1;
                }else if(temp.get(i).getFileName().toLowerCase().contains(".doc")){
                    doc = doc + 1;
                }
                documentFiles.add(temp.get(i));
            }
            i++;
        }

        List<FrequentModel> freq = new ArrayList<FrequentModel>();
        freq.add(new FrequentModel("doc",doc));
        freq.add(new FrequentModel("docx",docx));
        freq.add(new FrequentModel("pdf", pdf));

        Collections.sort(freq);
        Collections.sort(documentFiles);

        if(documentFiles.size() != 0){
            //call list adapter
            fileListAdapter = new FileListAdapter(MainActivity.this, documentFiles);
            fileList.setAdapter(fileListAdapter);

            findTheAverageFileSize();
            findFrequestExtension(freq);
        }else {
            commonToast("No Files Found");
        }
    }

    private void findFrequestExtension(List<FrequentModel> freq) {
        StringBuilder stringBuilder = new StringBuilder();
        int i=0;
        while (i<freq.size()){
            stringBuilder.append(freq.get(i).getName());
            if(i != (freq.size()-1)){
                stringBuilder.append(", ");
            }else {
                stringBuilder.append(".");
            }
            i++;
        }
        mostFrequentDoc.setText(stringBuilder.toString());
    }

    private void findTheAverageFileSize() {
        int i=0;
        Long fileTotal = 0l;
        while (i<documentFiles.size()){
            fileTotal = fileTotal + documentFiles.get(i).getFileSize();
            i++;
        }

        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileTotal / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;

        if(fileSizeInMB != 0){
            averageFileSize.setText(fileSizeInMB+" MB");
        }else {
            averageFileSize.setText(fileSizeInKB+" MB");
        }
    }

    @Override
    public void scanningPercentage(Float value) {
        percent = value;
        fileScanningPercent.setText("File Scanning in progress "+value+"%");
    }

    public void commonToast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
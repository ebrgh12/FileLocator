package com.filelocater.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filelocater.MainActivity;
import com.filelocater.R;
import com.filelocater.commonClass.CommonAdapter;
import com.filelocater.commonClass.CommonViewHolder;
import com.filelocater.model.FileModel;

import java.util.List;

/**
 * Created by ADMIN on 24-04-2018.
 */

public class FileListAdapter extends CommonAdapter {

    Activity activity;
    List<FileModel> documentFiles;

    public FileListAdapter(Activity activity,
                           List<FileModel> documentFiles) {
        this.activity = activity;
        this.documentFiles = documentFiles;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommonViewHolder(parent, R.layout.file_item) {
            @Override
            public void onItemSelected(int position) {

            }
        };
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        View view = holder.getView();
        TextView fileName = view.findViewById(R.id.file_name);
        TextView fileSize = view.findViewById(R.id.file_size);
        fileName.setText(documentFiles.get(position).getFileName());

        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = documentFiles.get(position).getFileSize() / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;
        if(fileSizeInMB != 0){
            fileSize.setText("File Size : "+fileSizeInMB+" MB");
        }else {
            fileSize.setText("File Size : "+fileSizeInKB+" KB");
        }
    }

    @Override
    public int getItemCount() {
        return documentFiles.size();
    }
}

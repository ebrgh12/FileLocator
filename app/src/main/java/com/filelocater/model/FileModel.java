package com.filelocater.model;

import android.support.annotation.NonNull;

/**
 * Created by ADMIN on 25-04-2018.
 */

public class FileModel implements Comparable<FileModel>{
    String fileName;
    Long fileSize;

    public FileModel(String fileName, Long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public int compareTo(@NonNull FileModel fileModel) {
        return fileModel.getFileSize().compareTo(this.fileSize);
    }
}

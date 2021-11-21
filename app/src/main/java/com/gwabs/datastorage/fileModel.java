package com.gwabs.datastorage;

public class fileModel {
    private final String FileName;
    private final String FileURL;

    public fileModel(String fileName, String fileURL) {
        FileName = fileName;
        FileURL = fileURL;
    }

    public String getFileName() {
        return FileName;
    }

    public String getFileURL() {
        return FileURL;
    }
}

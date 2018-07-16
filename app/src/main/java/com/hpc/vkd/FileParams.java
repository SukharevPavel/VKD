package com.hpc.vkd;

/**
 * Created by hpc on 1/27/18.
 */
class FileParams {
    private String path;
    private String url;
    private FileName fileName;

    FileParams(String url, String path, FileName fileName) {
        this.url = url;
        this.path = path;
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FileName getFileName() {
        return fileName;
    }

    public void setFileName(FileName fileName) {
        this.fileName = fileName;
    }
}

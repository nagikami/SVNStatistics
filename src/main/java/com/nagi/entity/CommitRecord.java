package com.nagi.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class CommitRecord {
    private long revision;
    private List<CommitFile> files;
    private Set<String> filteredFiles;
    private int[] sum;
    private Date date;
    private String message;

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    public List<CommitFile> getFiles() {
        return files;
    }

    public void setFiles(List<CommitFile> files) {
        this.files = files;
    }

    public Set<String> getFilteredFiles() {
        return filteredFiles;
    }

    public void setFilteredFiles(Set<String> filteredFiles) {
        this.filteredFiles = filteredFiles;
    }

    public int[] getSum() {
        return sum;
    }

    public void setSum(int[] sum) {
        this.sum = sum;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
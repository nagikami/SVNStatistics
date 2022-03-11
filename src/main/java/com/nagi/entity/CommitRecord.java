package com.nagi.entity;

import java.util.List;
import java.util.Set;

public class CommitRecord {
    private long revision;
    private List<CommitFile> files;
    private Set<String> filteredFiles;
    private double sum = 0;

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

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

}
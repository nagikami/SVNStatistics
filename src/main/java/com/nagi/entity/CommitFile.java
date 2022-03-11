package com.nagi.entity;

import java.util.Date;

public class CommitFile {
    private String filePath;
    private Character changeType;
    private double codeCount;
    private double factor;
    private Date date;

    public Character getChangeType() {
        return changeType;
    }

    public void setChangeType(Character changeType) {
        this.changeType = changeType;
    }

    public double getCodeCount() {
        return codeCount;
    }

    public void setCodeCount(double codeCount) {
        this.codeCount = codeCount;
    }

    public String getFilePath() {
        return filePath;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

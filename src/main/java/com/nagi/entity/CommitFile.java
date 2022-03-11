package com.nagi.entity;

import java.util.Date;

public class CommitFile {
    private String filePath;
    private Character changeType;
    private int addCount;
    private double factor;

    private int delCount;

    public Character getChangeType() {
        return changeType;
    }

    public void setChangeType(Character changeType) {
        this.changeType = changeType;
    }

    public int getAddCount() {
        return addCount;
    }

    public void setAddCount(int addCount) {
        this.addCount = addCount;
    }

    public int getDelCount() {
        return delCount;
    }

    public void setDelCount(int delCount) {
        this.delCount = delCount;
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
}

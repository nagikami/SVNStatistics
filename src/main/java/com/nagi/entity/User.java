package com.nagi.entity;

import java.util.List;

public class User {
    private String userName;
    private List<CommitRecord> recordList;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<CommitRecord> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<CommitRecord> recordList) {
        this.recordList = recordList;
    }

}

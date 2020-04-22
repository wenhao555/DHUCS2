package com.example.dhucs.model;

import java.io.Serializable;
import java.util.List;

public class Activities implements Serializable {
    // 唯一的id
    private int id;
    // 报名的user
    private List<User> activityUserList;
    private String image;
    private String date;
    private String title;
    // 活动内容
    private String content;
    // 活动管理员
    private User activityAdminUser;
    //建议内容
    private List<String> suggestList;
    // 时间段
    private String timeLong;
    // 地点
    private String place;
    // 工作时长
    private String workLong;

    public String getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(String timeLong) {
        this.timeLong = timeLong;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getWorkLong() {
        return workLong;
    }

    public void setWorkLong(String workLong) {
        this.workLong = workLong;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<User> getActivityUserList() {
        return activityUserList;
    }

    public void setActivityUserList(List<User> activityUserList) {
        this.activityUserList = activityUserList;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getActivityAdminUser() {
        return activityAdminUser;
    }

    public void setActivityAdminUser(User activityAdminUser) {
        this.activityAdminUser = activityAdminUser;
    }

    public List<String> getSuggestList() {
        return suggestList;
    }

    public void setSuggestList(List<String> suggestList) {
        this.suggestList = suggestList;
    }

    public User getSignOffUser() {
        return signOffUser;
    }

    public void setSignOffUser(User signOffUser) {
        this.signOffUser = signOffUser;
    }

    public List<User> getSignUserList() {
        return signUserList;
    }

    public void setSignUserList(List<User> signUserList) {
        this.signUserList = signUserList;
    }

    public List<User> getAccessUserList() {
        return accessUserList;
    }

    public void setAccessUserList(List<User> accessUserList) {
        this.accessUserList = accessUserList;
    }

    // 签退的user
    private User signOffUser;
    // 签到的user
    private List<User> signUserList;
    // 审核通过的user
    private List<User> accessUserList;
}

package com.example.dhucs.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private int id;
    private String account;
    private String password;
    private String name;
    private String sex;
    private String birth;
    private Boolean admin;
    private String image;
    // 兴趣爱好
    private String hobby;
    // 个人强项
    private String personalStrengths;
    // 时间段
    private String timeLong;
    // 工作时长
    private String workLong;

    public String getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(String timeLong) {
        this.timeLong = timeLong;
    }

    public String getWorkLong() {
        return workLong;
    }

    public void setWorkLong(String workLong) {
        this.workLong = workLong;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getPersonalStrengths() {
        return personalStrengths;
    }

    public void setPersonalStrengths(String personalStrengths) {
        this.personalStrengths = personalStrengths;
    }

    public List<Integer> getSignOnActivityList() {
        return signOnActivityList;
    }

    public void setSignOnActivityList(List<Integer> signOnActivityList) {
        this.signOnActivityList = signOnActivityList;
    }

    private String stuNo;
    private Boolean access;
    private Boolean sign;
    // 已签到的活动
    private List<Integer> signOnActivityList;
    // 已报名的活动
    private List<User> signActivityList;
    // 审核通过的活动
    private List<Integer> accessActivityList;
    private Boolean activityAdmin;

    public Boolean getSign() {
        return sign;
    }

    public void setSign(Boolean sign) {
        this.sign = sign;
    }

    public Boolean getAccess() {
        return access;
    }

    public void setAccess(Boolean access) {
        this.access = access;
    }

    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }


    public Boolean getActivityAdmin() {
        return activityAdmin;
    }

    public void setActivityAdmin(Boolean activityAdmin) {
        this.activityAdmin = activityAdmin;
    }

    // 审核通过的活动

    public List<Integer> getAccessActivityList() {
        return accessActivityList;
    }

    public void setAccessActivityList(List<Integer> accessActivityList) {
        this.accessActivityList = accessActivityList;
    }

    // 已报名的活动

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public List<User> getSignActivityList() {
        return signActivityList;
    }

    public void setSignActivityList(List<User> signActivityList) {
        this.signActivityList = signActivityList;
    }

}

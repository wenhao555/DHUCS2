package com.example.dhucs.net;

public class Urls
{
    public static String defaultUrl = "http://192.168.124.82:8000";
    public static String login = defaultUrl + "/login";//登陆
    public static String adminLogin = defaultUrl + "/adminLogin";//管理员登陆
    public static String createUser = defaultUrl + "/createUser";//注册用户
    public static String setUserInfo = defaultUrl + "/setUserInfo";//编辑用户
    public static String modifyPass = defaultUrl + "/modifyPass";//修改密码
    public static String removeUser = defaultUrl + "/removeUser";//修改密码
    public static String getUserInfo = defaultUrl + "/getUserInfo";//用户查看自己信息


    public static String setActivity = defaultUrl + "/setActivity";//管理员发布活动
    public static String removeActivity = defaultUrl + "/removeActivity";//删除活动
    public static String modifyActivity = defaultUrl + "/modifyActivity";//更新活动
    public static String getAllActivity = defaultUrl + "/getAllActivity";//查看所有活动


    public static String getAllHappyContent = defaultUrl + "/getAllHappyContent";//查看所有风采
    public static String modifyHappyContent = defaultUrl + "/modifyHappyContent";//修改风采
    public static String addHappyContent = defaultUrl + "/addHappyContent";//修改风采
    public static String removeHappyContent = defaultUrl + "/removeHappyContent";//修改风采

    public static String getAllUserInfo = defaultUrl + "/getAllUserInfo";//得到所有用户
    public static String getAllActivityForUser = defaultUrl + "/getAllActivityForUser";//用户得到已报名活动
    public static String getUserListForActivity = defaultUrl + "/getUserListForActivity";//管理员查看报名活动
    public static String setActivityAdminUser = defaultUrl + "/setActivityAdminUser";//管理员设置管理员
    public static String accessUserForActivity = defaultUrl + "/accessUserForActivity";//管理员设置管理员
    public static String getAllSuggest = defaultUrl + "/getAllSuggest";//管理员查看用户反馈
    public static String removeActivityAdminUser = defaultUrl + "/removeActivityAdminUser";//管理员查看用户反馈


    public static String signActivity = defaultUrl + "/signActivity";//用户报名
    public static String signOffActivity = defaultUrl + "/signOffActivity";//用户签退
    public static String signOnActivity = defaultUrl + "/signOnActivity";//用户签退
    public static String addSuggest = defaultUrl + "/addSuggest";//用户反馈


}

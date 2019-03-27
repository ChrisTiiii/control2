package com.example.administrator.control.util;

import android.content.Context;

/**
 * Author: create by ZhongMing
 * Time: 2019/3/26 0026 14:06
 * Description:
 */
public class SharedPreferencesManager {
    private SharedPreferencesUtils helper;
    public static volatile SharedPreferencesManager instance;

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SharedPreferencesManager.class) {
                instance = new SharedPreferencesManager(context);
            }
        }
        return instance;
    }

    public SharedPreferencesManager(Context context) {
        helper = new SharedPreferencesUtils(context, "setting");
    }

    /**
     * 获得保存在本地服务器端口号
     */
    public String getServiceIp() {
        return helper.getString("service_ip");
    }

    /**
     * 获得保存在本地服务器端口号
     */
    public String getServicePort() {
        return helper.getString("service_port");
    }

    /**
     * 获得保存在本地继电器IP
     */
    public String getJDIp() {
        return helper.getString("jd_ip");
    }

    /**
     * 获得保存在本地继电器端口号
     */
    public String getJDPort() {
        return helper.getString("jd_port");
    }

    /**
     * 获得保存在本地功放IP
     */
    public String getGFIp() {
        return helper.getString("gf_ip");
    }

    /**
     * 获得保存在本地功放端口号
     */
    public String getGFPort() {
        return helper.getString("gf_port");
    }

    /**
     * 获得保存在本地的chanel1的名称
     */
    public String getChanelName(int type) {
        return helper.getString("chanel" + type);
    }


    /**
     * 获得保存在本地的chanel1的名称
     */
    public String getChanel1() {
        return helper.getString("chanel1");
    }

    /**
     * 获得保存在本地的chanel1的名称
     */
    public String getChanel2() {
        return helper.getString("chanel2");
    }

    /**
     * 获得保存在本地的chanel1的名称
     */
    public String getChanel3() {
        return helper.getString("chanel3");
    }

    /**
     * 获得保存在本地的chanel1的名称
     */
    public String getChanel4() {
        return helper.getString("chanel4");
    }

}

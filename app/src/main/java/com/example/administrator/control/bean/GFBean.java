package com.example.administrator.control.bean;

/**
 * Author: create by ZhongMing
 * Time: 2019/3/27 0027 14:11
 * Description:
 */
public class GFBean {
    private int chanel;//通道号
    private int voice;//当前音量
    private int status;//1 为开 0 为关 -1为error


    public GFBean(int chanel, int voice, int status) {
        this.chanel = chanel;
        this.voice = voice;
        this.status = status;
    }

    public int getChanel() {
        return chanel;
    }

    public void setChanel(int chanel) {
        this.chanel = chanel;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }
}

package com.example.administrator.control.bean;

import com.example.administrator.control.util.EncodingConversionTools;

/**
 * Author: create by ZhongMing
 * Time: 2019/3/28 0028 16:17
 * Description:
 */
public class ControlOrderBean {
    private byte list[];
    public static volatile ControlOrderBean instance;

    public synchronized static ControlOrderBean getInstance() {
        if (instance == null)
            synchronized (ControlOrderBean.class) {
                if (instance == null)
                    instance = new ControlOrderBean();
            }
        return instance;
    }

    public String open(int order) {
        list = new byte[20];
        list[0] = (byte) 0xaa;
        list[1] = (byte) 0x0f;
        list[2] = (byte) order;
        list[3] = (byte) 0x01;
        list[4] = 0x01;
        for (int i = 5; i < 19; i++) {
            list[i] = 0x01;
        }
        list[19] = (byte) 0xbb;
        return EncodingConversionTools.byte2HexStr(list).replace(" ", "");
    }

    public String close(int order) {
        list = new byte[20];
        list[0] = (byte) 0xaa;
        list[1] = (byte) 0x0f;
        list[2] = (byte) order;
        list[3] = (byte) 0x02;
        list[4] = 0x02;
        for (int i = 5; i < 19; i++) {
            list[i] = 0x01;
        }
        list[19] = (byte) 0xbb;
        return EncodingConversionTools.byte2HexStr(list).replace(" ", "");
    }

}

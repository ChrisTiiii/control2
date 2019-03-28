package com.example.administrator.control.bean;

/**
 * Author: create by ZhongMing
 * Time: 2019/3/26 0026 15:37
 * Description:用于发送数据的具体拆分
 * <p>
 * voice 0A 1
 * 14 2
 * 1E 3
 * 28 4
 * 32 5
 * 3C 6
 * 46 7
 * 50 8
 * 5A 9
 * 64 10
 * chanle 1
 */


public class VoiceOrderBean {
    private final static int SIZE = 8;
    private byte list[];
    public static volatile VoiceOrderBean instance;

    public synchronized static VoiceOrderBean getInstance() {
        if (instance == null)
            synchronized (VoiceOrderBean.class) {
                if (instance == null)
                    instance = new VoiceOrderBean();
            }
        return instance;
    }

    public byte[] up(int chanle, int voice) {
        list = new byte[SIZE];
        list[0] = (byte) 0xA5;
        list[1] = (byte) 0xAB;
        list[2] = (byte) 0x02;
        list[3] = (byte) 0x33;
        list[4] = (byte) chanle;
        list[5] = (byte) chanle;
        list[6] = changeVoice(voice);
        list[7] = 0;
        for (int i = 2; i < 7; i++) {
            list[7] += list[i];
        }
        list[7] = (byte) (list[7] % 0x100);
        return list;
    }

    public byte[] down(int chanle, int voice) {
        list = new byte[SIZE];
        list[0] = (byte) 0xA5;
        list[1] = (byte) 0xAB;
        list[2] = (byte) 0x02;
        list[3] = (byte) 0x34;
        list[4] = (byte) chanle;
        list[5] = (byte) chanle;
        list[6] = changeVoice(voice);
        list[7] = 0;
        for (int i = 2; i < 7; i++) {
            list[7] += list[i];
        }
        list[7] = (byte) (list[7] % 0x100);
        return list;
    }

    public byte[] sendGFStatus(int chanel) {
        list = new byte[SIZE];
        list[0] = (byte) 0xA5;
        list[1] = (byte) 0xAB;
        list[2] = (byte) 0x02;
        list[3] = (byte) 0xB7;
        list[4] = (byte) chanel;
        list[5] = 0;
        list[6] = 0;
        list[7] = 0;
        for (int i = 2; i < 7; i++) {
            list[7] += list[i];
        }
        list[7] = (byte) (list[7] % 0x100);
        return list;
    }

    public byte changeVoice(int voice) {
        switch (voice) {
            case 1:
                return 0x0A;
            case 2:
                return 0x14;
            case 3:
                return 0x1E;
            case 4:
                return 0x28;
            case 5:
                return 0x32;
            case 6:
                return 0x3C;
            case 7:
                return 0x46;
            case 8:
                return 0x50;
            case 9:
                return 0x5A;
            case 10:
                return 0x64;
            case 11:
                return 0x6E;
            case 12:
                return 0x78;
            case 13:
                return (byte) 0x82;
            case 14:
                return (byte) 0x8C;
            case 15:
                return (byte) 0x96;
            case 16:
                return (byte) 0xA0;
            case 17:
                return (byte) 0xAA;
            case 18:
                return (byte) 0xB4;
            case 19:
                return (byte) 0xBE;
            case 20:
                return (byte) 0xC8;
            case 21:
                return (byte) 0xD2;
            case 22:
                return (byte) 0xDC;
            case 23:
                return (byte) 0xE6;
            case 24:

                return (byte) 0xF0;
            case 25:
                return (byte) 0xFA;
            default:
                return 0x00;
        }
    }

}

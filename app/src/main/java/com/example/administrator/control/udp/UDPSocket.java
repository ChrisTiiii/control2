package com.example.administrator.control.udp;

import android.content.Context;
import android.util.Log;

import com.example.administrator.control.util.EncodingConversionTools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by melo on 2017/9/20.
 */

public class UDPSocket {

    private static final String TAG = "UDPSocket";

    // 单个CPU线程池大小
    private static final int POOL_SIZE = 5;

    private static final int BUFFER_LENGTH = 1024;
    private byte[] receiveByte = new byte[BUFFER_LENGTH];

    private String BROADCAST_IP;
    public int CLIENT_PORT;
    private boolean isThreadRunning = false;

    private Context mContext;
    private DatagramSocket client;
    private DatagramPacket receivePacket;

    private long lastReceiveTime = 0;
    private static final long TIME_OUT = 30 * 1000;
    private static final long HEARTBEAT_MESSAGE_DURATION = 10 * 1000;

    private ExecutorService mThreadPool;
    private Thread clientThread;

    private WaitForDataListener waitForDataListener;

    public UDPSocket(Context context, String BROADCAST_IP, int port) {
        this.CLIENT_PORT = port;
        this.BROADCAST_IP = BROADCAST_IP;
        this.mContext = context;
        int cpuNumbers = Runtime.getRuntime().availableProcessors();
        // 根据CPU数目初始化线程池
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * POOL_SIZE);
        // 记录创建对象时的时间
        lastReceiveTime = System.currentTimeMillis();
        Log.i("UDP", BROADCAST_IP + ":" + port);
    }


    public void startUDPSocket() {
        if (client != null) return;
        try {
            // 表明这个 Socket 在设置的端口上监听数据。
            client = new DatagramSocket(CLIENT_PORT);

            if (receivePacket == null) {
                // 创建接受数据的 packet
                receivePacket = new DatagramPacket(receiveByte, BUFFER_LENGTH);
            }

            startSocketThread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启发送数据的线程
     */
    private void startSocketThread() {
        clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "clientThread is running...");
                receiveMessage();
            }
        });
        isThreadRunning = true;
        clientThread.start();
    }

    /**
     * 处理接受到的消息
     */
    private void receiveMessage() {
        while (isThreadRunning) {
            try {
                if (client != null) {
                    client.receive(receivePacket);
                }
                lastReceiveTime = System.currentTimeMillis();
                Log.d(TAG, "receive packet success...");
            } catch (IOException e) {
                Log.e(TAG, "UDP数据包接收失败！线程停止");
                stopUDPSocket();
                e.printStackTrace();
                return;
            }
            if (receivePacket == null || receivePacket.getLength() == 0) {
                Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }
            if (receivePacket.getLength() == 8) {
                System.out.println("receive data length:" + receivePacket.getLength());
                //将数据转换成16进制,不这么转换会有很多多余的0000
                byte[] temp = new byte[receivePacket.getLength()];
                for (int i = 0; i < receivePacket.getLength(); i++) {
                    temp[i] = receivePacket.getData()[i];
                }
                //查看 5 6两个位置的值即音量大小进行数学换算
                if (temp[5] != 00 && temp[6] != 00) {
                    System.out.println("通道号为：" + EncodingConversionTools.byte2HexStr(new byte[]{temp[4]}));
                    String tt = EncodingConversionTools.byte2HexStr(new byte[]{temp[5]}) + EncodingConversionTools.byte2HexStr(new byte[]{temp[6]});
                    System.out.println("16进制值为:" + tt);
                    System.out.println("第六位：" + EncodingConversionTools.byte2HexStr(new byte[]{temp[5]}) + "  第七位：" + EncodingConversionTools.byte2HexStr(new byte[]{temp[6]}));
                    System.out.println("获取到的：" + EncodingConversionTools.parseHex4(String.valueOf(tt)));
                    double finNum = EncodingConversionTools.parseHex4(String.valueOf(tt)) / 100;
                    finNum = finNum >= 0 ? (finNum - 72) * 0.84 : (72 + finNum) * 0.84;
                    waitForDataListener.waitForData(Integer.valueOf(EncodingConversionTools.byte2HexStr(new byte[]{temp[4]})), String.valueOf((int) finNum));
                }
            }
            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (receivePacket != null) {
                receivePacket.setLength(BUFFER_LENGTH);
            }

        }
    }

    public void stopUDPSocket() {
        isThreadRunning = false;
        receivePacket = null;
        if (clientThread != null) {
            clientThread.interrupt();
        }
        if (client != null) {
            client.close();
            client = null;
        }
    }


    /**
     * 发送字符串数据
     *
     * @param message
     */
    public void sendMessage(final String message) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress targetAddress = InetAddress.getByName(BROADCAST_IP);
                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), targetAddress, CLIENT_PORT);
                    client.send(packet);
                    // 数据发送事件
                    Log.d(TAG, "数据发送成功");

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * 发送十六进制字节数据
     *
     * @param
     */
    public void sendBytes(final byte[] bytes) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress targetAddress = InetAddress.getByName(BROADCAST_IP);
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, targetAddress, CLIENT_PORT);
                    client.send(packet);
                    // 数据发送事件
                    Log.d(TAG, "数据发送成功");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public boolean isConnect() {
        System.out.println("UDP is " + isThreadRunning);
        return isThreadRunning;
    }

    public void setWaitForDataListener(WaitForDataListener waitForDataListener) {
        this.waitForDataListener = waitForDataListener;
    }

    public interface WaitForDataListener {
        void waitForData(int chanel, String msg);
    }

}

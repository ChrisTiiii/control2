package com.example.administrator.control.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.control.MainActivity;
import com.example.administrator.control.R;
import com.example.administrator.control.bean.EqupmentBean;
import com.example.administrator.control.bean.GFBean;
import com.example.administrator.control.bean.OrderBean;
import com.example.administrator.control.bean.SendCommand;
import com.example.administrator.control.tcp.ClientThread;
import com.example.administrator.control.tcp.ControlThread;
import com.example.administrator.control.udp.UDPSocket;
import com.example.administrator.control.util.NetWorkUtil;
import com.example.administrator.control.util.SharedPreferencesManager;
import com.example.administrator.control.util.TimeUtil;
import com.example.administrator.control.weight.TempControlView;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * author: ZhongMing
 * DATE: 2019/1/14 0014
 * Description:
 **/
@SuppressLint("ValidFragment")
public class ControlFragment extends Fragment {
    @BindView(R.id.computer_id)
    TextView computerId;
    @BindView(R.id.btn_computer_open)
    Button btnComputerOpen;
    @BindView(R.id.btn_computer_close)
    Button btnComputerClose;
    @BindView(R.id.btn_light_open)
    Button btnLightOpen;
    @BindView(R.id.btn_light_close)
    Button btnLightClose;
    @BindView(R.id.btn_img_last)
    Button btnImgLast;
    @BindView(R.id.btn_img_next)
    Button btnImgNext;
    @BindView(R.id.btn_video_open)
    Button btnVideoOpen;
    @BindView(R.id.btn_video_close)
    Button btnVideoClose;
    @BindView(R.id.btn_video_start)
    Button btnVideoStart;
    @BindView(R.id.btn_video_stop)
    Button btnVideoStop;
    @BindView(R.id.btn_video_up)
    Button btnVideoUp;
    @BindView(R.id.btn_video_down)
    Button btnVideoDown;
    @BindView(R.id.btn_video_speed)
    Button btnVideoSpeed;
    @BindView(R.id.btn_video_backup)
    Button btnVideoBackup;
    Unbinder unbinder;
    @BindView(R.id.btn_video_voiceup)
    Button btnVideoVoiceup;
    @BindView(R.id.btn_video_voicelow)
    Button btnVideoVoicelow;
    @BindView(R.id.btn_video_mute)
    Button btnVideoMute;
    @BindView(R.id.fullscreen)
    Button fullscreen;
    @BindView(R.id.btn_video_normal)
    Button btnVideoNormal;
    @BindView(R.id.btn_img_start)
    Button btnImgStart;
    @BindView(R.id.btn_img_bigger)
    Button btnImgBigger;
    @BindView(R.id.btn_img_smaller)
    Button btnImgSmaller;
    @BindView(R.id.btn_img_full)
    Button btnImgFull;
    @BindView(R.id.btn_img_exit)
    Button btnImgExit;
    @BindView(R.id.ppt_start)
    Button pptStart;
    @BindView(R.id.ppt_last)
    Button pptLast;
    @BindView(R.id.ppt_next)
    Button pptNext;
    @BindView(R.id.ppt_first)
    Button pptFirst;
    @BindView(R.id.ppt_lastest)
    Button pptLastest;
    @BindView(R.id.ppt_exit)
    Button pptExit;
    @BindView(R.id.ll_all)
    LinearLayout llAll;
    @BindView(R.id.option)
    LinearLayout llOption;
    @BindView(R.id.computer_status)
    TextView computerStatus;
    @BindView(R.id.ll_wel)
    LinearLayout llWel;
    @BindView(R.id.et_wel1)
    EditText etWel1;
    @BindView(R.id.et_wel2)
    EditText etWel2;
    @BindView(R.id.et_wel3)
    EditText etWel3;
    @BindView(R.id.btn_wel_clear)
    Button btnWelClear;
    @BindView(R.id.btn_create_wel)
    Button btnCreateWel;
    @BindView(R.id.btn_show_wel)
    Button btnShowWel;
    @BindView(R.id.btn_close_wel)
    Button btnCloseWel;
    @BindView(R.id.temp_control1)
    TempControlView tempControl1;
    @BindView(R.id.temp_control2)
    TempControlView tempControl2;
    @BindView(R.id.ll_voice)
    LinearLayout llVoice;

    private EqupmentBean equpBean;
    private ClientThread thread;
    private ControlThread controlThread;
    private String account;
    private String orders[];
    UDPSocket socket;
    private List<GFBean> gfList;

    public ControlFragment(String account, EqupmentBean equpBean, ClientThread thread, ControlThread controlThread, UDPSocket socket, List<GFBean> gfList) {
        this.account = account;
        this.equpBean = equpBean;
        this.thread = thread;
        this.controlThread = controlThread;
        this.socket = socket;
        this.gfList = gfList;
    }

    public ControlFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.computer_content, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (equpBean != null) {
            if (equpBean.getName().equals("全部设备")) {
                llAll.setVisibility(View.VISIBLE);
                llOption.setVisibility(View.GONE);
                llWel.setVisibility(View.VISIBLE);
                llVoice.setVisibility(View.VISIBLE);
                if (gfList.size() > 0) {
                    if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(getContext()).getChanelName(gfList.get(0).getChanel())))
                        voiceControl(tempControl1, gfList.get(0).getVoice(), SharedPreferencesManager.getInstance(getContext()).getChanelName(gfList.get(0).getChanel()), gfList.get(0).getChanel() - 1);
                    else
                        voiceControl(tempControl1, gfList.get(0).getVoice(), "设备" + gfList.get(0).getChanel(), gfList.get(0).getChanel());
                    if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(getContext()).getChanelName(gfList.get(1).getChanel())))
                        voiceControl(tempControl2, gfList.get(1).getVoice(), SharedPreferencesManager.getInstance(getContext()).getChanelName(gfList.get(1).getChanel()), gfList.get(1).getChanel() - 1);
                    else
                        voiceControl(tempControl1, gfList.get(1).getVoice(), "设备" + gfList.get(1).getChanel(), gfList.get(1).getChanel());
                }
            } else {
                llVoice.setVisibility(View.GONE);
                llAll.setVisibility(View.GONE);
                llOption.setVisibility(View.VISIBLE);
                llWel.setVisibility(View.GONE);
            }
            switch (equpBean.getStatus()) {
                case 1:
                    computerId.setText(equpBean.getName());
                    computerStatus.setTextColor(Color.parseColor("#43fdff"));
                    computerStatus.setText("• 设备在线");
                    break;
                case -1:
                    computerStatus.setTextColor(Color.parseColor("#ff6e6e"));
                    computerId.setText(equpBean.getName());
                    computerStatus.setText("• 设备不在线");
                    break;
                case 0:
                    computerId.setText(equpBean.getName());
                    break;
            }
        }
        return view;
    }

    /**
     * 默认发送给具体设备
     *
     * @param kind
     * @param order
     * @param msg
     */
    public void sendMsg(final int kind, final int order, final String msg) {
        if (NetWorkUtil.isNetworkAvailable(getContext())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SendCommand.Command command = new SendCommand.Command(kind, order);
                    SendCommand sendCommand = new SendCommand(account, equpBean.getName(), TimeUtil.nowTime(), "Command", "client", command, msg);
                    thread.sendData(new Gson().toJson(sendCommand));
                }
            }).start();
        } else
            Toast.makeText(getContext(), "当前网络不可用", Toast.LENGTH_SHORT).show();
    }

    /**
     * @param kind
     * @param order
     * @param sendTo 指定发送给全部设备
     * @param msg
     */
    public void sendMsg(final int kind, final int order, final String sendTo, final String type, final String msg) {
        if (NetWorkUtil.isNetworkAvailable(getContext())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SendCommand.Command command = new SendCommand.Command(kind, order);
                    SendCommand sendCommand = new SendCommand(account, sendTo, TimeUtil.nowTime(), type, "client", command, msg);
                    thread.sendData(new Gson().toJson(sendCommand));
                }
            }).start();
        } else
            Toast.makeText(getContext(), "当前网络不可用", Toast.LENGTH_SHORT).show();
    }

    /**
     * 发送指令给继电器
     * "aa0a0101010101010101010101010101010101bb"
     */
    public void sendOrder(final String[] orders) {
        if (NetWorkUtil.isNetworkAvailable(getContext())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < orders.length; i++) {
                        controlThread.sendData(orders[i]);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } else
            Toast.makeText(getContext(), "当前网络不可用", Toast.LENGTH_SHORT).show();

    }

    @OnClick({R.id.btn_close_wel, R.id.btn_wel_clear, R.id.btn_create_wel, R.id.btn_show_wel, R.id.btn_video_normal, R.id.btn_computer_open, R.id.fullscreen, R.id.btn_computer_close, R.id.btn_light_open, R.id.btn_light_close, R.id.btn_img_last, R.id.btn_img_next, R.id.btn_video_open, R.id.btn_video_close, R.id.btn_video_start, R.id.btn_video_stop, R.id.btn_video_up, R.id.btn_video_down, R.id.btn_video_speed, R.id.btn_video_backup, R.id.btn_video_voiceup, R.id.btn_video_voicelow, R.id.btn_video_mute, R.id.btn_img_start, R.id.btn_img_bigger, R.id.btn_img_smaller, R.id.btn_img_full, R.id.btn_img_exit, R.id.ppt_start, R.id.ppt_last, R.id.ppt_next, R.id.ppt_first, R.id.ppt_lastest, R.id.ppt_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_wel_clear:
                etWel1.setText("");
                etWel2.setText("");
                etWel3.setText("");
                break;
            case R.id.btn_create_wel:
                String wel1 = etWel1.getText().toString();
                String wel2 = etWel2.getText().toString();
                String wel3 = etWel3.getText().toString();
                if (wel1 == null || wel1.equals("")) {
                    wel1 = "热烈欢迎";
                }
                if (wel2 == null || wel2.equals("")) {
                    wel2 = "江苏三棱智慧物联发展股份有限公司";
                }
                if (wel3 == null || wel3.equals("")) {
                    wel3 = "莅临我司参观指导";
                }
                final String temp = wel1 + "," + wel2 + "," + wel3;
                sendMsg(-1, -1, "All", "Wel", temp);
                break;
            case R.id.btn_show_wel:
                sendMsg(5, 1, "All", "Command", "open welcome");
                break;
            case R.id.btn_close_wel:
                sendMsg(5, 2, "All", "Command", "close welcome");
                break;
            case R.id.btn_computer_open:
                orders = new String[]{"aa0a0101010101010101010101010101010101bb"};
                sendOrder(orders);
                break;
            case R.id.btn_computer_close:
                orders = new String[]{"aa0b0202020202020202020202020202020201bb"};
                sendOrder(orders);
                break;
            case R.id.btn_light_open:
                orders = new String[]{"aa0f0001010101010101010101010101010101bb", "aa0f0101010101010101010101010101010101bb", "aa0f0201010101010101010101010101010101bb", "aa0f0301010101010101010101010101010101bb"};
                sendOrder(orders);
                break;
            case R.id.btn_light_close:
                orders = new String[]{"aa0f0002010101010101010101010101010101bb", "aa0f0102010101010101010101010101010101bb", "aa0f0202010101010101010101010101010101bb", "aa0f0302010101010101010101010101010101bb"};
                sendOrder(orders);
                break;
            case R.id.btn_img_last:
                sendMsg(2, 4, "img last");
                break;
            case R.id.btn_img_next:
                sendMsg(2, 5, "img next");
                break;
            case R.id.btn_img_start:
                sendMsg(2, 1, "img start");
                break;
            case R.id.btn_img_bigger:
                sendMsg(2, 2, "img bigger");
                break;
            case R.id.btn_img_smaller:
                sendMsg(2, 3, "img smaller");
                break;
            case R.id.btn_img_full:
                sendMsg(2, 7, "img full");
                break;
            case R.id.btn_img_exit:
                sendMsg(2, 6, "img exit");
                break;
            case R.id.btn_video_open:
                sendMsg(1, 1, "video open");
                break;
            case R.id.btn_video_close:
                sendMsg(1, 3, "video close");
                break;
            case R.id.btn_video_start:
                sendMsg(1, 2, "video start");
                break;
            case R.id.btn_video_stop:
                sendMsg(1, 2, "video stop");
                break;
            case R.id.btn_video_up:
                sendMsg(1, 12, "video previous");
                break;
            case R.id.btn_video_down:
                sendMsg(1, 11, "video next");
                break;
            case R.id.btn_video_speed:
                sendMsg(1, 8, "video speed up");
                break;
            case R.id.btn_video_backup:
                sendMsg(1, 9, "video speed low");
                break;
            case R.id.btn_video_voiceup:
                sendMsg(1, 5, "video up");
                break;
            case R.id.btn_video_voicelow:
                sendMsg(1, 6, "video down");
                break;
            case R.id.btn_video_mute:
                sendMsg(1, 7, "video mute");
                break;
            case R.id.fullscreen:
                sendMsg(1, 4, "video fullscreen");
                break;
            case R.id.btn_video_normal:
                sendMsg(1, 10, "video normal");
                break;
            case R.id.ppt_start:
                sendMsg(3, 1, "ppt start");
                break;
            case R.id.ppt_last:
                sendMsg(3, 2, "ppt last");
                break;
            case R.id.ppt_next:
                sendMsg(3, 3, "ppt next");
                break;
            case R.id.ppt_first:
                sendMsg(3, 4, "ppt first");
                break;
            case R.id.ppt_lastest:
                sendMsg(3, 5, "ppt lastest");
                break;
            case R.id.ppt_exit:
                sendMsg(3, 6, "ppt exit");
                break;

        }
    }

    public void voiceControl(TempControlView tempControl, int temp, String title, final int chanel) {
        tempControl.setTitle(title);
        tempControl.setTemp(temp);
        tempControl.setOnTempChangeListener(new TempControlView.OnTempChangeListener() {
            @Override
            public void change(int temp) {
                System.out.println("temp:" + temp + ",voice[" + chanel + "]:" + gfList.get(chanel).getVoice());
                if (temp > gfList.get(chanel).getVoice()) {
                    calTempOrderUp(temp, chanel);
                } else if (temp < gfList.get(chanel).getVoice()) {
                    calTempOrderDown(temp, chanel);
                }
            }
        });

        tempControl.setOnClickListener(new TempControlView.OnClickListener() {
            @Override
            public void onClick(int temp) {
                System.out.println("temp:" + temp + ",voice[" + chanel + "]:" + gfList.get(chanel).getVoice());
                if (temp > gfList.get(chanel).getVoice()) {
                    calTempOrderUp(temp, chanel);
                } else if (temp < gfList.get(chanel).getVoice()) {
                    calTempOrderDown(temp, chanel);
                }
            }
        });
    }

    /**
     * 计算控制指令发送的次数/数值
     *
     * @param temp
     * @param type
     */
    private void calTempOrderUp(int temp, int type) {
        int aa = temp - gfList.get(type).getVoice();//当前数值差
        int bb = aa / 25;//循环发送次数
        int cc = aa % 25;//剩下指令发送
        for (int i = 0; i < bb; i++) {
            socket.sendBytes(OrderBean.getInstance().up(type, 25));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (cc > 0) {
            socket.sendBytes(OrderBean.getInstance().up(type, cc));
        }
        gfList.get(type).setVoice(temp);
    }

    /**
     * 计算控制指令发送的次数/数值
     *
     * @param temp
     * @param type
     */
    private void calTempOrderDown(int temp, int type) {
        int aa = gfList.get(type).getVoice() - temp;//当前数值差
        int bb = aa / 25;//循环发送次数
        int cc = aa % 25;//剩下指令发送
        for (int i = 0; i < bb; i++) {
            socket.sendBytes(OrderBean.getInstance().down(type, 25));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (cc > 0) {
            socket.sendBytes(OrderBean.getInstance().down(type, cc));
        }
        gfList.get(type).setVoice(temp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public List<GFBean> getGfList() {
        return gfList;
    }

    public void setGfList(List<GFBean> gfList) {
        this.gfList = gfList;
    }
}

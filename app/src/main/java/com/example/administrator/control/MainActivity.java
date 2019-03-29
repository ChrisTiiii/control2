package com.example.administrator.control;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.control.activity.LoginActivity;
import com.example.administrator.control.activity.SettingActivity;
import com.example.administrator.control.adapter.ComupterAdapter;
import com.example.administrator.control.bean.AcceptCommand;
import com.example.administrator.control.bean.EqupmentBean;
import com.example.administrator.control.bean.GFBean;
import com.example.administrator.control.bean.VoiceOrderBean;
import com.example.administrator.control.bean.SendCommand;
import com.example.administrator.control.fragment.ControlFragment;
import com.example.administrator.control.tcp.ClientThread;
import com.example.administrator.control.tcp.ControlThread;
import com.example.administrator.control.udp.UDPSocket;
import com.example.administrator.control.util.MessageEvent;
import com.example.administrator.control.util.NetWorkUtil;
import com.example.administrator.control.util.SharedPreferencesManager;
import com.example.administrator.control.util.SharedPreferencesUtils;
import com.example.administrator.control.util.TimeUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * @Author ZhongMing
 * @Date 2019/1/15 0015 上午 11:22
 * @Description: 应用主界面
 */
public class MainActivity extends AppCompatActivity implements UDPSocket.WaitForDataListener, ControlFragment.UpdateEqupListInterFace {

    @BindView(R.id.computer_list)
    RecyclerView computerList;
    @BindView(R.id.menu)
    LinearLayout menu;
    @BindView(R.id.menu1)
    Button menu1;

    private ClientThread clientThread;
    private ControlThread controlThread;
    private ComupterAdapter comupterAdapter;
    private List<EqupmentBean> list;
    private List<GFBean> gfList;
    private String account;
    private SharedPreferencesUtils helper;
    private int _position;
    private long exitTime = 0;

    private UDPSocket socket;
    final String[] items = new String[]{"重新连接", "有效期", "退出登录", "配置文件"};//创建item
    ControlFragment controlFragment;
    private int voice[] = new int[255];//存放具体设备的声音

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        helper = new SharedPreferencesUtils(this, "setting");
        _position = -1;
        Intent intent = getIntent();
        if (intent != null)
            account = intent.getExtras().getString("account");
        initSocket();
        computerList.setLayoutManager(new LinearLayoutManager(this));
        comupterAdapter = new ComupterAdapter(this, list);
        computerList.setAdapter(comupterAdapter);
        setClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reConnect();
        //创建一个ContentVa对象（自定义的）
        String begin = helper.getString("begin");
        String end = helper.getString("end");
        String def = TimeUtil.getDef(TimeUtil.nowTime(), end);
        String deft = TimeUtil.getDef(begin, TimeUtil.nowTime());
        if (!(Integer.parseInt(def) > 0) || !(Integer.parseInt(deft) >= 0)) {
            new SweetAlertDialog(this)
                    .setTitleText("用户须知")
                    .setContentText("您的软件还没激活授权")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            //创建一个ContentVa对象（自定义的）
                            helper.putValues(new SharedPreferencesUtils.ContentValue("name", ""));
                            helper.putValues(new SharedPreferencesUtils.ContentValue("isVal", false));
                            finish();
                        }
                    })
                    .show();
        }
    }

    /**
     * 用于接收到的服务端的消息，更新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setData(MessageEvent messageEvent) {
        switch (messageEvent.getTAG()) {
            case MyApp.ERROR:
                reConnect();
                break;
            case MyApp.ACCEPPT:
                if (messageEvent.getMessage() != null) {
                    System.out.println("accept:" + messageEvent.getMessage());
                    Gson gson = new Gson();
                    try {
                        Object obj = gson.fromJson(messageEvent.getMessage(), AcceptCommand.class);
                        //获取列表数据
                        if (((AcceptCommand) obj).getType().equals("userlist")) {
                            list.clear();
                            list.add(new EqupmentBean("全部设备", 0));
                            for (String str : (List<String>) ((AcceptCommand) obj).getMsg()) {
                                list.add(new EqupmentBean(str, -1));
                            }
                            gfList.clear();
                            List<String> tempList = Arrays.asList(((String) ((AcceptCommand) obj).getCommand()).split(","));
                            gfList.add(new GFBean(-1, 0, -1));
                            for (String s : tempList) {
                                gfList.add(new GFBean(Integer.valueOf(s), 0, 1));
                            }
                            removeDuplicateWithOrder(list);
                            removeDuplicateWithOrder(gfList);
                            comupterAdapter.update(list);
                            if (list.size() > 0) {
                                initRight();
                            }
                        }
                        //判断设备是否在线
                        if (((AcceptCommand) obj).getType().equals("Connect")) {
                            switch (((AcceptCommand) obj).getStatus()) {
                                case "error4":
                                    list.get(_position).setStatus(-1);
                                    Toast.makeText(this, "设备：" + list.get(_position).getName() + "不在线", Toast.LENGTH_SHORT).show();
                                    comupterAdapter.update(list);
                                    break;
                                case "Success":
                                    list.get(_position).setStatus(1);
                                    System.out.println("通道号：" + ((AcceptCommand) obj).getMsg());
                                    int chanel = Integer.valueOf((String) ((AcceptCommand) obj).getMsg());
                                    String id = ((AcceptCommand) obj).getSendTo();
                                    int tt = whichChanel(chanel);
                                    if (tt != -1) {
                                        gfList.get(tt).setVoice(voice[chanel]);
                                    } else {
                                        GFBean gfBean = new GFBean(chanel, voice[chanel], 1);
                                        gfBean.setId(Integer.valueOf(id));
                                        gfList.add(gfBean);
                                        socket.sendBytes(VoiceOrderBean.getInstance().sendGFStatus(chanel));
                                        list.get(_position).setChanel(chanel);
                                    }
                                    Toast.makeText(this, "设备：" + list.get(_position).getName() + "在线", Toast.LENGTH_SHORT).show();
                                    comupterAdapter.update(list);
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * 判断当前通道是否存在GFList
     *
     * @param chanel
     * @return
     */
    public int whichChanel(int chanel) {
        int isExit = -1;
        for (int i = 0; i < gfList.size(); i++) {
            if (gfList.get(i).getChanel() == chanel) {
                isExit = i;
            }
        }
        return isExit;
    }

    private void setClick() {
        comupterAdapter.setOnItemClickListener(new ComupterAdapter.ItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                comupterAdapter.setPosition(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        _position = position;
                        if (!list.get(position).getName().equals("全部设备")) {
                            SendCommand connect = new SendCommand(account, list.get(position).getName(), TimeUtil.nowTime(), "Connect", "client", null, "client");
                            clientThread.sendData(new Gson().toJson(connect));
                        }
                        try {
                            Thread.sleep(100);
                            controlFragment = new ControlFragment(account, list.get(position), clientThread, controlThread, socket, gfList);
                            getSupportFragmentManager().beginTransaction().replace(R.id.right_fragment, controlFragment).commit();
                            controlFragment.setUpdateEqupListInterFace(MainActivity.this);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }


    /**
     * 初始化sockect
     */
    private void initSocket() {
        if (NetWorkUtil.isNetworkAvailable(this)) {
            initLeft();
            clientThread = new ClientThread(account, SharedPreferencesManager.getInstance(MainActivity.this).getServiceIp(), Integer.parseInt(SharedPreferencesManager.getInstance(MainActivity.this).getServicePort()));
            new Thread(clientThread).start();
            controlThread = new ControlThread(SharedPreferencesManager.getInstance(MainActivity.this).getJDIp(), Integer.parseInt(SharedPreferencesManager.getInstance(MainActivity.this).getJDPort()));
            new Thread(controlThread).start();
            socket = new UDPSocket(this, SharedPreferencesManager.getInstance(MainActivity.this).getGFIp(), Integer.valueOf(SharedPreferencesManager.getInstance(MainActivity.this).getGFPort()));
            socket.startUDPSocket();
            socket.setWaitForDataListener(this);
        } else
            Toast.makeText(this, "当前网络不可用", Toast.LENGTH_SHORT).show();
    }


    public void initLeft() {
        list = new ArrayList<>();
        gfList = new ArrayList<>();
    }


    /**
     * 初始化右侧fragmnet
     */
    private void initRight() {
        if (NetWorkUtil.isNetworkAvailable(this)) {
            controlFragment = new ControlFragment(account, list.get(0), clientThread, controlThread, socket, gfList);
            getSupportFragmentManager().beginTransaction().replace(R.id.right_fragment, controlFragment).commit();
            controlFragment.setUpdateEqupListInterFace(MainActivity.this);
            for (int i = 1; i < gfList.size(); i++) {
                try {
                    socket.sendBytes(VoiceOrderBean.getInstance().sendGFStatus(gfList.get(i).getChanel()));
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else
            Toast.makeText(this, "当前网络不可用", Toast.LENGTH_SHORT).show();
    }


    /**
     * 登出操作
     */
    private void logOut() {
        if (NetWorkUtil.isNetworkAvailable(this)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SendCommand connect = new SendCommand(account, "server", TimeUtil.nowTime(), "Logout", "client", null, "client logout");
                    clientThread.sendData(new Gson().toJson(connect));
                }
            }).start();
        } else
            Toast.makeText(this, "当前网络不可用", Toast.LENGTH_SHORT).show();
    }

    /**
     * 删除ArrayList中重复元素，保持顺序
     */
    public void removeDuplicateWithOrder(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
    }

    /**
     * 重新连接
     */
    public void reConnect() {
        if (!socket.isConnect() || !clientThread.isConnect() || !controlThread.isConnect()) {
            try {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this).setTitleText("即将断开连接")
                        .setContentText("尝试重新连接")
                        .setCancelText("取消")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setConfirmText("确认")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.right_fragment, new ControlFragment()).commit();
                                clientThread.destorySocket();
                                controlThread.destorySocket();
                                socket.stopUDPSocket();
                                list.clear();
                                comupterAdapter.update(list);
                                initSocket();
                                try {
                                    Thread.sleep(100);
                                    sweetAlertDialog.dismiss();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        clientThread.destorySocket();
        controlThread.destorySocket();
        socket.stopUDPSocket();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @OnClick({R.id.menu, R.id.menu1})
    public void onViewClicked() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("工具栏")
                .setItems(items, new DialogInterface.OnClickListener() {//添加列表
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0://重新连接
                                reConnect();
                                break;
                            case 1://有效期
                                //创建一个ContentVa对象（自定义的）
                                String begin = helper.getString("begin");
                                String end = helper.getString("end");
                                String def = TimeUtil.getDef(TimeUtil.nowTime(), end);
                                String deft = TimeUtil.getDef(begin, TimeUtil.nowTime());
                                if (Integer.parseInt(def) > 0 && Integer.parseInt(deft) >= 0)
                                    new SweetAlertDialog(MainActivity.this)
                                            .setTitleText("激活详情")
                                            .setContentText("起始时间:" + begin + "\n截止时间:" + end + "\n剩余有效期:" + def + "天")
                                            .show();
                                else
                                    Toast.makeText(MainActivity.this, "激活码已到期", Toast.LENGTH_SHORT).show();
                                break;
                            case 2://退出登录
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                logOut();
                                //创建一个ContentVa对象（自定义的）
                                helper.putValues(new SharedPreferencesUtils.ContentValue("name", ""));
                                clientThread.destorySocket();
                                controlThread.destorySocket();
                                socket.stopUDPSocket();
                                finish();
                                break;
                            case 3://配置文件
                                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                                break;
                        }
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public void waitForData(int chanel, String msg) {
        if (gfList.size() > 0) {
            gfList.get(whichChanel(chanel)).setVoice(Integer.parseInt(msg));
            controlFragment.setGfList(gfList);
            Log.i("ui", msg);
        }
    }


    @Override
    public void update(int position, int temp) {
        voice[gfList.get(position).getChanel()] = temp;
        gfList.get(position).setVoice(temp);
    }
}
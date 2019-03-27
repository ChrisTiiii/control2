package com.example.administrator.control.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.control.R;
import com.example.administrator.control.util.SharedPreferencesManager;
import com.example.administrator.control.util.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: create by ZhongMing
 * Time: 2019/3/26 0026 10:46
 * Description:
 */
public class SettingActivity extends AppCompatActivity {
    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.et_port)
    EditText etPort;
    @BindView(R.id.et_chanel1)
    EditText etChanel1;
    @BindView(R.id.et_chanel2)
    EditText etChanel2;
    @BindView(R.id.et_chanel3)
    EditText etChanel3;
    @BindView(R.id.et_chanel4)
    EditText etChanel4;
    @BindView(R.id.et_chanel5)
    EditText etChanel5;
    @BindView(R.id.et_chanel6)
    EditText etChanel6;
    @BindView(R.id.et_chanel7)
    EditText etChanel7;
    @BindView(R.id.et_chanel8)
    EditText etChanel8;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.et_jidian_ip)
    EditText etJidianIp;
    @BindView(R.id.et_jidian_port)
    EditText etJidianPort;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.et_gf_ip)
    EditText etGfIp;
    @BindView(R.id.et_gf_port)
    EditText etGfPort;
    private SharedPreferencesUtils helper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        setContentView(R.layout.setting);
        ButterKnife.bind(this);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        helper = new SharedPreferencesUtils(this, "setting");
        if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(SettingActivity.this).getServiceIp())) {
            etIp.setText(SharedPreferencesManager.getInstance(SettingActivity.this).getServiceIp());
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(SettingActivity.this).getServicePort())) {
            etPort.setText(SharedPreferencesManager.getInstance(SettingActivity.this).getServicePort());
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(SettingActivity.this).getJDIp())) {
            etJidianIp.setText(SharedPreferencesManager.getInstance(SettingActivity.this).getJDIp());
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(SettingActivity.this).getJDPort())) {
            etJidianPort.setText(SharedPreferencesManager.getInstance(SettingActivity.this).getJDPort());
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(SettingActivity.this).getGFIp())) {
            etGfIp.setText(SharedPreferencesManager.getInstance(SettingActivity.this).getGFIp());
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(SettingActivity.this).getGFPort())) {
            etGfPort.setText(SharedPreferencesManager.getInstance(SettingActivity.this).getGFPort());
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(SettingActivity.this).getChanel1())) {
            etChanel1.setText(SharedPreferencesManager.getInstance(SettingActivity.this).getChanel1());
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(SettingActivity.this).getChanel2())) {
            etChanel2.setText(SharedPreferencesManager.getInstance(SettingActivity.this).getChanel2());
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(SettingActivity.this).getChanel3())) {
            etChanel3.setText(SharedPreferencesManager.getInstance(SettingActivity.this).getChanel3());
        }
        if (!TextUtils.isEmpty(SharedPreferencesManager.getInstance(SettingActivity.this).getChanel4())) {
            etChanel4.setText(SharedPreferencesManager.getInstance(SettingActivity.this).getChanel4());
        }
    }


    @OnClick({R.id.btn_exit, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_save:
                save();
                finish();
                break;
        }
    }

    private void save() {
        if (!TextUtils.isEmpty(etIp.getText().toString())) {
            helper.putValues(new SharedPreferencesUtils.ContentValue("service_ip", etIp.getText().toString()));
        }
        if (!TextUtils.isEmpty(etPort.getText().toString())) {
            helper.putValues(new SharedPreferencesUtils.ContentValue("service_port", etPort.getText().toString()));
        }
        if (!TextUtils.isEmpty(etJidianIp.getText().toString())) {
            helper.putValues(new SharedPreferencesUtils.ContentValue("jd_ip", etJidianIp.getText().toString()));
        }
        if (!TextUtils.isEmpty(etJidianPort.getText().toString())) {
            helper.putValues(new SharedPreferencesUtils.ContentValue("jd_port", etJidianPort.getText().toString()));
        }
        if (!TextUtils.isEmpty(etGfIp.getText().toString())) {
            helper.putValues(new SharedPreferencesUtils.ContentValue("gf_ip", etGfIp.getText().toString()));
        }
        if (!TextUtils.isEmpty(etGfPort.getText().toString())) {
            helper.putValues(new SharedPreferencesUtils.ContentValue("gf_port", etGfPort.getText().toString()));
        }
        if (!TextUtils.isEmpty(etChanel1.getText().toString())) {
            helper.putValues(new SharedPreferencesUtils.ContentValue("chanel1", etChanel1.getText().toString()));
        }
        if (!TextUtils.isEmpty(etChanel2.getText().toString())) {
            helper.putValues(new SharedPreferencesUtils.ContentValue("chanel2", etChanel2.getText().toString()));
        }
        if (!TextUtils.isEmpty(etChanel3.getText().toString())) {
            helper.putValues(new SharedPreferencesUtils.ContentValue("chanel3", etChanel3.getText().toString()));
        }
        if (!TextUtils.isEmpty(etChanel4.getText().toString())) {
            helper.putValues(new SharedPreferencesUtils.ContentValue("chanel4", etChanel4.getText().toString()));
        }
    }


}

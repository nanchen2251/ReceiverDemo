package com.example.nanchen.maweinaitest;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nanchen.maweinaitest.MyStatusReceiver.StatusCallback;

import static android.content.Intent.ACTION_BATTERY_CHANGED;


public class MainActivity extends ActivityBase implements StatusCallback {

    public static StatusCallback callback;

    private static final String TAG = "MainActivity";
    private Switch mSwitchWifi;
    private SeekBar mSeekBar;
    private WifiManager mWifiManager;
    private AudioManager mAudioManager;
    private TextView mTextView;
    private MyStatusReceiver mMyStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callback = this;

        bindView();

        initManager();

        bindListener();

    }

    private void initManager() {
        // 获取Wifi管理器
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // 把动态获取的信息放在onResume设置  避免按home键后再把APP切换到前台获取不到正常的数据


        // 获取音频管理器
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 绑定监听
     */
    private void bindListener() {
        // 为wifi开关事件设置监听
        mSwitchWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    buttonView.setChecked(false);
                    mWifiManager.setWifiEnabled(false);
                    Toast.makeText(MainActivity.this, "wifi关闭成功！", Toast.LENGTH_SHORT).show();
                } else {
                    buttonView.setChecked(true);
                    mWifiManager.setWifiEnabled(true);
                    Toast.makeText(MainActivity.this, "wifi开启成功！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 再动态监听SeekBar
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 停止滑动
                mSeekBar.setProgress(progress);
                // 三个参数一次是  模式，值，标志位
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // 注册广播，添加三个Action
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BATTERY_CHANGED);
        intentFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mMyStatusReceiver = new MyStatusReceiver();
        registerReceiver(mMyStatusReceiver, intentFilter); // 注册监听广播
    }


    private int max;
    private int current;

    /**
     * 设置wifi开关
     */
    private void setWifiSwitch() {
        if (mWifiManager.isWifiEnabled()) {
            mSwitchWifi.setChecked(true);
        } else {
            mSwitchWifi.setChecked(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 先动态设置wifi
        setWifiSwitch();

        // 再动态设置音频音量  参数为音量模式
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM); // 最大音量
        current = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM); // 当前音量

        mSeekBar.setMax(max);// 设置seekBar
        mSeekBar.setProgress(current);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 一定记得注销广播，否则会造成内存泄漏
        unregisterReceiver(mMyStatusReceiver);
    }

    @SuppressWarnings("ConstantConditions")
    private void bindView() {
        mSwitchWifi = (Switch) findViewById(R.id.wifi);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mTextView = (TextView) findViewById(R.id.text);
        findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里获取当前电量信息

                // 这里就不写了，实际上监听系统广播，它会自动实时获取电量信息
            }
        });
    }


    @Override
    public void onPowerChanged(String status) {
        mTextView.setText(status);
    }

    @Override
    public void onAudioChanged(final int status) {
        mSeekBar.setProgress(status);
    }

    @Override
    public void onWifiChanged(boolean status) {
        mSwitchWifi.setChecked(status);
    }

}

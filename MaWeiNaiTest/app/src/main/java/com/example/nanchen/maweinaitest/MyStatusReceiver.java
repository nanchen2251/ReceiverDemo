package com.example.nanchen.maweinaitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import static android.content.Intent.ACTION_BATTERY_CHANGED;

/**
 * @author nanchen
 * @fileName MaWeiNaiTest
 * @packageName com.example.nanchen.maweinaitest
 * @date 2016/11/05  21:35
 */

public class MyStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "MyStatusReceiver";
    private StatusCallback mStatusCallback = MainActivity.callback;

    public MyStatusReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.e(TAG,action);
        Log.e(TAG,intent.getAction()+" ====   ");


        // 首先判断它是否是电量变化的Broadcast Action
        if (ACTION_BATTERY_CHANGED.equals(action)) {//如果监听到电量改变广播
            // 获取当前电量
            int level = intent.getIntExtra("level", 0);
            // 电量的总刻度
            int scale = intent.getIntExtra("scale", 100);
            // 把它转换为百分比
//            mActivity.mTextView.setText(level * 100 / scale + "%");
            String str = level * 100 / scale + "%";

            Log.e(TAG,level+"");
            Log.e(TAG,scale+"");
            Log.e(TAG,str+"");

            mStatusCallback.onPowerChanged(level * 100 / scale + "%");
        }
        // 监听一下音量
        if ("android.media.VOLUME_CHANGED_ACTION".equals(action)){
//            mActivity.mSeekBar.setProgress(mActivity.mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int progress = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
            Log.e(TAG,progress+"");
            mStatusCallback.onAudioChanged(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
        }
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            mStatusCallback.onWifiChanged(wifiManager.isWifiEnabled());
        }
    }

    /**
     * 一个回调接口
     */
    public interface StatusCallback {
        /**
         * 当电量改变时应该调用的回调接口
         * @param status 当前电量百分比
         */
        void onPowerChanged(String status);

        /**
         * 当音频音量改变时会调用的回调接口
         * @param status 当前音量数值
         */
        void onAudioChanged(int status);

        /**
         * 当wifi改变时会调用的回调接口
         * @param status  wifi的开关  true-开   false - 关
         */
        void onWifiChanged(boolean status);
    }
}

package app.yweather.com.yweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import app.yweather.com.yweather.receiver.AutoUpdateReceiver;
import app.yweather.com.yweather.util.HttpCallbackListener;
import app.yweather.com.yweather.util.HttpUtil;
import app.yweather.com.yweather.util.Utility;

/**
 * 后台运行更新得服务
 */
public class AutoUpdateService extends Service{
    //天气API接口的key
    private static final String KEY = "lgvasrmggjcsa66w";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;        //8小时
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    //更新天气
    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //先使用英文名进行更新，后面修改了数据库之后在修改为使用城市id来查询
        String cityId = prefs.getString("county_id","");
        String address = "https://api.thinkpage.cn/v3/weather/now.json?key=" + KEY + "&language=zh-Hans&unit=c&location=" + cityId;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

}

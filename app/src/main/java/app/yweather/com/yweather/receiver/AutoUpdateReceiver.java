package app.yweather.com.yweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.yweather.com.yweather.service.AutoUpdateService;

/**
 * Created by Administrator on 2016-07-16.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //创建一个Service，然后再次启动它，以达到该服务一直在后台运行的效果
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}

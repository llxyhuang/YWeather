package app.yweather.com.yweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import app.yweather.com.yweather.R;
import app.yweather.com.yweather.util.HttpCallbackListener;
import app.yweather.com.yweather.util.HttpUtil;
import app.yweather.com.yweather.util.LogUtil;
import app.yweather.com.yweather.util.Utility;

/**
 * Created by Administrator on 2016-07-15.
 */
public class WeatherActivity  extends Activity{
    //天气API接口的key
    private static final String KEY = "lgvasrmggjcsa66w";

    private LinearLayout weatherInfoLayout;
    //用于显示城市名
    private TextView cityNameText;
    //用于显示发布时间
    private TextView publishText;
    //用于显示天气描述
    private TextView weatherDespText;
    //用于显示气温1
    private TextView temp1Text;
    //用于显示当前日期
    private TextView currentDateText;
    //切换城市按钮
    private Button switchCity;
    //用于更新天气按钮
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        //初始化各控件
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        currentDateText = (TextView) findViewById(R.id.current_data);
        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            //有县级代码就去查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else{
            //没有县级代码，就显示直接的天气
            showWeather();
        }
    }

    //根据县级代码查询对应的天气
    public void queryWeatherCode(String countyCode){
        //String address = "https://api.thinkpage.cn/v3/weather/now.json?key=" + KEY + "&language=zh-Hans&unit=c&location=" + countyCode;
        String address = "http://192.168.0.102/city.json";
        LogUtil.e("WeatherActivity",address);
        queryFromService(address);
    }

    //根据传入的地址和类型去向服务器查询天气代号
    private void queryFromService(final String address){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //处理服务器返回的天气信息
                Utility.handleWeatherResponse(WeatherActivity.this,response);
                //调用runOnUiThread刷新界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                publishText.setText("同步失败");
            }
        });
    }

    //从SharedPreferences文件中读取存储的天气信息，并显示到界面上
    private void showWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temperature","") + "°C");
        weatherDespText.setText(prefs.getString("text",""));
        publishText.setText("今天" + prefs.getString("last_update_time","") + "发布");
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }
}

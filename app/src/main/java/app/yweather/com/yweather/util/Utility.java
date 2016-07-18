package app.yweather.com.yweather.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * 解析JSON工具类
 */
public class Utility {

    //解析服务器返回的JSON数据，并将解析出的数据存储到本地。
    public static void handleWeatherResponse(Context context, String response){
        try{
            JSONArray jsonObjs = new JSONObject(response).getJSONArray("results");
            JSONObject locationJsonObj = ((JSONObject)jsonObjs.opt(0)).getJSONObject("location");
            String id = locationJsonObj.getString("id");
            String name = locationJsonObj.getString("name");
            JSONObject nowJsonObj = ((JSONObject)jsonObjs.opt(0)).getJSONObject("now");
            String text = nowJsonObj.getString("text");
            String temperature = nowJsonObj.getString("temperature");
            String wind =  nowJsonObj.getString("wind_direction");
            String lastUpdateTime = ((JSONObject) jsonObjs.opt(0)).getString("last_update");
            lastUpdateTime = lastUpdateTime.substring(lastUpdateTime.indexOf("+") + 1,lastUpdateTime.length());
            LogUtil.e("Utility",  "name:" + name  + ",text:"+ text + "wind:" + wind + ",lastUpdateTime:" + lastUpdateTime);
            saveWeatherInfo(context,name,id,temperature,text,lastUpdateTime);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  将服务器返回的天气信息存储到SharedPreferences文件中
     *  context : Context对象
     *  cityName : 城市名称
     *  cityId : 城市id
     *  temperature: 温度
     *  text ：天气现象文字说明，如多云
     *  lastUpdateTime : 数据更新时间
     *  2016-07-16T13:10:00+08:00
     */
    @TargetApi(Build.VERSION_CODES.N)   //指定使用的系统版本
    public static void saveWeatherInfo(Context context, String cityName, String cityId, String temperature,
                                       String text, String lastUpdateTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CANADA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("city_id",cityId);
        editor.putString("temperature",temperature);
        editor.putString("text",text);
        editor.putString("last_update_time",lastUpdateTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}

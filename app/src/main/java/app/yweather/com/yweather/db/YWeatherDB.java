package app.yweather.com.yweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import app.yweather.com.yweather.model.City;
import app.yweather.com.yweather.model.County;
import app.yweather.com.yweather.model.Province;
import app.yweather.com.yweather.util.LogUtil;

/**
 * 封装一些常用的数据库操作
 */
public class YWeatherDB {
    //数据库名称
    public static final String DB_NAME = "y_weather";
    //数据库版本
    public static final int VERSION = 1;
    //本类的一个对象
    private static YWeatherDB yweatherDB;
    private SQLiteDatabase db;

    //将构造方法私有化，不允许实例化该对象
    private YWeatherDB(Context context){
        YWeatherOpenHelper dbHelper = new YWeatherOpenHelper(context,DB_NAME,null,VERSION);
        //连接数据库
        db = dbHelper.getWritableDatabase();
    }

    //获取该类的实例
    public synchronized static YWeatherDB getInstance(Context context){
        if(yweatherDB == null){
            yweatherDB = new YWeatherDB(context);
        }
        return yweatherDB;
    }



    //从数据库读取全国所有的省份信息
    public List<Province> loadProvinces(){
         List<Province> list = new ArrayList<Province>();
         Cursor cursor = db.query("Province",null,null,null,null,null,null);
         if(cursor.moveToFirst()){
             do {
                 Province province = new Province();
                 province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                 province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                 list.add(province);
             }while (cursor.moveToNext());
         }
        return list;
    }


    //从数据库内读取某省下所有城市的信息
    public List<City> loadCities(String provinceName){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City",null,"province_name = ?",new String[]{provinceName},null,null,null);
        if(cursor.moveToFirst()){
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setEnName(cursor.getString(cursor.getColumnIndex("en_name")));
                city.setProvinceName(provinceName);
                list.add(city);
            }while (cursor.moveToNext());
        }
        return list;
    }

    //从数据库内读取某城市下所有的县信息
    public List<County> loadCounties(String cityName){
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County",null,"city_name = ?",new String[]{String.valueOf(cityName)},null,null,null);
        if(cursor.moveToFirst()){
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setEnName(cursor.getString(cursor.getColumnIndex("en_name")));
                county.setCityName(cityName);
                list.add(county);
            }while (cursor.moveToNext());
        }
        return list;
    }
}

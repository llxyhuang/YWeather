package app.yweather.com.yweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016-07-14.
 */
public class YWeatherOpenHelper extends SQLiteOpenHelper{

    /**
     *  Province(省)表建表语句
     *  id 自增长主键，
     *  province_name 表示省名
     *  province_code 表示省级代号
     */
    public static final String CREATE_PROVINCE = "create table Province(" +
            "id integer primary key autoincrement," +
            "province_name text)";

    /**
     * City（市）表建表语句
     * id 自增长主键
     * city_name 表示城市名
     * city_code 表示市级代号
     * province_id 是City表关联Province表的外键
     */
    public static final String CREATE_CITY = "create table City(" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "en_name text," +
            "province_name text)";

    /**
     * County(县)表建表语句
     * id 自增长主键
     * county_name 县名
     * county_code 县级代号
     * city_id County关联City表的外键
     */
    public static final String CREATE_COUNTY ="create table County(" +
            "id integer primary key autoincrement," +
            "county_name text," +
            "county_code text," +
            "en_name text," +
            "city_name text)";

    public YWeatherOpenHelper(Context context, String name , SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //sqLiteDatabase.execSQL(CREATE_PROVINCE);        //创建Province表
        //sqLiteDatabase.execSQL(CREATE_CITY);            //创建city表
       // sqLiteDatabase.execSQL(CREATE_COUNTY);          //创建County表
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}

package app.yweather.com.yweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016-07-14.
 */
public class YWeatherOpenHelper extends SQLiteOpenHelper{
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

package app.yweather.com.yweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import app.yweather.com.yweather.R;
import app.yweather.com.yweather.db.YWeatherDB;
import app.yweather.com.yweather.model.City;
import app.yweather.com.yweather.model.County;
import app.yweather.com.yweather.model.Province;
import app.yweather.com.yweather.util.HttpCallbackListener;
import app.yweather.com.yweather.util.HttpUtil;
import app.yweather.com.yweather.util.LogUtil;
import app.yweather.com.yweather.util.Utility;

/**
 * Created by Administrator on 2016-07-14.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private YWeatherDB yWeatherDB;
    private List<String> dataList =  new ArrayList<String>();

    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;
    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;
    //判断是否从WeatherActivity中跳转过来
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);
        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);
        if(pres.getBoolean("is_First",true)){
            //如果是第一次加载，就把数据文件复制到相应的位置下
            showProgressDialog();
            imporDatabase();
        }
        //已经选择了城市且不是从WeatherActivity跳转过来的，才会直接跳转到WeatherActivity
        if(pres.getBoolean("city_selected",false) && !isFromWeatherActivity){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);

        yWeatherDB = YWeatherDB.getInstance(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel == LEVEL_PROVINCE){
                    //如果点击的是省
                    selectedProvince = provinceList.get(i);
                    LogUtil.e("ChooseAreaActivity",selectedProvince.getProvinceName());
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    //如果选择的是市
                    selectedCity = cityList.get(i);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    String countyEnName = countyList.get(i).getEnName();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyEnName);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();       //加载省级数据
    }

    //查询全国所有的省。
    private void queryProvinces(){
        provinceList = yWeatherDB.loadProvinces();
        if(provinceList.size() > 0){
            dataList.clear();       //先清空列表
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }
    }

    //查询选中的省内所有的市
    private void queryCities(){
        cityList = yWeatherDB.loadCities(selectedProvince.getProvinceName());
        if(cityList.size() > 0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            Toast.makeText(this,"该省下查询不出任何城市！",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    //查询选中市内的所有的县
    private void queryCounties(){
        countyList = yWeatherDB.loadCounties(selectedCity.getCityName());
        if(countyList.size() > 0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }
    }

    //显示进度对话框
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    //关闭进度对话框
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    //捕获back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel == LEVEL_CITY){
            queryProvinces();
        }else{
            if(isFromWeatherActivity){
                Intent intent = new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    public void imporDatabase() {
        //存放数据库的目录
        String dirPath="/data/data/app.yweather.com.yweather/databases";
        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdir();
        }
        //数据库文件
        File file = new File(dir, "y_weather");
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            //加载需要导入的数据库
            InputStream is = this.getApplicationContext().getResources().openRawResource(R.raw.y_weather);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffere=new byte[is.available()];
            is.read(buffere);
            fos.write(buffere);
            is.close();
            fos.close();

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean("is_First",false);
            editor.commit();
            LogUtil.e("ChooseAreaActivity","开始复制数据");
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this,"查询数据库失败",Toast.LENGTH_SHORT).show();
        } finally{
            closeProgressDialog();
        }
    }

}

package app.yweather.com.yweather.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
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
    private List<String> dataList = new ArrayList<String>();

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
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);
        if (pres.getBoolean("is_First", true)) {
            //如果是第一次加载，就把数据文件复制到相应的位置下
            showProgressDialog();
            imporDatabase();
        }
        //如果之前就已经打开过一次软件，且查询过相应城市的天气预报，则直接显示
        if (pres.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        //如何没有按返回主页的键，且定位成功了，则直接显示定位城市的天气预报
        if(!isFromWeatherActivity) {
            if (getLocation()) {
                return;
            }
        }
        //否则就显示全国城市列表，让用户自己选择一个城市来显示天气预报

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        yWeatherDB = YWeatherDB.getInstance(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    //如果点击的是省
                    selectedProvince = provinceList.get(i);
                    LogUtil.e("ChooseAreaActivity", selectedProvince.getProvinceName());
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    //如果选择的是市
                    selectedCity = cityList.get(i);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String countyEnName = countyList.get(i).getEnName();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyEnName);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();       //加载省级数据
    }

    //查询全国所有的省。
    private void queryProvinces() {
        provinceList = yWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();       //先清空列表
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }
    }

    //查询选中的省内所有的市
    private void queryCities() {
        cityList = yWeatherDB.loadCities(selectedProvince.getProvinceName());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            Toast.makeText(this, "该省下查询不出任何城市！", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    //查询选中市内的所有的县
    private void queryCounties() {
        countyList = yWeatherDB.loadCounties(selectedCity.getCityName());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }
    }

    //显示进度对话框
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.setMessage("正在加载....");
        progressDialog.show();
    }

    //关闭进度对话框
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    //显示正在定位的进度对话框
    private void showLocationDialog(){
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.setMessage("正在定位，请稍等....");
        progressDialog.show();
    }


    //捕获back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    public void imporDatabase() {
        //存放数据库的目录
        String dirPath = "/data/data/app.yweather.com.yweather/databases";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        //数据库文件
        File file = new File(dir, "y_weather");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            //加载需要导入的数据库
            InputStream is = this.getApplicationContext().getResources().openRawResource(R.raw.y_weather);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffere = new byte[is.available()];
            is.read(buffere);
            fos.write(buffere);
            is.close();
            fos.close();

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean("is_First", false);
            editor.commit();
            LogUtil.e("ChooseAreaActivity", "开始复制数据");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "查询数据库失败", Toast.LENGTH_SHORT).show();
        } finally {
            closeProgressDialog();
        }
    }

    private LocationManager locationManager;
    private String provider;

    //获取经纬度
    public Boolean getLocation() {
        showLocationDialog();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        LogUtil.e("ChooseAreaActivity","provider:" + providerList.toString());
        //判断是否有SIM卡
        int absent = TelephonyManager.SIM_STATE_ABSENT;
        if (providerList.contains(LocationManager.GPS_PROVIDER) && 1 != absent) {
            //如果gps打开，并且又sim卡，
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            //当没有可用的位置提供器的时候，弹出Toast提示框
            closeProgressDialog();
            Toast.makeText(this, "位置共享服务已关闭，无法定位！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        closeProgressDialog();
        if(location != null){
            //根据当前设备的经纬度查询
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("Latitude",location.getLatitude());
            intent.putExtra("Longitude",location.getLongitude());
            startActivity(intent);
            finish();
        }
        return true;
    }
}

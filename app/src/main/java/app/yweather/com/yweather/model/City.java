package app.yweather.com.yweather.model;

/**
 * Created by Administrator on 2016-07-14.
 */
public class City {
    private int id;
    private String cityName;
    private String cityCode;
    private int ProvinceId;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setProvinceId(int provinceId) {
        ProvinceId = provinceId;
    }

    public int getProvinceId() {
        return ProvinceId;
    }
}

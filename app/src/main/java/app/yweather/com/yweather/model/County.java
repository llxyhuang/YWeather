package app.yweather.com.yweather.model;

/**
 * Created by Administrator on 2016-07-14.
 */
public class County {
    private String id;
    private String countyName;
    private String enName;
    private String cityName;
    private String cityId;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getEnName() {
        return enName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityId() {
        return cityId;
    }
}


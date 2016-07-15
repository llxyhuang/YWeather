package app.yweather.com.yweather.model;

/**
 * Created by Administrator on 2016-07-14.
 */
public class City {
    private int id;
    private String cityName;
    private String cityCode;
    private String enName;
    private String provinceName;

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

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getEnName() {
        return enName;
    }
}

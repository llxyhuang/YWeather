package app.yweather.com.yweather.model;

/**
 * Created by Administrator on 2016-07-14.
 */
public class City {
    private String id;
    private String cityName;
    private String enName;
    private String provinceName;
    private String provinceId;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
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

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceId() {
        return provinceId;
    }
}

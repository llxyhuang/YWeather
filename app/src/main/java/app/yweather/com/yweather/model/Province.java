package app.yweather.com.yweather.model;

/**
 * Created by Administrator on 2016-07-14.
 */
public class Province {
    private String id;
    private String provinceName;
    private String enName;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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

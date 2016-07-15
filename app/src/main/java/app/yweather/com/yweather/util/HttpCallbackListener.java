package app.yweather.com.yweather.util;

/**
 * 回调接口
 */
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}

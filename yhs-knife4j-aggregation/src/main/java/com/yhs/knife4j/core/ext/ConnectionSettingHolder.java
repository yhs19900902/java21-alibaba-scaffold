package com.yhs.knife4j.core.ext;


import com.yhs.knife4j.spring.configuration.HttpConnectionSetting;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * @author 07664-linwei
 * @version Id: ConnectionSettingHolder.java, v 0.1 2023/7/25 9:48 lw Exp $
 */
public class ConnectionSettingHolder {
    public static final ConnectionSettingHolder ME = new ConnectionSettingHolder();
    private HttpConnectionSetting connectionSetting;
    private volatile RequestConfig defaultRequestConfig;
    private volatile PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    private ConnectionSettingHolder() {
    }

    public synchronized HttpConnectionSetting getConnectionSetting() {
        if (this.connectionSetting == null) {
            this.connectionSetting = new HttpConnectionSetting();
        }
        return connectionSetting;
    }

    public void setConnectionSetting(HttpConnectionSetting connectionSetting) {
        this.connectionSetting = connectionSetting;
    }

    public synchronized RequestConfig getDefaultRequestConfig() {
        if (this.defaultRequestConfig == null) {
            this.defaultRequestConfig = RequestConfig.custom().setSocketTimeout(this.getConnectionSetting().getSocketTimeout()).setConnectTimeout(this.getConnectionSetting().getConnectTimeout()).build();
        }
        return defaultRequestConfig;
    }

    public synchronized PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
        if (this.poolingHttpClientConnectionManager == null) {
            this.poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
            //将最大连接数增加到200
            this.poolingHttpClientConnectionManager.setMaxTotal(this.getConnectionSetting().getMaxConnectionTotal());
            //将每个路由基础的连接数增加到20
            this.poolingHttpClientConnectionManager.setDefaultMaxPerRoute(this.getConnectionSetting().getMaxPreRoute());
        }
        return poolingHttpClientConnectionManager;
    }
}

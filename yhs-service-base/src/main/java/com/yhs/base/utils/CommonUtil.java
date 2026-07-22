package com.yhs.base.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * @author 04628-duanchengjun
 * @version Id: CommonUtil.java, v 0.1 2019/4/25 9:50 duanchengjun Exp $
 */
@UtilityClass
public class CommonUtil {

    private static final Logger logger = LogUtil.getLogger();

    public static void safeCloseInputStream(InputStream in) {
        try {
            if (in != null) {
                in.close();
                in = null;
            }
        } catch (IOException e) {
            logger.error("safeClose InputStream exception", e);
        }
    }

    public static void safeCloseOutputStream(OutputStream out) {
        try {
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (IOException e) {
            logger.error("safeClose OutputStream exception", e);
        }
    }

    public static void safeCloseReader(Reader in) {
        try {
            if (in != null) {
                in.close();
                in = null;
            }
        } catch (IOException e) {
            logger.error("safeClose reader exception", e);
        }
    }

    public static void safeCloseWriter(Writer out) {
        try {
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (IOException e) {
            logger.error("safeClose writer exception", e);
        }
    }

    public static String getClasspath() {
        URL url = CommonUtil.class.getClassLoader().getResource(".");
        if (url != null) {
            String path = url.getPath();
            if (path.indexOf("/WEB-INF/classes/") != -1 || path.indexOf("/target/classes/") != -1 || path.indexOf("/target/test-classes/") != -1) {
                return path;
            }
        }
        url = CommonUtil.class.getClassLoader().getResource("/");
        if (url != null) {
            String path = url.getPath();
            if (path.indexOf("/WEB-INF/classes/") != -1 || path.indexOf("/target/classes/") != -1 || path.indexOf("/target/test-classes/") != -1) {
                return path;
            }
        }
        logger.error("!!!!!!!getClasspath error!!!!!!!");
        return "";
    }

    public static String getLocalHostIP() {
        String ip;
        try {
            /** 返回本地主机。 */
            InetAddress addr = InetAddress.getLocalHost();
            /** 返回 IP 地址字符串（以文本表现形式） */
            ip = addr.getHostAddress();
        } catch (Exception ex) {
            ip = "";
        }

        return ip;
    }

    public static String getLocalHostName() {
        String hostName;
        try {
            /** 返回本地主机。 */
            InetAddress addr = InetAddress.getLocalHost();
            /** 返回 服务name 地址字符串（以文本表现形式） */
            hostName = addr.getHostName();
        } catch (Exception ex) {
            logger.warn("getLocalHostName exception", ex);
            hostName = "";
        }
        return hostName;
    }

    public static Properties getPropertiesFromLocal(String propertiesFileName) {
        FileInputStream fis = null;
        File file = null;
        Properties properties = null;
        try {
            // 配置文件的路径是配置在启动脚本里面的
            //            String url = getClasspath() + propertiesFileName;
            //            file = new File(url);
            file = FileLoaderUtil.findConfigFile(propertiesFileName);
            if (file == null) {
                logger.warn("Not found file [" + propertiesFileName + "]");
                return null;
            }

            fis = new FileInputStream(file);
            properties = new Properties();
            properties.load(fis);
        } catch (FileNotFoundException e) {
            logger.error("getPropertiesFromLocal FileNotFoundException, propertiesFileName={}", propertiesFileName, e);
        } catch (Throwable t) {
            logger.error("getPropertiesFromLocal Throwable, propertiesFileName={}", propertiesFileName, t);
        } finally {
            safeCloseInputStream(fis);
        }
        return properties;
    }

    public static Integer getInt(String key) {
        if (StringUtils.isNotBlank(key)) {
            try {
                return Integer.parseInt(key);
            } catch (Throwable t) {
                return null;
            }
        }
        return null;
    }

    public static String urlDecode(String content) {
        if (StringUtils.isNotBlank(content)) {
            try {
                content = URLDecoder.decode(content, "UTF-8");
            } catch (Throwable t) {

            }
        }
        return content;
    }

    public static String getInterfaceName(String requestURI) {
        if (StringUtils.isNotBlank(requestURI) && requestURI.indexOf("/") != -1) {
            String root = requestURI.substring(0, requestURI.indexOf("/", 2) + 1);
            requestURI = requestURI.substring(requestURI.indexOf(root) + root.length());
        }
        return requestURI;
    }

}

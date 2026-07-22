package com.yhs.base.utils;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;

import java.io.File;
import java.net.URL;

/**
 * @author 04628-duanchengjun
 * @version Id: FileLoader.java, v 0.1 2019/4/25 9:51 duanchengjun Exp $
 */
@UtilityClass
public class FileLoaderUtil {

    private static final Logger logger = LogUtil.getLogger();

    public static File findConfigFile(String fileOrPath) {
        ClassLoader cl = FileLoaderUtil.class.getClassLoader();
        URL url = cl.getResource(fileOrPath);
        logger.debug("file url:{}", url);
        if (url != null) {
            return new File(url.getFile());
        }
        File file = new File(fileOrPath);
        if (file.exists()) {
            return file;
        }
        String newPath = "conf" + (fileOrPath.startsWith(File.separator) ? "" : "/") + fileOrPath;
        logger.debug("file newPath:{}", newPath);
        url = ClassLoader.getSystemResource(newPath);
        if (url != null) {
            return new File(url.getFile());
        }

        newPath = System.getProperty("catalina.home");
        if (newPath == null) {
            newPath = System.getenv("CATALINA.HOME");
        }
        if (newPath == null) {
            newPath = System.getenv("CATALINA.BASE");
        }
        if (newPath == null) {
            return null;
        }

        file = new File(newPath + File.separator + "conf" + File.separator + fileOrPath);
        if (file.exists()) {
            return file;
        }

        return null;
    }
}

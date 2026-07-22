package com.yhs.base.utils;

import lombok.experimental.UtilityClass;

import java.io.*;

/**
 * 文件操作类
 *
 * @author 05697-LongLiHua
 * @version Id: FileUtils.java, v 0.1 2020/10/27 16:30  LongLiHua Exp $
 * @Description
 */
@UtilityClass
public class FileUtil {

    /**
     * 根据字节创建文件
     *
     * @param bytes 字节数组
     * @param path  文件路径
     * @return void
     **/
    public static File byteToFile(byte[] bytes, String path) throws IOException {

        // 根据绝对路径初始化文件
        File localFile = new File(path);
        if (!localFile.exists()) {
            localFile.createNewFile();
        }
        // 输出流
        OutputStream os = new FileOutputStream(localFile);
        os.write(bytes);
        os.close();
        return localFile;
    }

    /**
     * 文件转为字节
     *
     * @param tradeFile 文件
     * @return byte[] 字节数组
     **/
    public static byte[] File2byte(File tradeFile) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return void
     **/
    public static void deleteLocalFile(File file) {
        file.deleteOnExit();
    }

}
    
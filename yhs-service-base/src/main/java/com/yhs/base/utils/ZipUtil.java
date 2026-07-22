package com.yhs.base.utils;

import com.yhs.base.exception.ZipException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author 04628-duanchengjun
 * @version Id: ZipUtil.java, v 0.1 2019/4/25 10:12 duanchengjun Exp $
 */
@UtilityClass
public class ZipUtil {

    private static final int BUFFER_SIZE = 8192;

    /**
     * 解压缩zip包
     *
     * @param zipFilePath   zip文件的全路径
     * @param unzipFilePath 解压后的文件保存的路径
     */
    public static void unzip(String zipFilePath, String unzipFilePath) throws Exception {
        if (StringUtils.isEmpty(zipFilePath) || StringUtils.isEmpty(unzipFilePath)) {
            throw new ZipException("param is null");
        }
        File zipFile = new File(zipFilePath);
        File unzipFileDir = new File(unzipFilePath);
        if (!unzipFileDir.exists() || !unzipFileDir.isDirectory()) {
            unzipFileDir.mkdirs();
        }

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        ZipFile zip = null;
        try {
            zip = new ZipFile(zipFile);
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
            //循环对压缩包里的每一个文件进行解压
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                //构建压缩包中一个文件解压后保存的文件全路径
                String entryFilePath = unzipFilePath + File.separator + entry.getName();

                File entryFile = new File(entryFilePath);
                if (entryFile.exists()) {
                    entryFile.delete();
                }

                // 文件夹
                if (!entry.getName().contains(".")) {
                    entryFile.mkdirs();
                } else {
                    bos = new BufferedOutputStream(new FileOutputStream(entryFile));
                    bis = new BufferedInputStream(zip.getInputStream(entry));

                    byte[] buffer = new byte[BUFFER_SIZE];
                    int count = 0;

                    while ((count = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        bos.write(buffer, 0, count);
                    }
                    bos.flush();
                }
            }
        } catch (Exception e) {
            throw new ZipException("unzip failed", e);
        } finally {
            if (zip != null) {
                zip.close();
            }
            CommonUtil.safeCloseInputStream(bis);
            CommonUtil.safeCloseOutputStream(bos);
        }

    }

    /**
     * 压缩文件
     *
     * @param filePath 待压缩的文件路径
     * @return 压缩后的文件
     */
    public static File zip(String filePath, String zipName) throws Exception {
        File target = null;
        File source = new File(filePath);
        if (source.exists()) {
            // 压缩文件名=源文件名.zip
            target = new File(source.getParent(), zipName);
            if (target.exists()) {
                target.delete(); // 删除旧的文件
            }
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            try {
                fos = new FileOutputStream(target);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                // 添加对应的文件Entry
                addEntry("", source, zos);
            } catch (IOException e) {
                throw new ZipException("zip failed", e);
            } finally {
                CommonUtil.safeCloseOutputStream(zos);
                CommonUtil.safeCloseOutputStream(fos);
            }
        }
        return target;
    }

    /**
     * 扫描添加文件Entry
     *
     * @param base   基路径
     * @param source 源文件
     * @param zos    Zip文件输出流
     * @throws IOException
     */
    private static void addEntry(String base, File source, ZipOutputStream zos) throws IOException {
        // 按目录分级，形如：/aaa/bbb.txt
        String entry = base + source.getName();
        if (source.isDirectory()) {
            for (File file : source.listFiles()) {
                // 递归列出目录下的所有文件，添加文件Entry
                addEntry(entry + "/", file, zos);
            }
        } else {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                fis = new FileInputStream(source);
                bis = new BufferedInputStream(fis, buffer.length);
                int read = 0;
                zos.putNextEntry(new ZipEntry(entry));
                while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
                    zos.write(buffer, 0, read);
                }
                zos.closeEntry();
            } finally {
                CommonUtil.safeCloseInputStream(bis);
                CommonUtil.safeCloseInputStream(fis);
            }
        }
    }
}

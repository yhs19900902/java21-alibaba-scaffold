package com.yhs.base.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 04628-duanchengjun
 * @version Id: CSVUtils.java, v 0.1 2019/4/25 9:52 duanchengjun Exp $
 */
@UtilityClass
public class CSVUtil {

    private static final Logger logger = LogUtil.getLogger();

    public static List<List<String>> parseCSV(String filePath) {
        Reader reader = null;
        List<List<String>> list = new ArrayList<>();
        try {
            reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            for (CSVRecord record : records) {
                List<String> tmp = new ArrayList<>();
                Iterator<String> it = record.iterator();
                while (it.hasNext()) {
                    String temp = it.next();
                    if (temp == null) {
                        temp = "";
                    } else {
                        temp = temp.replaceAll("\uFEFF", "");
                        temp = temp.replaceAll("\\\"", "");
                    }
                    tmp.add(temp);
                }
                list.add(tmp);
            }
        } catch (Exception e) {
            logger.error("parseCSV failed, filePath={}", filePath, e);
        } finally {
            CommonUtil.safeCloseReader(reader);
        }
        return list;
    }

    public static boolean writeCSV(List<List<String>> records, String filePath) {
        Writer out = null;
        try {
            CSVFormat format = CSVFormat.DEFAULT.withSkipHeaderRecord();
            out = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8);
            CSVPrinter printer = new CSVPrinter(out, format);
            for (List<String> record : records) {
                printer.printRecord(record);
            }
            return true;
        } catch (Exception e) {
            logger.error("writeCSV failed, filePath={}", filePath, e);
        } finally {
            CommonUtil.safeCloseWriter(out);
        }
        return false;
    }
}

package com.yhs.base.utils;

import com.yhs.base.enums.DateResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 04628-duanchengjun
 * @version Id: DateUtil.java, v 0.1 2019/4/25 9:53 duanchengjun Exp $
 */
public class DateUtil {

    public static final String DATE_TIME_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_STR = "yyyy-MM-dd";
    private final static Logger logger = LogUtil.getLogger();
    private static final int[] DAYS_OF_MONTH = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public static boolean isValidBirthday(String birthday) {
        boolean isValidDateFormat = isValidDate(birthday);
        if (!isValidDateFormat)
            return isValidDateFormat;

        String currentDate = getCurrentDateStr();
        int compareResult = compareDate(birthday, currentDate);
        return compareResult == DateResultEnum.EARLY.getRetCode()
                || compareResult == DateResultEnum.EQUAL.getRetCode();
    }

    /**
     * 给定日期是否大于当天（包含当天）
     *
     * @param dateStr
     * @return
     */
    public static boolean laterThanCurrentDate(String dateStr) {
        boolean isValidDateFormat = isValidDate(dateStr);
        if (!isValidDateFormat)
            return isValidDateFormat;

        String currentDate = getCurrentDateStr();
        int compareResult = compareDate(dateStr, currentDate);
        return compareResult == DateResultEnum.LATER.getRetCode()
                || compareResult == DateResultEnum.EQUAL.getRetCode();
    }

    /**
     * 给定日期是否大于当天（不包含当天）
     *
     * @param dateStr
     * @return
     */
    public static boolean laterThanAndNotContainCurrentDate(String dateStr) {
        boolean isValidDateFormat = isValidDate(dateStr);
        if (!isValidDateFormat)
            return isValidDateFormat;

        String currentDate = getCurrentDateStr();
        int compareResult = compareDate(dateStr, currentDate);
        return compareResult == DateResultEnum.LATER.getRetCode();
    }

    /**
     * 给定日期是否早于当天（不包含当天）
     *
     * @param dateStr
     * @return
     */
    public static boolean earlyThanCurrentDate(String dateStr) {
        boolean isValidDateFormat = isValidDate(dateStr);
        if (!isValidDateFormat)
            return isValidDateFormat;

        String currentDate = getCurrentDateStr();
        return compareDate(dateStr, currentDate) == DateResultEnum.EARLY.getRetCode();
    }

    public static String getCurrentDateStr() {
        return LocalDate.now().toString();
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static int compareDate(String date1, String date2) {
        int ret = DateResultEnum.ERROR.getRetCode();
        try {
            DateFormat format = new SimpleDateFormat(DATE_STR);
            Date dt1 = format.parse(date1);
            Date dt2 = format.parse(date2);
            ret = compareDate(dt1, dt2);
        } catch (Exception e) {
            logger.error("compare date error,param1=" + date1 + ",param2=" + date2, e);
        }
        return ret;
    }


    public static int compareDateTime(String date1, String date2) {
        int ret = DateResultEnum.ERROR.getRetCode();
        try {
            DateFormat format = new SimpleDateFormat(DATE_TIME_STR);
            Date dt1 = format.parse(date1);
            Date dt2 = format.parse(date2);
            ret = compareDate(dt1, dt2);
        } catch (Exception e) {
            logger.error("compare date error,param1=" + date1 + ",param2=" + date2, e);
        }
        return ret;
    }

    private static int compareDate(Date dt1, Date dt2) {
        if (dt1.getTime() > dt2.getTime())
            return DateResultEnum.LATER.getRetCode();
        else if (dt1.getTime() < dt2.getTime())
            return DateResultEnum.EARLY.getRetCode();
        return DateResultEnum.EQUAL.getRetCode();
    }


    /**
     * 校验日期格式是否为"yyyy-mm-dd HH:MM:SS"
     *
     * @param dateTime
     * @return
     */
    public static boolean isValidDateTime(String dateTime) {
        if (StringUtils.isBlank(dateTime))
            return false;

        dateTime = StringUtils.trim(dateTime);
        String[] dateTimeArr = StringUtils.split(dateTime, " ");
        if (dateTimeArr == null || dateTimeArr.length != 2)
            return false;

        String dateStr = dateTimeArr[0];
        String timeStr = dateTimeArr[1];
        return isValidDate(dateStr) && isValidTime(timeStr);
    }

    /**
     * @param date yyyy-MM-dd
     * @return
     */
    public static boolean isValidDate(String date) {
        if (StringUtils.isBlank(date))
            return false;

        date = StringUtils.trim(date);
        String[] dateArr = StringUtils.split(date, "-");
        if (dateArr == null || dateArr.length != 3)
            return false;

        try {
            int year = Integer.parseInt(dateArr[0]);
            if (year <= 0)
                return false;

            int month = Integer.parseInt(dateArr[1]);
            if (month <= 0 || month > 12)
                return false;

            int day = Integer.parseInt(dateArr[2]);
            if (day <= 0 || day > DAYS_OF_MONTH[month - 1])
                return false;

            if (month == 2 && day == 29 && !isGregorianLeapYear(year))
                return false;
        } catch (Exception e) {
            logger.error("compare date error,param=" + date, e);
            return false;
        }
        return true;
    }

    /**
     * 校验日期格式是否为HH:MM:SS
     *
     * @param dateStr
     * @return
     */
    public static boolean isValidTime(String dateStr) {
        if (StringUtils.isBlank(dateStr))
            return false;

        dateStr = StringUtils.trim(dateStr);
        try {
            String[] dateStrArr = StringUtils.split(dateStr, ":");
            if (dateStr == null || dateStrArr.length != 3)
                return false;

            int hour = Integer.parseInt(dateStrArr[0]);
            if (hour < 0 || hour > 23)
                return false;

            int minute = Integer.parseInt(dateStrArr[1]);
            if (minute < 0 || minute > 59)
                return false;

            int second = Integer.parseInt(dateStrArr[2]);
            if (second < 0 || second > 59)
                return false;
        } catch (Exception e) {
            logger.error("compare date error,param=" + dateStr, e);
            return false;
        }

        return true;
    }

    private static boolean isGregorianLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    public static Date strToDate(String dateStr, String dateFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            Date date = sdf.parse(dateStr);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToStr(Date date, String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(date);
    }

    public static String trimDateTimeStrToDate(String dateTimeStr) {
        Date date = strToDate(dateTimeStr, DATE_STR);
        if (date != null) {
            return dateToStr(date, DATE_STR);
        }
        return dateTimeStr;
    }

    /**
     * 判断是否周六周日
     *
     * @param input
     * @param format
     * @return
     */
    public static boolean isWeekend(String input, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        boolean weekend = false;
        try {
            Date date = sdf.parse(input);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                    || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                weekend = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return weekend;
    }
}

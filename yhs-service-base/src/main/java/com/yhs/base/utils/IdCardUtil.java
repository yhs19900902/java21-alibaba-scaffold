package com.yhs.base.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 04628-duanchengjun
 * @version Id: IdCardUtil.java, v 0.1 2019/4/25 9:59 duanchengjun Exp $
 */
@UtilityClass
public class IdCardUtil {

    public static final String FEMALE = "1";
    public static final String MALE = "0";
    static final Pattern pattern = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
    /**
     * 身份证号码验证
     * 1、号码的结构
     * 公民身份号码是特征组合码，由十七位数字本体码和一位校验码组成。
     * 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
     * 2、地址码(前六位数）
     * 表示编码对象常住户口所在县(市、旗、区)的行政区划代码，按GB/T2260的规定执行。
     * 3、出生日期码（第七位至十四位）
     * 表示编码对象出生的年、月、日，按GB/T7408的规定执行，年、月、日代码之间不用分隔符。
     * 4、顺序码（第十五位至十七位）
     * 表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号， 顺序码的奇数分配给男性，偶数分配给女性。
     * 5、校验码（第十八位数）
     * （1）十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0, ... , 16 ，先对前17位数字的权求和
     * Ai:表示第i位置上的身份证号码数字值 Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
     * （2）计算模 Y = mod(S, 11)
     * （3）通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 10 校验码: 1 0 X 9 8 7 6 5 4 3 2
     */

    private static final String[] ValCodeArr = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
    private static final String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};

    public static String validate(String idstr) {
        String errorInfo = "Y";// 记录错误信息

        try {
            if (StringUtils.isBlank(idstr)) {
                errorInfo = "身份证号码为空。";
                return errorInfo;
            }

            idstr = StringUtils.upperCase(idstr);
            // ================ 号码的长度 15位或18位 ================
            if (idstr.length() != 15 && idstr.length() != 18) {
                errorInfo = "身份证号码长度应该为15位或18位。";
                return errorInfo;
            }

            String Ai = "";
            // ================ 数字 除最后以为都为数字 ================
            if (idstr.length() == 18) {
                Ai = idstr.substring(0, 17);
            } else if (idstr.length() == 15) {
                Ai = idstr.substring(0, 6) + "19" + idstr.substring(6, 15);
            }
            if (!isNumeric(Ai)) {
                errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
                return errorInfo;
            }

            // ================ 出生年月是否有效 ================
            String strYear = Ai.substring(6, 10);// 年份
            String strMonth = Ai.substring(10, 12);// 月份
            String strDay = Ai.substring(12, 14);// 月份
            String birthday = strYear + "-" + strMonth + "-" + strDay;
            if (!DateUtil.isValidBirthday(birthday)) {
                errorInfo = "身份证生日无效。";
                return errorInfo;
            }

            // ================ 地区码时候有效 ================
            Hashtable<?, ?> h = getAreaCode();
            if (h.get(Ai.substring(0, 2)) == null) {
                errorInfo = "身份证地区编码错误。";
                return errorInfo;
            }

            // ================ 判断最后一位的值 ================
            int TotalmulAiWi = 0;
            for (int i = 0; i < 17; i++) {
                TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
            }
            int modValue = TotalmulAiWi % 11;
            String strVerifyCode = ValCodeArr[modValue];
            Ai = Ai + strVerifyCode;
            if (idstr.length() == 18) {
                if (!Ai.equals(idstr)) {
                    errorInfo = "身份证无效，不是合法的身份证号码";
                    return errorInfo;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorInfo = "身份证号码校验异常,idNo = " + idstr;
        }
        return errorInfo;
    }

    /**
     * 通过身份证号返回生日，格式为YYYY-MM-DD
     *
     * @param idNo
     * @return
     */
    public static String getBirthdayByIdNo(String idNo) {
        String checkResult = validate(idNo);
        if (StringUtils.equals("Y", checkResult)) {
            String Ai = completeIdNo(idNo);
            String strYear = Ai.substring(6, 10);// 年份
            String strMonth = Ai.substring(10, 12);// 月份
            String strDay = Ai.substring(12, 14);// 月份
            String birthday = strYear + "-" + strMonth + "-" + strDay;
            return birthday;
        }
        return "ERROR";
    }

    private static String completeIdNo(String idNo) {
        if (StringUtils.isNotBlank(idNo)) {
            if (idNo.length() == 18) {
                return idNo;
            } else if (idNo.length() == 15) {
                return idNo.substring(0, 6) + "19" + idNo.substring(6, 15);
            }
        }
        return "";
    }

    public static String getSexByIdNo(String idNo) {
        String checkResult = validate(idNo);
        int sexBit = -1;
        if (StringUtils.equals("Y", checkResult)) {
            if (idNo.length() == 15) {
                sexBit = Integer.parseInt(idNo.substring(14, 15));
                System.out.println(sexBit);
            } else if (idNo.length() == 18) {
                sexBit = Integer.parseInt(idNo.substring(16, 17));
            }

//            if (sexBit > 0) {
            if (sexBit % 2 == 0) {  //偶数为女生
                return FEMALE;
            } else if (sexBit % 2 == 1) {//  奇数为男生
                return MALE;
            }
        }
        return "ERROR";
    }

    private static Hashtable<String, String> getAreaCode() {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static boolean isDate(String strDate) {
        Matcher m = pattern.matcher(strDate);
        return m.matches();
    }
}

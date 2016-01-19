package com.carrey.common.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类描述：String 相关的工具类
 * 创建人：carrey
 * 创建时间：2016/1/19 16:18
 */

public class StringUtil {
    /**
     * 获取字符串的MD5值 StringToMD5
     *
     * @param str
     * @return
     * @since 1.0
     */
    public static String stringToMD5(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString().toLowerCase(Locale.getDefault());

    }

    /**
     * 判断是否为手机号码
     * isMobileNumber
     *
     * @param mobile
     * @return
     * @since 1.0
     */
    public static boolean isMobileNumber(String mobile) {
//        Pattern pattern = Pattern.compile("\\d{11}$");
        Pattern pattern = Pattern.compile("^[1][345678][0-9]{9}$");
        return pattern.matcher(mobile).find();
    }

    /**
     * 字符串不为空并大于6位
     */
    public static boolean isPasswordValidate(String mobile) {
        return !TextUtils.isEmpty(mobile) && mobile.length() >= 6;
    }


    /**
     * 判断密码是否过于简单
     *
     * @param pwd
     * @return
     */
    public static boolean isPasswordLeak(String pwd) {
        return TextUtils.isEmpty(pwd) || isNumeric(pwd);
    }

    /**
     * 给手机号局部隐藏
     * decodeMobile
     *
     * @param mobile
     * @return
     * @since 1.0
     */
    public static String decodeMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return mobile;
        }
        if (mobile.length() > 8) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mobile.length(); i++) {
                if (i >= mobile.length() - 8 && i < mobile.length() - 4) {
                    sb.append("*");
                } else {
                    sb.append(mobile.charAt(i));
                }
            }
            return sb.toString();
        }
        return mobile;
    }

    /**
     * 给身份证号局部隐藏
     * decodeMobile
     *
     * @param idCard 身份证号
     * @return
     * @since 1.0
     */
    public static String decodeIdCard(String idCard) {
        if (TextUtils.isEmpty(idCard)) {
            return idCard;
        }
        if (idCard.length() > 3) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < idCard.length(); i++) {
                if (i >= 2 && i < idCard.length() - 1) {
                    sb.append("*");
                } else {
                    sb.append(idCard.charAt(i));
                }
            }
            return sb.toString();
        }
        return idCard;
    }

    /**
     * 保留2位小数
     *
     * @param f
     * @return
     */
    public static String doubleKeepTwo(double f) {
        return String.format("%.2f", f);
    }

    /**
     * 取字符串的前toCount个字符
     *
     * @param str       被处理字符串
     * @param byteCount 截取长度
     * @return
     * @since 3.6
     */
    public static String subStringByCharCount(String str, int byteCount) {
        int reInt = 0;
        String reStr = "";
        if (str == null)
            return "";
        char[] tempChar = str.toCharArray();
        for (int i = 0; (i < tempChar.length && byteCount > reInt); i++) {
            String s1 = String.valueOf(tempChar[i]);
            byte[] b = s1.getBytes();
            reInt += b.length;
            if (reInt > byteCount) {
                break;
            } else {
                reStr += tempChar[i];
            }
        }
        return reStr;
    }

    /**
     * 返回字符串，参数为null时，返回空字符串
     *
     * @param str
     * @return
     */
    public static String toStringEx(Object str) {
        if (str == null) {
            return "";
        }

        return str.toString();
    }

    /**
     * 判断两个字符串是否相等
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equalsEx(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }

        if (str1 != null) {
            return str1.equals(str2);
        }
        return false;
    }

    /**
     * 判断是否位数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * http://xxx?uid=**1&message=哎哟，%s看上去心情不错哦！&mood=0
     * 获取URL中parameter指定的指
     *
     * @param url       URL地址
     * @param parameter 参数
     * @return
     */
    public static String getUrlParameter(String url, String parameter) {
        Pattern p = Pattern.compile(parameter + "=([^&]*)(&|$)");
        Matcher m = p.matcher(url);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public static String convertAppointmentCount(int appointmentCount, int offset) {
        if (appointmentCount < offset) {
            return String.valueOf(appointmentCount);
        } else {
            double computeValue = (appointmentCount / 10000) + (appointmentCount % 10000 / 10000.00);
            BigDecimal b = new BigDecimal(computeValue);
            double finalValue = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            return finalValue + "万";
        }
    }
}

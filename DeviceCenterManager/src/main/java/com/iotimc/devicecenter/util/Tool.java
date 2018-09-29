package com.iotimc.devicecenter.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提供如时间格式转换，当前时间等小工具
 */
public class Tool {
    // 日期转换
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    // 时间日期转换
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 时间转换
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    private static Pattern numberPattern = Pattern.compile("^\\d+$");

    public static Timestamp getNowTiestamp() {
        return Timestamp.valueOf(getNowDateTimeStr());
    }

    public static Date getNowDate() {
        return new Date();
    }

    public static String getNowDateStr() {
        return dateFormat.format(getNowDate());
    }

    public static String getNowDateTimeStr() {
        return dateTimeFormat.format(getNowDate());
    }

    public static String getNowTimeStr() {
        return timeFormat.format(getNowDate());
    }

    public static Long getTimestampLong() {
        return getNowDate().getTime();
    }

    public static Date strToDate(String str) {
        try {
            return dateFormat.parse(str);
        } catch(Exception e) {}
        return null;
    }

    public static Long getTimestampLong(Date date) {
        return date.getTime();
        //Timestamp.valueOf(dateTimeFormat.format(date)).getTime();
    }

    public static Timestamp long2timestamp(long time) {
        return Timestamp.valueOf(dateTimeFormat.format(new Date(time)));
    }

    public static Date long2date(long time) {
        return new Date(time);
    }

    public static String joinHttpParam(Map o, String flag) {
        StringBuffer str_buff = new StringBuffer();
        Set<Map.Entry> set = o.entrySet();
        for (Map.Entry item : set) {
            str_buff.append(item.getKey());
            str_buff.append("=");
            str_buff.append(item.getValue());
            str_buff.append(flag);
        }
        return str_buff.toString().substring(0, str_buff.length() - 1);
    }

    public static String joinString(String[] o, String flag) {
        if(o.length == 0) return "";
        StringBuffer str_buff = new StringBuffer();
        for(int i=0; i<o.length; i++) {
            str_buff.append(o[i]);
            str_buff.append(flag);
        }
        return str_buff.toString().substring(0, str_buff.length() - 1);
    }

    public static String joinString(String[] o) {
        return joinString(o, ",");
    }

    public static Object convert(Object value, String type, Byte ishex) {
        if(ishex == 1) {
            String[] types = value.getClass().getName().split("\\.");
            String orgtype = types[types.length - 1];
            if(!orgtype.equalsIgnoreCase("String") && !Tool.isNumber(String.valueOf(value)) && !orgtype.equalsIgnoreCase(type)) throw new RuntimeException("类型转换出错：" + orgtype + "不能转换为" + type);
            //在16机制下才执行转换
            switch (type) {
                case "float":
                    value = Float.parseFloat(String.valueOf(value));
                    break;
                case "double":
                    value = Double.parseDouble(String.valueOf(value));
                    break;
            }
        }
        return value;
    }

    public static String reciveConvert(String value, String type, Byte ishex) {
        if(ishex == 1) {
            value = value.toUpperCase();
            switch(type) {
                case "float":
                    Long temp = Long.parseLong(value, 16);
                    value = String.valueOf(Float.intBitsToFloat(temp.intValue()));
                    break;
                case "boolean":
                    if(value.equals("01")) value = "true";
                    else value = "false";
                    break;
                case "integer":
                    value = String.valueOf(Integer.parseInt(value, 16));
                    break;
            }
        }
        return value;
    }

    public static String[] isBlanks(JSONObject data, String[] names) {
        List<String> result = new ArrayList();
        String[] rs = new String[0];
        for(int i=0; i<names.length; i++) {
            if(StringUtils.isBlank(data.getString(names[i]))) result.add(names[i]);
        }
        return result.toArray(rs);
    }

    public static boolean isNumber(String numtext) {
        return numberPattern.matcher(numtext).find();
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("\\-", "");
    }

    /**
     * 往后倒推生成size月份
     * @param mindate
     * @param now
     * @param size
     * @param lastday
     * @return
     */
    public static List<String> createRangeMonth(Date mindate, Date now, int size, boolean lastday) {
        SimpleDateFormat ymdt = new SimpleDateFormat("yyyy-MM");
        List<String> list = new ArrayList<>();
        int nowMonth = now.getMonth() + 1;
        int nowYear = now.getYear() + 1900;
        int minmonth = mindate.getMonth() + 1;
        int minyear = mindate.getYear() + 1900;
        String nowDateStr = ymdt.format(now);
        String minDateStr = ymdt.format(mindate);
        for(int i = 1; i <= size; i++) {
            minmonth += 1;
            if(minmonth / 12 > 0 && minmonth % 12 > 0) {
                minmonth = 1;
                minyear += 1;
            }
            String current = minyear + "-" + (minmonth < 10?"0" + minmonth:minmonth);
            if(current.compareTo(nowDateStr) < 0) {
                list.add(current + (lastday?"-28":""));
            }
        }
        return list;
    }

    public static void main(String args[]) {

    }
}

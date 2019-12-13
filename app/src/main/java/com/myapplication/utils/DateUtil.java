package com.myapplication.utils;


import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Lin on 2019/6/15.
 */

public class DateUtil {
    /**
     * 将时间戳转化为月和日
     * @param s
     * @return
     */
    public static String stampToDate(String s){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
        long l = new Long(s);
        Date date = new Date(l);
        return simpleDateFormat.format(date);
    }
}

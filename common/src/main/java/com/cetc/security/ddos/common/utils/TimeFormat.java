package com.cetc.security.ddos.common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangtao on 2015/11/4.
 */
public class TimeFormat {
    private static DateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static String format(Date d) {
        return dateFormat.format(d);
    }
}

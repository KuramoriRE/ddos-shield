package com.cetc.security.ddos.common.utils;

import org.apache.log4j.Logger;

/**
 * Created by zhangtao on 2015/10/20.
 */
public class AntiLogger {


    public static Logger getLogger(Class clazz) {
        return Logger.getLogger(clazz);
    }
}

package com.cetc.security.ddos.common.iplocation;

/**
 * Created by lb on 2016/5/16.
 */
public class IpLocationTest {
    public static void main(String[] args) {
        //String ip = "220.166.121.15";
        String ip = "11.12.13.14";
        IpLocationInfo ipLocation;
        ipLocation = IpLocationOnline.GetIpLocation(ip);

    }
}

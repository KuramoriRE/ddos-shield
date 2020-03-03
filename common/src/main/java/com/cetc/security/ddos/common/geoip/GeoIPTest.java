package com.cetc.security.ddos.common.geoip;

/**
 * Created by lb on 2016/5/16.
 */
public class GeoIPTest {

    public static void main(String[] args) {
        String ip = "188.56.45.56";
        GeoIPLocation ipLocation;
        ipLocation = GeoIPLookup.getLocation(ip);
        System.out.println(ipLocation.getCountry());
        System.out.println(ipLocation.getCity());
        System.out.println(ipLocation.getLatitude());
        System.out.println(ipLocation.getLongitude());
        System.out.println(ipLocation.getIp());

    }
}

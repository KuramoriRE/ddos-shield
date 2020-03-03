package com.cetc.security.ddos.common.geoip;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class GeoIPLookup {
	private static DatabaseReader reader;
	private static Map<String, GeoIPLocation> cache = new ConcurrentHashMap<String, GeoIPLocation>();

	static {
		InputStream database = GeoIPLookup.class.getResourceAsStream("/GeoLite2-City.mmdb"); //;
        //File database = new File("/GeoLite2-City.mmdb");
		// This creates the DatabaseReader object, which should be reused across
		// lookups.
		try {
			reader = new DatabaseReader.Builder(database).build();
		} catch (IOException e) {
			System.out.println("fail to load geoip2 database");
		}
		
	}


	/**
	 * The interface to query geo location for one ip address.
	 * @param ip
	 * @return
	 */
	public static GeoIPLocation getLocation(String ip) {
		GeoIPLocation geoIP = getLocationByCache(ip);
		if (geoIP == null) {
			if (checkInnerIP(ip)) {
				geoIP = getDefaultLocation();
			} else {
			    geoIP = getLocationByDB(ip);
			}
			
			if (geoIP != null) {
			    cache.put(ip, geoIP);
			}
		}
		return geoIP;
	}
	
	private static GeoIPLocation getDefaultLocation(){
		String country = "中国";
		String countryCode = "CN";
		String city = "成都";
		double longitude = 104.0470;
		double latitude = 30.5484;

		GeoIPLocation location = new GeoIPLocation();
		location.setCountry(country);
		location.setCountryCode(countryCode);
		location.setCity(city);
		location.setLatitude(latitude);
		location.setLongitude(longitude);

		return location;
	}

	private static GeoIPLocation getLocationByDB(String ip) {
		GeoIPLocation geoIP = null;

		InetAddress ipAddress;
		try {
			ipAddress = InetAddress.getByName(ip);

			// Replace "city" with the appropriate method for your database, e.g.,
			// "country".
			CityResponse response = reader.city(ipAddress);
			Country country = response.getCountry();
			City city = response.getCity();
			Location location = response.getLocation();

			geoIP = new GeoIPLocation();
			geoIP.setCountryCode(country.getIsoCode());
			geoIP.setCountry(country.getNames().get("zh-CN"));
			geoIP.setCity(city.getNames().get("zh-CN"));
			geoIP.setLatitude(location.getLatitude());
			geoIP.setLongitude(location.getLongitude());
            geoIP.setIp(ip);
		} catch (Exception e) {
            System.out.printf("Can not find city by {}", ip);
		}
		return geoIP;
	}

	private static GeoIPLocation getLocationByCache(String ip) {
		 return cache.get(ip);
	}

	private static boolean checkInnerIP(String ipAddress){
		boolean isInnerIp = false;
        long ipNum = getIpNum(ipAddress);
        long bBegin = getIpNum("172.16.0.0");
        long bEnd = getIpNum("172.31.255.255");
        long cBegin = getIpNum("192.168.0.0");
        long cEnd = getIpNum("192.168.255.255");
        isInnerIp = isInner(ipNum,bBegin,bEnd) || isInner(ipNum,cBegin,cEnd) || ipAddress.equals("127.0.0.1");
        return isInnerIp;
	}

	private static long getIpNum(String ipAddress) {
	    String [] ip = ipAddress.split("\\.");
	    long a = Integer.parseInt(ip[0]);
	    long b = Integer.parseInt(ip[1]);
	    long c = Integer.parseInt(ip[2]);
	    long d = Integer.parseInt(ip[3]);

	    long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
	    return ipNum;
	}

	private static boolean isInner(long userIp,long begin,long end){
	     return (userIp>=begin) && (userIp<=end);
	}

	public static void main(String[] args) {
		String ip = "0.0.0.0";
		GeoIPLocation location  = getLocation(ip);
		System.out.println(location.getCountry() + ", " + location.getCity());
	}

}

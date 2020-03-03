package com.cetc.security.ddos.common.iplocation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import com.google.gson.Gson;
/**
 * Created by lb on 2016/5/16.
 */
public class IpLocationOnline {
    static String baidu_base_url = "http://api.map.baidu.com/location/ip?ak=265fd0c67df9acbb7757de202e1b7e45";
    static String taobao_base_url = "http://ip.taobao.com/service/getIpInfo2.php";

    /*
    {
    "address": "CN|四川|宜宾|None|CHINANET|0|0",
    "content": {
        "address": "四川省宜宾市",
        "address_detail": {
            "city": "宜宾市",
            "city_code": 186,
            "district": "",
            "province": "四川省",
            "street": "",
            "street_number": ""
        },
        "point": {
            "x": "104.63301906",
            "y": "28.76967480"
        }
    },
    "status": 0
}
百度地图API返回的json字符串如上，BaiduReturn为其对应的对象
     */
    private class BaiduReturn {
        private class Content {
            private class AddressDetail {
                private String city;
                private int city_code;
                private String province;

                public String getCity() {
                    return city;
                }

                public void setCity(String city) {
                    this.city = city;
                }

                public int getCity_code() {
                    return city_code;
                }

                public void setCity_code(int city_code) {
                    this.city_code = city_code;
                }

                public String getProvince() {
                    return province;
                }

                public void setProvince(String province) {
                    this.province = province;
                }
            };
            private class Point {
                private String x;
                private String y;

                public String getX() {
                    return x;
                }

                public void setX(String x) {
                    this.x = x;
                }

                public String getY() {
                    return y;
                }

                public void setY(String y) {
                    this.y = y;
                }
            }
            private String address;
            private AddressDetail address_detail;
            private Point point;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public AddressDetail getAddress_detail() {
                return address_detail;
            }

            public void setAddress_detail(AddressDetail address_detail) {
                this.address_detail = address_detail;
            }

            public Point getPoint() {
                return point;
            }

            public void setPoint(Point point) {
                this.point = point;
            }
        };
        private String address;
        private Content content;
        private int status;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Content getContent() {
            return content;
        }

        public void setContent(Content content) {
            this.content = content;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    private class IpInfo {
        String country;
        String city;
        String isp;
        String area;
        String region;

        public String getCountry() {
            return country;
        }

        public String getCity() {
            return city;
        }

        public String getIsp() {
            return isp;
        }

        public String getArea() {
            return area;
        }

        public String getRegion() {
            return region;
        }
    }

    private class TaobaoReturn {
        int code;
        IpInfo data;

        public int getCode() {
            return code;
        }

        public IpInfo getData() {
            return data;
        }
    }

    private static Gson gson = new Gson();
    private static IpLocationInfo GetIpLocationBybaidu(String ip) {
        String baidu_url = baidu_base_url + "&ip=" + ip + "&coor=bd09ll";
        String taobao_url = taobao_base_url + "?ip=" + ip;
        IpLocationInfo ipLocationInfo = null;
        //创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

        HttpGet httpGet = new HttpGet(baidu_url);
        try {
            //执行get请求
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            //获取响应消息实体
            HttpEntity entity = httpResponse.getEntity();
            //响应状态
            //System.out.println("status:" + httpResponse.getStatusLine());
            //判断响应实体是否为空
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                BaiduReturn responseInfo = gson.fromJson(content, BaiduReturn.class);
                if (responseInfo.status == 0) {
                    ipLocationInfo = new IpLocationInfo();
                    ipLocationInfo.setCity(responseInfo.getContent().getAddress_detail().getCity().replace("市",""));
                    //ipLocationInfo.setCity(responseInfo.getContent().getAddress_detail().getCity());
                    ipLocationInfo.setProvince(responseInfo.getContent().getAddress_detail().getProvince());
                    ipLocationInfo.setLongitude(responseInfo.getContent().getPoint().getX());
                    ipLocationInfo.setLatitude(responseInfo.getContent().getPoint().getY());
                    ipLocationInfo.setIp(ip);
                } else {
                    System.out.println("not find ip location at baidu:" + ip);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流并释放资源
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ipLocationInfo;
    }

    private static IpLocationInfo GetIpLocationBytaobao(String ip) {
        String taobao_url = taobao_base_url + "?ip=" + ip;
        IpLocationInfo ipLocationInfo = null;
        //创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

        HttpGet httpGet = new HttpGet(taobao_url);
        try {
            //执行get请求
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            //获取响应消息实体
            HttpEntity entity = httpResponse.getEntity();
            //响应状态
            //System.out.println("status:" + httpResponse.getStatusLine());
            //判断响应实体是否为空
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                TaobaoReturn responseInfo = gson.fromJson(content, TaobaoReturn.class);
                if (responseInfo.code == 0) {
                    ipLocationInfo = new IpLocationInfo();
                    ipLocationInfo.setCountry(responseInfo.getData().getCountry());
                    ipLocationInfo.setCity(responseInfo.getData().getCity().replace("市",""));
                    //ipLocationInfo.setCity(responseInfo.getData().getCity());
                    ipLocationInfo.setProvince(responseInfo.getData().getRegion());
                    ipLocationInfo.setIp(ip);

                } else {
                    System.out.println("not find ip location at taobao:" + ip);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流并释放资源
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ipLocationInfo;
    }

    public static IpLocationInfo GetIpLocation(String ip) {
        IpLocationInfo ipLocation = null;
        ipLocation = IpLocationOnline.GetIpLocationBybaidu(ip);
        if (ipLocation == null) {
            //System.out.println("not find at baidu api,search taobao api:"+ip);
            ipLocation = IpLocationOnline.GetIpLocationBytaobao(ip);
        }
//        if (ipLocation != null) {
//            System.out.println(ipLocation.getCountry());
//            System.out.println(ipLocation.getProvince());
//            System.out.println(ipLocation.getCity());
//            System.out.println(ipLocation.getLatitude());
//            System.out.println(ipLocation.getLongitude());
//            System.out.println(ipLocation.getIp());
//        }

        return ipLocation;
    }
}

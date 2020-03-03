package com.cetc.security.ddos.controller.adapter.ovs;

import com.cetc.security.ddos.controller.adapter.Flow;
import com.cetc.security.ddos.controller.adapter.FlowConfig;
import com.cetc.security.ddos.controller.adapter.TrafficTuple;
import org.springframework.web.client.RestClientException;
import java.util.Date;

import java.util.List;

/**
 * Created by zhangtao on 2015/11/26.
 */
public class TestOvsCmd {
    public static void main(String[] args) {
        //OvsController controller = new OvsController(2, "172.16.16.199", 22, "root", "cetc");
        OvsController controller = new OvsController(2, "172.16.16.11", 22, "root", "123456");
        OvsFlowConfig ovs = new OvsFlowConfig();

        Date date1;
        Date date2;
        // display time and date using toString()
        long start= 0;

        long end = 0;
        long diff = 0;

        TrafficTuple trafficTuple = new TrafficTuple();

        try {
            FlowConfig flowConfig = new FlowConfig();
            flowConfig.setNodeSwId("s1");
            flowConfig.setIpv4Destination("43.2.2.1/32");
            flowConfig.setInputNode("4");
            flowConfig.setIpProtocol(0);
            flowConfig.setTableId(0);
            flowConfig.setPriority(499);
            //ovs.getFlowFromController(controller, flowConfig, trafficTuple);
            //date1 = new Date();
            start = System.currentTimeMillis( );
           // System.out.println("start is : " + start);
            ovs.getFlowFromTest(controller, flowConfig, trafficTuple);
            //date2 = new Date();
            end = System.currentTimeMillis( );
           // System.out.println("end is : " + end);
           // System.out.println(date2.getSeconds() - date1.getSeconds());
            diff = end - start;
            System.out.println("Difference is : " + diff);
            System.out.println("Bytes: " + trafficTuple.getBytes());
            System.out.println("Packets: " + trafficTuple.getPackets());
            System.out.println("Duration: " + trafficTuple.getDuration());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

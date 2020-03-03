package com.cetc.security.ddos.defense.main;

import com.cetc.security.ddos.common.iplocation.IpLocationInfo;
import com.cetc.security.ddos.common.iplocation.IpLocationOnline;
import com.cetc.security.ddos.persistence.AttackIpEntity;
import com.cetc.security.ddos.persistence.IpCityEntity;
import com.cetc.security.ddos.persistence.PersistenceEntry;
import com.cetc.security.ddos.persistence.service.AttackIpService;
import com.cetc.security.ddos.persistence.service.IpCityService;


import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lb on 2016/5/17.
 */
public class ProcessIpLocation {
    private boolean isStop;
    private Timer ipLocationTimer;
    private final static int SECOND_VALUE = 1000;
    private final static int FIRST_DELAY = 5 * SECOND_VALUE;
    private int timerInterval = 60 * 60 * SECOND_VALUE;

    public ProcessIpLocation() {
        ipLocationTimer = new Timer();
    }

    public void startProcess() {
        isStop = false;
        ipLocationTimer.scheduleAtFixedRate(new IpLocationTimerTask(), FIRST_DELAY, timerInterval);
    }

    public void stopProcess() {
        isStop = true;
        ipLocationTimer.cancel();
    }

    private class IpLocationTimerTask extends TimerTask {
        @Override
        public void run() {
            attackIpLocation();
        }

        protected void attackIpLocation() {
            AttackIpService attackIpService = PersistenceEntry.getInstance().getAttackIpService();
            IpCityService ipCityService = PersistenceEntry.getInstance().getIpCityService();

            List<AttackIpEntity> list = attackIpService.getAttackIpNotHandle();
            if (list == null) {
                System.out.println("attackIpLocation list is null");
                return;
            }

            AttackIpEntity attackIpEntity;
            IpLocationInfo ipLocationInfo;
            IpCityEntity ipCityEntity;
            for(Iterator i=list.iterator();i.hasNext(); ) {
                attackIpEntity = (AttackIpEntity)i.next();//获取攻击ip

                ipLocationInfo = IpLocationOnline.GetIpLocation(attackIpEntity.getIp());//根据攻击ip在线定位
                if (ipLocationInfo != null) {
                    ipCityEntity = ipCityService.getIpCity(ipLocationInfo.getCity());
                    if (ipCityEntity != null) {//城市已经存在，更新计数
                        //System.out.println("ddddd "+ipCityEntity.getCity()+ipCityEntity.getCount());
                        ipCityEntity.setCount(ipCityEntity.getCount()+1);
                        if (ipLocationInfo.getLongitude() != null
                                && !ipLocationInfo.getLongitude().equals("")) {
                            ipCityEntity.setLng(ipLocationInfo.getLongitude());
                            ipCityEntity.setLat(ipLocationInfo.getLatitude());
                        }
                        ipCityService.updateCity(ipCityEntity);
                    } else {//数据库不存在该城市，添加到数据库
                        if (ipLocationInfo.getCity() != null
                                && !ipLocationInfo.getCity().equals("")) {
                            ipCityEntity = new IpCityEntity(ipLocationInfo.getCity(),ipLocationInfo.getLongitude(),
                                    ipLocationInfo.getLatitude(),1);

                            ipCityService.addCity(ipCityEntity);
                        }
                    }

                    //设置攻击IP的处理标记位为1，表示已经处理
                    attackIpEntity.setHandled(1);
                    attackIpService.update(attackIpEntity);
                }
                System.out.println(attackIpEntity.getIp());
            }
        }
    }


    /*public static void main(String[] args) {
        attackIpLocation();


    }*/

}

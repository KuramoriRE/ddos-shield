package com.cetc.security.ddos.defense.main;

import com.cetc.security.ddos.common.utils.AntiLogger;
import com.cetc.security.ddos.common.utils.Message;
import org.apache.log4j.Logger;

/**
 * Created by zhangtao on 2015/6/12.
 */
public class DdosMain {
    private static Logger logger = AntiLogger.getLogger(DdosMain.class);

    public static void main(String[] args) {
        ProcessIpLocation pil = null;
        Defense defense = Defense.getInstance();

        try {
            defense.start();

            /* 定时向外网查询IP地址归属地 */
            pil = new ProcessIpLocation();
            pil.startProcess();

            Handle h = new Handle(defense);
            Message msg = new Message(h);
            h.setMsg(msg);

            logger.info("Launch DDoS defense success");

            msg.listen();
        } catch (Exception e) {
            logger.error("Launch DDOS defense fail:" + e.getMessage());
        }

        defense.stop();
        defense.clear();
        if (pil != null) {
            pil.stopProcess();
        }
        //c.startProcess();
        logger.info("DDoS defense is be closed");
    }
}

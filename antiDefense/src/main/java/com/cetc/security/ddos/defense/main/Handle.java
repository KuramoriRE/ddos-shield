package com.cetc.security.ddos.defense.main;

import com.cetc.security.ddos.common.type.MsgType;
import com.cetc.security.ddos.common.utils.MsgHandle;
import org.apache.log4j.Logger;
import com.cetc.security.ddos.common.utils.AntiLogger;

/**
 * Created by zhangtao on 2015/8/27.
 */
public class Handle extends MsgHandle {
    private static Logger logger = AntiLogger.getLogger(Handle.class);
    Defense df;

    public Handle(Defense df) {
        this.df = df;
    }

    public void handle(String message) {
        logger.debug("Receive message content:" + message);
        switch (Short.valueOf(message)) {
            case MsgType.MSG_CONTROLLER:
                logger.debug("Receive controller loading message.");
                df.unLoadController();
                break;
            case MsgType.MSG_NETNODE:
                logger.debug("Receive netNode loading message.");
                df.unLoadNetNode();
                break;
            case MsgType.MSG_PO:
                logger.debug("Receive project object loading message.");
                df.unLoadPO();
                break;
            case MsgType.MSG_RESTART:
                logger.debug("Receive restart message.");
                logger.info("DDoS defense is restarting");
                df.stop();
                try {
                    df.start();
                } catch (Exception e) {
                    logger.error("Restart DDoS defense fail");
                    break;
                }

                logger.info("DDoS defense is restarted completely");
                break;
            case MsgType.MSG_CLEAN_DEV:
                df.unLoadCleanDev();
                break;
            case MsgType.MSG_CLOSE:
                logger.debug("Receive project object loading message.");
                msg.disconnect();
                break;
            default:
                logger.debug("Unknown message.");
                break;
        }

    }
}

package com.cetc.security.ddos.controller.adapter;

import com.cetc.security.ddos.common.type.ControllerType;
import com.cetc.security.ddos.controller.adapter.odlhelium.OdlHeliumController;
import com.cetc.security.ddos.controller.adapter.odllithium.OdlLithiumController;

import com.cetc.security.ddos.controller.adapter.ovs.OvsController;
import org.apache.log4j.Logger;
import com.cetc.security.ddos.common.utils.AntiLogger;

/**
 * Created by zhangtao on 2015/7/24.
 */
public class ControllerFactory {
    private static Logger logger = AntiLogger.getLogger(ControllerFactory.class);

    public static Controller getControllerInstance(int id, ControllerType type, String ip, int port, String user, String passw) {
        Controller c = null;
        switch (type) {
            case ODL_HELIUM:
                c = new OdlHeliumController(id, ip, port, user, passw);
                logger.debug("Get ODL Helium controller.");
                break;
            case ODL_LITHIUM:
                c = new OdlLithiumController(id, ip, port, user, passw);
                logger.debug("Get ODL Lithium controller.");
                break;
            case UNINET:
                logger.debug("Get ODL uninet controller.");
                break;
            case  SSH_OVS:
                logger.debug("SSH + Open vSwitch");
                c = new OvsController(id, ip, port, user, passw);
                break;
            default:
                break;
        }

        return c;
    }
}

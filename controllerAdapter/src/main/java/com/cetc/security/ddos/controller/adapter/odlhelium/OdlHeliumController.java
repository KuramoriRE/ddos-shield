package com.cetc.security.ddos.controller.adapter.odlhelium;

import com.cetc.security.ddos.controller.adapter.*;
import com.cetc.security.ddos.controller.adapter.FlowNodeInVentorys.*;
import com.cetc.security.ddos.controller.adapter.odl.OdlController;
import com.fasterxml.jackson.core.type.TypeReference;

import org.apache.log4j.Logger;
import com.cetc.security.ddos.common.utils.AntiLogger;

/**
 * Created by hasen on 2015/8/7.
 */
public class OdlHeliumController extends OdlController {
    private static Logger logger = AntiLogger.getLogger(OdlHeliumController.class);

    public OdlHeliumController(int id, String ip, int port, String user, String passwd) {
        super(id, ip, port, user, passwd);
    }
}

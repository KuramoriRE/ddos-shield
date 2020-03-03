package com.cetc.security.ddos.controller.adapter.odllithium;

import com.cetc.security.ddos.controller.adapter.FlowConfig;
import com.cetc.security.ddos.controller.adapter.TrafficTuple;
import com.cetc.security.ddos.controller.adapter.odl.OdlController;
import com.cetc.security.ddos.controller.adapter.FlowIdStatistic;
import com.fasterxml.jackson.core.type.TypeReference;

import org.apache.log4j.Logger;
import com.cetc.security.ddos.common.utils.AntiLogger;

/**
 * Created by hasen on 2015/8/7.
 */
public class OdlLithiumController extends OdlController {

    private static Logger logger = AntiLogger.getLogger(OdlLithiumController.class);

    public OdlLithiumController(int id, String ip, int port, String user, String passwd) {
        super(id, ip, port, user, passwd);
    }

/*********************************************************************************************************************/
    /* Append: {constUrlPrefix}/node/{nodeId}/table/{table id}/flow/{flow id}/opendaylight-flow-statistics:flow-statistics
   * constUrlPrefix = restconf/operational/opendaylight-inventory:nodes/
   *
   */
/*
    public StringBuilder constructRequireFlowStatsUrlByFlowId(String nodeId, int tableId,int flowId) {
        String httpUrl= "http://";
        String restconfUrl= "restconf/operational/opendaylight-inventory:nodes";
        String flowStatisticUrl = "opendaylight-flow-statistics:flow-statistics";

        StringBuilder urlPrefix = new StringBuilder();
        urlPrefix.append(httpUrl);
        urlPrefix.append(this.getControllerIp());
        urlPrefix.append(":");
        urlPrefix.append(Integer.toString(this.getControllerPort()));
        urlPrefix.append("/");
        urlPrefix.append(restconfUrl);
        urlPrefix.append("/node/");
        urlPrefix.append(nodeId);
        urlPrefix.append("/table/");
        urlPrefix.append(Integer.toString(tableId));
        urlPrefix.append("/flow/");
        urlPrefix.append(Integer.toString(flowId));
        urlPrefix.append("/");
        urlPrefix.append(flowStatisticUrl);
        logger.debug(urlPrefix.toString());
        return urlPrefix;
    }

    public  StringBuilder constructRequireFlowStatsRestApi(String nodeId, int tableId, int flowId) {
        StringBuilder stringUrl;

        stringUrl = constructRequireFlowStatsUrlByFlowId(nodeId, tableId, flowId);

        return stringUrl;
    }*/
/******************************************************************************************************************/

}

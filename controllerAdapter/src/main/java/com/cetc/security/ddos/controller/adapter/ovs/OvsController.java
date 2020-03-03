package com.cetc.security.ddos.controller.adapter.ovs;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import com.cetc.security.ddos.controller.adapter.*;
import com.cetc.security.ddos.controller.adapter.common.ChangeOutPort;
import com.cetc.security.ddos.controller.adapter.common.MeterMapFlow;
import com.cetc.security.ddos.controller.adapter.common.SwitchMeterIDs;
import com.cetc.security.ddos.controller.adapter.odl.InstructionConfig.Instruction;
import com.cetc.security.ddos.controller.adapter.odl.OdlFlowConfig;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cetc.security.ddos.common.utils.AntiLogger;

public class OvsController extends Controller {

    //List<OdlFlowConfig> odlFlows;
    private Map<Integer, OvsFlowConfig> ovsFlows;
    //private OvsFlowConfig ovsFlow;
    
    //private Session sess = null;
    private Connection conn = null;
    
    List<SwitchMeterIDs> lSwitchMeterIDs = new ArrayList<SwitchMeterIDs>();

    List<ChangeOutPort> lChangeOutPort = new ArrayList<ChangeOutPort>();


    private static Logger logger = AntiLogger.getLogger(OvsController.class);


    public OvsController(int id, String ip, int port, String user, String passwd) {
        super(id, ip, port, user, passwd);
        init();
    }

    public void init() {
        ovsFlows = new HashMap<Integer, OvsFlowConfig>();
        //ovsFlow = new OvsFlowConfig();
    }


    private void connect() throws IOException {
        conn = new Connection(this.getControllerIp(), this.getControllerPort());
        conn.connect();

        boolean isAuthenticated = conn.authenticateWithPassword(this.getUser(), this.getPasswd());
        if (isAuthenticated == false) {
            conn.close();
            conn = null;
            throw new IOException("Authentication failed.");
        }


    }

    protected Session exec(String cmd) throws IOException {
        try {
            if (conn == null) {
                connect();
            }

            Session sess = conn.openSession();
            sess.execCommand(cmd);
            return sess;
        } catch (IOException e) {
            conn.close();
            throw e;
        }

    }

    public Session execCmd(String cmd) throws IOException {
         Session s = null;

          try {
              s = exec(cmd);
              return s;
        } catch (IOException e) {
            try {
                s = exec(cmd);
                return s;
            } catch (IOException e1) {
                logger.warn("Connect " + this.getControllerIp() + " fail:" + e1.getMessage());
                throw e1;
            }
        }
    }

    public void closeCmd(Session sess) {
        if (sess == null) {
            return;
        }

        sess.close();
    }


    public <T> T getFromController(String url, TypeReference<?> typeRef) throws RestClientException {
        T t = null;

        return t;
    }

    public void getAllFlowInfo(List<FlowConfig> flows, String nodeId, int tableId) throws Exception {

    }

    public void printFlowNodeInVentorys(FlowNodeInVentorys flowNodes) {
        for (FlowNodeInVentorys.FlowNodeInVentory flowNode : flowNodes.getFlows()) {
            logger.debug("table_id:" + flowNode.getTableId());
            logger.debug("packetCount:"
                    + flowNode.getAggregateFlowStatistics().getPacketCount());
            logger.debug("byteCount:"
                    + flowNode.getAggregateFlowStatistics().getByteCount());
            logger.debug("flowCount:"
                    + flowNode.getAggregateFlowStatistics().getFlowCount());

            for (Flow flow : flowNode.getFlow()) {
                logger.debug("	flow_id:" + flow.getFlowId());
                if (flow.getMatch()!= null) {
                    logger.debug("		in_port:" + flow.getMatch().getInPort());
                    logger.debug("		ipv4_destiantion:" + flow.getMatch().getIpv4Destination());
                    if (flow.getMatch().getIpmatch() != null) {
                        logger.debug("		protocol:" + flow.getMatch().getIpmatch().getIpProtocol());
                    }
                }

                logger.debug("		Priority:" + flow.getPriority());
                logger.debug("		table_id:" + flow.getTableId());
                logger.debug("		Idle_timeout:" + flow.getIdleTimeout());
                logger.debug("		Hard_timeout:" + flow.getHardTimeout());
                logger.debug("		cookie:" + flow.getCookie());

                logger.debug("		ByteCount:" + flow.getFlowStatistics().getByteCount());
                logger.debug("		Second:" + flow.getFlowStatistics().getDuration().getSecond());
                logger.debug("		NanoSecond:" + flow.getFlowStatistics().getDuration().getNanosecond());
            }
        }
    }



    public void putFlowInfo(FlowConfig flow) throws Exception {
    	
    	OvsFlowConfig ovsFlowtmp =new OvsFlowConfig();
    	
    	ovsFlowtmp.PutFlowToController(this, flow);
        
        ovsFlows.put(flow.getFlowId(), ovsFlowtmp);
    }

    public void delFlowInfo(FlowConfig flow) throws Exception {

    	if (ovsFlows.get(flow.getFlowId()) != null)
    	{
    		ovsFlows.get(flow.getFlowId()).delFlowFromCotroller(this, flow);
            ovsFlows.remove(flow.getFlowId());
    	}
    	else
    	{
            OvsFlowConfig ovsFlow = new OvsFlowConfig();
            ovsFlow.delFlowFromCotroller(this, flow);
    	}

    }
    
    public void putFirstFlow(int in_port) throws Exception {
    	OvsFlowConfig ovsFlowtmp =new OvsFlowConfig();
    	ovsFlowtmp.putFirstFlowToController(this,in_port);
   
    }
    
    ChangeOutPort getNousedChangePort() {
        ChangeOutPort pRet = null;

        for (ChangeOutPort tmp : lChangeOutPort) {
            if (tmp.isUsed() == false) {
                pRet = tmp;
                break;
            }
        }

        return pRet;
    }

    ChangeOutPort getPortInfoByFlowID(int flowID) {
        ChangeOutPort pRet = null;

        for (ChangeOutPort tmp : lChangeOutPort) {
            if (tmp.getFlowID() == flowID) {
                pRet = tmp;
                break;
            }
        }

        return pRet;
    }
    
    void updateOutPort(int flowID, String oldOutPort, String newOutPort) {
        ChangeOutPort outPort = null;

        outPort = getNousedChangePort();

        if (null == outPort) {
            outPort = new ChangeOutPort();
            outPort.setFlowID(flowID);
            outPort.setOldOutPort(oldOutPort);
            outPort.setNewOutPort(newOutPort);
            outPort.setUsed(true);

            this.lChangeOutPort.add(outPort);
        } else {
            outPort.setFlowID(flowID);
            outPort.setOldOutPort(oldOutPort);
            outPort.setNewOutPort(newOutPort);
            outPort.setUsed(true);
        }
    }


    String setOutPortNoUsed(int flowID) {
        ChangeOutPort outPort = null;

        outPort = getPortInfoByFlowID(flowID);

        if (null != outPort) {
            outPort.setUsed(false);

            return outPort.getOldOutPort();
        }

        return null;
    }

    public void BindMeter(int meterId, FlowConfig flow) throws Exception {
        OvsFlowConfig ovsFlow = new OvsFlowConfig();
    	ovsFlow.BindMeterForFlow(this, flow, meterId);
    }

    public void UnBindMeter(int meterId, FlowConfig flow) throws Exception {
        OvsFlowConfig ovsFlow = new OvsFlowConfig();
    	ovsFlow.unBindMeterForFlow(this, flow, meterId);
    	  
    }

    SwitchMeterIDs getSwitchMeterIDs(String nodeId) {
        SwitchMeterIDs pRet = null;

        for (SwitchMeterIDs tmp : lSwitchMeterIDs) {
            if (tmp.getNodeId() == nodeId) {
                pRet = tmp;
                break;
            }
        }

        return pRet;
    }
    
    int getAvailableMeterID(String nodeId, int flowID) {
        MeterMapFlow pRet = null;
        int tmpmeterID = -1;
        SwitchMeterIDs pSwitchMeterID = null;

        pSwitchMeterID = getSwitchMeterIDs(nodeId);

        if (null == pSwitchMeterID) {
            pSwitchMeterID = new SwitchMeterIDs();
            pSwitchMeterID.setNodeId(nodeId);
            pSwitchMeterID.setMaxMeter(100);  //fix me

            pRet = new MeterMapFlow();
            pRet.setMeterID(1);
            pRet.setFlowID(flowID);
            pRet.setBinded(true);
            pSwitchMeterID.lMeterMapFlows.add(pRet);

            tmpmeterID = 1;

            for (int i = 2; i <= pSwitchMeterID.getMaxMeter(); i++) {
                MeterMapFlow meterMapFlow = new MeterMapFlow();
                meterMapFlow.setMeterID(i);
                meterMapFlow.setFlowID(-1);
                meterMapFlow.setBinded(false);
                pSwitchMeterID.lMeterMapFlows.add(meterMapFlow);
            }

            this.lSwitchMeterIDs.add(pSwitchMeterID);
        } else {
            pRet = pSwitchMeterID.getAvialMeter();

            if (null != pRet) {
                pRet.setFlowID(flowID);
                pRet.setBinded(true);
                tmpmeterID = pRet.getMeterID();
            }

        }

        return tmpmeterID;
    }
    
    void setMeterIDunBind(int FlowId, String nodeId) {
        MeterMapFlow pRet = null;
        SwitchMeterIDs pSwitchMeterID = null;

        pSwitchMeterID = getSwitchMeterIDs(nodeId);

        if (null != pSwitchMeterID) {

            pRet = pSwitchMeterID.getMeterwithFlowID(FlowId);

            if (null != pRet) {
                pRet.setFlowID(-1);
                pRet.setBinded(false);
            }
        }
    }


    public int AddMeter(int FlowId, double kbps, double pbps, String nodeId) throws Exception {
        String url;
        String meterJson;
        int meterID = FlowId;
        int iKbps = (int) kbps;
        int iPbps = (int) pbps;
        String cmd="";

        meterID = getAvailableMeterID(nodeId, FlowId);
        if (-1 == meterID) {
            return meterID;
        }
    
        cmd = "ovs-ofctl add-meter" + nodeId + "meter=" + meterID +", kbps, band=type=drop,rate=" +iKbps;
        OvsFlowConfig ovsFlow = new OvsFlowConfig();
		ovsFlow.dealOvsCmd(this, cmd);
        
        return meterID;
    }

    public void DelMeter(int FlowId, String nodeId) {
    	setMeterIDunBind(FlowId, nodeId);        
    }
    
    public void AMSinterface(FlowConfig flow, String outPut)  throws Exception {
        flow.setOutputNode(outPut);
        
        OvsFlowConfig ovsFlowtmp =new OvsFlowConfig();
    	
    	ovsFlowtmp.PutFlowToController(this, flow);
    	
    	ovsFlows.remove(flow.getFlowId());
        ovsFlows.put(flow.getFlowId(), ovsFlowtmp);
    }


    public void startGuideFlow(FlowConfig flow, String oldOutPort, String newOutPort) throws Exception {
        int flowID;
        
        flowID = flow.getFlowId();
        AMSinterface(flow, newOutPort);
        updateOutPort(flowID, oldOutPort, newOutPort);
    }

    public void endGuideFlow(FlowConfig flow) throws Exception {
        int flowID;
        
        String oldOutPort = null;
        flowID = flow.getFlowId();

        oldOutPort = setOutPortNoUsed(flowID);

        if (null != oldOutPort) {
            AMSinterface(flow, oldOutPort);
        }
    }

    public void fillTrafficTuple(FlowStatistic flowstat, TrafficTuple trafficTuple) {
        trafficTuple.setBytes(Double.valueOf(flowstat.getByteCount()));

        trafficTuple.setPackets(Double.valueOf(flowstat.getPacketCount()));

        trafficTuple.setDuration(flowstat.getDuration().getSecond());
    }

    public TrafficTuple getFlowStats(FlowConfig fc, TrafficTuple trafficTuple) throws Exception {
        OvsFlowConfig ovsFlow = new OvsFlowConfig();
        trafficTuple = ovsFlow.getFlowFromController(this, fc, trafficTuple);

        return trafficTuple;
    }
    
    public void getAllTraffic(FirstTrafficTuple firstFlow, SecondTrafficTuple secondFlow, ThirdTrafficTuple thirdFlow) throws Exception {
    
    	OvsFlowConfig ovsFlow = new OvsFlowConfig();	
    	ovsFlow.getAllTrafficInfo(this,firstFlow,secondFlow,thirdFlow);
    	return;
    }


    public void printFlow(Flow flow) {
        logger.debug("flow_id:" + flow.getFlowId());
        logger.debug("	Priority:" + flow.getPriority());
        logger.debug("	table_id:" + flow.getTableId());
        logger.debug("	Idle_timeout:" + flow.getIdleTimeout());
        logger.debug("	Hard_timeout:" + flow.getHardTimeout());
        logger.debug("	cookie:" + flow.getCookie());
        logger.debug("	PacketCount:" + flow.getFlowStatistics().getPacketCount());
        logger.debug("	ByteCount:" + flow.getFlowStatistics().getByteCount());
        logger.debug("	Second:" + flow.getFlowStatistics().getDuration().getSecond());
        logger.debug("	NanoSecond:" + flow.getFlowStatistics().getDuration().getNanosecond());
    }

    public void printFlowIdInFlowNode(FlowNodeInVentorys flowNodes) {
        for (FlowNodeInVentorys.FlowNodeInVentory flowNode : flowNodes.getFlows()) {
            for (Flow flow : flowNode.getFlow()) {
                this.printFlow(flow);
            }
        }
    }

}


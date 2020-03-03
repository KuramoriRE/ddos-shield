package com.cetc.security.ddos.controller.adapter.odl;

import com.cetc.security.ddos.controller.adapter.*;
import com.cetc.security.ddos.controller.adapter.common.ChangeOutPort;
import com.cetc.security.ddos.controller.adapter.common.MeterMapFlow;
import com.cetc.security.ddos.controller.adapter.common.SwitchMeterIDs;
import com.cetc.security.ddos.controller.adapter.odl.InstructionConfig.Instruction;
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

public abstract class OdlController extends Controller {

    public RestTemplate restTemplate;
    public ObjectMapper fasterxmlObjMapper;

    //List<OdlFlowConfig> odlFlows;
    private Map<Integer, OdlFlowConfig> odlFlows;

    List<SwitchMeterIDs> lSwitchMeterIDs = new ArrayList<SwitchMeterIDs>();

    List<ChangeOutPort> lChangeOutPort = new ArrayList<ChangeOutPort>();

    private static Logger logger = AntiLogger.getLogger(OdlController.class);

    /*odl controller common interface start*/
    public enum OdlVersion {
        HELIUM, //H version
        LITHIUM,//L version
    }

    public OdlVersion getOdlversion() {
        return odlversion;
    }

    public void setOdlversion(OdlVersion odlversion) {
        this.odlversion = odlversion;
    }

    private OdlVersion odlversion;

    public OdlController(int id, String ip, int port, String user, String passwd) {
        super(id, ip, port, user, passwd);
        init();
    }
    
    public void getAllTraffic(FirstTrafficTuple firstFlow, SecondTrafficTuple secondFlow, ThirdTrafficTuple thirdFlow) throws Exception {
        return;
    }

    public void init() {
        AuthScope authScope = new AuthScope(controllerIp, controllerPort, AuthScope.ANY_REALM);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, passwd);

        RestTemplateFactory.INSTANCE.setInsecureSsl(true);

        try {
            restTemplate = RestTemplateFactory.INSTANCE.createRestTemplate(authScope, credentials);
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block
            logger.error("KeyManagementException:" + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            logger.error("NoSuchAlgorithmException:" + e.getMessage());
            e.printStackTrace();
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            logger.error("KeyStoreException:" + e.getMessage());
            e.printStackTrace();
        }

        fasterxmlObjMapper = new ObjectMapper();
        fasterxmlObjMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignore unknownfields
        fasterxmlObjMapper.setSerializationInclusion(Include.NON_NULL);

        odlFlows = new HashMap<Integer, OdlFlowConfig>();
        OdlVersion odlversion = OdlVersion.HELIUM;
        this.setOdlversion(odlversion);
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ObjectMapper getFasterxmlObjMapper() {
        return fasterxmlObjMapper;
    }

    public void setFasterxmlObjMapper(ObjectMapper fasterxmlObjMapper) {
        this.fasterxmlObjMapper = fasterxmlObjMapper;
    }

    /*function 1--put, 2--delelte, 3--get*/
    public void restApiInterface(String ApiUrl, String ApiJsonStr, int function) throws RestClientException {
        if (function == 1) {
            try {
                logger.debug("Caller: " + "Put URL: " + ApiUrl);
                HttpEntity<String> entity = buildHttpEntityFromObject(ApiJsonStr);
                restTemplate.put(ApiUrl, entity);
            } catch (RestClientException e) {
                logger.error("Failed to put to controller " + e);
                throw e;
            }
        } else if (function == 2) {
            try {
                logger.debug("Caller: " + "Put URL: " + ApiUrl);
                restTemplate.delete(ApiUrl);
            } catch (RestClientException e) {
                logger.error("Failed to delete to controller " + e);
                throw e;
            }
        }
    }

    public void deleteRestApi(String ApiUrl) throws RestClientException
    {
    	HttpHeaders httpHeaders =  buildHttpHeaders();
	       try {
	       restTemplate.exchange(ApiUrl, HttpMethod.DELETE, new HttpEntity<Object>(httpHeaders), String.class);
	       } catch (RestClientException e) {
               logger.error("Failed to delete to controller " + e);
               throw e;
           }
    }


    //TODO: organize and generalize (use ResponseEntity<T>, and factor out authorization header creation).
    public HttpHeaders buildHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = user + ":" + passwd;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders.set("Authorization", authHeader);


        return httpHeaders;
    }

    public <T> T getFromController(String url, TypeReference<?> typeRef) throws RestClientException {
        T t = null;
        try {
            HttpHeaders httpHeaders = buildHttpHeaders();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
                    new HttpEntity<Object>(httpHeaders), String.class);

            String responseStr = response.getBody();
            System.out.println(responseStr);
            if (responseStr == null) {
                return null;
            }

            t = fasterxmlObjMapper.readValue(responseStr, typeRef);
        } catch (Throwable e) {
            logger.error("Failed to get flow statistics from controller " + this.getControllerIp(), e);
            throw new RestClientException(e.getMessage());
        }

        return t;
    }

    public HttpEntity<String> buildHttpEntityFromObject(String aJsonStr) {
        HttpHeaders headers = buildHttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(aJsonStr, headers);

        return entity;
    }

	/*odl controller common interface end*/

    public StringBuilder constructRequireGetFlow(String nodeId, int tableId) {
        String httpUrl= "http://";
        String restconfUrl= "restconf/operational/opendaylight-inventory:nodes";

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
        urlPrefix.append(tableId);
        urlPrefix.append("/");
        return urlPrefix;
    }

    public TrafficTuple getFlowStats(FlowConfig fc, TrafficTuple trafficTuple) throws Exception {

        try {
            StringBuilder urlPrefix = this.constructRequireGetFlow(fc.getNodeSwId(),
                    fc.getTableId());

            TypeReference<?> typeRef = new TypeReference<FlowNodeInVentorys>(){};
            FlowNodeInVentorys flowstat = this.getFromController(urlPrefix.toString(), typeRef);
            if (flowstat == null){
                logger.error("Failed to get flow statistic from controller: " + this.getControllerIp());
                throw new Exception("Failed to get flow statistic from controller: " +
                        this.getControllerIp());
            }

            FlowStatistic flowStatistic;
            flowStatistic = this.checkFlowInFlowNode(flowstat, fc);
            if (flowStatistic == null) {
                logger.error("Can't find flow-id " + fc.getFlowId() + " flow statistic");
                throw new Exception("Failed to get flow statistic from controller: "
                        + this.getControllerIp() + " for flow id: " + fc.getFlowId());
            }

            this.fillTrafficTuple(flowStatistic, trafficTuple);

            return trafficTuple;
        } catch (Throwable e) {
            String msg = "Excepted trying to getFlowStats for " + fc.getFlowId() + " from controller: "
                    + this.getControllerIp();
            logger.error(msg);
            throw new Exception(msg, e);
        }
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

    public FlowStatistic checkFlowInFlowNode(FlowNodeInVentorys flowNodes, FlowConfig fc) {
        printFlowNodeInVentorys(flowNodes);

        for (FlowNodeInVentorys.FlowNodeInVentory flowNode : flowNodes.getFlows()) {
            logger.debug("Read table id: " + flowNode.getTableId());
            logger.debug("Current table id: " + fc.getTableId());
            if (flowNode.getTableId() == fc.getTableId()) {
                for (Flow flow : flowNode.getFlow()) {
                    if (flow.getMatch() == null) {
                        continue;
                    }

                    if ((fc.getInputNode() != null) && !fc.getInputNode().equals("")) {
                        String inPort = fc.getNodeSwId() + ":" + fc.getInputNode();
                        if ((flow.getMatch().getInPort() == null)
                                || (!flow.getMatch().getInPort().equals(inPort))) {
                            continue;
                        }
                    }

                    if ((flow.getMatch().getIpv4Destination() == null)
                            || (!flow.getMatch().getIpv4Destination().equals(fc.getIpv4Destination()))) {
                        continue;
                    }


                    /*
                    if (flow.getMatch().getIpmatch() == null) {
                        continue;
                    }
                    */

                    if ((fc.getIpProtocol() == 0)
                            &&  (flow.getMatch().getIpmatch() == null)) {

                    } else {
                        if ((flow.getMatch().getIpmatch() == null)
                                || (flow.getMatch().getIpmatch().getIpProtocol() != fc.getIpProtocol())) {
                            continue;
                        }
                    }

                    /*
                    if ((fc.getIpProtocol() != 0)
                            && ((flow.getMatch().getIpmatch() == null)
                                || (flow.getMatch().getIpmatch().getIpProtocol() != fc.getIpProtocol()))) {
                        continue;
                    }
                    */

                    logger.debug("Find table:" + flowNode.getTableId() + ", PN:" + flow.getMatch().getIpv4Destination() +
                                "flow id:" + flow.getFlowId());
                    return flow.getFlowStatistics();
                }
            }
        }

        logger.debug("Can't Find table:" + fc.getTableId() + ", PN:" + fc.getIpv4Destination() +
                "Protocol:" + fc.getIpProtocol());
        return null;
    }


    /*flow manage*/
    public void getAllFlowInfo(List<FlowConfig> flows, String nodeId, int tableId) throws RestClientException
    {
    	StringBuilder urlPrefix = constructRequireGetFlow(nodeId,tableId);

    	try {

			TypeReference<?> typeRef = new TypeReference<OdlGetFlowType>(){};

			OdlGetFlowType OdlAllFlows = getFromController(urlPrefix.toString(), typeRef);

			if (OdlAllFlows == null)
			{
				return;
			}
			for(OdlFlowConfig.Flow f:OdlAllFlows.getFlowNodes().get(0).getFlow())
			{
				FlowConfig oneflow = new FlowConfig();

				oneflow.setFlowId(f.getId());
				oneflow.setIpProtocol(f.getMatchInfo().getIpMatch().getIpProtocol());
				oneflow.setIpv4Destination(f.getMatchInfo().getIpv4Destination());

				flows.add(oneflow);
			}

		} catch (Throwable e) {
			logger.error("Failed to get flow from controller " + e);
		}

    }

    public void putFlowInfo(FlowConfig flow) throws RestClientException {
        OdlFlowConfig odlFlow = new OdlFlowConfig();
        odlFlow.createFlow(flow);

        odlFlow.PutFlowToController(this, odlFlow, flow);
        odlFlows.put(flow.getFlowId(), odlFlow);
    }

    public void delFlowInfo(FlowConfig flow) throws RestClientException {

    	if (odlFlows.get(flow.getFlowId()) != null)
    	{
    		odlFlows.get(flow.getFlowId()).delFlowFromCotroller(this, flow);
            odlFlows.remove(flow.getFlowId());
    	}
    	else
    	{
    		OdlFlowConfig oldFlow = new OdlFlowConfig();
    		oldFlow.delFlowFromCotroller(this, flow);
    	}

    }
    
public void putFirstFlow(int in_port) throws Exception {
    		
    }

    public void AMSinterface(FlowConfig flow, String outPut) throws RestClientException {
        OdlFlowConfig odlFlowTmp;
        Instruction instructionTmp = null;
        int i;

        flow.setOutputNode(outPut);

        odlFlowTmp = odlFlows.get(flow.getFlowId());

        for (i = 0; i < odlFlowTmp.flow.get(0).getInstrInfo().instruction.size(); i++) {
            instructionTmp = odlFlowTmp.flow.get(0).getInstrInfo().instruction.get(i);
            if (instructionTmp.getApplyActions() != null) {
                break;
            }
        }

        if (instructionTmp != null) {
            instructionTmp.getApplyActions().getAction().get(0).updateOutputAction(flow);
            odlFlowTmp.PutFlowToController(this, odlFlowTmp, flow);
        }

    }

    public void BindMeter(int meterId, FlowConfig flow) throws RestClientException {

        OdlFlowConfig odlFlowTmp;

        odlFlowTmp = odlFlows.get(flow.getFlowId());

        InstructionConfig.Instruction instructionMeter = new InstructionConfig.Instruction();
        instructionMeter.setOrder(odlFlowTmp.flow.get(0).getInstrInfo().instruction.get(0).getOrder() + 1);
        instructionMeter.createMeter(meterId);

        odlFlowTmp.flow.get(0).getInstrInfo().instruction.add(instructionMeter);

        odlFlowTmp.PutFlowToController(this, odlFlowTmp, flow);

    }

    public void UnBindMeter(int meterId, FlowConfig flow) throws RestClientException {

        OdlFlowConfig odlFlowTmp;
        int meterFlag = 0;
        int i = 0;

        odlFlowTmp = odlFlows.get(flow.getFlowId());

        InstructionConfig instructions = odlFlowTmp.flow.get(0).getInstrInfo();

        for (i = 0; i < instructions.instruction.size(); i++) {
            if ((instructions.instruction.get(i).meterCase != null)
                    && (instructions.instruction.get(i).meterCase.getMeterId() == meterId)) {
                instructions.instruction.remove(i);
                meterFlag = 1;
            }
        }

        if (meterFlag == 0) {
            logger.info("There is no meter action for flow id " + Integer.toString(flow.getFlowId()));
            return;
        }

        odlFlowTmp.PutFlowToController(this, odlFlowTmp, flow);

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

    void putRestAPItoOdl(String url, String json) throws RestClientException {
        ObjectMapper fasterxmlObjMapper;

        System.out.println("url is " + url);
        System.out.println("json is " + json);

        fasterxmlObjMapper = this.getFasterxmlObjMapper();

        this.restApiInterface(url, json, 1); /*1--put function, 2--delete function*/

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


    public int AddMeter(int FlowId, double kbps, double pbps, String nodeId) throws RestClientException {
        String url;
        String meterJson;
        int meterID = FlowId;
        int iKbps = (int) kbps;
        int iPbps = (int) pbps;

        meterID = getAvailableMeterID(nodeId, FlowId);

        if (-1 == meterID) {
            return meterID;
        }

        BuildMeters buildMeters = new BuildMeters("meter-kbps", meterID, iKbps);

        meterJson = buildMeters.BuildMetersJson();

        logger.debug("meter json is " + meterJson);

        url = "http://" + this.getControllerIp() + ":" + Integer.toString(this.getControllerPort()) + "/restconf/config/opendaylight-inventory:nodes/node/" + nodeId + "/flow-node-inventory:meter/" + meterID;

        logger.debug("meter url is " + url);

        putRestAPItoOdl(url, meterJson);

        return meterID;
    }

    public void DelMeter(int FlowId, String nodeId) {
        setMeterIDunBind(FlowId, nodeId);
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


    public void startGuideFlow(FlowConfig flow, String oldOutPort, String newOutPort) throws RestClientException {
        int flowID;

        flowID = flow.getFlowId();
        AMSinterface(flow, newOutPort);
        updateOutPort(flowID, oldOutPort, newOutPort);
    }

    public void endGuideFlow(FlowConfig flow) throws RestClientException {
        int flowID;

        String oldOutPort = null;
        flowID = flow.getFlowId();

        oldOutPort = setOutPortNoUsed(flowID);

        if (null != oldOutPort) {
            AMSinterface(flow, oldOutPort);
        }

    }


    public void printOdlControllerInfo() {
        logger.debug("======================");
        logger.debug("contrl info: " + "ip:" + controllerIp + "port:" + controllerPort);
        logger.debug("flow info:");
        //Iterator<Map.Entry<Integer, OdlFlowConfig>> it = odlFlows.entrySet().iterator();

        for (Map.Entry<Integer, OdlFlowConfig> entry : odlFlows.entrySet()) {
        //while (it.hasNext()) {
            //Map.Entry<Integer, OdlFlowConfig> entry = it.next();
            logger.debug("------------------------------");
            logger.debug("flow id:" + entry.getValue().getFlow().get(0).getId());
            logger.debug("flow name:" + entry.getValue().getFlow().get(0).getFlowName());
        }

    }

    public void fillTrafficTuple(FlowStatistic flowstat, TrafficTuple trafficTuple) {
        trafficTuple.setBytes(Double.valueOf(flowstat.getByteCount()));

        trafficTuple.setPackets(Double.valueOf(flowstat.getPacketCount()));

        trafficTuple.setDuration(flowstat.getDuration().getSecond());
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


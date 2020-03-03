package com.cetc.security.ddos.controller.adapter.odl;

import java.util.ArrayList;
import java.util.List;

import com.cetc.security.ddos.controller.adapter.FlowConfig;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import com.cetc.security.ddos.common.utils.AntiLogger;


public class OdlFlowConfig {
    private static Logger logger = AntiLogger.getLogger(OdlFlowConfig.class);
	List<Flow> flow;
	
	public List<Flow> getFlow() {
		return flow;
	}
	
	public void setFlow(List<Flow> flow) {
		this.flow = flow;
	}
	
	public void createFlow(FlowConfig flowInfo)
	{
		flow = new ArrayList<Flow>();
		
		Flow flow1 = new Flow(flowInfo);
		
		flow1.createInstrInfo(flowInfo);
		flow1.createMatchInfo(flowInfo);
		flow1.createStatisticInfo(flowInfo);
		
		flow.add(flow1);
	}
	
	public static final class Flow {
	private int id;
	
	/* match info*/
	@JsonProperty("match")
	private MatchConfig matchInfo;
	
	/* instruction info */
	@JsonProperty("instructions")
	private InstructionConfig instrInfo;
	
	/* flow statistic */
	@JsonProperty("flow-statistics")
	private FlowStatistics statisticInfo;
	
	private int priority;
    
    @JsonProperty("idle-timeout")
    private int idleTimeout;
    
    @JsonProperty("hard-timeout")
    private int hardTimeout;
    
	@JsonProperty("table_id")
	private int tableId;
	
	@JsonProperty("flow-name")
	private String flowName;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public int getHardTimeout() {
		return hardTimeout;
	}

	public void setHardTimeout(int hardTimeout) {
		this.hardTimeout = hardTimeout;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public Flow(FlowConfig flowInfo)
	{
		this.id = flowInfo.getFlowId();
		this.priority = flowInfo.getPriority();
		this.tableId = flowInfo.getTableId();
		this.flowName = flowInfo.getFlowName();
		this.idleTimeout = flowInfo.getIdleTimeout();
		this.hardTimeout = flowInfo.getHardTimeout();
		
	}

	public MatchConfig getMatchInfo() {
		return matchInfo;
	}

	public void setMatchInfo(MatchConfig matchInfo) {
		this.matchInfo = matchInfo;
	}
	
	public void createMatchInfo(FlowConfig flowInfo)
	{
		matchInfo = new MatchConfig();
		
		if (flowInfo.getIpv4Destination() != null)
		{
			matchInfo.setIpv4Destination(flowInfo.getIpv4Destination());
		}
		else if(flowInfo.getIpv6Destination() != null)
		{
			matchInfo.setIpv6Destination(flowInfo.getIpv6Destination());
		}
		
		
		if (flowInfo.getInputNode()!=null && !(flowInfo.getInputNode().equals("")))
		{
			matchInfo.setInPort(flowInfo.getInputNode());
		}
		
		if (flowInfo.getInputNode()!=null && !(flowInfo.getInputNode().equals("")))
		{
			matchInfo.setInPhyPort(flowInfo.getInputphyNode());
		}
			
		if (flowInfo.getPort() != 0)
		{
			if (flowInfo.getIpProtocol() == 6)
			{
				matchInfo.setTcpDestinationPort(String.valueOf(flowInfo.getPort()));
			}
			else if (flowInfo.getIpProtocol() == 17)
			{
				matchInfo.setUdpDestinationPort(String.valueOf(flowInfo.getPort()));
			}	
		}
		
		matchInfo.createEthernetMatch(flowInfo);
		if (flowInfo.getIpProtocol() != 0)
		{
			matchInfo.createIpMatch(flowInfo);
		}
		
	}

	public InstructionConfig getInstrInfo() {
		return instrInfo;
	}

	public void setInstrInfo(InstructionConfig instrInfo) {
		this.instrInfo = instrInfo;
	}
	
	public void createInstrInfo(FlowConfig flowInfo)
	{
		instrInfo = new InstructionConfig();
		instrInfo.createInstruction(flowInfo);
	}

	public FlowStatistics getStatisticInfo() {
		return statisticInfo;
	}

	public void setStatisticInfo(FlowStatistics statisticInfo) {
		this.statisticInfo = statisticInfo;
	}
	
	public void createStatisticInfo(FlowConfig flowInfo)
	{
		statisticInfo = new FlowStatistics();
		statisticInfo.setByteCount(flowInfo.getByteCount());
		statisticInfo.setPacketCount(flowInfo.getPacketCount());
	}
	
	}
	
	/*
	 * they are function used to send flow table to the controller*/
	
	public void delFlowFromCotroller(OdlController contrl, FlowConfig flowInfo) throws RestClientException
	{
		String url;
		url = ConstructFlowUrl(contrl, flowInfo);
		logger.debug("delete flow: url is " + url);
		contrl.deleteRestApi(url);
	}
	
	public void PutFlowToController(OdlController contrl, Object object, FlowConfig flowInfo) throws RestClientException
	{
		String url;
		String jsonStrTmp;
   	    ObjectMapper fasterxmlObjMapper;
	   
        url = ConstructFlowUrl(contrl, flowInfo);
		
        logger.debug("put flow: url is " + url);
  	  
    	fasterxmlObjMapper = contrl.getFasterxmlObjMapper();
   	 
		 try {
				jsonStrTmp = fasterxmlObjMapper.writeValueAsString(object);
				logger.debug("json is " + jsonStrTmp);
			
			contrl.restApiInterface(url, jsonStrTmp, 1); /*1--put function, 2--delete function*/
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.debug("put flow fail:"+e);
		}
	}
	
	public String ConstructFlowUrl(OdlController contrl, FlowConfig flowInfo)
	{
		String urltmp;
		String restPrefix;
		StringBuilder urlPrefix;
		
		urlPrefix = constructFlowUrlPrefix(flowInfo.getNodeSwId(), flowInfo.getTableId(), flowInfo.getFlowId());
		//logger.debug("Flow Controller flow id:" + flowInfo.getFlowId());
		
		restPrefix = "http://" + contrl.getControllerIp() + ":" + Integer.toString(contrl.getControllerPort()) + "/restconf/config/opendaylight-inventory:nodes";
		urltmp = restPrefix + urlPrefix;
		
		return urltmp;	
	}
	
     public StringBuilder constructFlowUrlPrefix(String nodeId, int tablid, int flowid) {
   		StringBuilder urlPrefix = new StringBuilder();		
   		urlPrefix.append("/node");
   		urlPrefix.append("/"); urlPrefix.append(nodeId); 
   		urlPrefix.append("/table");
   		urlPrefix.append("/"); urlPrefix.append(tablid);
   		urlPrefix.append("/flow");
   		urlPrefix.append("/"); urlPrefix.append(flowid); 		
   		return urlPrefix;
   	}

}

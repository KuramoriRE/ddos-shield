package com.cetc.security.ddos.controller.adapter;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.web.client.RestClientException;

public abstract class Controller {
    private int id;
    protected String controllerIp;
    protected int controllerPort;
    protected String user;
    protected String passwd;
	private int type;

    private int detectionDeviationPercentage = 50;
    private int attackSuspicionsThreshold = 3;
    private int recoverNormalThreshold = 3;

	public Controller(int id, String ip, int port, String user, String passwd)
	{
        this.id = id;
		this.controllerIp = ip;
		this.controllerPort = port;
		this.user = user;
		this.passwd = passwd;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getControllerIp() {
		return controllerIp;
	}
	public void setControllerIp(String controllerIp) {
		this.controllerIp = controllerIp;
	}
	public int getControllerPort() {
		return controllerPort;
	}
	public void setControllerPort(int controllerPort) {
		this.controllerPort = controllerPort;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

    public int getDetectionDeviationPercentage() {
        return detectionDeviationPercentage;
    }

    public void setDetectionDeviationPercentage(int detectionDeviationPercentage) {
        this.detectionDeviationPercentage = detectionDeviationPercentage;
    }

    public int getAttackSuspicionsThreshold() {
        return attackSuspicionsThreshold;
    }

    public void setAttackSuspicionsThreshold(int attackSuspicionsThreshold) {
        this.attackSuspicionsThreshold = attackSuspicionsThreshold;
    }

    public int getRecoverNormalThreshold() {
        return recoverNormalThreshold;
    }

    public void setRecoverNormalThreshold(int recoverNormalThreshold) {
        this.recoverNormalThreshold = recoverNormalThreshold;
    }

    public abstract void getAllFlowInfo(List<FlowConfig> flows, String nodeId, int tableId) throws Exception;
    
	public abstract void putFlowInfo(FlowConfig flow) throws Exception;
	
	public abstract void delFlowInfo(FlowConfig flow) throws Exception;
	
	public abstract void putFirstFlow(int in_port) throws Exception;
	
	public abstract void BindMeter(int meterId, FlowConfig flow) throws Exception;
	
	//public abstract void UnBindMeter();
	
	public abstract int AddMeter(int FlowId, double kbps, double pbps, String nodeId) throws Exception;
	
	public abstract void DelMeter(int FlowId, String nodeId);

	public abstract void UnBindMeter(int meterId, FlowConfig flow) throws Exception;
	
	public abstract void startGuideFlow(FlowConfig flow, String oldOutPort, String newOutPort)throws Exception;
	
	public abstract void endGuideFlow(FlowConfig flow)throws Exception;

    //public abstract <T> T getFromController(String url, TypeReference<?> typeRef) throws RestClientException;

    public abstract TrafficTuple getFlowStats(FlowConfig fc, TrafficTuple trafficTuple) throws Exception;
    public abstract void getAllTraffic(FirstTrafficTuple firstFlow, SecondTrafficTuple secondFlow, ThirdTrafficTuple thirdFlow) throws Exception;
}


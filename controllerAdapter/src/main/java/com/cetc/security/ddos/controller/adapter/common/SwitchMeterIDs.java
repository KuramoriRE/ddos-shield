package com.cetc.security.ddos.controller.adapter.common;

import com.cetc.security.ddos.controller.adapter.common.*;

import java.util.ArrayList;
import java.util.List;

public class SwitchMeterIDs {
	
	public List <MeterMapFlow> lMeterMapFlows = new ArrayList<MeterMapFlow>();
	String nodeId;
	int maxMeter;
	
	public List<MeterMapFlow> getlMeterMapFlows() {
		return lMeterMapFlows;
	}
	public void setlMeterMapFlows(List<MeterMapFlow> lMeterMapFlows) {
		this.lMeterMapFlows = lMeterMapFlows;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public int getMaxMeter() {
		return maxMeter;
	}
	public void setMaxMeter(int maxMeter) {
		this.maxMeter = maxMeter;
	}
	
	
	public MeterMapFlow getAvialMeter()
    {
		MeterMapFlow pRet = null;
    	
    	for (MeterMapFlow tmp : lMeterMapFlows) {
            if (tmp.isBinded() == false) {
            	pRet = tmp;
                break;
            }
        }   	
    	
    	return pRet;
    }
	
	
	public MeterMapFlow getMeterwithFlowID(int flowID)
    {
		MeterMapFlow pRet = null;
    	
    	for (MeterMapFlow tmp : lMeterMapFlows) {
            if (tmp.getFlowID() == flowID) {
            	pRet = tmp;
                break;
            }
        }   	
    	
    	return pRet;
    }
	

}

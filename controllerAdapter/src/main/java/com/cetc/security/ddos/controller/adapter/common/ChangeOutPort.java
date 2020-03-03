package com.cetc.security.ddos.controller.adapter.common;

public class ChangeOutPort {
	
	int flowID;
	String oldOutPort;
	String newOutPort;
	boolean used;
	
	public int getFlowID() {
		return flowID;
	}
	public void setFlowID(int flowID) {
		this.flowID = flowID;
	}
	public String getOldOutPort() {
		return oldOutPort;
	}
	public void setOldOutPort(String oldOutPort) {
		this.oldOutPort = oldOutPort;
	}
	public String getNewOutPort() {
		return newOutPort;
	}
	public void setNewOutPort(String newOutPort) {
		this.newOutPort = newOutPort;
	}
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}

}

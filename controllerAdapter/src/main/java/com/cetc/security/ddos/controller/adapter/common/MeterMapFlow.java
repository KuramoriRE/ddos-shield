package com.cetc.security.ddos.controller.adapter.common;

public class MeterMapFlow {
	
	int meterID;
	int FlowID;
	boolean Binded ;
	
	public int getMeterID() {
		return meterID;
	}
	public void setMeterID(int meterID) {
		this.meterID = meterID;
	}
	public int getFlowID() {
		return FlowID;
	}
	public void setFlowID(int flowID) {
		FlowID = flowID;
	}
	public boolean isBinded() {
		return Binded;
	}
	public void setBinded(boolean binded) {
		Binded = binded;
	}

}

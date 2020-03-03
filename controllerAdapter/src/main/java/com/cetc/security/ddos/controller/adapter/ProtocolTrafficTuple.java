/**
 * Copyright (c) <2015> <ss-cetc> and others.  All rights reserved.
 *
 *
 * @author lingbo on 2015/6/18
 * @version 0.1
 */

package com.cetc.security.ddos.controller.adapter;


public class ProtocolTrafficTuple {
	int protocol;
	String dstNetwork;
	String srcNetwork;
	TrafficInformation trafficInfo;
	
	public ProtocolTrafficTuple(){
		
		trafficInfo = new TrafficInformation();
	}
	
	public int getProtocol() {
		return protocol;
	}
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	public String getDstNetwork() {
		return dstNetwork;
	}
	public void setDstNetwork(String dstNetwork) {
		this.dstNetwork = dstNetwork;
	}
	public String getSrcNetwork() {
		return srcNetwork;
	}
	public void setSrcNetwork(String srcNetwork) {
		this.srcNetwork = srcNetwork;
	}
	public TrafficInformation getTrafficInfo() {
		return trafficInfo;
	}
	public void setTrafficInfo(TrafficInformation trafficInfo) {
		this.trafficInfo = trafficInfo;
	}
	
	
	
}

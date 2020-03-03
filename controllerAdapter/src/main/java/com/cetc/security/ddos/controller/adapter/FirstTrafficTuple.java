/**
 * Copyright (c) <2015> <ss-cetc> and others.  All rights reserved.
 *
 *
 * @author lingbo on 2015/6/18
 * @version 0.1
 */

package com.cetc.security.ddos.controller.adapter;


public class FirstTrafficTuple {
	
	ProtocolTrafficTuple tcpFirstTraffic;
	ProtocolTrafficTuple udpFirstTraffic;
	ProtocolTrafficTuple icmpFirstTraffic;
	ProtocolTrafficTuple ipFirstTraffic;
	
	public FirstTrafficTuple(){
		tcpFirstTraffic = new ProtocolTrafficTuple();
		udpFirstTraffic = new ProtocolTrafficTuple();
		icmpFirstTraffic = new ProtocolTrafficTuple();
		ipFirstTraffic = new ProtocolTrafficTuple();
	}
	
	public ProtocolTrafficTuple getTcpFirstTraffic() {
		return tcpFirstTraffic;
	}
	public void setTcpFirstTraffic(ProtocolTrafficTuple tcpFirstTraffic) {
		this.tcpFirstTraffic = tcpFirstTraffic;
	}
	public ProtocolTrafficTuple getUdpFirstTraffic() {
		return udpFirstTraffic;
	}
	public void setUdpFirstTraffic(ProtocolTrafficTuple udpFirstTraffic) {
		this.udpFirstTraffic = udpFirstTraffic;
	}
	public ProtocolTrafficTuple getIcmpFirstTraffic() {
		return icmpFirstTraffic;
	}
	public void setIcmpFirstTraffic(ProtocolTrafficTuple icmpFirstTraffic) {
		this.icmpFirstTraffic = icmpFirstTraffic;
	}
	public ProtocolTrafficTuple getIpFirstTraffic() {
		return ipFirstTraffic;
	}
	public void setIpFirstTraffic(ProtocolTrafficTuple ipFirstTraffic) {
		this.ipFirstTraffic = ipFirstTraffic;
	}

	
}

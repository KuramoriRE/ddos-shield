/**
 * Copyright (c) <2015> <ss-cetc> and others.  All rights reserved.
 *
 *
 * @author lingbo on 2015/6/18
 * @version 0.1
 */

package com.cetc.security.ddos.controller.adapter;


public class PoTrafficTuple {
	
	String dstNetwork;
	long attack_bps=0;
	long attack_pps=0;
	long durtion = 0;
	long sum_packages=0;
	long sum_bytes=0;
	ProtocolTrafficTuple tcpFirstTraffic;
	ProtocolTrafficTuple udpFirstTraffic;
	ProtocolTrafficTuple icmpFirstTraffic;
	ProtocolTrafficTuple ipFirstTraffic;
	
	public PoTrafficTuple(){
		
		tcpFirstTraffic = new ProtocolTrafficTuple();
		udpFirstTraffic = new ProtocolTrafficTuple();
		icmpFirstTraffic = new ProtocolTrafficTuple();
		ipFirstTraffic = new ProtocolTrafficTuple();
		
	}
	
	
	public long getAttack_bps() {
		return attack_bps;
	}



	public void setAttack_bps(long attack_bps) {
		this.attack_bps = attack_bps;
	}



	public long getSum_packages() {
		return sum_packages;
	}


	public void setSum_packages(long sum_packages) {
		this.sum_packages = sum_packages;
	}


	public long getSum_bytes() {
		return sum_bytes;
	}


	public void setSum_bytes(long sum_bytes) {
		this.sum_bytes = sum_bytes;
	}


	public long getDurtion() {
		return durtion;
	}


	public void setDurtion(long durtion) {
		this.durtion = durtion;
	}


	public long getAttack_pps() {
		return attack_pps;
	}



	public void setAttack_pps(long attack_pps) {
		this.attack_pps = attack_pps;
	}



	public String getDstNetwork() {
		return dstNetwork;
	}
	public void setDstNetwork(String dstNetwork) {
		this.dstNetwork = dstNetwork;
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

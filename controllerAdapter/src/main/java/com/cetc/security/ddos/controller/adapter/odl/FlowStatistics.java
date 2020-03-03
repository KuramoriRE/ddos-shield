package com.cetc.security.ddos.controller.adapter.odl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlowStatistics {
	
	@JsonProperty("packet-count")
	private int packetCount;
	
	@JsonProperty("byte-count")
	private int byteCount;
	
	public int getByteCount() {
		return byteCount;
	}
	public void setByteCount(int byteCount) {
		this.byteCount = byteCount;
	}
	public int getPacketCount() {
		return packetCount;
	}
	public void setPacketCount(int packetCount) {
		this.packetCount = packetCount;
	}
}

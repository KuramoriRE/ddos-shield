package com.cetc.security.ddos.controller.adapter;
import java.nio.charset.Charset;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hasen on 2015/6/22.
 */
//@JsonRootName("aggregate-flow-statistics")
public class AggregateFlowStatistic {
	@JsonProperty("packet-count")
    public String packetCount;
    @JsonProperty("byte-count")
    public String byteCount;
    @JsonProperty("flow-count")
    public long flowCount;
	public String getPacketCount() {
		return packetCount;
	}
	public void setPacketCount(String packetCount) {
		this.packetCount = packetCount;
	}
	public String getByteCount() {
		return byteCount;
	}
	public void setByteCount(String byteCount) {
		this.byteCount = byteCount;
	}
	public long getFlowCount() {
		return flowCount;
	}
	public void setFlowCount(long flowCount) {
		this.flowCount = flowCount;
	}
        
}

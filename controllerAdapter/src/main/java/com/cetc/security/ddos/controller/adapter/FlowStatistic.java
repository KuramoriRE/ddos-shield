package com.cetc.security.ddos.controller.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hasen on 2015/6/22.
 */
public class FlowStatistic{
	@JsonProperty("packet-count")
	private String packetCount;
	
	@JsonProperty("byte-count")
	private String byteCount;

	@JsonProperty("duration")
	private Duration duration;

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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}


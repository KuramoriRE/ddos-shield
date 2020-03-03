/**
 * Copyright (c) <2015> <ss-cetc> and others.  All rights reserved.
 *
 *
 * @author lingbo on 2015/6/18
 * @version 0.1
 */

package com.cetc.security.ddos.controller.adapter;


public class TrafficTuple {
	private double bytes;
    private double packets;
    private long duration; //单位：秒
	
	public TrafficTuple() {
		this.bytes = 0; 
		this.packets = 0;
		this.duration = 0;
	}

    public TrafficTuple(double bytes, double packets, long duration) {
        this.bytes = bytes;
        this.packets = packets;
        this.duration = duration;
    }


    public double getBytes() {
        return bytes;
    }

    public void setBytes(double bytes) {
        this.bytes = bytes;
    }

    public double getPackets() {
        return packets;
    }

    public void setPackets(double packets) {
        this.packets = packets;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setTrafficTuple(TrafficTuple other) {
        this.bytes = other.bytes;
        this.packets = other.packets;
        this.duration = other.duration;
	}
    public void setTrafficTuple2(double bytes, double packets, long duration) {
        this.bytes = bytes;
        this.packets = packets;
        this.duration = duration;
    }

	public boolean isZero() {
        return (bytes == 0 && packets == 0);
	}
	
	
}

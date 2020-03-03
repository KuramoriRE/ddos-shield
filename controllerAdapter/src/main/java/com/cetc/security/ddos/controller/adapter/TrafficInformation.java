/**
 * Copyright (c) <2015> <ss-cetc> and others.  All rights reserved.
 *
 *
 * @author lingbo on 2015/6/18
 * @version 0.1
 */

package com.cetc.security.ddos.controller.adapter;


public class TrafficInformation {
	private long bytes;
    private long packets;
    private long idle_age;
    private long hard_age;
    private long   duration; //单位：秒
	
	public TrafficInformation() {
		this.bytes = 0; 
		this.packets = 0;
		this.duration = 0;
	}

    public TrafficInformation(long bytes, long packets, long duration) {
        this.bytes = bytes;
        this.packets = packets;
        this.duration = duration;
    }


    public long getIdle_age() {
		return idle_age;
	}

	public void setIdle_age(long idle_age) {
		this.idle_age = idle_age;
	}

	public long getHard_age() {
		return hard_age;
	}

	public void setHard_age(long hard_age) {
		this.hard_age = hard_age;
	}

	public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public long getPackets() {
        return packets;
    }

    public void setPackets(long packets) {
        this.packets = packets;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setTrafficTuple(TrafficInformation other) {
        this.bytes = other.bytes;
        this.packets = other.packets;
        this.duration = other.duration;
	}
    public void setTrafficTuple2(long bytes, long packets, long duration) {
        this.bytes = bytes;
        this.packets = packets;
        this.duration = duration;
    }

	public boolean isZero() {
        return (bytes == 0 && packets == 0);
	}
	
	
}

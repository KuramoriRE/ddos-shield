/**
 * Copyright (c) <2015> <ss-cetc> and others.  All rights reserved.
 *
 *
 * @author lingbo on 2015/6/18
 * @version 0.1
 */

package com.cetc.security.ddos.controller.adapter;



public class SrcTrafficTuple {
	
	String srcNetwork;
	long duration;
	long sum_attack_packages=0;
	long sum_attack_bytes=0;

	public String getSrcNetwork() {
		return srcNetwork;
	}

	public void setSrcNetwork(String srcNetwork) {
		this.srcNetwork = srcNetwork;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getSum_attack_packages() {
		return sum_attack_packages;
	}

	public void setSum_attack_packages(long sum_attack_packages) {
		this.sum_attack_packages = sum_attack_packages;
	}

	public long getSum_attack_bytes() {
		return sum_attack_bytes;
	}

	public void setSum_attack_bytes(long sum_attack_bytes) {
		this.sum_attack_bytes = sum_attack_bytes;
	}

}

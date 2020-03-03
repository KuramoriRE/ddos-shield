/**
 * Copyright (c) <2015> <ss-cetc> and others.  All rights reserved.
 *
 *
 * @author lingbo on 2015/6/18
 * @version 0.1
 */

package com.cetc.security.ddos.controller.adapter;



public class AllTrafficTuple {
	
	long  time;
	FirstTrafficTuple first;
	SecondTrafficTuple second;
	ThirdTrafficTuple third;
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public FirstTrafficTuple getFirst() {
		return first;
	}
	public void setFirst(FirstTrafficTuple first) {
		this.first = first;
	}
	public SecondTrafficTuple getSecond() {
		return second;
	}
	public void setSecond(SecondTrafficTuple second) {
		this.second = second;
	}
	public ThirdTrafficTuple getThird() {
		return third;
	}
	public void setThird(ThirdTrafficTuple third) {
		this.third = third;
	}
	
}

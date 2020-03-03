/**
 * Copyright (c) <2015> <ss-cetc> and others.  All rights reserved.
 *
 *
 * @author lingbo on 2015/6/18
 * @version 0.1
 */

package com.cetc.security.ddos.controller.adapter;

import java.util.ArrayList;
import java.util.List;


public class SecondTrafficTuple {
	
	List<PoTrafficTuple> secondTraffic = new ArrayList<PoTrafficTuple>();

	public List<PoTrafficTuple> getSecondTraffic() {
		return secondTraffic;
	}

	public void setSecondTraffic(List<PoTrafficTuple> secondTraffic) {
		this.secondTraffic = secondTraffic;
	}

}

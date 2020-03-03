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


public class ThirdTrafficTuple {
	
	List<ProtocolTrafficTuple> thirdTraffic = new ArrayList<ProtocolTrafficTuple>();
	List<SrcTrafficTuple> attackInfoList = new ArrayList<SrcTrafficTuple>();

	public List<ProtocolTrafficTuple> getThirdTraffic() {
		return thirdTraffic;
	}

	public void setThirdTraffic(List<ProtocolTrafficTuple> thirdTraffic) {
		this.thirdTraffic = thirdTraffic;
	}

	public List<SrcTrafficTuple> getAttackInfoList() {
		return attackInfoList;
	}

	public void setAttackInfoList(List<SrcTrafficTuple> attackInfoList) {
		this.attackInfoList = attackInfoList;
	}
	
}

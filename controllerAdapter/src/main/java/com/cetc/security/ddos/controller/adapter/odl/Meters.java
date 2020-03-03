package com.cetc.security.ddos.controller.adapter.odl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Meters {
	
	@JsonProperty("meter")
	List<Meter> lmeter;
	
}

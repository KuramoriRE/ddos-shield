package com.cetc.security.ddos.controller.adapter.odl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeterBandHeaders {

	@JsonProperty("meter-band-header")
	List<MeterBandHeader> lMeterBandHeader; 
	
}

package com.cetc.security.ddos.controller.adapter.odl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Meter {
	
	@JsonProperty("flags")
	String flags;
	
	@JsonProperty("meter-id")
	String    meterId;
	
	@JsonProperty("meter-band-headers")
	MeterBandHeaders meterBanderHeaders;
	
	
	public Meter(String flags, int meterId, MeterBandHeaders meterBanderHeaders)
	{		
		this.flags = flags;
		this.meterId = String.valueOf(meterId);
		this.meterBanderHeaders = meterBanderHeaders;
	}	


}

package com.cetc.security.ddos.controller.adapter.odl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeterBandHeader {
	
	
	@JsonProperty("band-id")
	String BandId;
	
	@JsonProperty("meter-band-types")
	MeterBandTypes meterBandTypes;
	
	@JsonProperty("drop-rate")
	String DropRate;
	
	@JsonProperty("drop-burst-size")
	String DropBurstSize;
	
	public MeterBandHeader(int BandId, int DropRate)
	{		
		this.BandId = String.valueOf(BandId);
		this.DropRate = String.valueOf(DropRate);
		this.DropBurstSize = String.valueOf(DropRate);
	}	
}


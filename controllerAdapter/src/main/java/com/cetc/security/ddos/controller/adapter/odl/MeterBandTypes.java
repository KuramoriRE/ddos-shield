package com.cetc.security.ddos.controller.adapter.odl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeterBandTypes {
	
	 @JsonProperty("flags")
	    String flags;

	    public MeterBandTypes()
	    {
	        this.flags = "ofpmbt-drop";
	    }

}

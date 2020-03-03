package com.cetc.security.ddos.controller.adapter.odl;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BuildMeters {

	int MeterId;
	int DropRate;
	String MeterFlags;


	public BuildMeters( String meterflags, int meterid, int droprate)
	{
		this.MeterId = meterid;
		this.DropRate = droprate;
		this.MeterFlags = meterflags;
	}


	public String BuildMetersJson() {

		String jsonStrTmp = "xxxx";
   	    ObjectMapper fasterxmlObjMapper;

   	    MeterBandTypes meterBandTypes = new MeterBandTypes();
		MeterBandHeader meterBandHeader = new MeterBandHeader(1,DropRate);
		meterBandHeader.meterBandTypes = meterBandTypes;

		MeterBandHeaders meterBandHeaders = new MeterBandHeaders();

		meterBandHeaders.lMeterBandHeader = new ArrayList<MeterBandHeader>();

		meterBandHeaders.lMeterBandHeader.add(meterBandHeader);

		Meter meter = new Meter(MeterFlags, MeterId, meterBandHeaders);	//MeterFlags= meter-kbps meter-pktps

		Meters meters = new Meters();

		meters.lmeter = new ArrayList<Meter>();

		meters.lmeter.add(meter);


		fasterxmlObjMapper  = new ObjectMapper();
		fasterxmlObjMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		fasterxmlObjMapper.setSerializationInclusion(Include.NON_NULL);


		try {
			jsonStrTmp = fasterxmlObjMapper.writeValueAsString(meters);
			System.out.println("json is " + jsonStrTmp);

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonStrTmp;
	}

}

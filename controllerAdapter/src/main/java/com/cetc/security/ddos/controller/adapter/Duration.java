package com.cetc.security.ddos.controller.adapter;
import java.nio.charset.Charset;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hasen on 2015/6/22.
 */
public  class Duration {
	@JsonProperty("second")
	private long second;
	@JsonProperty("nanosecond")
	private long nanosecond;

    public long getSecond() {
        return second;
    }

    public void setSecond(long second) {
        this.second = second;
    }

    public long getNanosecond() {
        return nanosecond;
    }

    public void setNanosecond(long nanosecond) {
        this.nanosecond = nanosecond;
    }
}
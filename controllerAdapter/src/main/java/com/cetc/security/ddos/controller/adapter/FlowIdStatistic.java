package com.cetc.security.ddos.controller.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hasen on 2015/6/30.
 */
public class FlowIdStatistic {

    @JsonProperty("opendaylight-flow-statistics:flow-statistics")
    private FlowStatistic flowStatistics;

    public FlowStatistic getFlowStatistics() {
        return flowStatistics;
    }

    public void setFlowStatistics(FlowStatistic flowStatistics) {
        this.flowStatistics = flowStatistics;
    }
}
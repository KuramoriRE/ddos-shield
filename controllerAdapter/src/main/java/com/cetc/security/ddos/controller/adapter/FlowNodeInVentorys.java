package com.cetc.security.ddos.controller.adapter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hasen on 2015/6/22.
 */
public class FlowNodeInVentorys {

	@JsonProperty("flow-node-inventory:table")
	private List<FlowNodeInVentory> flows;
	
	
	public List<FlowNodeInVentory> getFlows() {
		return flows;
	}


	public void setFlows(List<FlowNodeInVentory> flows) {
		this.flows = flows;
	}


	public static class FlowNodeInVentory {
		@JsonProperty("id")
		public int tableId;
		
		@JsonProperty("opendaylight-flow-statistics:aggregate-flow-statistics")
		public AggregateFlowStatistic aggregateFlowStatistics;
		
		@JsonProperty("flow")
		public List<Flow> flow;
		
		public int getTableId() {
			return tableId;
		}
		
		public void setTableId(int tableId) {
			this.tableId = tableId;
		}
		
		public AggregateFlowStatistic getAggregateFlowStatistics() {
			return aggregateFlowStatistics;
		}
		
		public void setAggregateFlowStatistics(
                AggregateFlowStatistic aggregateFlowStatistics) {
			this.aggregateFlowStatistics = aggregateFlowStatistics;
		}
		
		public List<Flow> getFlow() {
			return flow;
		}
		public void setFlow(List<Flow> flow) {
			this.flow = flow;
		}
		
	}
}
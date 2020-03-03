package com.cetc.security.ddos.controller.adapter.odl;

import java.util.List;

import com.cetc.security.ddos.controller.adapter.odl.OdlFlowConfig.Flow;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OdlGetFlowType {
	
	@JsonProperty("flow-node-inventory:table")
	private List<FlowNode> flowNodes;
	
	public List<FlowNode> getFlowNodes() {
		return flowNodes;
	}

	public void setFlowNodes(List<FlowNode> flowNodes) {
		this.flowNodes = flowNodes;
	}

	public static class FlowNode {
		@JsonProperty("id")
		public int table_id;
		
		@JsonProperty("flow")
		public List<Flow> flow;
		
		public int getTable_id() {
			return table_id;
		}
		
		public void setTable_id(int table_id) {
			this.table_id = table_id;
		}

		public List<Flow> getFlow() {
			return flow;
		}

		public void setFlow(List<Flow> flow) {
			this.flow = flow;
		}
			
	}
}

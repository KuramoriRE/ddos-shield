package com.cetc.security.ddos.controller.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hasen on 2015/6/22.
 */
public class Flow {
	@JsonProperty("id")
	private String flowId;
	@JsonProperty("table_id")
	private int tableId;
	@JsonProperty("priority")
	private short priority;
	@JsonProperty("cookie")
	private long cookie;
	@JsonProperty("idle-timeout")
	private short idleTimeout;
	@JsonProperty("hard-timeout")
	private short hardTimeout;
	
	public static final class Match {
		@JsonProperty("in-port")
		private String inPort;
        @JsonProperty("ipv4-destination")
        private String ipv4Destination;

        public static final class IpMatch {
            @JsonProperty("ip-protocol")
            private int ipProtocol;

            public int getIpProtocol() {
                return ipProtocol;
            }

            public void setIpProtocol(int ipProtocol) {
                this.ipProtocol = ipProtocol;
            }

        }

        @JsonProperty("ip-match")
        private IpMatch ipmatch;

        public IpMatch getIpmatch() {
            return ipmatch;
        }

        public void setIpmatch(IpMatch ipmatch) {
            this.ipmatch = ipmatch;
        }

		public String getInPort() {
			return inPort;
		}

		public void setInPort(String inPort) {
			this.inPort = inPort;
		}

        public String getIpv4Destination() {
            return ipv4Destination;
        }

        public void setIpv4Destination(String ipv4Destination) {
            this.ipv4Destination = ipv4Destination;
        }

    }
	
	@JsonProperty("match")
	private Match match;
	
	@JsonProperty("opendaylight-flow-statistics:flow-statistics")
	private FlowStatistic flowStatistics;

	public String getFlowId() {
		return flowId;
	}
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
	
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	
	public short getPriority() {
		return priority;
	}
	public void setPriority(short priority) {
		this.priority = priority;
	}
	
	public long getCookie() {
		return cookie;
	}
	public void setCookie(long cookie) {
		this.cookie = cookie;
	}
	
	public short getIdleTimeout() {
		return idleTimeout;
	}
	public void setIdleTimeout(short idleTimeout) {
		this.idleTimeout = idleTimeout;
	}
	
	public short getHardTimeout() {
		return hardTimeout;
	}
	public void setHardTimeout(short hardTimeout) {
		this.hardTimeout = hardTimeout;
	}

    public Match getMatch() {
		return match;
	}
	public void setMatch(Match match) {
		this.match = match;
	}
	
	public FlowStatistic getFlowStatistics() {
		return flowStatistics;
	}
	public void setFlowStatistics(FlowStatistic flowStatistics) {
		this.flowStatistics = flowStatistics;
	}	
}
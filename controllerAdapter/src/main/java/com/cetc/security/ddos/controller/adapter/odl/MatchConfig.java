package com.cetc.security.ddos.controller.adapter.odl;

import com.cetc.security.ddos.controller.adapter.FlowConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchConfig {
	
	@JsonProperty("ipv4-destination")
	private String ipv4Destination;
	
	@JsonProperty("ipv6-destination")
	private String ipv6Destination;
	
	@JsonProperty("in-port")
	private String inPort;
	
	@JsonProperty("in-phy-port")
	private String inPhyPort;
	
	@JsonProperty("tcp-destination-port")
	private String tcpDestinationPort;
	
	@JsonProperty("udp-destination-port")
	private String udpDestinationPort;
	
	public String getIpv4Destination() {
		return ipv4Destination;
	}
	public void setIpv4Destination(String ipv4Destination) {
		this.ipv4Destination = ipv4Destination;
	}
	
	public String getIpv6Destination() {
		return ipv6Destination;
	}
	public void setIpv6Destination(String ipv6Destination) {
		this.ipv6Destination = ipv6Destination;
	}
	
	public String getInPort() {
		return inPort;
	}
	public void setInPort(String inPort) {
		this.inPort = inPort;
	}
	public String getInPhyPort() {
		return inPhyPort;
	}
	public void setInPhyPort(String inPhyPort) {
		this.inPhyPort = inPhyPort;
	}
	public String getTcpDestinationPort() {
		return tcpDestinationPort;
	}
	public void setTcpDestinationPort(String tcpDestinationPort) {
		this.tcpDestinationPort = tcpDestinationPort;
	}
	public String getUdpDestinationPort() {
		return udpDestinationPort;
	}
	public void setUdpDestinationPort(String udpDestinationPort) {
		this.udpDestinationPort = udpDestinationPort;
	}

	@JsonProperty("ethernet-match")
	private EthernetMatch ethernetMatch;
	
	public EthernetMatch getEthernetMatch() {
		return ethernetMatch;
	}
	public void setEthernetMatch(EthernetMatch ethernetMatch) {
		this.ethernetMatch = ethernetMatch;
	}
	
	public void createEthernetMatch(FlowConfig odlFlow)
	{
		ethernetMatch = new MatchConfig.EthernetMatch();
		ethernetMatch.createEthernetType(odlFlow);
	}

	public static final class EthernetMatch 
	{
		@JsonProperty("ethernet-type")
		private EthernetType ethernetType;
				
		public EthernetType getEthernetType() {
			return ethernetType;
		}

		public void setEthernetType(EthernetType ethernetType) {
			this.ethernetType = ethernetType;
		}
		
		public void createEthernetType(FlowConfig odlFlow)
		{
			ethernetType = new MatchConfig.EthernetMatch.EthernetType();
			ethernetType.setType(odlFlow.getEthernetType());
		}

		public static final class EthernetType
		{
			private int type;

			public int getType() {
				return type;
			}

			public void setType(int type) {
				this.type = type;
			}
		}
	}
	
	
	@JsonProperty("ip-match")
	private IpMatch ipMatch;
	
	public IpMatch getIpMatch() {
		return ipMatch;
	}
	public void setIpMatch(IpMatch ipMatch) {
		this.ipMatch = ipMatch;
	}
	
	public void createIpMatch(FlowConfig odlFlow)
	{
		ipMatch = new IpMatch();
		ipMatch.setIpProtocol(odlFlow.getIpProtocol());
	}

	public static final class IpMatch 
	{
		@JsonProperty("ip-protocol")
		private int ipProtocol;

		public int getIpProtocol() {
			return ipProtocol;
		}

		public void setIpProtocol(int ipProtocol) {
			this.ipProtocol = ipProtocol;
		}
		
	}
     
}

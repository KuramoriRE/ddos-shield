package com.cetc.security.ddos.controller.adapter;

public class FlowConfig {
	
	private int flowId;
	private String nodeSwId;
	private int priority;
	private int idleTimeout;
	private int hardTimeout;
	private int tableId;
	private String flowName;
	
	private String ipv4Source;
	private String ipv4Destination;
	private String ipv6Destination;
	private int ethernetType;
	private int ipProtocol;
	private int port = 0;
	private String inputNode;
	private String inputphyNode;

	private int instrOrder;
	private int actionOrder;
	private String outputNode;
	private int maxLength;
	private int meterId;
    private String gotoTable;
	
	private int packetCount;
	private int byteCount;

    private String path;    /* 程序执行路径 */
	
	public String getNodeSwId() {
		return nodeSwId;
	}
	public void setNodeSwId(String nodeSwId) {
		this.nodeSwId = nodeSwId;
	}
	
	public String getInputNode() {
		return inputNode;
	}
	public void setInputNode(String inputNode) {
		this.inputNode = inputNode;
	}


	
	public String getInputphyNode() {
		return inputphyNode;
	}
	public void setInputphyNode(String inputphyNode) {
		this.inputphyNode = inputphyNode;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getMeterId() {
		return meterId;
	}
	public void setMeterId(int meterId) {
		this.meterId = meterId;
	}
	
	public String getIpv6Destination() {
		return ipv6Destination;
	}
	public void setIpv6Destination(String ipv6Destination) {
		this.ipv6Destination = ipv6Destination;
	}
	public int getFlowId() {
		return flowId;
	}
	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getIdleTimeout() {
		return idleTimeout;
	}
	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}
	public int getHardTimeout() {
		return hardTimeout;
	}
	public void setHardTimeout(int hardTimeout) {
		this.hardTimeout = hardTimeout;
	}
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	public String getFlowName() {
		return flowName;
	}
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}
	public String getIpv4Destination() {
		return ipv4Destination;
	}
	public void setIpv4Destination(String ipv4Destination) {
		this.ipv4Destination = ipv4Destination;
	}
	public int getEthernetType() {
		return ethernetType;
	}
	public void setEthernetType(int ethernetType) {
		this.ethernetType = ethernetType;
	}
	public int getIpProtocol() {
		return ipProtocol;
	}
	public void setIpProtocol(int ipProtocol) {
		this.ipProtocol = ipProtocol;
	}
	public int getInstrOrder() {
		return instrOrder;
	}
	public void setInstrOrder(int instrOrder) {
		this.instrOrder = instrOrder;
	}
	public int getActionOrder() {
		return actionOrder;
	}
	public void setActionOrder(int actionOrder) {
		this.actionOrder = actionOrder;
	}
	public String getOutputNode() {
		return outputNode;
	}
	public void setOutputNode(String outputNode) {
		this.outputNode = outputNode;
	}
	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	public int getPacketCount() {
		return packetCount;
	}
	public void setPacketCount(int packetCount) {
		this.packetCount = packetCount;
	}
	public int getByteCount() {
		return byteCount;
	}
	public void setByteCount(int byteCount) {
		this.byteCount = byteCount;
	}

    public String getGotoTable() {
        return gotoTable;
    }

    public void setGotoTable(String gotoTable) {
        this.gotoTable = gotoTable;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
	public String getIpv4Source() {
		return ipv4Source;
	}
	public void setIpv4Source(String ipv4Source) {
		this.ipv4Source = ipv4Source;
	}
    
    
}

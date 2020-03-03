package com.cetc.security.ddos.controller.adapter.ovs;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;


import ch.ethz.ssh2.StreamGobbler;

import com.cetc.security.ddos.controller.adapter.FirstTrafficTuple;
import com.cetc.security.ddos.controller.adapter.PoTrafficTuple;
import com.cetc.security.ddos.controller.adapter.ProtocolTrafficTuple;
import com.cetc.security.ddos.controller.adapter.SecondTrafficTuple;
import com.cetc.security.ddos.controller.adapter.SrcTrafficTuple;
import com.cetc.security.ddos.controller.adapter.ThirdTrafficTuple;
import com.cetc.security.ddos.controller.adapter.TrafficInformation;
import com.cetc.security.ddos.controller.adapter.TrafficTuple;

import org.apache.log4j.Logger;
import org.springframework.web.client.RestClientException;

import com.cetc.security.ddos.common.utils.AntiLogger;
import com.cetc.security.ddos.controller.adapter.FlowConfig;


public class OvsFlowConfig {
    private static Logger logger = AntiLogger.getLogger(OvsFlowConfig.class);
	
	//private String addFlow;
	//private String delFlow;
	private int flowId;

    //private Session sess = null;
    //private Connection conn = null;
	
	/*
	public String getAddFlow() {
		return addFlow;
	}
	public void setAddFlow(String addFlow) {
		this.addFlow = addFlow;
	}
	public String getDelFlow() {
		return delFlow;
	}
	public void setDelFlow(String delFlow) {
		this.delFlow = delFlow;
	}
	*/
	public int getFlowId() {
		return flowId;
	}
	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}
	


    public String getProtocolType(int type) {
        String t = null;
        switch (type) {
            case 0:
                t = "ip";
                break;
            case 1:
                t = "icmp";
                break;
            case 6:
                t = "tcp";
                break;
            case 17:
                t = "udp";
                break;
            default:
                break;
        }

        return t;
    }
/*
    void openSession(OvsController contrl) throws IOException {
        String hostname = contrl.getControllerIp();
        String username = contrl.getUser();
        String password = contrl.getPasswd();
        conn = new Connection(hostname, contrl.getControllerPort());
        conn.connect();

        boolean isAuthenticated = conn.authenticateWithPassword(username, password);

        if (isAuthenticated == false) {
            throw new IOException("Authentication failed.");
        }

        sess = conn.openSession();
    }

    void closeSession() {
        if (sess != null) {
            sess.close();
            sess = null;
        }
        if (conn != null) {
            conn.close();
            conn = null;
        }
    }
*/
	void dealOvsCmd(OvsController contrl, String cmd) throws IOException
	{
        Session sess = null;

		logger.debug("ovs cmd is "+ cmd);
		try {
            //openSession(contrl);
            //sess.execCommand(cmd);

            sess = contrl.execCmd(cmd);

            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            
            while (true) {
            	String line = br.readLine();
                if (line == null) {
                    break;
                }
                logger.debug("ovs cmd result is "+line);
            }
            logger.debug("ExitCode: " + sess.getExitStatus());
            
		} catch (IOException e) {
			e.printStackTrace(System.err);
            throw e;
		} finally {
            //closeSession();
            contrl.closeCmd(sess);
        }
   	}
	
	/*
	public void PutFlowToController(OvsController contrl, FlowConfig flowInfo) throws RestClientException, IOException
	{
		String addFlowTmp="";
		String cmd="";
		
		addFlowTmp = addFlowTmp+" dl_type="+flowInfo.getEthernetType();
		
		if (flowInfo.getInputNode()!=null && flowInfo.getInputNode()!="")
		{
			addFlowTmp = addFlowTmp+",in_port="+flowInfo.getInputNode();
		}
		
		addFlowTmp = addFlowTmp+",priority="+flowInfo.getPriority();
		
		if (flowInfo.getIpProtocol() != 0 )
		{
			addFlowTmp = addFlowTmp+",nw_proto="+flowInfo.getIpProtocol();
		}
		
		if (flowInfo.getIpv4Destination() != null && flowInfo.getIpv4Destination()!="")
		{
			addFlowTmp = addFlowTmp+",nw_dst="+flowInfo.getIpv4Destination();
		}
		
		if (flowInfo.getOutputNode() != null && flowInfo.getOutputNode()!="")
		{
			addFlowTmp = addFlowTmp+",actions=output:"+flowInfo.getOutputNode();
		}
		
		addFlow = addFlowTmp;
		flowId = flowInfo.getFlowId();
		
		cmd = "ovs-ofctl add-flow " + flowInfo.getNodeSwId()  + " "+addFlow;
		
		dealOvsCmd(contrl, cmd);
		
	}*/

    protected String buildCmd(String opFlow, FlowConfig flowInfo, String strFlow) {
        String cmd = "ovs-ofctl " + opFlow + " " + flowInfo.getNodeSwId()  + " "+ strFlow;
        if (flowInfo.getPath() != null) {
            cmd = flowInfo.getPath() + cmd;
        }

        return cmd;
    }
	
	public void PutFlowToController(OvsController contrl, FlowConfig flowInfo) throws RestClientException, IOException
	{
		String addFlowTmp="";
		String cmd="";
		
		addFlowTmp += " table=" + flowInfo.getTableId();
		if (flowInfo.getEthernetType() != 0) {
			addFlowTmp = addFlowTmp+",dl_type="+flowInfo.getEthernetType();
		}		
		
		
		if (flowInfo.getInputNode()!=null && !flowInfo.getInputNode().equals(""))
		{
			addFlowTmp = addFlowTmp+",in_port="+flowInfo.getInputNode();
		}
		
		addFlowTmp = addFlowTmp+",priority="+flowInfo.getPriority();
		
		if (flowInfo.getIpProtocol() != 0 )
		{
			addFlowTmp = addFlowTmp+",nw_proto="+flowInfo.getIpProtocol();
		}	
		
		if (flowInfo.getIpv4Source() != null && !flowInfo.getIpv4Source().equals(""))
		{
			addFlowTmp = addFlowTmp+",nw_src=" + flowInfo.getIpv4Source();
		}
		
		
		if (flowInfo.getIpv4Destination() != null && !flowInfo.getIpv4Destination().equals(""))
		{
			addFlowTmp = addFlowTmp+",nw_dst="+flowInfo.getIpv4Destination();
		}
		

		if (flowInfo.getOutputNode() != null && !flowInfo.getOutputNode().equals(""))
		{
			addFlowTmp = addFlowTmp+",actions=output:"+flowInfo.getOutputNode();
		}

        if ((flowInfo.getGotoTable() != null) && !flowInfo.getGotoTable().equals("")) {
            addFlowTmp = addFlowTmp + ",action=goto_table=" + flowInfo.getGotoTable();
        }

		flowId = flowInfo.getFlowId();

        cmd = buildCmd("add-flow", flowInfo, addFlowTmp);
		
		dealOvsCmd(contrl, cmd);
		
	}
	
	public void putFirstFlowToController(OvsController contrl, int in_port) throws RestClientException, IOException
	{
		String cmd="";
		
		cmd = "ovs-ofctl add-flow br0 priority=10,tcp,in_port=1,action=goto_table=1";
		dealOvsCmd(contrl, cmd);
		cmd = "ovs-ofctl add-flow br0 priority=10,udp,in_port=1,action=goto_table=1";
		dealOvsCmd(contrl, cmd);
		cmd = "ovs-ofctl add-flow br0 priority=10,icmp,in_port=1,action=goto_table=1";
		dealOvsCmd(contrl, cmd);
		cmd = "ovs-ofctl add-flow br0 priority=9,ip,in_port=1,action=goto_table=1";
		dealOvsCmd(contrl, cmd);
	}
	
	public void BindMeterForFlow(OvsController contrl, FlowConfig flowInfo, int meterId) throws RestClientException, IOException
	{
		String addFlowTmp="";
		String cmd="";
		
		addFlowTmp = addFlowTmp+" dl_type="+flowInfo.getEthernetType();
		
		if (flowInfo.getInputNode()!=null && flowInfo.getInputNode()!="")
		{
			addFlowTmp = addFlowTmp+",in_port="+flowInfo.getInputNode();
		}
		
		addFlowTmp = addFlowTmp+",priority="+flowInfo.getPriority();
		
		if (flowInfo.getIpProtocol() != 0 )
		{
			addFlowTmp = addFlowTmp+",nw_proto="+flowInfo.getIpProtocol();
		}
		
		if (flowInfo.getIpv4Destination() != null && flowInfo.getIpv4Destination()!="")
		{
			addFlowTmp = addFlowTmp+",nw_dst="+flowInfo.getIpv4Destination();
		}
		
		addFlowTmp = addFlowTmp +",actions=meter:"+ meterId;
		
		if (flowInfo.getOutputNode() != null && flowInfo.getOutputNode()!="")
		{
			addFlowTmp = addFlowTmp+",output:"+flowInfo.getOutputNode();
		}

		flowId = flowInfo.getFlowId();

        cmd = buildCmd("add-flow", flowInfo, addFlowTmp);
		
		dealOvsCmd(contrl, cmd);
		
	}
	
	public void unBindMeterForFlow(OvsController contrl, FlowConfig flowInfo, int meterId) throws RestClientException, IOException
	{
		PutFlowToController(contrl, flowInfo);
	}
	
	public void delFlowFromCotroller(OvsController contrl, FlowConfig flowInfo) throws RestClientException, IOException
	{
		String delFlowTmp="";
		String cmd="";
		
		delFlowTmp += " table=" + flowInfo.getTableId();
		if (flowInfo.getEthernetType() != 0) {
			delFlowTmp = delFlowTmp+",dl_type="+flowInfo.getEthernetType();
		}
		
		delFlowTmp = delFlowTmp + ",priority="+flowInfo.getPriority();
		
		if (flowInfo.getInputNode()!=null && !flowInfo.getInputNode().equals(""))
		{
			delFlowTmp = delFlowTmp+",in_port="+flowInfo.getInputNode();
		}
		
		if (flowInfo.getIpProtocol() != 0)
		{
			delFlowTmp = delFlowTmp+",nw_proto="+flowInfo.getIpProtocol();
		}
		
		if (flowInfo.getIpv4Destination() != null && !flowInfo.getIpv4Destination().equals(""))
		{
			delFlowTmp = delFlowTmp+",nw_dst="+flowInfo.getIpv4Destination();
		}
		
		
		if (flowInfo.getIpv4Source() != null && !flowInfo.getIpv4Source().equals(""))
		{
			delFlowTmp = delFlowTmp+",nw_src="+flowInfo.getIpv4Source();
		}
		

		/* --strict用于精确匹配，防止误删其他流表 */
        cmd = buildCmd("--strict del-flows", flowInfo, delFlowTmp);
		
		dealOvsCmd(contrl, cmd);

	}

    private int findSubStringOffset(String buf, String keyWord) {
        int offset = buf.indexOf(keyWord);
        return offset;
    }

    private String getKeyWordValue(String buf, String keyWord) {
        int offset = buf.indexOf(keyWord);
        if (offset == -1) {
            return null;
        }

        String ip = buf.substring(offset + keyWord.length());
        String[] split = ip.split(" ");
        String[] s = split[0].split(",");

        return s[0];
    }

    public TrafficTuple getFlowFromController(OvsController contrl, FlowConfig flowConfig, TrafficTuple trafficTuple)
            throws IOException {
        String cmd = "ovs-ofctl dump-flows " + flowConfig.getNodeSwId();
        String ipProtocol = null;
        String tableId = null;
        String packets = null;
        String bytes = null;
        String priority = null;
        String inPort = null;
        String nwDst =null;
        String duration = null;
        int offset = -1;
        double durations = 0.0;
        Session sess = null;

        try {
            sess = contrl.execCmd(cmd);

            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }

            /* 比较IP地址 */
                tableId = getKeyWordValue(line, "table=");
                if (tableId == null) {
                    continue;
                }

                if (Integer.valueOf(tableId).intValue() != flowConfig.getTableId()) {
                    continue;
                }

                priority = getKeyWordValue(line, "priority=");
                if (priority == null) {
                    continue;
                }

                if (Integer.valueOf(priority).intValue() != flowConfig.getPriority()) {
                    continue;
                }

                ipProtocol = getProtocolType(flowConfig.getIpProtocol());
                if (ipProtocol == null) {
                    continue;
                }

                if (findSubStringOffset(line, ipProtocol) == -1) {
                    continue;
                }

                if ((flowConfig.getInputNode() != null) && !flowConfig.getInputNode().equals("")) {
                    inPort = getKeyWordValue(line, "in_port=");
                    if (inPort == null) {
                        continue;
                    }

                    if (!inPort.equals(flowConfig.getInputNode())) {
                        continue;
                    }
                }

                nwDst = getKeyWordValue(line, "nw_dst=");
                if (nwDst == null) {
                    continue;
                }
                if (!nwDst.equals(flowConfig.getIpv4Destination())) {
                    String[] split = flowConfig.getIpv4Destination().split("/");
                    if (split.length < 2) {
                        continue;
                    }

                    if (!split[1].equals("32")) {    /* 表示配的单IP地址 */
                        continue;
                    }
                    if (!nwDst.equals(split[0])) {
                        continue;
                    }
                    /* IP地址比较完成 */
                }

                duration = getKeyWordValue(line, "duration=");
                if (duration == null) {
                    continue;
                } else {
                    offset = findSubStringOffset(duration, "s");
                    if (offset != -1) {
                        duration = duration.substring(0, offset);
                    }

                    durations = Double.valueOf(duration).doubleValue();
                    trafficTuple.setDuration(Math.round(durations));
                }

                packets = getKeyWordValue(line, "n_packets=");
                if (packets == null) {
                    continue;
                } else if (packets.equals("n/a")){
                	packets = "0";
                    trafficTuple.setPackets(Double.valueOf(packets).doubleValue());
                } else {
                    trafficTuple.setPackets(Double.valueOf(packets).doubleValue());
                }

                bytes = getKeyWordValue(line, "n_bytes=");
                if (bytes == null) {
                    continue;
                } else {
                    trafficTuple.setBytes(Double.valueOf(bytes).doubleValue());
                }

                //System.out.println(line);
            }

        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("Get flow statistics fail:" + e.getMessage());
            throw e;
        } finally {
            //closeSession();
            contrl.closeCmd(sess);
        }

        return trafficTuple;
    }	
	
	public TrafficTuple getFlowFromTest(OvsController contrl, FlowConfig flowConfig, TrafficTuple trafficTuple)
            throws IOException {
        String cmd = "ovs-ofctl dump-flows br0";
        String ipProtocol = null;
        String tableId = null;
        String packets = null;
        String bytes = null;
        String priority = null;
        String inPort = null;
        String nwDst =null;
        String duration = null;
        int offset = -1;
        double durations = 0.0;
        int count = 0;
        Session sess = null;

        try {
            sess = contrl.execCmd(cmd);
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }

            /* 比较IP地址 */
                tableId = getKeyWordValue(line, "table=");
                if (tableId == null) {
                    continue;
                }


//                priority = getKeyWordValue(line, "priority=");
//                if (priority == null) {
//                    continue;
//                }


                /*ipProtocol = getProtocolType(flowConfig.getIpProtocol());
                if (ipProtocol == null) {
                    continue;
                }

                if (findSubStringOffset(line, ipProtocol) == -1) {
                    continue;
                }
*/

                /*nwDst = getKeyWordValue(line, "nw_dst=");
                if (nwDst == null) {
                    continue;
                }*/

                duration = getKeyWordValue(line, "duration=");
                if (duration == null) {
                    continue;
                } else {
                    offset = findSubStringOffset(duration, "s");
                    if (offset != -1) {
                        duration = duration.substring(0, offset);
                    }

                    durations = Double.valueOf(duration).doubleValue();
                    trafficTuple.setDuration(Math.round(durations));
                }

                packets = getKeyWordValue(line, "n_packets=");
                if (packets == null) {
                    continue;
                } else if (packets.equals("n/a")){
                    packets = "0";
                    trafficTuple.setPackets(Double.valueOf(packets).doubleValue());
                } else {
                    trafficTuple.setPackets(Double.valueOf(packets).doubleValue());
                }

                bytes = getKeyWordValue(line, "n_bytes=");
                if (bytes == null) {
                    continue;
                } else {
                    trafficTuple.setBytes(Double.valueOf(bytes).doubleValue());
                }
                count ++;
                //System.out.println("n_packets: "+ trafficTuple.getPackets()+", n_bytes: " + trafficTuple.getBytes()+".\n");
            }

        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("Get flow statistics fail:" + e.getMessage());
            throw e;
        } finally {
            //closeSession();
            contrl.closeCmd(sess);
        }
        System.out.println("count: "+ count);
        return trafficTuple;
    }
	 public void fillFlowInfo(TrafficInformation trafficTuple,String line)
    {
    	String packets = null;
        String bytes = null;
        String duration = null;
        int offset = -1;
        double durations = 0.0;
        
    	duration = getKeyWordValue(line, "duration=");
        if (duration == null) {
            return;
        } else {
            offset = findSubStringOffset(duration, "s");
            if (offset != -1) {
                duration = duration.substring(0, offset);
            }

            durations = Double.valueOf(duration).doubleValue();
            trafficTuple.setDuration(Math.round(durations));
        }

        packets = getKeyWordValue(line, "n_packets=");
        if (packets == null) {
            return;
        } else if (packets.equals("n/a")){
        	packets = "0";
            trafficTuple.setPackets(Long.valueOf(packets).longValue());
        } else {
            trafficTuple.setPackets(Long.valueOf(packets).longValue());
        }

        bytes = getKeyWordValue(line, "n_bytes=");
        if (bytes == null) {
            return;
        } else {
            trafficTuple.setBytes(Long.valueOf(bytes).longValue());
        }
    	
    }
    
    public void getFirstFlow(String line, FirstTrafficTuple firstFlow)
    {
    	 String inPort = null;
    	 
    	 inPort = getKeyWordValue(line, "in_port=");
         if (inPort == null) {
             return;
         }

         if (Integer.valueOf(inPort).intValue() != 1) {
             return;
         }
         
         TrafficInformation tt = new TrafficInformation();
 		 fillFlowInfo(tt,line);
         
    	if (findSubStringOffset(line, "tcp") != -1) {
    		firstFlow.getTcpFirstTraffic().setProtocol(6);
    		firstFlow.getTcpFirstTraffic().setTrafficInfo(tt);
  
        }
    	else if(findSubStringOffset(line, "udp") != -1){
    			
    		firstFlow.getUdpFirstTraffic().setProtocol(17);
    		firstFlow.getUdpFirstTraffic().setTrafficInfo(tt);
    		
    	}
    	else if(findSubStringOffset(line, "icmp") != -1){    		
    		firstFlow.getIcmpFirstTraffic().setProtocol(1);
    		firstFlow.getIcmpFirstTraffic().setTrafficInfo(tt);
    	}
    	else if(findSubStringOffset(line, "ip") != -1){
    		
    		firstFlow.getIpFirstTraffic().setProtocol(0);
    		firstFlow.getIpFirstTraffic().setTrafficInfo(tt);
    		
    	}
    	
    	return;
    	
    }
    
    public void getSecondFlow(String line, SecondTrafficTuple secondFlow)
    {
    	String nwDst =null;
    	PoTrafficTuple poTraffic=null;
    	long sum_packages=0;
    	long sum_bytes=0;
    	nwDst = getKeyWordValue(line, "nw_dst=");
        if (nwDst == null) {
            return;
        }
        
        //test split
        String[] split = nwDst.split("/");
        if (split.length<2)
        {
        	nwDst = nwDst+"/32";
        }
        
        TrafficInformation tt = new TrafficInformation();
		fillFlowInfo(tt,line);
		
		for (int i=0;i<secondFlow.getSecondTraffic().size();i++)
		{
			if(secondFlow.getSecondTraffic().get(i).getDstNetwork().equals(nwDst)){
				
				poTraffic = secondFlow.getSecondTraffic().get(i);
				break;
			}
		}
		
		if (poTraffic == null)
		{
			poTraffic = new PoTrafficTuple();	
			poTraffic.setDstNetwork(nwDst);
			secondFlow.getSecondTraffic().add(poTraffic);
		}
		
		//取最大的duration
		if (tt.getDuration() > poTraffic.getDurtion())
		{
			poTraffic.setDurtion(tt.getDuration());
		}
		
		sum_packages = poTraffic.getSum_packages()+tt.getPackets();
    	sum_bytes = poTraffic.getSum_bytes()+tt.getBytes();
    	poTraffic.setSum_packages(sum_packages);
    	poTraffic.setSum_bytes(sum_bytes);
        
        if (findSubStringOffset(line, "tcp") != -1) {
        	
        	poTraffic.getTcpFirstTraffic().setProtocol(6);
        	poTraffic.getTcpFirstTraffic().setDstNetwork(nwDst);
        	poTraffic.getTcpFirstTraffic().setTrafficInfo(tt);
        	
  
        }
    	else if(findSubStringOffset(line, "udp") != -1){
    		
    		poTraffic.getUdpFirstTraffic().setProtocol(17);
    		poTraffic.getUdpFirstTraffic().setDstNetwork(nwDst);
    		poTraffic.getUdpFirstTraffic().setTrafficInfo(tt);
    		
    	}
    	else if(findSubStringOffset(line, "icmp") != -1){
    		poTraffic.getIcmpFirstTraffic().setProtocol(1);
    		poTraffic.getIcmpFirstTraffic().setDstNetwork(nwDst);
    		poTraffic.getIcmpFirstTraffic().setTrafficInfo(tt);
    	}
    	else if(findSubStringOffset(line, "ip") != -1){
    		poTraffic.getIpFirstTraffic().setProtocol(0);
    		poTraffic.getIpFirstTraffic().setDstNetwork(nwDst);
    		poTraffic.getIpFirstTraffic().setTrafficInfo(tt);

    	}

        
        return;
    }
    
    public void getThirdFlow(String line,ThirdTrafficTuple thirdFlow)
    {
    	String nwDst =null;
    	String nwSrc =null;
    	SrcTrafficTuple srcTraffic = null;
    	nwDst = getKeyWordValue(line, "nw_dst=");
        if (nwDst == null) {
            return;
        }
        
        nwSrc = getKeyWordValue(line, "nw_src=");
        if (nwSrc == null) {
            return;
        }
        
        String[] split = nwDst.split("/");
        if (split.length<2)
        {
        	nwDst = nwDst+"/32";
        }
        
        TrafficInformation tt = new TrafficInformation();
		fillFlowInfo(tt,line);
		
		ProtocolTrafficTuple flow = new ProtocolTrafficTuple();
		flow.setDstNetwork(nwDst);
		flow.setSrcNetwork(nwSrc);
		flow.setTrafficInfo(tt);
		
		thirdFlow.getThirdTraffic().add(flow);
		
		//fill the srcTrafficTuple
		int flag = 0;
		for (int i=0;i<thirdFlow.getAttackInfoList().size();i++)
		{
			if(thirdFlow.getAttackInfoList().get(i).getSrcNetwork().equals(nwSrc))
			{
				srcTraffic = thirdFlow.getAttackInfoList().get(i);
				flag = 1;
				break;
			}
		}
		
		if (flag == 0)
		{
			srcTraffic = new SrcTrafficTuple();
			srcTraffic.setDuration(tt.getDuration());
			srcTraffic.setSrcNetwork(nwSrc);
			thirdFlow.getAttackInfoList().add(srcTraffic);
		}
		
		long sum_bytes = srcTraffic.getSum_attack_bytes()+tt.getBytes();
		long sum_package = srcTraffic.getSum_attack_packages()+tt.getPackets();
		
		srcTraffic.setSum_attack_bytes(sum_bytes);
		srcTraffic.setSum_attack_packages(sum_package);
		
		/*use the max duration*/
		if (tt.getDuration()>srcTraffic.getDuration())
		{
			srcTraffic.setDuration(tt.getDuration());
		}
		
		return;
        
    }
    
    
    public void getAllTrafficInfo(OvsController contrl, FirstTrafficTuple firstFlow, SecondTrafficTuple secondFlow, ThirdTrafficTuple thirdFlow)
            throws IOException {
        String cmd = "ovs-ofctl dump-flows br0" ;
        String tableId = null;
        Session sess = null;
        

        try {
            sess = contrl.execCmd(cmd);

            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }

            /* 比较IP地址 */
                tableId = getKeyWordValue(line, "table=");
                if (tableId == null) {
                    continue;
                }

                if (Integer.valueOf(tableId).intValue() == 0) {
                    getFirstFlow(line,firstFlow);
                    continue;
                }
                
                if (Integer.valueOf(tableId).intValue() == 1) {
                    getSecondFlow(line,secondFlow);
                    continue;
                }
                
                if (Integer.valueOf(tableId).intValue() == 2) {
                    getThirdFlow(line,thirdFlow);
                    continue;
                }
            }

        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("Get flow statistics fail:" + e.getMessage());
            throw e;
        } finally {
            //closeSession();
            contrl.closeCmd(sess);
        }

        return ;
    }

}

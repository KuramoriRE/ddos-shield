package com.cetc.security.ddos.defense.cleandev;

import com.cetc.security.ddos.defense.main.Defense;
import com.cetc.security.ddos.persistence.CleanDevEntity;
import com.cetc.security.ddos.persistence.PersistenceEntry;
import com.cetc.security.ddos.persistence.ProtectObjectEntity;
import com.cetc.security.ddos.persistence.service.CleanDevService;
import com.cetc.security.ddos.persistence.service.POService;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangtao on 2016/4/25.
 */
public class ClientSocket {
	private static final short MSG_MAGIC = 0x7abc;
    private static final short MSG_VERSION = 0x01;
    private static final short MSG_CLEAN_DEV_POLICY_UPDATE = 0x01;
    private static final short MSG_PO_POLICY_UPDATE = 0x02;
    private static final int MIN_CLEAN_DEV_SPORT = 7777;
    private static final int MAX_CLEAN_DEV_SPORT = 8500;
    private static final int CLEAN_DEV_DPORT = 8888;
    private static CleanDevService cleanS = PersistenceEntry.getInstance().getCleanDevService();
    private static POService Po = PersistenceEntry.getInstance().getPOService();
    private Socket client;
    private int id;
    private String ip;
    private int sport;
    private Defense df;
    private CleanDevEntity c;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ClientSocket(int id, String ip, Defense df, CleanDevEntity c) {
        this.id = id;
        this.ip = ip;
        client = null;
        sport = MIN_CLEAN_DEV_SPORT;
        this.df = df;
        this.c = c;
    }

    public Defense getDf() {
        return df;
    }

    public CleanDevEntity getC() {
        return c;
    }

    /**
     *
     * @param n
     * @return
     */
    private byte[] toLH(int n) {
        byte[] b = new byte[4];
        b[0] = (byte)(n & 0xff);
        b[1] = (byte)(n >> 8 & 0xff);
        b[2] = (byte)(n >> 16 & 0xff);
        b[3] = (byte)(n >> 24 & 0xff);
        return b;
    }

    /**
     * 将 int转为高字节在前，低字节在后的byte数组
     * @param n int
     * @return byte[]
     */
    public byte [] toHH( int  n) {
        byte [] b =  new   byte [ 4 ];
        b[3 ] = ( byte ) (n &  0xff );
        b[2 ] = ( byte ) (n >>  8  &  0xff );
        b[1 ] = ( byte ) (n >>  16  &  0xff );
        b[0 ] = ( byte ) (n >>  24  &  0xff );
        return  b;
    }

    /**
     * 将 short转为低字节在前，高字节在后的byte数组
     * @param n short
     * @return byte[]
     */
    public byte [] toLH( short  n) {
        byte [] b =  new   byte [ 2 ];
        b[0 ] = ( byte ) (n &  0xff );
        b[1 ] = ( byte ) (n >>  8  &  0xff );
        return  b;
    }


    /**
     * 将 short转为高字节在前，低字节在后的byte数组
     * @param n short
     * @return byte[]
     */
    public byte [] toHH( short  n) {
        byte [] b =  new   byte [ 2 ];
        b[1 ] = ( byte ) (n &  0xff );
        b[0 ] = ( byte ) (n >>  8  &  0xff );
        return  b;
    }

    /*
        struct msg_header {
            uint16_t ver;
            uint16_t type;
            uint16_t flag;
            uint32_t len;
            uint32_t dev_id;
        }


     */

    private void fillHeader(short magic, short ver, short type, short flag, int len, int devId, byte[] output) {
        byte[] tmp = toLH(magic);
        int pos = 0;
        System.arraycopy(tmp, 0, output, pos, tmp.length);
        pos += tmp.length;
        
        tmp=toLH(ver);
        System.arraycopy(tmp, 0, output, pos, tmp.length);
        pos += tmp.length;
        
        tmp = toLH(type);
        System.arraycopy(tmp, 0, output, pos, tmp.length);
        pos += tmp.length;

        tmp = toLH(flag);
        System.arraycopy(tmp, 0, output, pos, tmp.length);
        pos += tmp.length;

        tmp = toLH(len);
        System.arraycopy(tmp, 0, output, pos, tmp.length);
        pos += tmp.length;
        tmp = toLH(devId);
        System.arraycopy(tmp, 0, output, pos, tmp.length);
}
    private void fillCleanDevBody(CleanDevEntity clean, byte[] buf, int offset){
    	byte[] tmp;
        int pos = offset;
        short a=0;
        
        tmp=toLH(clean.getId());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getDirect().getValue());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getCheck_interval());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getTcp());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getTcp_abnormal());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        
        if (clean.isTcpFirst() == true)
        {
        	a=1;
        }
        else
        {
        	a=0;
        }
        
        tmp=toLH(a);
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getUdp());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getIcmp());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getHttp());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getHttp_port());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getHttp_header());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getHttp_post());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getHttps());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getHttps_port());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getHttps_thc());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getDns_request());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getDns_reply());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getDns_abnormal());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getDns_port());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getSnmp());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getSnmpPort());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
         
        pos += tmp.length;
        tmp=toLH(clean.getNtp());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getNtpPort());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getFlow_timeout());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
        pos += tmp.length;
        tmp=toLH(clean.getFlag());
        System.arraycopy(tmp, 0, buf, pos, tmp.length);
        
    }
    
    private void fillPoBody(List<ProtectObjectEntity> protectObjects, byte[] buf, int offset,int count){
    	
    	byte[] tmp;
    	
        int pos = offset;
        short a=1;
        
        tmp=toLH(count);
    	System.arraycopy(tmp, 0, buf, pos, tmp.length);
    	pos += tmp.length;
        
        for(int i=0;i<count;i++)
        {
        	tmp=toLH(protectObjects.get(i).getId());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getFlag());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	byte[] name_tmp=new byte [ 32 ];
        	System.arraycopy(protectObjects.get(i).getName().getBytes(), 0, name_tmp, 0, protectObjects.get(i).getName().length());
        	System.arraycopy(name_tmp, 0, buf, pos, name_tmp.length);
        	
        	pos += name_tmp.length;
        	
        	byte[] ip_tmp=new byte [ 32 ];
        	System.arraycopy(protectObjects.get(i).getNetWork().getBytes(), 0, ip_tmp, 0, protectObjects.get(i).getNetWork().length());
        	System.arraycopy(ip_tmp, 0, buf, pos, ip_tmp.length);
        	
        	pos += ip_tmp.length;	
        	
        	byte[] in_port_tmp=new byte [ 16 ];
        	System.arraycopy(protectObjects.get(i).getCleanInport().getBytes(), 0, in_port_tmp, 0, protectObjects.get(i).getCleanInport().length());
        	System.arraycopy(in_port_tmp, 0, buf, pos, in_port_tmp.length);
        	
        	pos += in_port_tmp.length;
        	
        	byte[] out_port_tmp=new byte [ 16 ];
        	System.arraycopy(protectObjects.get(i).getCleanOutport().getBytes(), 0, out_port_tmp, 0, protectObjects.get(i).getCleanOutport().length());
        	System.arraycopy(out_port_tmp, 0, buf, pos, out_port_tmp.length);
        	
        	pos += out_port_tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getCheckInterval());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getTcpSyn());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getTcpSynAck());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getUdp());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getIcmp());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	if (protectObjects.get(i).isIcmpRedirect() == true)
        	{
        		a=1;
        	}
        	else
        	{
        		a=0;
        	}
        	
        	tmp=toLH(a);
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getHttp());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getHttp_port());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getHttp_post());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);      	
        	
        	pos += tmp.length;
        	
        	if (protectObjects.get(i).isHttpSrcAuth())
        	{
        		a=1;
        	}
        	else
        	{
        		a=0;
        	}
        	
        	tmp=toLH(a);
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getHttps());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getHttps_port());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getHttps_thc());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getDns_request());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getDns_reply());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getDns_port());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getSnmp());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getSnmpPort());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getNtp());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getNtpPort());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        	
        	tmp=toLH(protectObjects.get(i).getIpOption());
        	System.arraycopy(tmp, 0, buf, pos, tmp.length);
        	
        	pos += tmp.length;
        }
    }
   

    public void connect() throws Exception {
        client = new Socket(ip, CLEAN_DEV_DPORT);
        /*
        client = new Socket(ip, CLEAN_DEV_DPORT, null, sport);
        client.setReuseAddress(true);
        sport++;
        if (sport > MAX_CLEAN_DEV_SPORT) {
            sport = MIN_CLEAN_DEV_SPORT;
        }
        */
        //client.setKeepAlive(true);

        df.flowProcess(c);
        notifyCleanDevUpdate();
        notifyPOUpdate(null);
        changePoFlags(c);
    }
    
    public void changePoFlags(CleanDevEntity cc) {
    	List<ProtectObjectEntity> pos = Po.getPOByCleanDevId(cc.getId());
        for (ProtectObjectEntity po : pos) {
            if(po.getFlag() == 0||po.getFlag() == 1)
            {
            	Po.setPoNormalFlag(po);
            }
        }
		
	}


    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {

            }
            client = null;
        }
    }

    /**
     *
     * @throws Exception
     */
    public void notifyCleanDevUpdate() throws Exception {
        byte[] buf = new byte[16+94];
        CleanDevEntity cleanOne = null;
        cleanOne = cleanS.getCleanDev(id);
        if (cleanOne == null)
        {
        	return;
        }
        int body_offset = 0;
        int len = buf.length-2;
        fillHeader(MSG_MAGIC, MSG_VERSION, MSG_CLEAN_DEV_POLICY_UPDATE, (short)0, len, id, buf);
        body_offset = 16;
        fillCleanDevBody(cleanOne, buf, body_offset);
        client.getOutputStream().write(buf);
    }

    public void notifyPOUpdate(ProtectObjectEntity protectObj) throws Exception {
    	
        
        List<ProtectObjectEntity> protectObjects;
        int count=0;
        
        
        if(protectObj == null)
        {
        	 protectObjects=Po.getPOByCleanDevId(c.getId());
        }
        else
        {
        	protectObjects = new ArrayList<ProtectObjectEntity>();
        	protectObjects.add(protectObj);
        }
        
        if (protectObjects == null)
        {
        	count=0;
        }
        else
        {
        	count=protectObjects.size();
        }
        int buff_size = 16+4+182*count;
        int offset = 16;
        
        byte[] buf = new byte[buff_size];
        int len = buf.length-2;
        
        fillHeader(MSG_MAGIC, MSG_VERSION, MSG_PO_POLICY_UPDATE, (short)0, len, id, buf);
        fillPoBody(protectObjects,buf,offset,count);
        client.getOutputStream().write(buf);
        
    }

    public void sendKeepAlive() throws Exception {
        /*
        char[] buf = new char[1];

        buf[0] = 0xff;

        Byte b = new Byte(buf.toString());
        client.getOutputStream().write(b);
       */

        client.sendUrgentData(0xff);
    }
}

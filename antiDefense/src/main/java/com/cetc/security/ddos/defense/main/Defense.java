package com.cetc.security.ddos.defense.main;

import java.util.*;

import com.cetc.security.ddos.common.type.ControllerType;
import com.cetc.security.ddos.common.type.DeviceType;
import com.cetc.security.ddos.flow.FlowTable;
import com.cetc.security.ddos.controller.adapter.ControllerFactory;
import com.cetc.security.ddos.defense.cleandev.*;
import com.cetc.security.ddos.flow.*;
import com.cetc.security.ddos.persistence.*;
import com.cetc.security.ddos.persistence.service.CleanDevService;
import com.cetc.security.ddos.persistence.service.ControllerService;
import com.cetc.security.ddos.persistence.service.NetNodeService;
import com.cetc.security.ddos.persistence.service.POService;

import org.apache.log4j.Logger;

import com.cetc.security.ddos.common.utils.AntiLogger;
import com.cetc.security.ddos.controller.adapter.Controller;


/**
 * Created by zhangtao on 2015/7/23.
 */
public class Defense {
    private static Logger logger = AntiLogger.getLogger(Defense.class);
    private final static int POLICY_TIMER_INTERVAL = 30 * 1000;
    private static Defense instance = null;
    private PersistenceEntry p;
    private ControllerService controllerService;
    private NetNodeService netNodeService;
    private POService poService;
    private CleanDevService cleanDevService;
    //private NetNodes netNodes;
    private ClientHandle ch;
    private FlowTable flowTable;
    private FlowTable cleanFlowTable;
    private Timer policyTimer;
    private PolicyTimerTask ptt;
    private Map<Integer, ProcessFlow> processFlowMap;
    private Map<Integer, GetTrafficInfo> getTrafficInfoMap;
    private List<RetryCreateFlow> rcpfs;
    private List<ForwardFlow> forwardFlows;

    private Defense() {
        p = PersistenceEntry.getInstance();

        processFlowMap = new HashMap<Integer, ProcessFlow>();
        getTrafficInfoMap = new HashMap<Integer, GetTrafficInfo>();
        controllerService = p.getControllerService();
        netNodeService = p.getNetNodeService();
        poService = p.getPOService();
        cleanDevService = p.getCleanDevService();
        rcpfs = new ArrayList<RetryCreateFlow>();
        forwardFlows = new ArrayList<ForwardFlow>();

        ch = new ClientHandle();

        flowTable = new FlowTable();
        cleanFlowTable = new FlowTable();
        policyTimer = new Timer();

    }

    public static synchronized Defense getInstance() {
        if (instance == null) {
            instance = new Defense();
        }
        return instance;
    }

    private synchronized void addProcessFlow(int protectId, ProcessFlow pf) {
        processFlowMap.put(protectId, pf);
    }

    /*
    private synchronized void addProcessFlowSync(int protectId, ProcessFlow pf) {
        addProcessFlow(protectId, pf);
    }
    */

    private synchronized ProcessFlow delProcessFlow(int protectId) {
        return processFlowMap.remove(protectId);
    }

    private synchronized ProcessFlow getProcessFlow(int protectId) {
        return processFlowMap.get(protectId);
    }

    public synchronized void addRetryCreatePoFlow(RetryCreateFlow rcpf) {
        rcpfs.add(rcpf);
    }

    public synchronized boolean delRetryCreatePoFlowByPoId(int poId, DeviceType type) {
        boolean ret = false;

        for (RetryCreateFlow r : rcpfs) {
            if ((r.getThread().getPoId() == poId)
            		&& (r.getThread().getType() == type)) {
                delRetryCreatePoFlow(r);
                ret = true;
                break;
            }
        }

        return ret;
    }
    
    public synchronized boolean delRetryCreateFlowByController(int controllerId) {
    	boolean ret = false;

        for (RetryCreateFlow r : rcpfs) {
        	if ((r.getThread() instanceof ForwardFlowThread)
        			&& r.getThread().getCleanDevId() == controllerId) {
        		delRetryCreatePoFlow(r);
        		ret = true;
        		break;
        	}     	
        }
        
        return ret;
    }

    public synchronized void delRetryCreatePoFlow(RetryCreateFlow rcpf) {
        rcpfs.remove(rcpf);
    }

    public synchronized void clearRetryCreatePoFlow() {
        rcpfs.clear();
    }

    protected ControllerInfo buildController(ControllerEntity ce, DDoSParamEntity dDoSParam) {
        if (ce == null) {
            logger.error("Controller entity is null." );
            return null;
        }

        if (dDoSParam == null) {
            logger.error("DDoS parmter entity is null." );
            return null;
        }

        Controller controller = ControllerFactory.getControllerInstance(ce.getId(), ce.getType(), ce.getIp(), ce.getPort(),
                ce.getUser(), ce.getPassword());

        controller.setAttackSuspicionsThreshold(dDoSParam.getAttackSuspicionsThreshold());
        controller.setDetectionDeviationPercentage(dDoSParam.getDetectionDeviationPercentage());
        controller.setRecoverNormalThreshold(dDoSParam.getRecoverNormalThreshold());

        return new ControllerInfo(ce.getId(), controller, dDoSParam);
    }

    public void clear() {
        policyTimer.cancel();
    }

    protected void detectionStart(SendFlowBaseThread thread) {
        (new Thread(thread)).start();
    }


    protected void detectionStart(ControllerEntity ce, ProtectObjectEntity po, List<FlowEntity> flowEntities) {
        /* 为了防止连不上控制器而造成后面阻塞，因此这里采用线程方式启动 */
        StartThread startThread = new StartThread(ce, po, flowEntities);
        detectionStart(startThread);
    }

    protected void start(ControllerInfo cc, NetNodeEntity n) {
        /*
        NetNode netNode = new NetNode(n);
        netNodeService.setNetNodeNormalFlag(n);
        flowTable.addNetNode(cc, netNode);
        */


    }

    protected void detectionStart(ControllerEntity ce) {
        /* 获取DDOS攻击检测参数 */
        DDoSParamEntity dDoSParam = controllerService.getDDoSParam(ce.getId());
        if (dDoSParam == null) {
            logger.error("Get controller(" + ce.getIp() + ":"
                    + ce.getPort() + ") DDoS parameter fail.");
            return;
        }

        controllerService.setControllerNormalFlag(ce);
        flowTable.addCleanDev(DeviceType.DEVICE_DETECTION, ce.getId(), ce.getType(), ce.getIp(), ce.getPort(), ce.getUser(), ce.getPassword());
        ControllerIfaceEntity iface = controllerService.getControllerIface(ce.getId());
        
        ForwardFlowThread forwardThread = new ForwardFlowThread(ce, iface);
        detectionStart(forwardThread);
        
        List<ProtectObjectEntity> pos = poService.getPOByControllerId(ce.getId());
        if (pos == null) {
            return;
        }

        for (ProtectObjectEntity po : pos) {
            /* 如果PO里面没有流表，就不进行流表下发和对应的流量统计收集、处理操作 */
            List<FlowEntity> flows = poService.getFlowByPoId(po.getId());
            if (flows == null) {
                continue;
            }


            //flowTable.addCleanFlows(ce.getId(), po, flows);
            /*
            ProtectObjectFlow pof = flowTable.addPO(cc, netNode, po, flows);
            if (pof == null) {
                continue;
            }
            */

            poService.setPoNormalFlag(po);
            detectionStart(ce, po, flows);
        }



/*
        List<NetNodeEntity> nodeInfos = netNodeService.getNetNodeByControllerId(ce.getId());
        if (nodeInfos != null) {
            for (NetNodeEntity n : nodeInfos) {
                start(cc, n);
            }
        }
*/
        //NetNodes nodes = cc.getNetNodes();

    }

    protected synchronized void cleanStart() {
        List<CleanDevEntity> cleanDevEntities = cleanDevService.getCleanDev();
        if (cleanDevEntities != null) {
            for (CleanDevEntity c : cleanDevEntities) {
                cleanStart(c);
            }
        }
    }

    protected synchronized void cleanStart(CleanDevEntity c) {
    	cleanDevService.setCleanDevNormalFlag(c);
    	
        cleanFlowTable.addCleanDev(DeviceType.DEVICE_CLEAN, c.getId(), ControllerType.SSH_OVS,
                c.getIp(), 22, c.getUser(), c.getPassword());
        ch.addHandle(c, this);  
    }

    public synchronized void flowProcess(CleanDevEntity c) {
        List<ProtectObjectEntity> pos = poService.getPOByCleanDevId(c.getId());
        for (ProtectObjectEntity po : pos) {
            cleanStart(c, po);
        }



        try {
            //ch.updatePO(c.getId());

            GetTrafficInfo getTrafficInfo = new GetTrafficInfo(cleanFlowTable.getCleanDevController(c.getId()));
            getTrafficInfo.startProcess();
            getTrafficInfoMap.put(c.getId(), getTrafficInfo);
        } catch (Exception e) {
            logger.error("Update clean device PO fail:" + e.getMessage());
        }
    }


    protected synchronized void cleanStart(CleanDevEntity c, ProtectObjectEntity po) {
        List<FlowEntity> flows = poService.getFlowByPoId(po.getId());
        if (flows == null) {
            return;
        }

        cleanStart(c, po, flows);
    }

    protected synchronized void cleanStart(CleanDevEntity c, ProtectObjectEntity po, List<FlowEntity> flows) {
    	
    	//ControllerEntity ce, ProtectObjectEntity po, List<FlowEntity> flowEntities
    	CreateCleanFlowThread createCleanFlowThread = new CreateCleanFlowThread(c, po, flows);
        (new Thread(createCleanFlowThread)).start();
    }

    protected synchronized void cleanStop(int cleanDevId) {
        ch.delHandle(cleanDevId);
        GetTrafficInfo getTrafficInfo = getTrafficInfoMap.remove(cleanDevId);
        if (getTrafficInfo != null) {
            getTrafficInfo.stopProcess();
        }
        cleanFlowTable.delCleanDev(cleanDevId);
    }

    protected synchronized void cleanStop(CleanDevEntity c) {
        cleanStop(c.getId());
    }

    protected synchronized void cleanStop(ProtectObjectEntity po) {
    	delRetryCreatePoFlowByPoId(po.getId(), DeviceType.DEVICE_DETECTION);    	
        cleanFlowTable.delCleanFlows(po.getCleanDevEntity().getId(), po);
    }



    protected void cleanStop() {
        cleanFlowTable.clearFlowTable();
    }

    protected void detectionStart() {
        List<ControllerEntity> controllerEntities = controllerService.getController();
        if (controllerEntities != null) {
            for (ControllerEntity ce : controllerEntities) {
                detectionStart(ce);
            }
        }
    }


    public synchronized void start() throws Exception {
        logger.info("Defense is starting");
        cleanStart();
        ch.init();

        detectionStart();

        /* 用于处理异常情况下，重复处理相应操作，直到成功 */
        ptt = new PolicyTimerTask();
        policyTimer.scheduleAtFixedRate(ptt, POLICY_TIMER_INTERVAL, POLICY_TIMER_INTERVAL);

        logger.info("Defense is started completely");
    }

    protected synchronized void detectionStop(ProtectObjectEntity po) {
    	delRetryCreatePoFlowByPoId(po.getId(), DeviceType.DEVICE_CLEAN);
        flowTable.delCleanFlows(po.getControllerId(), po);
    }

    protected void detectionStop(ControllerEntity ce) {
        if (ce == null) {
            return;
        }

        /* 先停止正在运行的收集检测线程 */
        /*
        List<NetNodeEntity> netNodeEntities = netNodeService.getNetNodeByControllerId(ce.getId());
        if (netNodeEntities == null) {
            return;
        }

        ControllerInfo cc = flowTable.getControllerInfo(ce.getId());
        if (cc == null) {
            return;
        }



        for (NetNodeEntity n : netNodeEntities) {
            NetNode node = flowTable.getNetNode(cc, n.getId());
            stop(cc, node);
        }

        flowTable.delControllerInfo(cc);
        */
        for(ForwardFlow f: forwardFlows) {
        	if (f.getController().getId() == ce.getId()) {
        		f.delFlows();
        	}
        }
        
        flowTable.delCleanDev(ce.getId());
    }

    protected void detectionStop() {
        flowTable.clearFlowTable();
        for(ForwardFlow f: forwardFlows) {
        	f.delFlows();
        }
        
        forwardFlows.clear();
    }

    protected void stop(ControllerInfo cc, NetNode node) {
        if ((cc == null) || (node == null)) {
            return;
        }

/*
        List<ProtectObjectFlow> poFlow = node.getProtectObjects();
        if (poFlow == null) {
            return;
        }
*/
        /*
        List<ProtectObjectFlow> poFlow = new ArrayList<ProtectObjectFlow>(node.getProtectObjects());
        for (ProtectObjectFlow pof: poFlow) {
            stop(pof.getPo());
        }

        flowTable.delNetNode(cc, node.getNetNodeEntity().getId());
    */
    }
/*
    protected void stop(ProtectObjectEntity po) {
        if (delRetryCreatePoFlowByPoId(po.getId())) {
            return;
        }

        
        ProcessFlow pf = getProcessFlow(po.getId());
        try {
            stop(pf);
        } catch(Exception e) {
            logger.error("Stop protect object:" + po.getId() + " run fail.");
        }
        
    }
*/

    public void stop(ProcessFlow pf) throws InterruptedException {
        /*
        delProcessFlow(pf.getPof().getPo().getId());
        try {
            pf.stopProcess();
            pf.getPof().delPOFlows();
        } catch (InterruptedException e) {
            logger.error("Stop protect object id:" + pf.getPof().getPo().getId() + " process fail.");
            throw e;
        } finally {
            flowTable.delPO(pf.getNetNode(), pf.getPof().getPo().getId());
        }
        */
    }

    public synchronized void stop() {
        logger.info("Defense is stoping");

        if (ptt !=  null) {
            ptt.cancel();
        }
        //policyTimer.cancel();
        clearRetryCreatePoFlow();

        ch.exit();
        cleanStop();
        detectionStop();

        /*
        List<ControllerEntity> controllerEntities = controllerService.getController();
        for (ControllerEntity ce : controllerEntities) {
            stop(ce);
        }
        */

        /* 停止和清除所有处理的流 */
        /*
        Iterator<Map.Entry<Integer, ProcessFlow>> it = processFlowMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ProcessFlow> entry = it.next();
            ProcessFlow pf = entry.getValue();
            if (pf == null) {
                continue;
            }
            try {
                stop(pf);
            } catch (InterruptedException e) {
                logger.error("Stop protect object process fail.");
            }
        }
        */

        processFlowMap.clear();

        logger.info("Defense is be stopped completely");
    }

    protected void addControllerProc() {
        List<ControllerEntity> controllerEntities = controllerService.getAddController();
        if (controllerEntities == null) {
            return;
        }
        for (ControllerEntity ce : controllerEntities) {

            /* 得到DDOS攻击检测参数 */
            DDoSParamEntity dDoSParam = controllerService.getDDoSParam(ce.getId());
            if (dDoSParam == null) {
                logger.error("Get controller(" + ce.getIp() + ":"
                        + ce.getPort() + ") DDoS parameter fail." );
                continue;
            }

            detectionStart(ce);
/*
            ControllerInfo cc = buildController(ce, dDoSParam);
            if (cc == null) {
                continue;
            }

            controllerService.setControllerNormalFlag(ce);
            flowTable.addControllerInfo(cc);
  */
            logger.debug("Add Controller id:" + ce.getId() + " to map.");

        }
    }



    protected void editControllerProc() {
        List<ControllerEntity> controllerEntities = controllerService.getEditController();
        if (controllerEntities == null) {
            return;
        }

        for (ControllerEntity ce : controllerEntities) {
            detectionStop(ce);
            detectionStart(ce);
        }
    }


    protected void delControllerProc() {
        List<ControllerEntity> controllerEntities = controllerService.getDelController();
        if (controllerEntities == null) {
            return;
        }

        for (ControllerEntity ce : controllerEntities) {
            detectionStop(ce);
            controllerService.delController(ce.getId());
        }

    }

    public synchronized void unLoadController() {
        addControllerProc();
        editControllerProc();
        delControllerProc();
    }





    protected void addPOProc() {
        List<ProtectObjectEntity> pos = poService.getAddPO();
        if (pos == null) {
            return;
        }

        for (ProtectObjectEntity po : pos) {
            List<FlowEntity> flows = poService.getFlowByPoId(po.getId());
            if (flows == null) {
                continue;
            }

            cleanStart(po.getCleanDevEntity(), po, flows);

            ControllerEntity ce = controllerService.getController(po.getControllerId());
            if (ce != null) {
                detectionStart(ce, po, flows);
            }
            //start(po);
            poService.setPoNormalFlag(po);
           
            try {
                ch.updatePO(po.getCleanDevEntity().getId(),po);
            } catch (Exception e) {
                logger.error("Add PO to clean device fail:" + e.getMessage());
            }
            
            
        }
    }

    protected void editPOProc() {
        List<ProtectObjectEntity> pos = poService.getEditPO();
        if (pos == null) {
            return;
        }

        for (ProtectObjectEntity po : pos) {        	        	
            cleanStop(po);
            ControllerEntity ce = controllerService.getController(po.getControllerId());
            if (ce != null) {
                detectionStop(po);
            }

            List<FlowEntity> flows = poService.getFlowByPoId(po.getId());
            if (flows != null) {
                cleanStart(po.getCleanDevEntity(), po, flows);
                if (ce != null) {
                    detectionStart(ce, po, flows);
                }
            }
            poService.setPoNormalFlag(po);
            
            try {
                ch.updatePO(po.getCleanDevEntity().getId(),po);                
            } catch (Exception e) {
                logger.error("Update PO to clean device fail:" + e.getMessage());
            }
            
            
        }
    }

    protected void delPOProc() {
        List<ProtectObjectEntity> pos = poService.getDelPO();
        if (pos == null) {
            return;
        }

        for (ProtectObjectEntity po : pos) {
            cleanStop(po);
            ControllerEntity ce = controllerService.getController(po.getControllerId());
            if (ce != null) {
                detectionStop(po);
            }
            poService.delPo(po.getId());

            try {
                ch.updatePO(po.getCleanDevEntity().getId(),po);
                logger.info("Delete PO:" + po.getId() + " is successfully.");
            } catch (Exception e) {
                logger.error("Delete PO:" + po.getId() + " to clean device fail:" + e.getMessage());
            }
        }
    }

    public synchronized void unLoadPO() {
        addPOProc();
        editPOProc();
        delPOProc();
    }

    protected void addNetNodeProc() {
        /*
        List<NetNodeEntity> netNodes = netNodeService.getAddNetNode();
        if (netNodes == null) {
            return;
        }
        for (NetNodeEntity net : netNodes) {
            ControllerInfo cc = flowTable.getControllerInfo(net.getControllerId());
            if (cc == null) {
                logger.error("Get controller:" + net.getControllerId() + "fail.");
                continue;
            }

            NetNode netNode = new NetNode(net);
            netNodeService.setNetNodeNormalFlag(net);
            flowTable.addNetNode(cc, netNode);
        }
        */
    }

    protected void editNetNodeProc() {
        /*
        List<NetNodeEntity> netNodes = netNodeService.getEditNetNode();
        if (netNodes == null) {
            return;
        }
        for (NetNodeEntity n : netNodes) {
            ControllerInfo cc = flowTable.getControllerInfo(n.getControllerId());
            if (cc == null) {
                continue;
            }
            NetNode node = flowTable.getNetNode(cc, n.getId());
            stop(cc, node);

            start(cc, n);
        }
        */
    }

    protected void delNetNodeProc() {
        /*
        List<NetNodeEntity> netNodes = netNodeService.getDelNetNode();
        if (netNodes == null) {
            return;
        }

        for (NetNodeEntity n : netNodes) {
            ControllerInfo cc = flowTable.getControllerInfo(n.getControllerId());
            if (cc != null) {
                NetNode node = flowTable.getNetNode(cc, n.getId());
                stop(cc, node);
            }
            netNodeService.delNetNode(n.getId());
        }
        */
    }

    public synchronized void unLoadNetNode() {
        addNetNodeProc();
        editNetNodeProc();
        delNetNodeProc();
    }

    protected void addCleanDevProc() {
        List<CleanDevEntity> l = cleanDevService.getAddCleanDev();
        if (l == null) {
            return;
        }

        for (CleanDevEntity c : l) {
            cleanStart(c);
        }
    }

    protected void editCleanDevProc() {
        List<CleanDevEntity> l = cleanDevService.getEditCleanDev();
        if (l == null) {
            return;
        }

        for (CleanDevEntity c : l) {
            //ch.updateHandle(c);
            cleanStop(c);
            cleanStart(c);
            cleanDevService.setCleanDevNormalFlag(c);
        }
    }

    protected void delCleanDevProc() {
        List<CleanDevEntity> l = cleanDevService.getDelCleanDev();
        if (l == null) {
            return;
        }

        for (CleanDevEntity c : l) {
            cleanStop(c);
            cleanDevService.delCleanDev(c.getId());
        }
    }

    public synchronized void unLoadCleanDev() {
        addCleanDevProc();
        editCleanDevProc();
        delCleanDevProc();
    }

    private class RetryCreateFlow {
        private SendFlowBaseThread thread;

        public RetryCreateFlow(SendFlowBaseThread thread) {
            this.thread = thread;

            /*
            this.controllerInfo = controllerInfo;
            this.netNode = netNode;
            this.protectObjectFlow = protectObjectFlow;
            this.dDoSParam = dDoSParam;
            */
        }

        public SendFlowBaseThread getThread() {
            return thread;
        }
    }

    protected void dealFlow(SendFlowBaseThread s) throws Exception {
        //detectionStart(s);
        
        (new Thread(s)).start();



        //pof.createPOFlows();
        /*
        ProcessFlow pf = new ProcessFlow(cc.getController(), pof, netNode, dDoSParam.getDetectionInterval());

        pf.startProcess();
        addProcessFlow(pof.getPo().getId(), pf);
        */
        logger.debug("Start process project object flow.");
    }

    protected synchronized void dealRetryFlow() {
        List<RetryCreateFlow> tmp = new ArrayList<RetryCreateFlow>(rcpfs);

        rcpfs.clear();
        for (RetryCreateFlow r : tmp) {
        	SendFlowBaseThread s = r.getThread();
            try {
                dealFlow(s);
                //delRetryCreatePoFlow(r);
            } catch (Exception e) {
                addRetryCreatePoFlow(r);
                logger.error("Retry process"
                        + "  flow fail:" + e.getMessage());
                //addRetryCreatePoFlow(r);
            }
        }
    }
    
    private abstract class SendFlowBaseThread implements Runnable {
    	private int cleanDevId;
    	private int poId;
    	private DeviceType type;
		public int getPoId() {
			return poId;
		}
		public void setPoId(int poId) {
			this.poId = poId;
		}
		public DeviceType getType() {
			return type;
		}
		public void setType(DeviceType type) {
			this.type = type;
		}
		public int getCleanDevId() {
			return cleanDevId;
		}
		public void setCleanDevId(int cleanDevId) {
			this.cleanDevId = cleanDevId;
		}   
		
		
    }
    
    private class ForwardFlowThread extends SendFlowBaseThread {
    	private ControllerEntity ce;
    	private ControllerIfaceEntity iface;
    	
    	ForwardFlowThread(ControllerEntity ce, ControllerIfaceEntity iface) {
    		this.ce = ce;
    		this.iface = iface;   
    		setType(DeviceType.DEVICE_DETECTION);
    		setCleanDevId(ce.getId());
    	}
    	
    	@Override
        public void run() {
    		Controller controller = flowTable.getCleanDevController(ce.getId());
    		if (controller == null) {
    			return;
    		}
    		
    		ForwardFlow f = new ForwardFlow(controller);
    		try {
    		f.createFlow(iface);
    		forwardFlows.add(f);
    		} catch (Exception e) {
    			RetryCreateFlow rcpf = new RetryCreateFlow(this);
                addRetryCreatePoFlow(rcpf);
                logger.warn("The forward flow"
                        + " will be retry processing again after a few minutes.");
    		}
    	}
    }

    private class StartThread extends SendFlowBaseThread {
        private ControllerEntity ce;
        private ProtectObjectEntity po;
        private List<FlowEntity> flowEntities;

        StartThread(ControllerEntity ce, ProtectObjectEntity po, List<FlowEntity> flowEntities) {
            this.ce = ce;
            this.po = po;
            this.flowEntities = flowEntities;
            setPoId(this.po.getId());
            setType(DeviceType.DEVICE_DETECTION);
            setCleanDevId(ce.getId());
        }

        public ProtectObjectEntity getPo() {
            return po;
        }

        @Override
        public void run() {
            try {
                //dealFlow(cc, pof, netNode, dDoSParam);
                flowTable.addCleanFlows(ce.getId(), po, flowEntities);
                logger.info("Loading PO:" + po.getId()
                        + " detection flow is successfully");
            } catch(Exception e) {
                logger.error("Loading PO:" + po.getId()
                        + " detection flow fail:" + e.getMessage());
                /* 删除已下发成功的流表 */
                flowTable.delCleanFlows(ce.getId(), po);

             /* 失败后，让系统自动进行重新下发流表 */
                RetryCreateFlow rcpf = new RetryCreateFlow(this);
                addRetryCreatePoFlow(rcpf);
                logger.warn("The protect object:" + po.getId()
                        + " will be retry loading again after a few minutes.");
            }
        }
    }

    
    private class CreateCleanFlowThread extends SendFlowBaseThread {
        CleanDevEntity c;
        ProtectObjectEntity po;
        List<FlowEntity> flows;

        CreateCleanFlowThread(CleanDevEntity c, ProtectObjectEntity po, List<FlowEntity> flows) {
            this.c = c;
            this.po = po;
            this.flows = flows;
            setPoId(this.po.getId());
            setType(DeviceType.DEVICE_DETECTION);
            setCleanDevId(c.getId());
        }

        @Override
        public void run() {
            try {
                cleanFlowTable.addCleanFlows(c.getId(), po, flows);
                logger.info("Loading clean(" + c.getIp() + ") flow (po:"+ po.getId() + ") is successfully.");
            } catch (Exception e) {
            	/* 删除已下发成功的流表 */
            	cleanFlowTable.delCleanFlows(c.getId(), po);

             /* 失败后，让系统自动进行重新下发流表 */
                RetryCreateFlow rcpf = new RetryCreateFlow(this);
                addRetryCreatePoFlow(rcpf);
                logger.warn("The protect object clean flow:" + po.getId()
                        + " will be retry processing again after a few minutes.");
            }
        }
    }
    

    /* 该类用于异常流程后续的重复处理 */
    private class PolicyTimerTask extends TimerTask {
        @Override
        public void run() {
            dealRetryFlow();

            unLoadController();
            unLoadCleanDev();
            //unLoadNetNode();
            unLoadPO();
        }


    }

}

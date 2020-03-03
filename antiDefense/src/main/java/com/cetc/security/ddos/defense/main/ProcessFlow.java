package com.cetc.security.ddos.defense.main;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.cetc.security.ddos.controller.adapter.Controller;
import com.cetc.security.ddos.controller.adapter.FlowConfig;
import com.cetc.security.ddos.flow.NetNode;
import com.cetc.security.ddos.flow.ProtectObjectFlow;
import com.cetc.security.ddos.flowstatistic.RequireFlowStatistic;
import com.cetc.security.ddos.persistence.FlowEntity;
import com.cetc.security.ddos.persistence.ProtectObjectEntity;
import com.cetc.security.ddos.statanalysis.StatReport;
import com.cetc.security.ddos.controller.adapter.TrafficTuple;
import org.apache.log4j.Logger;
import com.cetc.security.ddos.common.utils.AntiLogger;

import com.cetc.security.ddos.statanalysis.RateBasedDetectorImpl;

/**
 * Created by zhangtao on 2015/6/16.
 */
public class ProcessFlow {
    private static Logger logger = AntiLogger.getLogger(ProcessFlow.class);
    private final static int SECOND_VALUE = 1000;
    private final static int DEFAULT_CAPACITY = 10;
    private final static int DEFAULT_COLLECT_INTERVAL = 60;
    private final static int FIRST_DELAY = 5 * 1000;

    private Thread thread;
    private int collectInterval;
    private BlockingQueue<QueueStat> queue;
    private RateBasedDetectorImpl rateBasedDetector;
    private boolean isStop;
    private Timer collectStatTimer;
    private ProcessStatThread processStatThread;
    private Controller controller;
    RequireFlowStatistic rfs;
    ProtectObjectFlow pof;
    NetNode netNode;

    public ProcessFlow(Controller controller, ProtectObjectFlow pof, NetNode netNode, int collectInterval)
                                    throws Exception {
        this(DEFAULT_CAPACITY, controller, pof, netNode, collectInterval);
    }

    public ProcessFlow(int capacity, Controller controller, ProtectObjectFlow pof,
                                        NetNode netNode) throws Exception {
        this(capacity, controller, pof, netNode, DEFAULT_COLLECT_INTERVAL);
    }

    public ProcessFlow(int capacity, Controller controller, ProtectObjectFlow pof, NetNode netNode, int collectInterval)
            throws Exception {
        this.collectInterval = collectInterval * SECOND_VALUE;
        queue = new ArrayBlockingQueue<QueueStat>(capacity);
        this.controller = controller;
        this.pof = pof;
        this.netNode = netNode;
        try {
            rfs = new RequireFlowStatistic(this.controller);
        } catch (Exception e) {
            logger.error("New RequireFlowStatistics fail:" + e.getMessage());
            throw e;
        }

        rateBasedDetector = new RateBasedDetectorImpl(DEFAULT_CAPACITY);
        processStatThread = new ProcessStatThread();

        collectStatTimer = new Timer();
        thread = new Thread(processStatThread);
    }

    public ProtectObjectFlow getPof() {
        return pof;
    }

    public int getCollectInterval() {
        return collectInterval;
    }

    public void setCollectInterval(int collectInterval) {
        this.collectInterval = collectInterval * SECOND_VALUE;
        collectStatTimer.cancel();
        if (!isStop) {
            startCollectStat();
        }
    }

    public NetNode getNetNode() {
        return netNode;
    }

    public void setNetNode(NetNode netNode) {
        this.netNode = netNode;
    }

    private void startCollectStat() {
        collectStatTimer.scheduleAtFixedRate(new CollectStatTimerTask(), FIRST_DELAY, collectInterval);
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void startProcess() {
        isStop = false;
        startCollectStat();
        thread.start();
    }

    public void stopProcess() throws InterruptedException {
        isStop = true;
        try {
            collectStatTimer.cancel();
            processStatThread.stop();
        } catch (InterruptedException e) {
            logger.error("Stop Process thread fail:" + e.getMessage());
            throw e;
        }
    }

    private class CollectStatTimerTask extends TimerTask {
        private FlowEntity getFlowByFlowId(int id, List<FlowEntity> flows) {
            FlowEntity ret = null;

            for (FlowEntity f : flows) {
                if (f.getId() == id) {
                    ret = f;
                    break;
                }
            }

            return ret;
        }

        @Override
        public void run() {
            try {
                logger.debug("Begin collect po id:" + pof.getPo().getId() + "flow statistics");
                //Iterator<Map.Entry<Integer, FlowConfig>> it = pof.getFlowInfoes().entrySet().iterator();

                for (Map.Entry<Integer, FlowConfig> entry : pof.getFlowInfoes().entrySet()) {
                //while (it.hasNext()) {
                    //Map.Entry<Integer, FlowConfig> entry = it.next();
                    FlowConfig fc = entry.getValue();
                    if (fc == null) {
                        logger.warn("Get FlowConfig fail.");
                        continue;
                    }

                    logger.debug("Collect flow info, flow id:" + fc.getFlowId()
                            + ", table id:" + fc.getTableId()
                            + ", switch id:" + fc.getNodeSwId());
                    TrafficTuple trafficTuple = new TrafficTuple();
                    trafficTuple = rfs.getFlowStats(fc, trafficTuple);

                    /* 获取对应流信息 */
                    ProtectObjectEntity po = pof.getPo();
                    if (po == null) {
                        logger.warn("Get project object fail.");
                        continue;
                    }

                    List<FlowEntity> flows = pof.getFlows();
                    FlowEntity f = getFlowByFlowId(fc.getFlowId(), flows);
                    if (f == null) {
                        logger.warn("Get Flow fail, flow id:." + fc.getFlowId());
                        continue;
                    }

                    logger.debug("Collect flow id:" + fc.getFlowId() + ", limit:kbps:" + f.getThresholdKBps()
                        + ", pps:" + f.getThresholdPps());

                    StatReport statsReport = new StatReport(fc.getIpProtocol(), fc.getPort(), trafficTuple,
                            System.currentTimeMillis()/1000, pof.getPo().getName(), fc.getFlowName(),
                            pof, fc.getFlowId(), /*ThresholdType.AUTO_LEARNING */f.getThresholdType(),
                            f.getThresholdKBps(), f.getThresholdPps());
                    QueueStat queueStat = new QueueStat(statsReport);
                    logger.debug("byte:" + statsReport.getStats().getBytes());
                    queue.put(queueStat);
                }

            } catch (Exception e) {
                logger.error(this.getClass().getName() + ":" + e.getMessage());
            }
        }
    }

    private class ProcessStatThread implements Runnable {
        void stop() throws InterruptedException {
            QueueStat queueStat = new QueueStat(null);
            try {
                queue.put(queueStat);
            } catch (InterruptedException e) {
                logger.error("Stop process statistics thread fail:" + e.getMessage());
                throw e;
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    QueueStat queueStat = queue.take();
                    if (isStop) {
                        logger.debug("Exit process statistics thread");
                        break;
                    }
                    rateBasedDetector.processStatReport(queueStat.getStatsReport());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class QueueStat {
        private StatReport statsReport;

        QueueStat(StatReport statsReport) {
            this.statsReport = statsReport;
        }

        public StatReport getStatsReport() {
            return statsReport;
        }

        public void setStatsReport(StatReport statsReport) {
            this.statsReport = statsReport;
        }
    }
}

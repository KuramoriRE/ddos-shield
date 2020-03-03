package com.cetc.security.ddos.defense.cleandev;

import com.cetc.security.ddos.common.utils.AntiLogger;
import com.cetc.security.ddos.defense.main.Defense;
import com.cetc.security.ddos.persistence.CleanDevEntity;
import com.cetc.security.ddos.persistence.ProtectObjectEntity;
import com.cetc.security.ddos.persistence.dao.CleanDevDao;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by zhangtao on 2016/4/26.
 */
public class ClientHandle {
    private static Logger logger = AntiLogger.getLogger(ClientHandle.class);
    private static final int HANDLE_TIMER_INTERVAL = 10 * 1000;
    private List<ClientSocket> normalList;
    private List<ClientSocket> abnormalList;
    private Timer handleTimer;
    private HandleTimerTask handleTimerTask;

    public ClientHandle() {
        normalList = new ArrayList<ClientSocket>();
        abnormalList = new ArrayList<ClientSocket>();
        handleTimer = new Timer();
    }

    public synchronized void init() {
        handleTimerTask = new HandleTimerTask();
        handleTimer.scheduleAtFixedRate(handleTimerTask, HANDLE_TIMER_INTERVAL, HANDLE_TIMER_INTERVAL);
    }

    /*
    public synchronized void init(int id, String ip) {
        CleanDevEntity c = new CleanDevEntity();
        c.setId(id);
        c.setIp(ip);

        addHandle(c);
        handleTimerTask = new HandleTimerTask();
        handleTimer.scheduleAtFixedRate(handleTimerTask, HANDLE_TIMER_INTERVAL, HANDLE_TIMER_INTERVAL);
    }
    */

    public synchronized void exit() {
        handleTimer.cancel();

        for (ClientSocket cs : normalList) {
            cs.close();
        }

        normalList.clear();
        abnormalList.clear();
    }

    private void checkIsOnline() {
        Iterator<ClientSocket> it;
        List tmp;

        synchronized(this) {
            /* 定期检查对端是否在线 */
            it = normalList.iterator();
            while (it.hasNext()) {
                ClientSocket cs = it.next();
                try {
                    cs.sendKeepAlive();
                } catch (Exception e) {
                    cs.close();
                    it.remove();
                    abnormalList.add(cs);
                }
            }

            /* 之前不通的，通过不断尝试是否能连通 */
            tmp = new ArrayList<ClientSocket>();
            tmp.addAll(abnormalList);
            abnormalList.clear();
        }

        it = tmp.iterator();
        while (it.hasNext()) {
            ClientSocket cs = it.next();
            //connectHandle(cs);

            ReconnectThread rt = new ReconnectThread(cs);
            (new Thread(rt)).start();
        }
    }

    public void addHandle(CleanDevEntity c, Defense df) {
        ConnectThread t = new ConnectThread(c, df);
        (new Thread(t)).start();
    }

    protected void connectHandle(CleanDevEntity c, Defense df) {
        ClientSocket cs = new ClientSocket(c.getId(), c.getIp(), df, c);
        connectHandle(cs);
    }

    protected void connectHandle(ClientSocket cs) {
        try {
            cs.connect();
            synchronized(this) {
                normalList.add(cs);
            }

            logger.info("Loading policy to clean device:" + cs.getIp() + " is successfully.");
        } catch (Exception e) {

            synchronized(this) {
                abnormalList.add(cs);
            }
            logger.warn("Loading policy to clean device:" + cs.getIp() + " fail:" + e.toString());
        }
    }

    public synchronized void updateHandle(CleanDevEntity c) {
        Iterator<ClientSocket> it = normalList.iterator();
        while (it.hasNext()) {
            ClientSocket cs = it.next();
            if (cs.getId() == c.getId()) {
                it.remove();
                addHandle(c, cs.getDf());
                return;
            }
        }

        it = abnormalList.iterator();
        while (it.hasNext()) {
            ClientSocket cs = it.next();
            if (cs.getId() == c.getId()) {
                it.remove();
                addHandle(c, cs.getDf());
                return;
            }
        }
    }

    public synchronized void delHandle(CleanDevEntity c) {
        delHandle(c.getId());
    }


    public synchronized void delHandle(int cleanDevId) {
        Iterator<ClientSocket> it = normalList.iterator();
        while (it.hasNext()) {
            ClientSocket cs = it.next();
            if (cs.getId() == cleanDevId) {
                cs.close();
                it.remove();
                return;
            }
        }

        it = abnormalList.iterator();
        while (it.hasNext()) {
            ClientSocket cs = it.next();
            if (cs.getId() == cleanDevId) {
                it.remove();
                return;
            }
        }
    }

    public synchronized void updatePO(int cleanDevId,ProtectObjectEntity po) throws Exception {
        Iterator<ClientSocket> it = normalList.iterator();
        
        while (it.hasNext()) {
            ClientSocket cs = it.next();
            if (cs.getId() == cleanDevId) {
            	try {
                cs.notifyPOUpdate(po);
                logger.info("Update PO:(" + po.getId() + ") is successfully.");
            	} catch (Exception e) {
            		cs.close();
            		it.remove();
            		abnormalList.add(cs);
            		logger.error("Update PO:(" + po.getId() + ") is fail:" + e.toString());
            		throw e;
            	}
            	return;
            }
        }
        
    }


    private class HandleTimerTask extends TimerTask {
        @Override
        public void run() {
            checkIsOnline();
        }
    }

/*
    public static void main(String[] args) {
        ClientHandle ch = new ClientHandle();
        ch.init(1, "172.16.16.11");
        //ch.exit();
    }
    */

    private class ConnectThread implements Runnable {
        private CleanDevEntity c;
        private Defense df;

        ConnectThread(CleanDevEntity c, Defense df) {
            this.c = c;
            this.df = df;
        }

        @Override
        public void run() {
            connectHandle(c, df);
        }
    }

    private class ReconnectThread implements Runnable {
        private ClientSocket cs;

        ReconnectThread(ClientSocket cs) {
            this.cs = cs;
        }

        @Override
        public void run() {
            connectHandle(cs);
        }
    }
}

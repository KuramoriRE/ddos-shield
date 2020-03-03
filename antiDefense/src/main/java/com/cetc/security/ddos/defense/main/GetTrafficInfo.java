package com.cetc.security.ddos.defense.main;

import java.util.*;

import javax.persistence.Column;

import com.cetc.security.ddos.controller.adapter.AllTrafficTuple;
import com.cetc.security.ddos.controller.adapter.Controller;
import com.cetc.security.ddos.controller.adapter.FirstTrafficTuple;
import com.cetc.security.ddos.controller.adapter.PoTrafficTuple;
import com.cetc.security.ddos.controller.adapter.ProtocolTrafficTuple;
import com.cetc.security.ddos.controller.adapter.SecondTrafficTuple;
import com.cetc.security.ddos.controller.adapter.SrcTrafficTuple;
import com.cetc.security.ddos.controller.adapter.ThirdTrafficTuple;
import com.cetc.security.ddos.persistence.AttackDetailEntity;
import com.cetc.security.ddos.persistence.InputTrafficInfoEntity;
import com.cetc.security.ddos.persistence.IpTrafficInfoEntity;
import com.cetc.security.ddos.persistence.PersistenceEntry;
import com.cetc.security.ddos.persistence.PoTrafficInfoEntity;
import com.cetc.security.ddos.persistence.ProtectObjectEntity;
import com.cetc.security.ddos.persistence.TrafficInfoEntity;
import com.cetc.security.ddos.persistence.service.AttackDetailService;
import com.cetc.security.ddos.persistence.service.InputTrafficInfoService;
import com.cetc.security.ddos.persistence.service.POService;
import com.cetc.security.ddos.persistence.service.PoTrafficInfoService;
import com.cetc.security.ddos.persistence.service.TrafficDataService;



public class GetTrafficInfo {
    private final static int SECOND_VALUE = 1000;
    private final static int FIRST_DELAY = 5 * 1000;
    private int collectInterval=20000;//20s
    private final static long HOUR_VALUE = 60*1000;//60s
    private final static long DAY_VALUE = 30*60*1000;//30m
    private final static long WEEK_VALUE = 4*60*60*1000;//4h
    private final static long MONTH_VALUE = 24*60*60*1000;//1day
    private final static long YEAR_VALUE = 7*24*60*60*1000;//1week
    
    private static TrafficDataService trafficData = PersistenceEntry.getInstance().getTrafficDataService();
    private static POService Po = PersistenceEntry.getInstance().getPOService();
    private static AttackDetailService attackDetail = PersistenceEntry.getInstance().getAttackDetailService();
    private static InputTrafficInfoService inputService = PersistenceEntry.getInstance().getInputTrafficService();
    private static PoTrafficInfoService poTrafficService = PersistenceEntry.getInstance().getPoTrafficService();
    private boolean isStop;
    private Controller controller;
    private Timer collectTimer;
    private boolean isFirst=true;
    private AllTrafficTuple lastTrafficHour;
    private AllTrafficTuple lastTrafficDay;
    private AllTrafficTuple lastTrafficWeek;
    private AllTrafficTuple lastTrafficMonth;
    private AllTrafficTuple lastTrafficYear;
    private AllTrafficTuple lastCollectTraffic;

    public GetTrafficInfo(Controller ctl) {
        //this.collectInterval = collectInterval * SECOND_VALUE;
        this.controller = ctl;

        collectTimer = new Timer();
    }


    public GetTrafficInfo(int collectInterval, Controller ctl) {
        this(ctl);
        this.collectInterval = collectInterval * SECOND_VALUE;

    }

    public int getCollectInterval() {
        return collectInterval;
    }

    public void setCollectInterval(int collectInterval) {
        this.collectInterval = collectInterval * SECOND_VALUE;
        collectTimer.cancel();
        if (!isStop) {
            startCollectStat();
        }
    }

    private void startCollectStat() {
        collectTimer.scheduleAtFixedRate(new CollectStatTimerTask(), FIRST_DELAY, collectInterval);
    }


    public void startProcess() {
        isStop = false;
        startCollectStat();
    }

    public void stopProcess()  {
        isStop = true;
        collectTimer.cancel();
    }

    private class CollectStatTimerTask extends TimerTask {
    	
    	
    	private boolean isInNowListThird(ProtocolTrafficTuple lastThirdOne, List<ProtocolTrafficTuple> ptListThird){
    		boolean flag=false;
    		if(lastThirdOne==null||ptListThird==null) return false;
    		
    		for (int i=0;i<ptListThird.size();i++)
    		{
    			if (lastThirdOne.getDstNetwork().equals(ptListThird.get(i).getDstNetwork())
    					&&lastThirdOne.getSrcNetwork().equals(ptListThird.get(i).getSrcNetwork()))
    			{
    				flag = true;
    				break;
    			}
    		}

            return flag;	
    	}
    	
    	private void parseTrafficData(List<PoTrafficTuple> ptListSecond,List<ProtocolTrafficTuple> ptListThird,
    			List<PoTrafficTuple> ptListSecondLast,List<ProtocolTrafficTuple> ptListThirdLast,
    			long time,long timePeriod,List<TrafficInfoEntity> entity,int type){
    		
    		int j=0;
    		int i=0;
    		int k=0;
    		int a=0;
    		int poCount;
    		
    		poCount = ptListSecond.size();
    		
    		for (i=0;i<poCount;i++)
    		{
    			int flag = 0;
    			for(j=0;j<ptListSecondLast.size();j++)
    			{
    				if (ptListSecond.get(i).getDstNetwork().equals(ptListSecondLast.get(j).getDstNetwork()))
    				{
    					flag = 1;
    					break;
    				}
    			}
    			
    			if (flag == 0)
    			{
    				continue;
    			}
    			
    			long pps_tcp=(ptListSecond.get(i).getTcpFirstTraffic().getTrafficInfo().getPackets()-ptListSecondLast.get(j).getTcpFirstTraffic().getTrafficInfo().getPackets())/timePeriod;
    			long pps_udp=(ptListSecond.get(i).getUdpFirstTraffic().getTrafficInfo().getPackets()-ptListSecondLast.get(j).getUdpFirstTraffic().getTrafficInfo().getPackets())/timePeriod;
    			long pps_icmp=(ptListSecond.get(i).getIcmpFirstTraffic().getTrafficInfo().getPackets()-ptListSecondLast.get(j).getIcmpFirstTraffic().getTrafficInfo().getPackets())/timePeriod;
    			long pps_other=(ptListSecond.get(i).getIpFirstTraffic().getTrafficInfo().getPackets()-ptListSecondLast.get(j).getIpFirstTraffic().getTrafficInfo().getPackets())/timePeriod;
    			long pps_all;
    			long bps_tcp=(ptListSecond.get(i).getTcpFirstTraffic().getTrafficInfo().getBytes()-ptListSecondLast.get(j).getTcpFirstTraffic().getTrafficInfo().getBytes())/timePeriod;
    			long bps_udp=(ptListSecond.get(i).getUdpFirstTraffic().getTrafficInfo().getBytes()-ptListSecondLast.get(j).getUdpFirstTraffic().getTrafficInfo().getBytes())/timePeriod;
    			long bps_icmp = (ptListSecond.get(i).getIcmpFirstTraffic().getTrafficInfo().getBytes()-ptListSecondLast.get(j).getIcmpFirstTraffic().getTrafficInfo().getBytes())/timePeriod;
    			long bps_other=(ptListSecond.get(i).getIpFirstTraffic().getTrafficInfo().getBytes()-ptListSecondLast.get(j).getIpFirstTraffic().getTrafficInfo().getBytes())/timePeriod;
    			long bps_all;
    			long output_pps=0;
    			long output_bps=0;
    			int po_id;
    			long attack_bps;
    			long attack_pps;
    			
    			long attack_byte_now=0;
    			long attack_package_now=0;
    			long attack_byte_last=0;
    			long attack_package_last=0;
    			
    			ProtectObjectEntity poEntity;
    			
    			poEntity = Po.getPOByNetWork(ptListSecond.get(i).getDstNetwork());
    			if (poEntity == null)
    			{
    				continue;
    			}
    			po_id = poEntity.getId();
    			
    			if(pps_tcp<0) {
    				pps_tcp=0;
    			}
    			if(pps_udp<0) {
    				pps_udp=0;
    			}
    			if(pps_icmp<0) {
    				pps_icmp=0;
    			}
    			if(pps_other<0) {
    				pps_other=0;
    			}
    			if(bps_tcp<0) {
    				bps_tcp=0;
    			}
    			if(bps_udp<0) {
    				bps_udp=0;
    			}
    			if(bps_icmp<0) {
    				bps_icmp=0;
    			}
    			if(bps_other<0) {
    				bps_other=0;
    			}
    			
    			pps_all = pps_tcp+pps_udp+pps_icmp+pps_other;
    			bps_all = bps_tcp+bps_udp+bps_icmp+bps_other;
    			
    			TrafficInfoEntity entityOne = new TrafficInfoEntity();
    			entityOne.setTime(time);
    			
    			entityOne.setPps_tcp(pps_tcp);
    			entityOne.setPps_udp(pps_udp);
    			entityOne.setPps_icmp(pps_icmp);
    			entityOne.setPps_other(pps_other);
    			//entityOne.setPps_all(pps_all);
    			
    			entityOne.setBps_icmp(bps_icmp);
    			entityOne.setBps_tcp(bps_tcp);
    			entityOne.setBps_udp(bps_udp);
    			entityOne.setBps_other(bps_other);
    			//entityOne.setBps_all(bps_all);
    			
    			entityOne.setPo_id(po_id);
    			entityOne.setType(type);
    			
    			//如果攻击源消失了一条怎么办，保留上一次的数据
    			
    			for(k=0;k<ptListThird.size();k++)
    			{
    				if (ptListSecond.get(i).getDstNetwork().equals(ptListThird.get(k).getDstNetwork()))
    				{
    					attack_byte_now+=ptListThird.get(k).getTrafficInfo().getBytes();
    					attack_package_now+=ptListThird.get(k).getTrafficInfo().getPackets();
    				}
    			}
    			
    			for(a=0;a<ptListThirdLast.size();a++)
    			{
    				if (ptListSecond.get(i).getDstNetwork().equals(ptListThirdLast.get(a).getDstNetwork())
    						&& isInNowListThird(ptListThirdLast.get(a),ptListThird))
    				{
    					attack_byte_last+=ptListThirdLast.get(a).getTrafficInfo().getBytes();
    					attack_package_last+=ptListThirdLast.get(a).getTrafficInfo().getPackets();
    				}
    			}
    			
    			attack_bps = (attack_byte_now - attack_byte_last)/timePeriod;
    			attack_pps = (attack_package_now - attack_package_last)/timePeriod;
    			
    			/*这里不可能出现小于零的情况，为保险起见，这里多做一次判断*/
    			if (attack_bps < 0)
    			{
    				attack_bps = ptListSecondLast.get(j).getAttack_bps();
    			}
    			if (attack_pps < 0)
    			{
    				attack_pps = ptListSecondLast.get(j).getAttack_pps();
    			}
    			
    			ptListSecond.get(i).setAttack_bps(attack_bps);
    			ptListSecond.get(i).setAttack_pps(attack_pps);
    			
    			
    			/*由于这里是bps_all由四个直接相加，有可能小数取整会比总和与时间相除要小，会出现bps_all比攻击bps还小的情况，这时应该直接修改bps_all*/
    			if (bps_all>=attack_bps) {
    				output_bps = bps_all-attack_bps;
    			}
    			else{
    				bps_all = attack_bps;
    				output_bps = 0;
    			}
    			if (pps_all >= attack_pps) {
    				output_pps = pps_all - attack_pps;
    			}
    			else{
    				pps_all = attack_pps;
    				output_pps=0;
    			}
    			
    			entityOne.setPps_all(pps_all);
    			entityOne.setBps_all(bps_all);
    			entityOne.setAttack_bps(attack_bps);
    			entityOne.setAttack_pps(attack_pps);
    			entityOne.setOutput_bps(output_bps);
    			entityOne.setOutput_pps(output_pps);
    			
    			entity.add(entityOne);	
    		}
    		
    		
    	}
    	
    	private void parseHourTrafficData(SecondTrafficTuple second, ThirdTrafficTuple third, long time,List<TrafficInfoEntity> entity){
    		
    		List<PoTrafficTuple> ptListSecond = second.getSecondTraffic();
    		List<PoTrafficTuple> ptListSecondLast = lastTrafficHour.getSecond().getSecondTraffic();
    		
    		List<ProtocolTrafficTuple> ptListThird = third.getThirdTraffic();
    		List<ProtocolTrafficTuple> ptListThirdLast = lastTrafficHour.getThird().getThirdTraffic();
    		
    		long timePeriod;
    		
    		
    		timePeriod = (time-lastTrafficHour.getTime())/1000;
    		
    		parseTrafficData(ptListSecond,ptListThird,ptListSecondLast,ptListThirdLast,time,timePeriod,entity,1);
    		
    		
    	}
    	
    	private void parseDayTrafficData(SecondTrafficTuple second, ThirdTrafficTuple third, long time,List<TrafficInfoEntity> entity){
    		List<PoTrafficTuple> ptListSecond = second.getSecondTraffic();
    		List<PoTrafficTuple> ptListSecondLast = lastTrafficDay.getSecond().getSecondTraffic();
    		
    		List<ProtocolTrafficTuple> ptListThird = third.getThirdTraffic();
    		List<ProtocolTrafficTuple> ptListThirdLast = lastTrafficDay.getThird().getThirdTraffic();
    		
    		long timePeriod;
    		
    		timePeriod = (time-lastTrafficDay.getTime())/1000;
    		
    		parseTrafficData(ptListSecond,ptListThird,ptListSecondLast,ptListThirdLast,time,timePeriod,entity,2);
    		
    	}
    	
    	private void parseWeekTrafficData(SecondTrafficTuple second, ThirdTrafficTuple third, long time,List<TrafficInfoEntity> entity){
    		List<PoTrafficTuple> ptListSecond = second.getSecondTraffic();
    		List<PoTrafficTuple> ptListSecondLast = lastTrafficWeek.getSecond().getSecondTraffic();
    		
    		List<ProtocolTrafficTuple> ptListThird = third.getThirdTraffic();
    		List<ProtocolTrafficTuple> ptListThirdLast = lastTrafficWeek.getThird().getThirdTraffic();
    		
    		long timePeriod;
    		
    		timePeriod = (time-lastTrafficWeek.getTime())/1000;
    		
    		parseTrafficData(ptListSecond,ptListThird,ptListSecondLast,ptListThirdLast,time,timePeriod,entity,3);
    		
    	}
    	
    	private void parseMonthTrafficData(SecondTrafficTuple second, ThirdTrafficTuple third, long time,List<TrafficInfoEntity> entity){
    		List<PoTrafficTuple> ptListSecond = second.getSecondTraffic();
    		List<PoTrafficTuple> ptListSecondLast = lastTrafficMonth.getSecond().getSecondTraffic();
    		
    		List<ProtocolTrafficTuple> ptListThird = third.getThirdTraffic();
    		List<ProtocolTrafficTuple> ptListThirdLast = lastTrafficMonth.getThird().getThirdTraffic();
    		
    		long timePeriod;
    		
    		timePeriod = (time-lastTrafficMonth.getTime())/1000;
    		
    		parseTrafficData(ptListSecond,ptListThird,ptListSecondLast,ptListThirdLast,time,timePeriod,entity,4);
    		
    	}
    	
    	private void parseYearTrafficData(SecondTrafficTuple second, ThirdTrafficTuple third, long time,List<TrafficInfoEntity> entity){
    		List<PoTrafficTuple> ptListSecond = second.getSecondTraffic();
    		List<PoTrafficTuple> ptListSecondLast = lastTrafficYear.getSecond().getSecondTraffic();
    		
    		List<ProtocolTrafficTuple> ptListThird = third.getThirdTraffic();
    		List<ProtocolTrafficTuple> ptListThirdLast = lastTrafficYear.getThird().getThirdTraffic();
    		
    		long timePeriod;
    		
    		timePeriod = (time-lastTrafficYear.getTime())/1000;
    		
    		parseTrafficData(ptListSecond,ptListThird,ptListSecondLast,ptListThirdLast,time,timePeriod,entity,5);
    		
    	}
    	
    	private void writeTrafficData(FirstTrafficTuple first, SecondTrafficTuple second, ThirdTrafficTuple third, long time){
    		
    		List<TrafficInfoEntity> entity = new ArrayList<TrafficInfoEntity>();
	
    		
    		if (time-lastTrafficHour.getTime()>=HOUR_VALUE)
    		{
    			parseHourTrafficData(second, third,time,entity);
    			lastTrafficHour.setFirst(first);
        		lastTrafficHour.setSecond(second);
        		lastTrafficHour.setThird(third);
        		lastTrafficHour.setTime(time);
    		}
    		if (time-lastTrafficDay.getTime()>=DAY_VALUE)
    		{
    			parseDayTrafficData(second, third,time,entity);
    			lastTrafficDay.setFirst(first);
        		lastTrafficDay.setSecond(second);
        		lastTrafficDay.setThird(third);
        		lastTrafficDay.setTime(time);
    		}
    		if (time-lastTrafficWeek.getTime()>=WEEK_VALUE)
    		{
    			parseWeekTrafficData(second, third,time,entity);
    			lastTrafficWeek.setFirst(first);
        		lastTrafficWeek.setSecond(second);
        		lastTrafficWeek.setThird(third);
        		lastTrafficWeek.setTime(time);
    		}
    		if (time-lastTrafficMonth.getTime()>=MONTH_VALUE)
    		{
    			parseMonthTrafficData(second, third,time,entity);
    			lastTrafficMonth.setFirst(first);
        		lastTrafficMonth.setSecond(second);
        		lastTrafficMonth.setThird(third);
        		lastTrafficMonth.setTime(time);
    		}
    		if (time-lastTrafficYear.getTime()>=YEAR_VALUE)
    		{
    			parseYearTrafficData(second, third,time,entity);
    			lastTrafficYear.setFirst(first);
        		lastTrafficYear.setSecond(second);
        		lastTrafficYear.setThird(third);
        		lastTrafficYear.setTime(time);
    		}
    		
    		if (entity.size()>0)
    		{
    			trafficData.addTrafficNodes(entity);
    		}
    		
    		return;
    	}
    	
    	private void fillInputTraffic(int protocol, FirstTrafficTuple first, List<InputTrafficInfoEntity> inputList)
    	{
    		InputTrafficInfoEntity inputEntity;
    		long rate_bps=0;
    		long rate_pps=0;
    		
    		if (protocol == 0)
    		{
    			if (first.getIpFirstTraffic().getTrafficInfo().getDuration()==0)
    			{
    				rate_bps = 0;
    				rate_pps = 0;
    			}
    			else
    			{
    				rate_bps = first.getIpFirstTraffic().getTrafficInfo().getBytes()/first.getIpFirstTraffic().getTrafficInfo().getDuration();
        			rate_pps = first.getIpFirstTraffic().getTrafficInfo().getPackets()/first.getIpFirstTraffic().getTrafficInfo().getDuration();
    			}
    			
    		}
    		else if (protocol == 1)
    		{
    			if (first.getIcmpFirstTraffic().getTrafficInfo().getDuration()==0)
    			{
    				rate_bps = 0;
    				rate_pps = 0;
    			}
    			else
    			{
    				rate_bps = first.getIcmpFirstTraffic().getTrafficInfo().getBytes()/first.getIcmpFirstTraffic().getTrafficInfo().getDuration();
        			rate_pps = first.getIcmpFirstTraffic().getTrafficInfo().getPackets()/first.getIcmpFirstTraffic().getTrafficInfo().getDuration();
    			}
    			
    		}
    		else if (protocol == 6)
    		{
    			if (first.getTcpFirstTraffic().getTrafficInfo().getDuration()==0)
    			{
    				rate_bps = 0;
    				rate_pps = 0;
    			}
    			else
    			{
    				rate_bps = first.getTcpFirstTraffic().getTrafficInfo().getBytes()/first.getTcpFirstTraffic().getTrafficInfo().getDuration();
        			rate_pps = first.getTcpFirstTraffic().getTrafficInfo().getPackets()/first.getTcpFirstTraffic().getTrafficInfo().getDuration();
    			}
    			
    		}
    		else if(protocol == 17)
    		{
    			if (first.getUdpFirstTraffic().getTrafficInfo().getDuration()==0)
    			{
    				rate_bps=0;
    				rate_pps=0;
    			}
    			else
    			{
    				rate_bps = first.getUdpFirstTraffic().getTrafficInfo().getBytes()/first.getUdpFirstTraffic().getTrafficInfo().getDuration();
        			rate_pps = first.getUdpFirstTraffic().getTrafficInfo().getPackets()/first.getUdpFirstTraffic().getTrafficInfo().getDuration();
    			}
    			
    		}
    		
    		inputEntity = inputService.findInputTrafficInfoByProtocol(protocol);
    		if (inputEntity == null)
    		{
    			inputEntity = new InputTrafficInfoEntity();
    			inputEntity.setProtocol(protocol);
    			inputEntity.setRate_bps(rate_bps);
        		inputEntity.setRate_pps(rate_pps);
        		inputService.addInputTrafficInfo(inputEntity);
        		return;
    		}

    		//inputEntity.setProtocol(protocol);
    		inputEntity.setRate_bps(rate_bps);
    		inputEntity.setRate_pps(rate_pps);
    		
    		inputService.updateInputTrafficInfo(inputEntity);
    	}
    	
    	private void writeInputTraffic(FirstTrafficTuple first)
    	{
    		List<InputTrafficInfoEntity> inputList = new ArrayList<InputTrafficInfoEntity>();
    		
    		fillInputTraffic(0,first,inputList);
    		fillInputTraffic(1,first,inputList);
    		fillInputTraffic(6,first,inputList);
    		fillInputTraffic(17,first,inputList);
    		
    	}
    	
    	private void writeIpTrafficInfo(ThirdTrafficTuple third)
    	{	
    		List<SrcTrafficTuple> srcList = third.getAttackInfoList();
    		int i=0;
    		int j=0;
    		long bps=0;
    		long pps=0;
    		
    		for (i=0;i<srcList.size();i++)
    		{
    			IpTrafficInfoEntity ipEntity = new IpTrafficInfoEntity();
    			ipEntity.setIp(srcList.get(i).getSrcNetwork());
    			bps = srcList.get(i).getSum_attack_bytes()/srcList.get(i).getDuration();
    			pps = srcList.get(i).getSum_attack_packages()/srcList.get(i).getDuration();
    			
    			ipEntity.setFlowrate_bps(bps);
    			ipEntity.setFlowrate_pps(pps);
    			
    			//first update
    			//update is not success, then add
    		}
    		return;
    	}
    	
    	private void writePoTrafficInfo(SecondTrafficTuple second)
    	{
    		List<PoTrafficTuple> secondList = second.getSecondTraffic();
    		long bps=0;
    		long pps=0;
    		int po_id=0;
    		for(int i=0;i<secondList.size();i++)
    		{
    			PoTrafficInfoEntity poTraffic = null;
    			if (secondList.get(i).getDurtion()==0)
    			{
    				continue;
    			}
    			bps = secondList.get(i).getSum_bytes()/secondList.get(i).getDurtion();
    			pps = secondList.get(i).getSum_packages()/secondList.get(i).getDurtion();
    			
    			ProtectObjectEntity poEntity;    			
    			poEntity = Po.getPOByNetWork(secondList.get(i).getDstNetwork());
    			if (poEntity == null)
    			{
    				continue;
    			}
    			po_id = poEntity.getId();
    			
    			//这里要用po_id进行查找，用另外一个接口
    			poTraffic = poTrafficService.findPoTrafficByPoId(po_id);
    			if (poTraffic == null)
    			{
    				poTraffic = new PoTrafficInfoEntity();
    				poTraffic.setPo_id(po_id);
        			poTraffic.setFlowrate_bps(bps);
        			poTraffic.setFlowrate_pps(pps);
        			poTrafficService.addPoTrafficInfo(poTraffic);
    				
    			}
    			else
    			{
    				//poTraffic.setPo_id(po_id);
        			poTraffic.setFlowrate_bps(bps);
        			poTraffic.setFlowrate_pps(pps);
        			poTrafficService.updatePoTrafficInfo(poTraffic);
    			}
    		
    		}
    		
    	}
    	
    	private void writeAttackDetail(ThirdTrafficTuple third)
    	{
    		List<ProtocolTrafficTuple> thirdTraffic = third.getThirdTraffic();
    		List<ProtocolTrafficTuple> thirdTrafficLast = lastCollectTraffic.getThird().getThirdTraffic();
    		
    		List<AttackDetailEntity> attackEntityList = null;
    		AttackDetailEntity       attackRunning = null;
    		AttackDetailEntity       attackStop = null;
    		int po_id=0;
    		long peak = 0;
    		
    		/*如果上次有，这次没有，则表明该攻击已经消失，需要更新其状态*/
    		for (int i=0;i<thirdTrafficLast.size();i++)
    		{
    			int flag =0;
    			attackEntityList = null;
    			for(int j=0;j<thirdTraffic.size();j++)
    			{
    				if (thirdTrafficLast.get(i).getSrcNetwork().equals(thirdTraffic.get(j).getSrcNetwork()) &&
    						thirdTrafficLast.get(i).getDstNetwork().equals(thirdTraffic.get(j).getDstNetwork()))
    				{
    					flag=1;
    					break;
    				}
    			}
    			
    			
    			if (flag == 1)
    			{
    				continue;
    			}
    			
    			/*消失了的攻击，需要更新状态*/
				ProtectObjectEntity poEntity;    			
    			poEntity = Po.getPOByNetWork(thirdTrafficLast.get(i).getDstNetwork());
    			if (poEntity == null)
    			{
    				continue;
    			}
    			po_id = poEntity.getId();
    			
    			attackEntityList = attackDetail.get(thirdTrafficLast.get(i).getSrcNetwork(), 1, po_id);
    			if (attackEntityList == null)
    			{
    				continue;
    			}
    			
    			for (int k=0;k<attackEntityList.size();k++)
    			{
    				attackEntityList.get(k).setStatus(2);
    				attackDetail.update(attackEntityList.get(k));
    			}
    		}
    		
    		/*这次查上来的应全部属于正在攻击的，需要对所有的进行更新*/
    		for(int i=0;i<thirdTraffic.size();i++)
    		{
    			attackEntityList = null;
    			attackRunning = null;
    			ProtectObjectEntity poEntity;    			
    			poEntity = Po.getPOByNetWork(thirdTraffic.get(i).getDstNetwork());
    			if (poEntity == null)
    			{
    				continue;
    			}
    			po_id = poEntity.getId();
    			
    			attackEntityList = attackDetail.get(thirdTraffic.get(i).getSrcNetwork(), 1, po_id);
    			if (attackEntityList == null)
    			{
    				continue;
    			}
    			
    			/*最多两个，将start时间早的那个状态赋为2*/
    			if (attackEntityList.size()==2) 
    			{
    				if (attackEntityList.get(0).getStartTime().getTime()>attackEntityList.get(1).getStartTime().getTime())
    				{
    					attackRunning = attackEntityList.get(0);
    					attackStop =  attackEntityList.get(1);
    				}
    				else
    				{
    					attackRunning = attackEntityList.get(1);
    					attackStop =  attackEntityList.get(0);
    				}
    				
    				attackStop.setStatus(2);
    				attackDetail.update(attackStop);
    			}
    			else if(attackEntityList.size()==1)
    			{
    				attackRunning = attackEntityList.get(0);
    			}
    			else
    			{
    				System.out.println("some error!\n");
    			}
    			
    			if (attackRunning == null)
    			{
    				continue;
    			}
    			
    			//更新攻击流
    			attackRunning.setDuration(thirdTraffic.get(i).getTrafficInfo().getDuration());
    			attackRunning.setStatus(1);
    			attackRunning.setTotalBytes(thirdTraffic.get(i).getTrafficInfo().getBytes());
    			attackRunning.setTotalPkts(thirdTraffic.get(i).getTrafficInfo().getPackets());
    			if (thirdTraffic.get(i).getTrafficInfo().getDuration()!=0)
    			{
    				peak = thirdTraffic.get(i).getTrafficInfo().getBytes()/thirdTraffic.get(i).getTrafficInfo().getDuration();
    				if (peak>attackRunning.getPeak())
    				{
    					attackRunning.setPeak(peak);
    				}
    			}
    			
    			attackDetail.update(attackRunning);
    			
    		}
    		
    	}
    	
    	private void writeDataBase(FirstTrafficTuple first, SecondTrafficTuple second, ThirdTrafficTuple third, long time){
    		writeTrafficData(first,second, third,time);
    		writeInputTraffic(first);
    		//writeIpTrafficInfo(third);
    		writePoTrafficInfo(second);
    		writeAttackDetail(third);
    	}
        
        @Override
        public void run() {
            try {
            	
            	FirstTrafficTuple first = new FirstTrafficTuple();
            	SecondTrafficTuple second = new SecondTrafficTuple();
            	ThirdTrafficTuple third  = new ThirdTrafficTuple();
            	
            	controller.getAllTraffic(first,second,third);
            	long time = System.currentTimeMillis();
            	
            	if (isFirst == true)
            	{
            		lastTrafficHour = new AllTrafficTuple();
            		lastTrafficHour.setFirst(first);
            		lastTrafficHour.setSecond(second);
            		lastTrafficHour.setThird(third);
            		lastTrafficHour.setTime(time);
            		
            		lastTrafficDay = new AllTrafficTuple();
            		lastTrafficDay.setFirst(first);
            		lastTrafficDay.setSecond(second);
            		lastTrafficDay.setThird(third);
            		lastTrafficDay.setTime(time);
            		
            		lastTrafficWeek = new AllTrafficTuple();
            		lastTrafficWeek.setFirst(first);
            		lastTrafficWeek.setSecond(second);
            		lastTrafficWeek.setThird(third);
            		lastTrafficWeek.setTime(time);
            		
            		lastTrafficMonth = new AllTrafficTuple();
            		lastTrafficMonth.setFirst(first);
            		lastTrafficMonth.setSecond(second);
            		lastTrafficMonth.setThird(third);
            		lastTrafficMonth.setTime(time);
            		
            		lastTrafficYear = new AllTrafficTuple();
            		lastTrafficYear.setFirst(first);
            		lastTrafficYear.setSecond(second);
            		lastTrafficYear.setThird(third);
            		lastTrafficYear.setTime(time);
            		
            		lastCollectTraffic = new AllTrafficTuple();
            		lastCollectTraffic.setFirst(first);
            		lastCollectTraffic.setSecond(second);
            		lastCollectTraffic.setThird(third);
            		lastCollectTraffic.setTime(time);
            		
            		
            		isFirst = false;
            		
            		return;
            	}
            	
            	writeDataBase(first,second,third,time);
            	lastCollectTraffic.setFirst(first);
        		lastCollectTraffic.setSecond(second);
        		lastCollectTraffic.setThird(third);
        		lastCollectTraffic.setTime(time);
            	
            } catch (Exception e) {
            }
        }
    }
}

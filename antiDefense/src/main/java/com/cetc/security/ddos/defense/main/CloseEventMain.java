package com.cetc.security.ddos.defense.main;

import com.cetc.security.ddos.common.type.MsgType;
import org.apache.log4j.Logger;

import com.cetc.security.ddos.common.utils.AntiLogger;
import com.cetc.security.ddos.common.utils.Message;

public class CloseEventMain {
	private static Logger logger = AntiLogger.getLogger(CloseEventMain.class);

	public static void main(String[] args) {
        try {
            Message notifyMsg = new Message();

            logger.info("Start send closing message.");
            notifyMsg.send(String.valueOf(MsgType.MSG_CLOSE));
            logger.info("End send closing message.");
        } catch (Exception e) {
            logger.error("Init close event message fail.");
        }
	}

}

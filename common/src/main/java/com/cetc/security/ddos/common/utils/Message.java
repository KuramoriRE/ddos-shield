package com.cetc.security.ddos.common.utils;

import org.springframework.core.io.support.PropertiesLoaderUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import java.util.Properties;

import org.apache.log4j.Logger;
import com.cetc.security.ddos.common.utils.AntiLogger;

/**
 * Created by zhangtao on 2015/8/26.
 */
public class Message extends JedisPubSub {
    private static Logger logger = AntiLogger.getLogger(Message.class);
    private Jedis jr;
    private String channel;
    MsgHandle proc;

    public Message() throws Exception {
        Properties props = PropertiesLoaderUtils.loadAllProperties("message.properties");
        String host = props.get("host").toString();
        int port = Integer.valueOf(props.get("port").toString());
        int timeout = Integer.valueOf(props.get("timeout").toString());
        channel = props.get("channel").toString();

        jr = new Jedis(host, port, timeout);
    }

    public Message(MsgHandle proc) throws Exception {
        this();
        this.proc = proc;
    }

    @Override
    public void onMessage(String channel, String message) {
        logger.debug("onMessage: channel[" + channel + "], message[" + message + "]");
        proc.handle(message);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        logger.debug("onPMessage: channel[" + channel + "], message[" + message + "]");
        proc.handle(message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        logger.debug("onSubscribe: channel[" + channel + "]," +
                "subscribedChannels[" + subscribedChannels + "]");
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        logger.debug("onUnsubscribe: channel[" + channel + "]," +
                "subscribedChannels[" + subscribedChannels + "]");
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        logger.debug("onPUnsubscribe: pattern[" + pattern + "]," +
                "subscribedChannels[" + subscribedChannels + "]");
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        logger.debug("onPSubscribe: pattern[" + pattern + "]," +
                "subscribedChannels[" + subscribedChannels + "]");
    }

    public void disconnect() {
        jr.disconnect();
    }


    public void listen() throws Exception {

        try {
            proceed(jr.getClient(), channel);
        } catch (Exception e) {
            if (!e.getMessage().equals("java.net.SocketException: socket closed")) {
                logger.error("Listen error:" + e.getMessage());
                throw e;
            }
        }
    }

    public void send(String msg) {
        jr.publish(channel, msg);
    }
}

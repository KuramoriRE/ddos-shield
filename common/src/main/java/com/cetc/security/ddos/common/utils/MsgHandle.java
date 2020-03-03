package com.cetc.security.ddos.common.utils;

import redis.clients.jedis.Jedis;

/**
 * Created by zhangtao on 2015/8/27.
 */
public abstract class MsgHandle {
    protected Message msg;

    public  abstract void  handle(String message);

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }
}

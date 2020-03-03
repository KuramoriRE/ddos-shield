package com.cetc.security.ddos.common.type;

/**
 * Created by zhangtao on 2016/8/10.
 */
public enum DeviceType {
    DEVICE_CLEAN((short)0), DEVICE_DETECTION((short)1);

    private short value;

    private DeviceType(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}

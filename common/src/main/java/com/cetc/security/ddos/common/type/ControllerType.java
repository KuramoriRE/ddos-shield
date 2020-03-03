package com.cetc.security.ddos.common.type;

/**
 * Created by zhangtao on 2015/8/11.
 */
public enum ControllerType {
    ODL_HELIUM((short)0), ODL_LITHIUM((short)1), UNINET((short)2), SSH_OVS((short)3);

    private short value;

    private ControllerType(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    public static ControllerType getControllerType(int type) {
        ControllerType t = null;
        switch (type) {
            case 0:
                t = ODL_HELIUM;
                break;
            case 1:
                t = ODL_LITHIUM;
                break;
            case 2:
                t = UNINET;
                break;
            case 3:
                t = SSH_OVS;
                break;
            default:
                break;
        }
        return t;
    }
}

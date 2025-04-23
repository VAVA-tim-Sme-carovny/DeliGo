package com.deligo.Model;

import java.util.List;

public class DeviceLoginResponse extends Response {
    private String deviceID;

    public DeviceLoginResponse(String deviceID, String message, int status) {
        super(message, status);
    }

    public String getDeviceID() {
        return deviceID;
    }
}

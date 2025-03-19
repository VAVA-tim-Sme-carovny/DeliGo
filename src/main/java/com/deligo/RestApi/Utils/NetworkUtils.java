package com.deligo.RestApi.Utils;

import java.net.InetAddress;

public class NetworkUtils {

    public static String getLocalIpAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (Exception e) {
            System.err.println("Failed to get local IP address: " + e.getMessage());
            return "127.0.0.1"; // fallback
        }
    }
}

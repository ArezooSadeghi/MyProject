package com.example.sipmobileapp.model;

public class ServerData {

    private String centerName;
    private String ipAddress;
    private String port;

    public ServerData() {
    }

    public ServerData(String centerName, String ipAddress, String port) {
        this.centerName = centerName;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}

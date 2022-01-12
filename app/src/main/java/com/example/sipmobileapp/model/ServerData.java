package com.example.sipmobileapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "server_data_tb")
public class ServerData {
    @PrimaryKey(autoGenerate = true)
    @NotNull
    private int serverDataID;
    private String centerName;
    private String ip;
    private String port;

    public ServerData() {
    }

    public ServerData(String centerName, String ip, String port) {
        this.centerName = centerName;
        this.ip = ip;
        this.port = port;
    }

    public int getServerDataID() {
        return serverDataID;
    }

    public void setServerDataID(int serverDataID) {
        this.serverDataID = serverDataID;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}

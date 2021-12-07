package com.example.sipmobileapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "server_data_table")
public class ServerDataTwo {

    @PrimaryKey(autoGenerate = true)
    @NotNull
    private int ID;
    private String centerName;
    private String ip;
    private String port;

    public ServerDataTwo() {
    }

    public ServerDataTwo(String centerName, String ip, String port) {
        this.centerName = centerName;
        this.ip = ip;
        this.port = port;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

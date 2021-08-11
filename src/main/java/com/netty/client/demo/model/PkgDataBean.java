package com.netty.client.demo.model;

public class PkgDataBean {
    private byte cmd;
    private byte dataLength;
    private String data;

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte getDataLength() {
        return dataLength;
    }

    public void setDataLength(byte dataLength) {
        this.dataLength = dataLength;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PkgDataBean{" +
                "cmd=" + cmd +
                ", dataLength=" + dataLength +
                ", data='" + data + '\'' +
                '}';
    }
}

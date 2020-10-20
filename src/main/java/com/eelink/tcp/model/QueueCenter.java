package com.eelink.tcp.model;

import com.eelink.tcp.utils.CONST;

public class QueueCenter {

    private byte[] buffer = new byte[CONST.MX_BUFFER_SIZE];
    private int start = 0;
    private int size = 0;

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}

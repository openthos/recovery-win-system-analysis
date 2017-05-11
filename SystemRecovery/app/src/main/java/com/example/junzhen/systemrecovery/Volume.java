package com.example.junzhen.systemrecovery;

/**
 * Created by root on 5/8/17.
 */

public class Volume {
    private String fDisk;
    private String block;
    private long size;
    private long sectorStart;
    private long sectorEnd;
    private String info;

    public long getSectorStart() {
        return sectorStart;
    }
    public long getSectorEnd() {
        return sectorEnd;
    }

    public void setSectorStart(long sectorNum) {
        this.sectorStart = sectorNum;
    }
    public void setSectorEnd(long sectorNum) {
        this.sectorEnd = sectorNum;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getfDisk() {
        return fDisk;
    }
    public String getBlock() {
        return block;
    }

    public void setfDisk(String disk) {
        this.fDisk = disk;
    }
    public void setBlock(String block) {
        this.block = block;
    }
}

package com.example.junzhen.systemrecovery;

import java.util.ArrayList;

/**
 * Created by root on 5/8/17.
 */

public class Disk {
    private String block;
    private long size;
    private ArrayList<Volume> volumes = new ArrayList<>();
    private long sectorSize;
    private long sectorNum;

    public long getSectorSize() {
        return sectorSize;
    }

    public void setSectorSize(long sectorSize) {
        this.sectorSize = sectorSize;
    }

    public long getSectorNum() {
        return sectorNum;
    }

    public void setSectorNum(long sectorNum) {
        this.sectorNum = sectorNum;
    }

    public String getBlock() {
        return block;
    }
    public long getSize() {
        return size;
    }

    public void setBlock(String block) {
        this.block = block;
    }
    public void setSize(long mSize) {
        this.size = mSize;
    }

    public ArrayList<Volume> getVolumes() {
        return volumes;
    }

    public void setVolumes(ArrayList<Volume> volumes) {
        this.volumes = volumes;
    }
}

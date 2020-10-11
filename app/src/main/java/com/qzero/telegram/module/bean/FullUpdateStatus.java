package com.qzero.telegram.module.bean;

public class FullUpdateStatus {

    public static final int FULL_UPDATE_NEEDED_VERSION_CODE=1;

    private int lastFullUpdateVersionCode;

    public FullUpdateStatus() {
    }

    public FullUpdateStatus(int lastFullUpdateVersionCode) {
        this.lastFullUpdateVersionCode = lastFullUpdateVersionCode;
    }

    public int getLastFullUpdateVersionCode() {
        return lastFullUpdateVersionCode;
    }

    public void setLastFullUpdateVersionCode(int lastFullUpdateVersionCode) {
        this.lastFullUpdateVersionCode = lastFullUpdateVersionCode;
    }

    @Override
    public String toString() {
        return "FullUpdateStatus{" +
                "lastFullUpdateVersionCode=" + lastFullUpdateVersionCode +
                '}';
    }
}

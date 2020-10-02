package com.qzero.telegram.module.bean;

public class FullUpdateStatus {

    public static final int NEED_UPDATE_VERSION=1;

    private int lastUpdatedVersion;
    private boolean updated;


    public FullUpdateStatus() {
    }

    public FullUpdateStatus(int lastUpdatedVersion, boolean updated) {
        this.lastUpdatedVersion = lastUpdatedVersion;
        this.updated = updated;
    }

    public int getLastUpdatedVersion() {
        return lastUpdatedVersion;
    }

    public void setLastUpdatedVersion(int lastUpdatedVersion) {
        this.lastUpdatedVersion = lastUpdatedVersion;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "FullUpdateStatus{" +
                "lastUpdatedVersion=" + lastUpdatedVersion +
                ", updated=" + updated +
                '}';
    }
}

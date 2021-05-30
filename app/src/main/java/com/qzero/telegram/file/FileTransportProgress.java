package com.qzero.telegram.file;

public class FileTransportProgress {

    private double percentage;
    private int total;
    private int finished;

    public FileTransportProgress(double percentage, int total, int finished) {
        this.percentage = percentage;
        this.total = total;
        this.finished = finished;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "FileTransportProgress{" +
                "percentage=" + percentage +
                ", total=" + total +
                ", finished=" + finished +
                '}';
    }
}

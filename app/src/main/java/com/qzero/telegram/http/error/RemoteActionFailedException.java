package com.qzero.telegram.http.error;

public class RemoteActionFailedException extends Exception {

    private int errorCode;
    private String remoteMessage;

    public RemoteActionFailedException(int errorCode, String remoteMessage) {
        super(String.format("RemoteMessage\t%s\nErrorCode\t%d", remoteMessage,errorCode));
        this.errorCode = errorCode;
        this.remoteMessage = remoteMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getRemoteMessage() {
        return remoteMessage;
    }
}

package com.qzero.telegram.http.bean;

import com.qzero.telegram.http.exchange.ParameterObject;

@ParameterObject(name = "actionResult")
public class ActionResult {

    private boolean succeeded;
    private int statusCode;
    private String message;

    public ActionResult() {
    }

    public ActionResult(boolean succeeded, String message) {
        this.succeeded=succeeded;
        this.message = message;
    }

    public ActionResult(boolean succeeded, String message, Object resultObject) {
        this.succeeded=succeeded;
        this.message = message;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded=succeeded;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "ActionResult{" +
                "isSucceeded=" + succeeded +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}

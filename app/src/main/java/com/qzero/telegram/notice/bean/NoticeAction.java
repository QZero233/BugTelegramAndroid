package com.qzero.telegram.notice.bean;

import java.util.Map;

public class NoticeAction {

    private String dataType;
    private String actionType;
    private String dataId;
    private Map<String,String> parameter;
    private String operator;

    public NoticeAction() {
    }

    public NoticeAction(String dataType, String actionType, String dataId, Map<String, String> parameter, String operator) {
        this.dataType = dataType;
        this.actionType = actionType;
        this.dataId = dataId;
        this.parameter = parameter;
        this.operator = operator;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Map<String, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "NoticeAction{" +
                "dataType='" + dataType + '\'' +
                ", actionType='" + actionType + '\'' +
                ", dataId='" + dataId + '\'' +
                ", parameter=" + parameter +
                ", operator='" + operator + '\'' +
                '}';
    }

}

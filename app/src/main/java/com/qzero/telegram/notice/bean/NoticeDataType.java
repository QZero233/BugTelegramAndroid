package com.qzero.telegram.notice.bean;

public enum NoticeDataType {

    TYPE_MESSAGE("chatMessage"),
    TYPE_SESSION("chatSession");

    private String typeInString;

    NoticeDataType(String typeInString) {
        this.typeInString = typeInString;
    }

    public String getTypeInString() {
        return typeInString;
    }
}

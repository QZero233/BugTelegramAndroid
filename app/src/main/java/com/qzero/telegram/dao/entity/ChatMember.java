package com.qzero.telegram.dao.entity;

import com.qzero.telegram.http.exchange.ParameterObject;

@ParameterObject(name = "ChatMember")
public class ChatMember {

    private int chatMemberId;

    private String sessionId;

    private String userName;

    private int level;

    public ChatMember() {
    }

    public ChatMember(String sessionId, String userName, int level) {
        this.sessionId = sessionId;
        this.userName = userName;
        this.level = level;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "ChatMember{" +
                "sessionId='" + sessionId + '\'' +
                ", userName='" + userName + '\'' +
                ", level=" + level +
                '}';
    }
}

package com.qzero.telegram.dao.entity;

import com.qzero.telegram.http.exchange.ParameterObject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
@ParameterObject(name = "ChatMember")
public class ChatMember {

    public static final int LEVEL_NORMAL=0;
    public static final int LEVEL_OPERATOR=1;
    public static final int LEVEL_OWNER=2;

    @Id(autoincrement = true)
    @Property(nameInDb = "chatMemberId")
    private Long chatMemberId;

    @Property(nameInDb = "sessionId")
    private String sessionId;

    @Property(nameInDb = "userName")
    private String userName;

    @Property(nameInDb = "level")
    private int level;

    public ChatMember() {
    }

    public ChatMember(String sessionId, String userName, int level) {
        this.sessionId = sessionId;
        this.userName = userName;
        this.level = level;
    }

    @Generated(hash = 1363838781)
    public ChatMember(Long chatMemberId, String sessionId, String userName,
            int level) {
        this.chatMemberId = chatMemberId;
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

    public Long getChatMemberId() {
        return this.chatMemberId;
    }

    public void setChatMemberId(Long chatMemberId) {
        this.chatMemberId = chatMemberId;
    }
}

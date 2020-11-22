package com.qzero.telegram.dao.entity;

import com.qzero.telegram.http.exchange.ParameterObject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.util.Arrays;
import org.greenrobot.greendao.annotation.Generated;

@ParameterObject(name = "ChatMessage")
@Entity(nameInDb = "chat_message")
public class ChatMessage {

    public static final String STATUS_DELETED="#deleted";

    public static final String TYPE_SYSTEM_NOTICE="systemNotice";

    @Id
    @Property(nameInDb = "messageId")
    private String messageId;

    @Property(nameInDb = "senderUserName")
    private String senderUserName;

    @Property(nameInDb = "sessionId")
    private String sessionId;

    @Transient
    private byte[] content;

    @Property(nameInDb = "sendTime")
    private long sendTime;

    @Property(nameInDb = "messageStatus")
    private String messageStatus;

    /**
     * To mark which type of message it belongs to
     * If it's null, it's normal message
     */
    @Property(nameInDb = "messageType")
    private String messageType;

    public ChatMessage() {
    }

    @Generated(hash = 986747747)
    public ChatMessage(String messageId, String senderUserName, String sessionId,
            long sendTime, String messageStatus, String messageType) {
        this.messageId = messageId;
        this.senderUserName = senderUserName;
        this.sessionId = sessionId;
        this.sendTime = sendTime;
        this.messageStatus = messageStatus;
        this.messageType = messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "messageId='" + messageId + '\'' +
                ", senderUserName='" + senderUserName + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", content=" + Arrays.toString(content) +
                ", sendTime=" + sendTime +
                ", messageStatus='" + messageStatus + '\'' +
                '}';
    }

    public String getMessageType() {
        return this.messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}

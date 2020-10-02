package com.qzero.telegram.dao.entity;

import com.qzero.telegram.http.exchange.ParameterObject;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity
@ParameterObject(name = "chat_session")
public class ChatSession {

    @Id
    @Property(nameInDb = "sessionId")
    private String sessionId;

    @Property(nameInDb = "sessionName")
    private String sessionName;

    @Property(nameInDb = "chatMembers")
    @Convert(columnType = String.class,converter = ChatMemberConverter.class)
    private List<ChatMember> chatMembers;

    @Generated(hash = 250435511)
    public ChatSession(String sessionId, String sessionName,
            List<ChatMember> chatMembers) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.chatMembers = chatMembers;
    }

    @Generated(hash = 1350292942)
    public ChatSession() {
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return this.sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public List<ChatMember> getChatMembers() {
        return this.chatMembers;
    }

    public void setChatMembers(List<ChatMember> chatMembers) {
        this.chatMembers = chatMembers;
    }

}

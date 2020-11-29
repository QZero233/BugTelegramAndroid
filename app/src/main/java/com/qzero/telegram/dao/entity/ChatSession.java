package com.qzero.telegram.dao.entity;

import com.qzero.telegram.http.exchange.ParameterObject;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;

import java.util.List;
import java.util.Map;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.qzero.telegram.dao.gen.DaoSession;
import com.qzero.telegram.dao.gen.ChatMemberDao;
import com.qzero.telegram.dao.gen.ChatSessionDao;
import com.qzero.telegram.dao.gen.ChatSessionParameterDao;

@Entity
@ParameterObject(name = "ChatSession")
public class ChatSession {

    @Id
    @Property(nameInDb = "sessionId")
    private String sessionId;

    @ToMany(referencedJoinProperty = "sessionId")
    private List<ChatSessionParameter> sessionParameters;

    @ToMany(referencedJoinProperty = "sessionId")
    private List<ChatMember> chatMembers;

    @Property(nameInDb = "deleted")
    private boolean deleted=false;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1308162568)
    private transient ChatSessionDao myDao;

    @Generated(hash = 1170711239)
    public ChatSession(String sessionId, boolean deleted) {
        this.sessionId = sessionId;
        this.deleted = deleted;
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

    public boolean getDeleted() {
        return this.deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Keep
    public String getSessionParameter(String parameterName){
        if(daoSession==null){
            if(sessionParameters==null)
                return null;

            for(ChatSessionParameter parameter:sessionParameters){
                if(parameter.getParameterName().equals(parameterName))
                    return parameter.getParameterValue();
            }

            return null;
        }else{
            ChatSessionParameterDao parameterDao=daoSession.getChatSessionParameterDao();
            QueryBuilder queryBuilder =parameterDao.queryBuilder().where(ChatSessionParameterDao.Properties.SessionId.eq(sessionId),
                    ChatSessionParameterDao.Properties.ParameterName.eq(parameterName));

            if(queryBuilder.count()!=1)
                return null;

            ChatSessionParameter parameter= (ChatSessionParameter) queryBuilder.unique();
            return parameter.getParameterValue();
        }
    }

    @Keep
    public void setSessionParameters(List<ChatSessionParameter> sessionParameters) {
        this.sessionParameters = sessionParameters;
    }

    @Keep
    public void setChatMembers(List<ChatMember> chatMembers) {
        this.chatMembers = chatMembers;
    }

    @Keep
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 563070834)
    public List<ChatMember> getChatMembers() {
        if (chatMembers == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChatMemberDao targetDao = daoSession.getChatMemberDao();
            List<ChatMember> chatMembersNew = targetDao
                    ._queryChatSession_ChatMembers(sessionId);
            synchronized (this) {
                if (chatMembers == null) {
                    chatMembers = chatMembersNew;
                }
            }
        }
        return chatMembers;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 670580613)
    public synchronized void resetChatMembers() {
        chatMembers = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 776461846)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getChatSessionDao() : null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 433470329)
    public List<ChatSessionParameter> getSessionParameters() {
        if (sessionParameters == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChatSessionParameterDao targetDao = daoSession.getChatSessionParameterDao();
            List<ChatSessionParameter> sessionParametersNew = targetDao
                    ._queryChatSession_SessionParameters(sessionId);
            synchronized (this) {
                if (sessionParameters == null) {
                    sessionParameters = sessionParametersNew;
                }
            }
        }
        return sessionParameters;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 769195259)
    public synchronized void resetSessionParameters() {
        sessionParameters = null;
    }
}

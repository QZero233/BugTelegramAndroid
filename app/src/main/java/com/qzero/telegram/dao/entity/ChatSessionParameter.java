package com.qzero.telegram.dao.entity;

import com.qzero.telegram.http.exchange.ParameterObject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
@ParameterObject(name = "ChatSessionParameter")
public class ChatSessionParameter {

    public static final String NAME_SESSION_NAME="sessionName";
    public static final String NAME_SESSION_TYPE="sessionType";
    public static final String NAME_SESSION_SECRET_KEY="sessionSecretKey";

    public static final String SESSION_TYPE_NORMAL="normal";
    public static final String SESSION_TYPE_SECRET="secret";
    public static final String SESSION_TYPE_PERSONAL="personal";

    @Id(autoincrement = true)
    private Long parameterId;

    @Property(nameInDb = "sessionId")
    private String sessionId;

    @Property(nameInDb = "parameterName")
    private String parameterName;

    @Property(nameInDb = "parameterValue")
    private String parameterValue;

    @Generated(hash = 1313765492)
    public ChatSessionParameter(Long parameterId, String sessionId,
            String parameterName, String parameterValue) {
        this.parameterId = parameterId;
        this.sessionId = sessionId;
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    @Generated(hash = 1681852575)
    public ChatSessionParameter() {
    }
    
    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    @Override
    public String toString() {
        return "ChatSessionParameter{" +
                "parameterId=" + parameterId +
                ", sessionId='" + sessionId + '\'' +
                ", parameterName='" + parameterName + '\'' +
                ", parameterValue='" + parameterValue + '\'' +
                '}';
    }
}

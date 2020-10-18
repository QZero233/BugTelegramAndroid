package com.qzero.telegram.dao.entity;


import com.qzero.telegram.http.exchange.ParameterObject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
@ParameterObject(name = "UserInfo")
public class UserInfo {

    public static final int STATUS_OFFLINE=0;
    public static final int STATUS_ONLINE=1;
    public static final int STATUS_BUSY=2;
    public static final int STATUS_LEAVING=3;

    public static final int GROUP_USER=0;
    public static final int GROUP_ADMIN=1;
    public static final int GROUP_SYSTEM_ADMIN=2;


    /**
     * The username
     */
    @Id
    @Property(nameInDb = "userName")
    private String userName;


    /**
     * The account status
     * It can be seen by everyone
     * It can be Busy,Leaving etc.
     */
    @Property(nameInDb = "accountStatus")
    private int accountStatus;

    /**
     * The motto of the account
     * It can also be seen by everyone
     */
    @Property(nameInDb = "motto")
    private String motto;

    /**
     * The group level which the account belongs to
     */
    @Property(nameInDb = "groupLevel")
    private int groupLevel;

    public UserInfo() {
    }

    @Generated(hash = 1866952460)
    public UserInfo(String userName, int accountStatus, String motto,
            int groupLevel) {
        this.userName = userName;
        this.accountStatus = accountStatus;
        this.motto = motto;
        this.groupLevel = groupLevel;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public int getGroupLevel() {
        return groupLevel;
    }

    public void setGroupLevel(int groupLevel) {
        this.groupLevel = groupLevel;
    }

    @Override
    public String toString() {
        return "UserInfoEntity{" +
                "userName='" + userName + '\'' +
                ", accountStatus=" + accountStatus +
                ", motto='" + motto + '\'' +
                ", groupLevel=" + groupLevel +
                '}';
    }
}

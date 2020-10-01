package com.qzero.telegram.http.bean;


import com.qzero.telegram.http.exchange.ParameterObject;

@ParameterObject(name = "token")
public class Token {

    public static final int APP_ID_GUARD=1;

    public static final int APP_ID_BT=2;

    /**
     * Which means you can use this to access all applications
     * Including 'Guard'
     */
    public static final int PERMISSION_LEVEL_GLOBAL=1;
    /**
     * Can only be used in specified application
     */
    public static final int PERMISSION_LEVEL_APPLICATION=2;

    /**
     * The id of the token
     */
    private String tokenId;

    /**
     * The userName of the token's owner
     */
    private String ownerUserName;

    /**
     * The time when the token is generated
     */
    private long generateTime;

    /**
     * The time when the token will be expired
     * If it is less or equal than 0,it will always exist
     */
    private long endTime;

    /**
     * The id of the application sending login request
     * Specially,app 'Guard' must login through codeHash
     */
    private int applicationId;

    /**
     * The description of the token
     * Just used to show users
     */
    private String tokenDescription;

    /**
     * Permission level of the token
     */
    private int permissionLevel;

    public Token() {
    }

    public Token(String tokenId, String ownUserName, long generateTime, long endTime, int applicationId, String tokenDescription, int permissionLevel) {
        this.tokenId = tokenId;
        this.ownerUserName = ownUserName;
        this.generateTime = generateTime;
        this.endTime = endTime;
        this.applicationId = applicationId;
        this.tokenDescription = tokenDescription;
        this.permissionLevel = permissionLevel;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownUserName) {
        this.ownerUserName = ownUserName;
    }

    public long getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(long generateTime) {
        this.generateTime = generateTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getTokenDescription() {
        return tokenDescription;
    }

    public void setTokenDescription(String tokenDescription) {
        this.tokenDescription = tokenDescription;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    @Override
    public String toString() {
        return "TokenEntity{" +
                "tokenId='" + tokenId + '\'' +
                ", ownUserName='" + ownerUserName + '\'' +
                ", generateTime=" + generateTime +
                ", endTime=" + endTime +
                ", applicationId=" + applicationId +
                ", tokenDescription='" + tokenDescription + '\'' +
                ", permissionLevel=" + permissionLevel +
                '}';
    }
}

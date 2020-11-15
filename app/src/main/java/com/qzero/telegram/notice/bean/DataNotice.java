package com.qzero.telegram.notice.bean;


public class DataNotice {

    private String noticeId;

    private String targetUserName;

    private String actionDetail;

    private Long generateTime;

    public DataNotice() {
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public String getActionDetail() {
        return actionDetail;
    }

    public void setActionDetail(String actionDetail) {
        this.actionDetail = actionDetail;
    }

    public Long getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(Long generateTime) {
        this.generateTime = generateTime;
    }

    @Override
    public String toString() {
        return "DataNotice{" +
                "noticeId='" + noticeId + '\'' +
                ", targetUserName='" + targetUserName + '\'' +
                ", actionDetail='" + actionDetail + '\'' +
                ", generateTime=" + generateTime +
                '}';
    }
}

package com.qzero.telegram.dao.entity;


import com.qzero.telegram.http.exchange.ParameterObject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "file_resource")
@ParameterObject(name = "FileResource")
public class FileResource {

    public static final int STATUS_ERROR=-1;
    public static final int STATUS_TRANSPORTING=0;
    public static final int STATUS_READY=1;
    public static final int STATUS_FREEZING=2;

    @Id
    @Property(nameInDb = "resourceId")
    private String resourceId;

    @Property(nameInDb = "resourceName")
    private String resourceName;

    @Property(nameInDb = "resourceLength")
    private Long resourceLength;

    @Property(nameInDb = "resourceStatus")
    private int resourceStatus;

    public FileResource() {
    }


    @Generated(hash = 45741967)
    public FileResource(String resourceId, String resourceName, Long resourceLength,
            int resourceStatus) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceLength = resourceLength;
        this.resourceStatus = resourceStatus;
    }
    

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Long getResourceLength() {
        return resourceLength;
    }

    public void setResourceLength(Long resourceLength) {
        this.resourceLength = resourceLength;
    }

    public int getResourceStatus() {
        return resourceStatus;
    }

    public void setResourceStatus(int resourceStatus) {
        this.resourceStatus = resourceStatus;
    }

    @Override
    public String toString() {
        return "FileResource{" +
                "resourceId='" + resourceId + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", resourceLength=" + resourceLength +
                ", resourceStatus=" + resourceStatus +
                '}';
    }
}

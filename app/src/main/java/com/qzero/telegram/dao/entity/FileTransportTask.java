package com.qzero.telegram.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "file_transport_task")
public class FileTransportTask {

    @Id
    @Property(nameInDb = "resourceId")
    private String resourceId;

    @Property(nameInDb = "resourceId")
    private String fileName;

    @Property(nameInDb = "resourceId")
    private long fileLength;

    @Property(nameInDb = "resourceId")
    private long blockLength;

    @Property(nameInDb = "fullPath")
    private String fullPath;


    @Generated(hash = 1276046795)
    public FileTransportTask() {
    }

    @Generated(hash = 571303956)
    public FileTransportTask(String resourceId, String fileName, long fileLength,
            long blockLength, String fullPath) {
        this.resourceId = resourceId;
        this.fileName = fileName;
        this.fileLength = fileLength;
        this.blockLength = blockLength;
        this.fullPath = fullPath;
    }

    @Override
    public String toString() {
        return "FileTransportTask{" +
                "resourceId='" + resourceId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileLength=" + fileLength +
                ", blockLength=" + blockLength +
                '}';
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileLength() {
        return this.fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public long getBlockLength() {
        return this.blockLength;
    }

    public void setBlockLength(long blockLength) {
        this.blockLength = blockLength;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
}

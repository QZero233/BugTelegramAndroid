package com.qzero.telegram.dao.entity;

import com.qzero.telegram.dao.converter.TransportedIndexConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.util.List;

@Entity(nameInDb = "file_transport_task")
public class FileTransportTask {

    public static final int TRANSPORT_TYPE_UPLOAD=1;
    public static final int TRANSPORT_TYPE_DOWNLOAD=2;

    public static final int DEFAULT_BLOCK_LENGTH=500000;//0.5MB

    @Id
    @Property(nameInDb = "resourceId")
    private String resourceId;

    @Property(nameInDb = "fileName")
    private String fileName;

    @Property(nameInDb = "fileLength")
    private long fileLength;

    @Property(nameInDb = "blockLength")
    private int blockLength;

    @Property(nameInDb = "fullPath")
    private String fullPath;

    @Property(nameInDb = "transportType")
    private int transportType;

    @Property(nameInDb = "transportedBlockIndexes")
    @Convert(columnType = String.class,converter = TransportedIndexConverter.class)
    private List<Integer> transportedBlockIndexes;

    @Generated(hash = 1276046795)
    public FileTransportTask() {
    }


    @Generated(hash = 1370488288)
    public FileTransportTask(String resourceId, String fileName, long fileLength,
            int blockLength, String fullPath, int transportType,
            List<Integer> transportedBlockIndexes) {
        this.resourceId = resourceId;
        this.fileName = fileName;
        this.fileLength = fileLength;
        this.blockLength = blockLength;
        this.fullPath = fullPath;
        this.transportType = transportType;
        this.transportedBlockIndexes = transportedBlockIndexes;
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

    @Keep
    public int calculateBlockCount(){
        if(fileLength%blockLength==0)
            return (int) (fileLength/blockLength);

        return (int) (fileLength/blockLength)+1;
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

    public int getBlockLength() {
        return this.blockLength;
    }

    public void setBlockLength(int blockLength) {
        this.blockLength = blockLength;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public List<Integer> getTransportedBlockIndexes() {
        return this.transportedBlockIndexes;
    }

    public void setTransportedBlockIndexes(List<Integer> transportedBlockIndexes) {
        this.transportedBlockIndexes = transportedBlockIndexes;
    }

    public int getTransportType() {
        return this.transportType;
    }

    public void setTransportType(int transportType) {
        this.transportType = transportType;
    }
}

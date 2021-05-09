package com.qzero.telegram.module.impl;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.FileTransportTask;
import com.qzero.telegram.dao.gen.FileTransportTaskDao;
import com.qzero.telegram.http.RetrofitHelper;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.http.exchange.CommonPackedObject;
import com.qzero.telegram.http.exchange.CommonPackedObjectFactory;
import com.qzero.telegram.http.exchange.PackedObject;
import com.qzero.telegram.http.service.DefaultTransformer;
import com.qzero.telegram.http.service.FileTransportService;
import com.qzero.telegram.module.FileTransportModule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;

public class FileTransportModuleImpl implements FileTransportModule {

    private Context context;
    private FileTransportTaskDao taskDao;
    private FileTransportService transportService;

    private CommonPackedObjectFactory packedObjectFactory=new CommonPackedObjectFactory();

    public FileTransportModuleImpl(Context context) {
        this.context = context;
        taskDao= SessionManager.getInstance(context).getSession().getFileTransportTaskDao();
        transportService= RetrofitHelper.getInstance(context).getService(FileTransportService.class);
    }

    @Override
    public Observable<ActionResult> uploadFileBlock(String resourceId, int blockIndex) throws IOException {
        FileTransportTask task=taskDao.load(resourceId);
        if(task==null)
            return Observable.error(new IllegalArgumentException("Transport task does not exist"));

        if(task.getTransportType()!=FileTransportTask.TRANSPORT_TYPE_UPLOAD){
            return Observable.error(new IllegalArgumentException("This is not a upload task"));
        }

        File file=new File(task.getFullPath());
        RandomAccessFile randomAccessFile=new RandomAccessFile(file,"r");

        long offset=blockIndex*task.getBlockLength();
        long length=task.getBlockLength();
        if(offset+task.getBlockLength()>task.getFileLength()){
            length=task.getFileLength()-offset;
        }
        randomAccessFile.seek(offset);

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream((int) length);

        byte[] buf=new byte[2048];
        int len;
        int totalLen=0;
        while((len=randomAccessFile.read(buf))!=-1){
            totalLen+=len;
            if(totalLen>length){
                len= (int) (length-(totalLen-len));
            }

            byteArrayOutputStream.write(buf,0,len);
        }

        RequestBody responseBody=RequestBody.create(byteArrayOutputStream.toByteArray());
        return transportService.uploadFileBlock(responseBody,resourceId,blockIndex)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                    ActionResult actionResult=packedObject.parseObject(ActionResult.class);

                    if(actionResult.isSucceeded()){
                        task.getTransportedBlockIndexes().add(blockIndex);
                        taskDao.insertOrReplace(task);
                    }

                    return Observable.just(actionResult);
                });
    }

    @Override
    public Observable<ActionResult> downloadFileBlock(String resourceId, int blockIndex) throws IOException {
        FileTransportTask task=taskDao.load(resourceId);
        if(task==null)
            return Observable.error(new IllegalArgumentException("Transport task does not exist"));

        if(task.getTransportType()!=FileTransportTask.TRANSPORT_TYPE_DOWNLOAD){
            return Observable.error(new IllegalArgumentException("This is not a download task"));
        }

        File file=new File(task.getFullPath());
        RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");

        long offset=blockIndex*task.getBlockLength();
        long length=task.getBlockLength();
        if(offset+task.getBlockLength()>task.getFileLength()){
            length=task.getFileLength()-offset;
        }
        randomAccessFile.seek(offset);

        return transportService.downloadFileBlock(resourceId,offset,length)
                .flatMap(responseBody -> {
                    byte[] buf=responseBody.bytes();

                    if(responseBody.contentType().equals("application/octet-stream")){
                        randomAccessFile.write(buf);

                        PackedObject packedObject=packedObjectFactory.getPackedObject();
                        packedObject.addObject(new ActionResult(true,null));
                        return Observable.just(packedObject);
                    }else{
                        ObjectMapper objectMapper=new ObjectMapper();
                        return Observable.just(objectMapper.readValue(buf, CommonPackedObject.class));
                    }
                })
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                    ActionResult actionResult=packedObject.parseObject(ActionResult.class);

                    if(actionResult.isSucceeded()){
                        task.getTransportedBlockIndexes().add(blockIndex);
                        taskDao.insertOrReplace(task);
                    }

                    return Observable.just(actionResult);
                });
    }

    

    @Override
    public FileTransportTask getTransportTask(String resourceId) {
        return null;
    }

    @Override
    public void deleteTransportTask(String resourceId) {

    }


}

package com.qzero.telegram.module.impl;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.FileResource;
import com.qzero.telegram.dao.entity.FileTransportTask;
import com.qzero.telegram.dao.gen.FileResourceDao;
import com.qzero.telegram.dao.gen.FileTransportTaskDao;
import com.qzero.telegram.http.RetrofitHelper;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.http.exchange.CommonPackedObject;
import com.qzero.telegram.http.exchange.CommonPackedObjectFactory;
import com.qzero.telegram.http.exchange.PackedObject;
import com.qzero.telegram.http.service.DefaultTransformer;
import com.qzero.telegram.http.service.FileResourceService;
import com.qzero.telegram.http.service.FileTransportService;
import com.qzero.telegram.module.FileTransportModule;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileTransportModuleImpl implements FileTransportModule {

    private Context context;

    private FileTransportTaskDao taskDao;
    private FileResourceDao resourceDao;

    private FileTransportService transportService;
    private FileResourceService resourceService;

    private CommonPackedObjectFactory packedObjectFactory=new CommonPackedObjectFactory();

    public FileTransportModuleImpl(Context context) {
        this.context = context;

        taskDao= SessionManager.getInstance(context).getSession().getFileTransportTaskDao();
        resourceDao=SessionManager.getInstance(context).getSession().getFileResourceDao();

        transportService= RetrofitHelper.getInstance(context).getService(FileTransportService.class);
        resourceService=RetrofitHelper.getInstance(context).getService(FileResourceService.class);
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
        int length=task.getBlockLength();
        if(offset+task.getBlockLength()>task.getFileLength()){
            length= (int) (task.getFileLength()-offset);
        }
        randomAccessFile.seek(offset);

        byte[] buf=new byte[length];
        randomAccessFile.readFully(buf);

        RequestBody requestBody=RequestBody.create(buf);

        MultipartBody.Part part=MultipartBody.Part.createFormData("resource_file",file.getName(),requestBody);
        return transportService.uploadFile(part,resourceId,offset,length)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                    ActionResult actionResult=packedObject.parseObject(ActionResult.class);

                    if(actionResult.isSucceeded()){
                        if(task.getTransportedBlockIndexes()==null)
                            task.setTransportedBlockIndexes(new ArrayList<>());
                        task.getTransportedBlockIndexes().add(blockIndex);
                        taskDao.insertOrReplace(task);

                        if(task.getTransportedBlockIndexes().size()==task.calculateBlockCount()){
                            return markTaskFinished(resourceId)
                                    .flatMap(actionResult1 -> {
                                        taskDao.delete(task);
                                       return Observable.just(actionResult1);
                                    });
                        }
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
        int length=task.getBlockLength();
        if(offset+task.getBlockLength()>task.getFileLength()){
            length= (int) (task.getFileLength()-offset);
        }
        randomAccessFile.seek(offset);

        return transportService.downloadFile(resourceId,offset,length)
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
                        if(task.getTransportedBlockIndexes()==null)
                            task.setTransportedBlockIndexes(new ArrayList<>());
                        task.getTransportedBlockIndexes().add(blockIndex);
                        taskDao.insertOrReplace(task);
                    }

                    return Observable.just(actionResult);
                });
    }

    @Override
    public Observable<ActionResult> markTaskFinished(String resourceId) {
        return transportService.markTaskFinished(resourceId)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                    FileTransportTask task=taskDao.load(resourceId);
                    if(task!=null)
                        taskDao.delete(task);

                    FileResource resource=resourceDao.load(resourceId);
                    if(resource!=null){
                        resource.setResourceStatus(FileResource.STATUS_READY);
                        resourceDao.insertOrReplace(resource);
                    }

                   return Observable.just(packedObject.parseObject(ActionResult.class));
                });
    }

    @Override
    public void deleteDownloadTask(String resourceId){
        FileTransportTask task=taskDao.load(resourceId);
        if(task==null)
            throw new IllegalArgumentException("Transport task does not exist");

        File file=new File(task.getFullPath());
        if(file.exists())
            file.delete();

        taskDao.delete(task);
    }

    @Override
    public Observable<ActionResult> deleteUploadTask(String resourceId) {
        FileTransportTask task=taskDao.load(resourceId);
        if(task==null)
            return Observable.error(new IllegalArgumentException("Transport task does not exist"));

        return resourceService.deleteFileResource(resourceId)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                    ActionResult actionResult=packedObject.parseObject(ActionResult.class);

                    if(actionResult.isSucceeded()){
                        taskDao.delete(task);
                    }

                    return Observable.just(actionResult);
                });
    }

}

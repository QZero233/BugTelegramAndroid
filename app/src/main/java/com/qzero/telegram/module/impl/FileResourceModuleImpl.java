package com.qzero.telegram.module.impl;

import android.content.Context;

import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.FileTransportTask;
import com.qzero.telegram.dao.gen.FileResourceDao;
import com.qzero.telegram.dao.gen.FileTransportTaskDao;
import com.qzero.telegram.http.RetrofitHelper;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.dao.entity.FileResource;
import com.qzero.telegram.http.exchange.CommonPackedObjectFactory;
import com.qzero.telegram.http.exchange.PackedObject;
import com.qzero.telegram.http.exchange.PackedObjectFactory;
import com.qzero.telegram.http.service.DefaultTransformer;
import com.qzero.telegram.http.service.FileResourceService;
import com.qzero.telegram.module.FileResourceModule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class FileResourceModuleImpl implements FileResourceModule {

    private Context context;

    private FileResourceService resourceService;
    private FileTransportTaskDao taskDao;
    private FileResourceDao resourceDao;

    private PackedObjectFactory objectFactory=new CommonPackedObjectFactory();

    public FileResourceModuleImpl(Context context) {
        this.context = context;

        resourceService= RetrofitHelper.getInstance(context).getService(FileResourceService.class);
        taskDao= SessionManager.getInstance(context).getSession().getFileTransportTaskDao();
        resourceDao=SessionManager.getInstance(context).getSession().getFileResourceDao();
    }

    @Override
    public Observable<ActionResult> newFileResource(File resource, long resourceLength, long transportBlockSize) {
        FileResource fileResource=new FileResource();
        fileResource.setResourceName(resource.getName());
        fileResource.setResourceLength(resourceLength);

        PackedObject packedObject=objectFactory.getPackedObject();
        packedObject.addObject(fileResource);

        return resourceService.newFileResource(packedObject,transportBlockSize)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject1 -> {
                    ActionResult actionResult=packedObject1.parseObject(ActionResult.class);

                    String resourceId=actionResult.getMessage();
                    fileResource.setResourceId(resourceId);
                    fileResource.setResourceStatus(FileResource.STATUS_TRANSPORTING);
                    resourceDao.insertOrReplace(fileResource);

                    FileTransportTask task=new FileTransportTask();
                    task.setResourceId(resourceId);
                    task.setFileLength(resourceLength);
                    task.setBlockLength(transportBlockSize);
                    task.setFileName(resource.getName());
                    task.setFullPath(resource.getAbsolutePath());
                    taskDao.insertOrReplace(task);

                    return Observable.just(actionResult);
                });
    }

    @Override
    public Observable<ActionResult> deleteFileResource(String resourceId) {
        return resourceService.deleteFileResource(resourceId)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                    FileTransportTask task=taskDao.load(resourceId);
                    if(task!=null){
                        taskDao.delete(task);
                    }

                    FileResource resource=resourceDao.load(resourceId);
                    if(resource!=null){
                        resourceDao.delete(resource);
                    }

                    return Observable.just(packedObject.parseObject(ActionResult.class));
                });
    }

    @Override
    public Observable<FileResource> getFileResourceInfo(String resourceId) {
        return resourceService.getFileResourceInfo(resourceId)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                    FileResource fileResource=packedObject.parseObject(FileResource.class);

                    if(fileResource!=null){
                        resourceDao.insertOrReplace(fileResource);
                    }

                    return Observable.just(fileResource);
                });
    }

    @Override
    public Observable<List<FileResource>> getAllFileResources() {
        return resourceService.getAllFileResources()
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                   List<FileResource> fileResources= (List<FileResource>) packedObject.parseCollectionObject("FileResourceList",
                           FileResource.class, List.class);

                   if(fileResources!=null){
                       for(FileResource fileResource:fileResources){
                           resourceDao.insertOrReplace(fileResource);
                       }
                   }

                   return Observable.just(fileResources);
                });
    }

}

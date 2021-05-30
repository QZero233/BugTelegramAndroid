package com.qzero.telegram.file;

import android.content.Context;

import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.FileTransportTask;
import com.qzero.telegram.dao.gen.FileTransportTaskDao;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.FileTransportModule;
import com.qzero.telegram.module.impl.FileTransportModuleImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class FileTransportTaskRunner extends Thread {

    private FileTransportTask task;
    private boolean running=true;

    private FileTransportTaskDao taskDao;
    private FileTransportModule transportModule;

    private Logger log= LoggerFactory.getLogger(getClass());

    public FileTransportTaskRunner(String resourceId, Context context) {
        taskDao= SessionManager.getInstance(context).getSession().getFileTransportTaskDao();
        transportModule=new FileTransportModuleImpl(context);

        task=taskDao.load(resourceId);
    }

    @Override
    public void run() {
        super.run();

        int blockCount=task.calculateBlockCount();
        List<Integer> transportedIndexes=task.getTransportedBlockIndexes();
        if(transportedIndexes==null)
            transportedIndexes=new ArrayList<>();

        for(Integer i=0;i<blockCount;i++){
            final int blockIndex=i;
            if(transportedIndexes.contains(i))
                continue;
            if(!running)
                break;

            if(task.getTransportType()==FileTransportTask.TRANSPORT_TYPE_UPLOAD){
                try {
                    transportModule.uploadFileBlock(task.getResourceId(),i)
                            .subscribe(new Observer<ActionResult>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@NonNull ActionResult actionResult) {

                                }

                                @Override
                                public void onError(@NonNull Throwable e){
                                    log.error(String.format("Failed to upload resource block (id:%s, blockIndex:%d)", task.getResourceId(),blockIndex),e);
                                }

                                @Override
                                public void onComplete() {
                                    log.debug(String.format("Uploaded resource block (id:%s, blockIndex:%d)", task.getResourceId(),blockIndex));
                                }
                            });
                } catch (IOException e) {
                    log.error(String.format("Failed to upload resource (id:%s, blockIndex:%d)", task.getResourceId(),i),e);
                }
            }else{
                try {
                    transportModule.downloadFileBlock(task.getResourceId(),i)
                            .subscribe(new Observer<ActionResult>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@NonNull ActionResult actionResult) {

                                }

                                @Override
                                public void onError(@NonNull Throwable e){
                                    log.error(String.format("Failed to download resource block (id:%s, blockIndex:%d)", task.getResourceId(),blockIndex),e);
                                }

                                @Override
                                public void onComplete() {
                                    log.debug(String.format("Downloaded resource block (id:%s, blockIndex:%d)", task.getResourceId(),blockIndex));
                                }
                            });
                } catch (IOException e) {
                    log.error(String.format("Failed to download resource (id:%s, blockIndex:%d)", task.getResourceId(),i),e);
                }
            }
        }

    }

    public void stopTask(){
        running=false;
    }
}

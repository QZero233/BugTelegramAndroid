package com.qzero.telegram.notice.processor;

import android.content.Context;
import android.text.TextUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.NoticeModule;
import com.qzero.telegram.module.impl.NoticeModuleImpl;
import com.qzero.telegram.notice.bean.DataNotice;
import com.qzero.telegram.notice.bean.NoticeAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class NoticeProcessorManager {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Context context;
    private Map<String,NoticeProcessor> processorMap=new HashMap<>();

    //If a process is running
    //Variable processing will be true
    //Whenever if a new call comes
    //runningCount will add 1
    //When the a process is over
    //It will minus 1
    //And it will check runningCount and repeat until it comes to 0
    private Integer runningCount=0;
    private boolean processing=false;

    private NoticeModule noticeModule;

    private ObjectMapper objectMapper;

    public NoticeProcessorManager(Context context) {
        this.context = context;
        noticeModule=new NoticeModuleImpl(context);

        loadProcessors();

        objectMapper=new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private void loadProcessors(){
        addProcessor(new MessageNoticeProcession(context));
        addProcessor(new SessionNoticeProcessor(context));
    }

    private void addProcessor(NoticeProcessor processor){
        processorMap.put(processor.getDataType().getTypeInString(),processor);
    }


    private void processNoticeList(List<DataNotice> noticeList){
        for(DataNotice notice:noticeList){
            String actionDetail=notice.getActionDetail();

            if(TextUtils.isEmpty(actionDetail)){
                noticeModule.deleteNotice(notice.getNoticeId()).subscribe(actionResult -> {log.debug("Deleted empty notice with id "+notice.getNoticeId());},
                        e -> {log.error("Failed to delete notice with id "+notice.getNoticeId(),e);});
                return;
            }

            NoticeAction action;
            try {
                action=objectMapper.readValue(actionDetail,NoticeAction.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse notice action with id "+notice.getNoticeId(),e);
                return;
            }

            NoticeProcessor processor=processorMap.get(action.getDataType());
            if(processorMap!=null){
                notice.setActionDetail(null);
                if(processor.processNotice(notice,action)){
                    //Do deleting
                    noticeModule.deleteNotice(notice.getNoticeId())
                            .subscribe(new Observer<ActionResult>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@NonNull ActionResult actionResult) {
                                    log.info(String.format("Delete notice with id %s ,result is %s", notice.getNoticeId(),actionResult+""));
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    log.error("Failed to delete notice with id "+notice.getNoticeId(),e);
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }
            }
        }
    }

    /**
     *
     * @param isRepeat It can be true only when the method is called in this method
     */
    public synchronized void getAndProcessNotice(boolean isRepeat){
        if(!isRepeat)
            modifyRunningCount(1);

        if(getRunningCount()<=0)
            return;

        if(processing && !isRepeat){
            log.debug("Another processing is running,have to wait");
            log.debug(String.format("Till now there have been %d on queue", getRunningCount()));
            return;
        }

        noticeModule.getAllNotices()
                .subscribe(new Observer<List<DataNotice>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<DataNotice> dataNotices) {
                        processNoticeList(dataNotices);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        log.error("Failed to get notice list",e);
                        modifyRunningCount(-1);
                        getAndProcessNotice(true);
                    }

                    @Override
                    public void onComplete() {
                        modifyRunningCount(-1);
                        getAndProcessNotice(true);

                    }
                });
    }

    private void modifyRunningCount(int relativeValue){
        synchronized (runningCount){
            runningCount+=relativeValue;
        }
    }

    private int getRunningCount(){
        synchronized (runningCount){
            return runningCount;
        }
    }

}

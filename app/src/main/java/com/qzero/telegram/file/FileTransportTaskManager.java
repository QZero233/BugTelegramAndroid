package com.qzero.telegram.file;

import android.content.Context;

import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.FileTransportTask;
import com.qzero.telegram.dao.gen.FileTransportTaskDao;

import java.util.HashMap;
import java.util.Map;

public class FileTransportTaskManager {

    private static FileTransportTaskManager instance;

    private Context context;

    private FileTransportTaskDao taskDao;

    private Map<String,FileTransportTaskRunner> runnerMap=new HashMap<>();

    private FileTransportTaskManager(){

    }

    public void loadContext(Context context){
        this.context=context;
        taskDao= SessionManager.getInstance(context).getSession().getFileTransportTaskDao();
    }

    public static FileTransportTaskManager getInstance() {
        if(instance==null)
            instance=new FileTransportTaskManager();
        return instance;
    }

    public FileTransportProgress getProgress(String resourceId){
        FileTransportTask task=taskDao.load(resourceId);
        if(task==null)
            throw new IllegalArgumentException("File transport task does not exist(resourceId:)"+resourceId);

        int total=task.calculateBlockCount();
        int finished;

        if(task.getTransportedBlockIndexes()==null)
            finished=0;
        else
            finished=task.getTransportedBlockIndexes().size();

        return new FileTransportProgress((double)finished/(double)total,total,finished);
    }

    public boolean isTaskRunning(String resourceId){
        return runnerMap.containsKey(resourceId);
    }

    public void startTask(String resourceId){
        if(runnerMap.containsKey(resourceId))
            return;

        FileTransportTaskRunner runner=new FileTransportTaskRunner(resourceId,context);
        runnerMap.put(resourceId,runner);
        runner.start();
    }

    public void stopTask(String resourceId){
        if(!runnerMap.containsKey(resourceId))
            return;

        FileTransportTaskRunner runner=runnerMap.get(resourceId);
        runner.stopTask();
        runnerMap.remove(resourceId);
    }

}

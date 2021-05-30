package com.qzero.telegram;

import android.app.Application;


import com.qzero.telegram.file.FileTransportTaskManager;

import org.apache.log4j.Level;

import de.mindpipe.android.logging.log4j.LogConfigurator;


public class TelegramApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        configureLog();

        FileTransportTaskManager.getInstance().loadContext(this);
    }

    private void configureLog(){
        LogConfigurator logConfigurator=new LogConfigurator();
        logConfigurator.setRootLevel(Level.DEBUG);
        logConfigurator.setLevel("com.qzero", Level.DEBUG);
        logConfigurator.setUseLogCatAppender(true);
        logConfigurator.setLogCatPattern("[BTLOG][%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} method:%l%n%m%n");
        logConfigurator.setUseFileAppender(false);
        logConfigurator.configure();
    }
}

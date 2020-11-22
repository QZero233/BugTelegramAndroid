package com.qzero.telegram.notice;

import com.qzero.telegram.R;
import com.qzero.telegram.module.bean.NoticeConnectInfo;
import com.qzero.telegram.notice.processor.NoticeProcessorManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.Socket;

public class NoticeMonitorThread extends Thread {

    public static final byte REMIND_CONSTANT='c';

    private Socket socket;
    private String ip;
    private int port;

    private NoticeMonitorService service;
    private boolean isAlive=true;

    private Logger log= LoggerFactory.getLogger(getName());

    private NoticeProcessorManager processorManager;

    public NoticeMonitorThread(NoticeMonitorService service, NoticeConnectInfo connectInfo) {
        this.service=service;
        ip=service.getString(R.string.server_ip);
        port=connectInfo.getPort();

        processorManager=NoticeProcessorManager.getInstance(service);
    }

    public boolean isConnectionAlive(){
        return isAlive;
    }

    @Override
    public void run() {
        super.run();

        try {
            socket=new Socket(ip,port);
            InputStream inputStream=socket.getInputStream();

            log.debug("Monitor connection is running......");

            while (true){
                byte b= (byte) inputStream.read();
                if(b==REMIND_CONSTANT){
                    log.info("Got notice remind");
                    Thread.sleep(500);

                    processorManager.getAndProcessNotice(false);
                }else if(b==0){
                    break;
                }
            }
        }catch (Exception e){
            log.error("Error when monitoring notice update",e);
        }

        isAlive=false;
        log.info("Quit notice monitor thread");
        log.info("Trying to restart thread");
        service.startMonitorThread();
    }
}

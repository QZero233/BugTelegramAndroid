package com.qzero.telegram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.impl.LocalDataStorageImpl;
import com.qzero.telegram.http.bean.Token;
import com.qzero.telegram.module.FullUpdateModule;
import com.qzero.telegram.module.impl.FullUpdateModuleImpl;
import com.qzero.telegram.notice.processor.NoticeProcessorManager;
import com.qzero.telegram.view.activity.FullUpdateActivity;
import com.qzero.telegram.view.activity.LoginActivity;
import com.qzero.telegram.view.activity.UserCenterActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            LocalDataStorage localDataStorage=new LocalDataStorageImpl(this);
            Token token=localDataStorage.getObject(LocalDataStorage.NAME_LOCAL_TOKEN,Token.class);
            if(token==null){
                log.info("No token yet,jump to login");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }else{

                log.info("Already have had a token");

                FullUpdateModule fullUpdateModule=new FullUpdateModuleImpl(this);
                if(fullUpdateModule.checkIfNeedFullUpdate()){
                    log.info("Need full update,jump to fullUpdate");
                    startActivity(new Intent(this, FullUpdateActivity.class));
                    finish();
                }else{
                    log.info("Don't need a full update, pull notices, jump to userCenter");
                    NoticeProcessorManager.getInstance(this).getAndProcessNotice(false);
                    startActivity(new Intent(this, UserCenterActivity.class));
                    finish();
                }
            }
        } catch (IOException e) {
            log.error("Failed to check token status",e);
            Toast.makeText(this,"Failed to check token status",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
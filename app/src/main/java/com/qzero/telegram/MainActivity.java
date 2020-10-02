package com.qzero.telegram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.impl.LocalDataStorageImpl;
import com.qzero.telegram.http.bean.Token;
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
            Token token=new LocalDataStorageImpl(this).getObject(LocalDataStorage.NAME_LOCAL_TOKEN,Token.class);
            if(token==null){
                log.info("No token yet,jump to login");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }else{
                log.info("Already had a token,jump to userCenter");
                startActivity(new Intent(this, UserCenterActivity.class));
                finish();
            }
        } catch (IOException e) {
            log.error("Failed to check token status",e);
            Toast.makeText(this,"Failed to check token status",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
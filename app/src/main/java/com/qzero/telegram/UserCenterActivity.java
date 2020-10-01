package com.qzero.telegram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.qzero.telegram.notice.NoticeMonitorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCenterActivity extends AppCompatActivity {

    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        log.debug("Started service");
        startService(new Intent(this, NoticeMonitorService.class));
    }
}
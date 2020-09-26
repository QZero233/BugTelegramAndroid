package com.qzero.telegram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends AppCompatActivity {

    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log.debug("Started");
    }
}
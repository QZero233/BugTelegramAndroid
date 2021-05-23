package com.qzero.telegram.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.qzero.telegram.R;
import com.qzero.telegram.dao.entity.FileResource;

public class FileResourceDetailActivity extends AppCompatActivity {

    private FileResource fileResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_resource_detail);

        fileResource= (FileResource) getIntent().getSerializableExtra("fileResource");
        if(fileResource==null){
            finish();
            return;
        }


    }
}
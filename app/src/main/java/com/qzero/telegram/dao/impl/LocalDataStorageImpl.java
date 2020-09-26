package com.qzero.telegram.dao.impl;

import android.content.Context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.utils.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocalDataStorageImpl implements LocalDataStorage {

    private static final String FILE_NAME_PATTEN="storage/%s";

    private Context context;

    public LocalDataStorageImpl(Context context) {
        this.context = context;
        new File(context.getFilesDir(),"storage/").mkdirs();
    }

    @Override
    public <T> void storeObject(String name, T obj) throws IOException {
        File file=new File(context.getFilesDir(), String.format(FILE_NAME_PATTEN, name));

        FileOutputStream outputStream=new FileOutputStream(file);

        ObjectMapper mapper=new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);

        String json=mapper.writeValueAsString(obj);

        outputStream.write(json.getBytes());
        outputStream.close();
    }

    @Override
    public <T> T getObject(String name, Class<T> cls) throws IOException {
        File file=new File(context.getFilesDir(), String.format(FILE_NAME_PATTEN, name));
        if(!file.exists())
            return null;

        FileInputStream inputStream=new FileInputStream(file);

        byte[] buf=StreamUtils.readDataFromInputStream(inputStream);

        ObjectMapper mapper=new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(buf,cls);
    }

    @Override
    public void removeObject(String name) throws IOException {
        File file=new File(context.getFilesDir(), String.format(FILE_NAME_PATTEN, name));

        if(!file.exists())
            return;

        file.delete();
    }
}

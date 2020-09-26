package com.qzero.telegram.dao;

import com.qzero.telegram.utils.StreamUtils;

import java.io.IOException;

public interface LocalDataStorage {

    final String NAME_LOCAL_TOKEN="localToken";

    <T> void storeObject(String name,T obj) throws IOException;

    <T> T getObject(String name,Class<T> cls) throws IOException;

    void removeObject(String name) throws IOException;

}

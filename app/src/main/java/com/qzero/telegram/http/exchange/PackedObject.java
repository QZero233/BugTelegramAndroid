package com.qzero.telegram.http.exchange;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Collection;

@JsonSerialize(as = CommonPackedObject.class)
@JsonDeserialize(as=CommonPackedObject.class)
public interface PackedObject {

    void addObject(Object obj);

    void addObject(String specialName, Object obj);

    <T> T parseObject(Class<T> cls);

    <T> T parseObject(String specialName, Class<T> cls);

    Collection parseCollectionObject(Class collectionClass, Class... elementClasses);

    Collection parseCollectionObject(String specialName, Class collectionClass, Class... elementClasses);
}

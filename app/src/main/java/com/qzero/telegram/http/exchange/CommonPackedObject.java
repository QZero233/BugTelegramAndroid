package com.qzero.telegram.http.exchange;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommonPackedObject implements PackedObject {

    /**
     * Object map
     * Key:name of the parameter
     * Value:object in json
     */
    private Map<String,String> object;

    public CommonPackedObject() {
        object=new HashMap<>();
    }

    public CommonPackedObject(Map<String, String> object) {
        this.object = object;
        if(this.object==null)
            this.object=new HashMap<>();
    }

    public Map<String, String> getObject() {
        return object;
    }

    public void setObject(Map<String, String> object) {
        this.object = object;
    }

    @Override
    public void addObject(Object obj) {
        addObject(getDefaultName(obj.getClass()),obj);
    }

    @Override
    public void addObject(String specialName, Object obj) {
        try {
            ObjectMapper mapper=new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            object.put(specialName,json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T parseObject(Class<T> cls) {
        return parseObject(getDefaultName(cls),cls);
    }

    @Override
    public <T> T parseObject(String specialName, Class<T> cls) {
        try {
            String json=object.get(specialName);
            ObjectMapper mapper=new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(json,cls);
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection parseCollectionObject(Class collectionClass, Class... elementClasses) {
        return parseCollectionObject(getDefaultName(collectionClass),collectionClass,elementClasses);
    }

    @Override
    public Collection parseCollectionObject(String specialName, Class collectionClass, Class... elementClasses) {
        try {
            ObjectMapper mapper=new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JavaType type=mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
            return mapper.readValue(object.get(specialName), type);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getDefaultName(Class cls){
        ParameterObject parameterObjectAnnotation= (ParameterObject) cls.getDeclaredAnnotation(ParameterObject.class);
        if(parameterObjectAnnotation!=null && !parameterObjectAnnotation.name().equals(""))
            return parameterObjectAnnotation.name();

        return cls.getSimpleName();
    }

    @Override
    public String toString() {
        return "CommonPackedObject{" +
                "object=" + object +
                '}';
    }
}

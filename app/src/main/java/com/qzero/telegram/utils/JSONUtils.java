package com.qzero.telegram.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONUtils {

    private static ObjectMapper mapper=new ObjectMapper();
    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String listToJson(List list) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
    }

    public static<T> List<T> jsonToList(String json,Class<T> cls) throws JsonProcessingException {
        JavaType type=mapper.getTypeFactory().constructParametricType(List.class,cls);
        List<T> result= mapper.readValue(json, type);
        if(result==null)
            return new ArrayList<>();

        return result;
    }

}

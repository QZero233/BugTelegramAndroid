package com.qzero.telegram.dao.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ChatMemberConverter implements PropertyConverter<List<ChatMember>,String> {

    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public List<ChatMember> convertToEntityProperty(String databaseValue) {
        ObjectMapper mapper=new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JavaType type=mapper.getTypeFactory().constructParametricType(List.class, ChatMember.class);
        try {
            return mapper.readValue(databaseValue, type);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert String to List<ChatMember>",e);
            return null;
        }
    }

    @Override
    public String convertToDatabaseValue(List<ChatMember> entityProperty) {
        ObjectMapper mapper=new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);


        try {
            return mapper.writeValueAsString(entityProperty);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert List<ChatMember> to String",e);
            return null;
        }
    }
}

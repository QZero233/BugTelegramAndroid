package com.qzero.telegram.dao.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qzero.telegram.utils.JSONUtils;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TransportedIndexConverter implements PropertyConverter<List<Integer>,String> {

    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public List<Integer> convertToEntityProperty(String databaseValue) {
        try {
            List<Integer> list= JSONUtils.jsonToList(databaseValue,Integer.class);
            return list;
        } catch (JsonProcessingException e) {
            log.error("Failed to convert transported index json string into list",e);
            return null;
        }
    }

    @Override
    public String convertToDatabaseValue(List<Integer> entityProperty) {
        try {
            String json=JSONUtils.listToJson(entityProperty);
            return json;
        } catch (JsonProcessingException e) {
            log.error("Failed to convert transported index list into json string",e);
            return null;
        }
    }
}

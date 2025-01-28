package com.dream.six.service.impl;


import com.dream.six.enums.*;
import com.dream.six.service.EnumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
@Slf4j
public class EnumServiceImpl implements EnumService {

    public Map<String, List<Map<String, Object>>> getAllEnumValues() {
        Map<String, List<Map<String, Object>>> enumValuesMap = new HashMap<>();

        for (Class<?> enumClass : getEnumClasses()) {
            String enumName = enumClass.getSimpleName();
            try {
                Object[] enumValues = getEnumValues(enumClass);

                List<Map<String, Object>> valuesList = new ArrayList<>();
                for (Object enumValue : enumValues) {
                    Map<String, Object> enumData = new HashMap<>();
                    enumData.put("name", getValue(enumValue));
                    enumData.put("value", enumValue.toString());
                    valuesList.add(enumData);
                }

                enumValuesMap.put(enumName, valuesList);
            } catch (Exception e) {
                log.error("Exception occurred while retrieving enum values for {}: {}", enumName, e.getMessage());
                enumValuesMap.put(enumName, Collections.emptyList());
            }
        }

        return enumValuesMap;
    }

    private List<Class<?>> getEnumClasses() {
        return Arrays.asList(
                StatusEnum.class,
                SuppressionEnum.class,
                MarketingMethodsEnum.class,
                ABMEnum.class,
                PacingEnum.class,
                ProductEnum.class,
                FileType.class,
                TimeFormatEnum.class
        );
    }

    private Object[] getEnumValues(Class<?> enumClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = enumClass.getMethod("values");
        return (Object[]) method.invoke(null);
    }

    private Object getValue(Object enumValue) {
        try {
            Method getValueMethod = enumValue.getClass().getMethod("getValue");
            return  getValueMethod.invoke(enumValue);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return enumValue.toString();
        }
    }
}

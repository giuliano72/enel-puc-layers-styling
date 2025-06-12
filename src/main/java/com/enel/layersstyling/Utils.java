package com.enel.layersstyling;

import java.util.Map;

public class Utils {

    public static Object getMapValue(String field, Object defaultValue, Map<String, Object> mapObject) {
        if (mapObject != null) {
            if (mapObject.containsKey(field)) {
                Object value = mapObject.get(field);
                if (value != null) {
                    return value;
                }
            }
        }
        return defaultValue;
    }
}

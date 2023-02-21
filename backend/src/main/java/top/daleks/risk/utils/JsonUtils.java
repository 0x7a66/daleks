/*
 * Copyright 2023-present Daleks Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.daleks.risk.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

/**
 * 基于 jackson 的 json 工具
 */
@Slf4j
public class JsonUtils {
    private final static ObjectMapper JSON = new ObjectMapper();

    static {
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 对象转json
     *
     * @param object 对象
     * @return
     */
    public static String string(Object object) {
        if (object == null)
            return null;
        if (object instanceof String)
            return (String) object;
        try {
            return JSON.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("object to json string error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * json转对象
     *
     * @param json      json字符串
     * @param valueType 对象类型
     * @param <T>       范型
     * @return
     */
    public static <T> T entity(String json, Class<T> valueType) {
        try {
            return JSON.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            log.error("json to entity error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * json转对象
     *
     * @param json         json字符串
     * @param valueTypeRef 对象类型
     * @param <T>          范型
     * @return
     */
    public static <T> T entity(String json, TypeReference<T> valueTypeRef) {
        try {
            return JSON.readValue(json, valueTypeRef);
        } catch (JsonProcessingException e) {
            log.error("json to entity error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * json转List
     *
     * @param json      json字符串
     * @param valueType 对象类型
     * @param <T>       泛型
     * @return 对象
     */
    public static <T> List<T> list(String json, Class<T> valueType) {
        if (json == null)
            return new ArrayList<>();
        JavaType javaType = JSON.getTypeFactory().constructParametricType(List.class, valueType);
        try {
            return JSON.readValue(json, javaType);
        } catch (IOException e) {
            log.error("json to list error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * json转Set
     *
     * @param json      json字符串
     * @param valueType 对象类型
     * @param <T>       泛型
     * @return 对象
     */
    public static <T> Set<T> set(String json, Class<T> valueType) {
        if (json == null)
            return new HashSet<>();
        JavaType javaType = JSON.getTypeFactory().constructParametricType(Set.class, valueType);
        try {
            return JSON.readValue(json, javaType);
        } catch (IOException e) {
            log.error("json to set error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * json转Map, key和value为字符串
     *
     * @param json json字符串
     * @return 对象
     */
    public static Map<String, String> map(String json) {
        JavaType javaType = JSON.getTypeFactory().constructParametricType(HashMap.class, String.class, Object.class);
        HashMap<String, Object> result;
        try {
            result = JSON.readValue(json, javaType);
        } catch (IOException e) {
            log.error("json to map error", e);
            throw new RuntimeException(e);
        }
        HashMap<String, String> map = new HashMap<>();
        result.forEach((key, value) -> map.put(key, value instanceof String ? (String) value : string(value)));
        return map;
    }

    /**
     * json转Map, key为字符串
     *
     * @param json      json字符串
     * @param valueType value类型
     * @param <V>       value的泛型
     * @return 对象
     */
    public static <V> Map<String, V> map(String json, Class<V> valueType) {
        return map(json, String.class, valueType);
    }

    /**
     * json转Map
     *
     * @param json      json字符串
     * @param keyType   key类型
     * @param valueType value类型
     * @param <K>       key的泛型
     * @param <V>       value的泛型
     * @return 对象
     */
    public static <K, V> Map<K, V> map(String json, Class<K> keyType, Class<V> valueType) {
        if (json == null)
            return new HashMap<>();
        JavaType javaType = JSON.getTypeFactory().constructParametricType(HashMap.class, keyType, valueType);
        try {
            return JSON.readValue(json, javaType);
        } catch (Exception e) {
            log.error("json to map error", e);
            throw new RuntimeException(e);
        }
    }
}

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

package top.daleks.risk.access.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件行为数据
 */
public class RiskAction implements Serializable {

    public final static String KEY_EVENT = "Event";
    public final static String KEY_USER_ID = "UserId";
    public final static String KEY_DEVICE_ID = "DeviceId";
    public final static String KEY_CLIENT_IP = "ClientIp";
    public final static String KEY_MOBILE = "Mobile";
    public final static String KEY_APP_ID = "AppId";
    public final static String KEY_TIMEOUT = "Timeout";
    public final static String KEY_RISK_ID = "RiskId";
    public final static String KEY_REQUEST_TIME = "RequestTime";
    public final static String KEY_SOURCE_FROM = "SourceFrom";
    public final static String KEY_ASYNC = "Async";
    public final static String KEY_TAGS = "Tags";

    private final Map<String, Object> data = new ConcurrentHashMap<>();

    public RiskAction(String event) {
        put(KEY_EVENT, event);
        put(KEY_REQUEST_TIME, System.currentTimeMillis());
    }

    public static RiskAction create(String event) {
        return new RiskAction(event);
    }

    public RiskAction put(String key, Object value) {
        if (key != null && key.length() > 0) {
            data.put(key, value);
        }
        return this;
    }

    public String getEvent() {
        return getString(KEY_EVENT);
    }

    public String getRiskId() {
        return getString(KEY_RISK_ID);
    }

    public Long getRequestTime() {
        return getLong(KEY_REQUEST_TIME);
    }

    public String getUserId() {
        return getString(KEY_USER_ID);
    }

    public RiskAction setUserId(String userId) {
        return put(KEY_USER_ID, userId);
    }

    public String getDeviceId() {
        return getString(KEY_DEVICE_ID);
    }

    public RiskAction setDeviceId(String deviceId) {
        put(KEY_DEVICE_ID, deviceId);
        return this;
    }

    public String getClientIp() {
        return getString(KEY_CLIENT_IP);
    }

    public RiskAction setClientIp(String clientIp) {
        return put(KEY_CLIENT_IP, clientIp);
    }

    public String getMobile() {
        return getString(KEY_MOBILE);
    }

    public RiskAction setMobile(String mobile) {
        return put(KEY_MOBILE, mobile);
    }

    public String getAppId() {
        return getString(KEY_APP_ID);
    }

    public RiskAction setAppId(String appId) {
        return put(KEY_APP_ID, appId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public RiskAction addTag(String tag) {
        data.putIfAbsent(KEY_TAGS, new HashSet<>());
        ((Set) data.get(KEY_TAGS)).add(tag);
        return this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Set<String> getTags() {
        Object value = data.getOrDefault(KEY_TAGS, new HashSet<>());
        if (value instanceof Set) {
            return (Set) value;
        }
        return new HashSet<>();
    }

    public RiskAction setTags(Set<String> tags) {
        return put(KEY_TAGS, tags);
    }

    public boolean hasTag(String tag) {
        return getTags().contains(tag);
    }

    public String getSourceFrom() {
        return getString(KEY_SOURCE_FROM);
    }

    public RiskAction setSourceFrom(String sourceFrom) {
        return put(KEY_SOURCE_FROM, sourceFrom);
    }

    public Long getTimeout() {
        return getLong(KEY_TIMEOUT);
    }

    public RiskAction setTimeout(long timeout) {
        return put(KEY_TIMEOUT, timeout < 0 ? -timeout : timeout);
    }

    public boolean isAsync() {
        Boolean b = getBoolean(KEY_ASYNC);
        return b != null && b;
    }

    public RiskAction setAsync(boolean async) {
        return put(KEY_ASYNC, async);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Object getValue(String key) {
        if (key == null || key.length() <= 0) {
            return null;
        }
        return data.get(key);
    }

    public String getString(String key) {
        Object value = getValue(key);
        return value == null ? null : value.toString();
    }

    public Long getLong(String key) {
        Object value = getValue(key);
        if (value instanceof Long) {
            return (Long) value;
        }
        return null;
    }

    public Boolean getBoolean(String key) {
        Object value = getValue(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return null;
    }

    @Override
    public String toString() {
        return data.toString();
    }

}

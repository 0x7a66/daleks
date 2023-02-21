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

package top.daleks.risk.engine.accumulate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import top.daleks.risk.common.AccumulateRequest;
import top.daleks.risk.common.enums.WindowType;
import top.daleks.risk.common.model.Accumulate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合函数
 */
public abstract class AbstractAggFunction implements AggFunction {

    // 2023-01-01 00:00:00 时间锚点
    protected final static LocalDateTime ANCHOR_TIME = LocalDateTime.ofInstant(Instant.ofEpochMilli(1672502400000L), ZoneId.systemDefault());

    // accumulateId + groupKey + timestampKey
    protected final static String REDIS_KEY_FORMAT = "%s#%s#%s";

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    protected String currentKey(AccumulateRequest request) {
        Accumulate accumulate = request.getAccumulate();
        long anchor = anchor(request);
        String groupKey = request.groupKey();
        return String.format(REDIS_KEY_FORMAT, accumulate.getId(), groupKey, anchor);
    }

    protected List<String> keys(AccumulateRequest request) {
        Accumulate accumulate = request.getAccumulate();
        long anchor = anchor(request);
        String groupKey = request.groupKey();

        if (accumulate.getWindowType().equals(WindowType.FIXED)) {
            return Collections.singletonList(String.format(REDIS_KEY_FORMAT, accumulate.getId(), groupKey, anchor));
        }

        List<String> keys = new ArrayList<>();
        long windowMinutes = accumulate.getWindowUnit().toChronoUnit().getDuration().toMinutes() * accumulate.getTimeWindow();
        long spanMinutes = accumulate.getSpanUnit().toChronoUnit().getDuration().toMinutes() * accumulate.getTimeSpan();
        long d = 0;
        while (d < windowMinutes) {
            keys.add(String.format(REDIS_KEY_FORMAT, accumulate.getId(), groupKey, anchor));
            d += spanMinutes;
            anchor -= accumulate.getTimeSpan();
        }

        return keys;
    }

    protected Long getExpireSeconds(AccumulateRequest request) {
        Accumulate accumulate = request.getAccumulate();
        long seconds = accumulate.getWindowUnit().toChronoUnit().getDuration().getSeconds() * accumulate.getTimeWindow();
        if (accumulate.getWindowType().equals(WindowType.SLIDING)) {
            seconds += accumulate.getSpanUnit().toChronoUnit().getDuration().getSeconds() * accumulate.getTimeSpan();
        }
        return seconds;
    }

    private long anchor(AccumulateRequest request) {
        Accumulate accumulate = request.getAccumulate();
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(request.getTimestamp()), ZoneId.systemDefault());
        long anchor = accumulate.getWindowUnit().toChronoUnit().between(ANCHOR_TIME, now) / accumulate.getTimeWindow() * accumulate.getTimeWindow();
        if (accumulate.getWindowType().equals(WindowType.SLIDING)) {
            anchor = accumulate.getSpanUnit().toChronoUnit().between(ANCHOR_TIME, now) / accumulate.getTimeSpan() * accumulate.getTimeSpan();
        }
        return anchor;
    }
}

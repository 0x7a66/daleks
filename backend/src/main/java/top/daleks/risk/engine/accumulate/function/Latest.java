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

package top.daleks.risk.engine.accumulate.function;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import top.daleks.risk.common.AccumulateRequest;
import top.daleks.risk.common.enums.AccumulateFunction;
import top.daleks.risk.engine.accumulate.AbstractAggFunction;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Latest extends AbstractAggFunction {
    @Override
    public void calculate(AccumulateRequest request) {

        String key = currentKey(request);
        Object value = request.getAggKeyValue();
        log.debug("Latest.calculate, key: {}, value: {}", key, value);
        redisTemplate.opsForValue().set(key, value, getExpireSeconds(request), TimeUnit.SECONDS);
    }

    @Override
    public Object getValue(AccumulateRequest request) {
        List<String> keys = keys(request);
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        log.debug("Latest.getFactorValue, keys: {}, values: {}", keys, values);
        if (CollectionUtils.isNotEmpty(values)) {
            for (Object value : values) {
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    @Override
    public String function() {
        return AccumulateFunction.LATEST.name();
    }
}

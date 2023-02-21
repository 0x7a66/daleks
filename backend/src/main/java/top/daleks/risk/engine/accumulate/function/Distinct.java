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
import org.springframework.stereotype.Component;
import top.daleks.risk.common.AccumulateRequest;
import top.daleks.risk.common.enums.AccumulateFunction;
import top.daleks.risk.engine.accumulate.AbstractAggFunction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Distinct extends AbstractAggFunction {
    @Override
    public void calculate(AccumulateRequest request) {
        String key = currentKey(request);
        Object value = request.getAggKeyValue();
        log.debug("Distinct.calculate, key: {}, value: {}", key, value);
        redisTemplate.opsForSet().add(key, value);
        redisTemplate.expire(key, getExpireSeconds(request), TimeUnit.SECONDS);
    }

    @Override
    public Object getValue(AccumulateRequest request) {
        List<String> keys = keys(request);
        Set<Object> values = redisTemplate.opsForSet().union(keys);
        log.debug("Distinct.getFactorValue, keys: {}, values: {}", keys, values);
        return values == null ? new HashSet<>() : values;
    }

    @Override
    public String function() {
        return AccumulateFunction.DISTINCT.name();
    }
}

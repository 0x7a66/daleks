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
import top.daleks.risk.utils.NumberUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Avg extends AbstractAggFunction {

    private final static String COUNT_KEY = "%s_k";
    private final static String VALUE_KEY = "%s_v";

    @Override
    public void calculate(AccumulateRequest request) {
        String currentSpanKey = currentKey(request);
        double value = NumberUtils.toDouble(request.getAggKeyValue());
        String countKey = String.format(COUNT_KEY, currentSpanKey);
        String valueKey = String.format(VALUE_KEY, currentSpanKey);
        redisTemplate.opsForValue().increment(valueKey, value);
        redisTemplate.opsForValue().increment(countKey, 1L);

        log.debug("Avg.calculate countKey: {}, valueKey: {}, value: {}", countKey, valueKey, value);

        Long expireSeconds = getExpireSeconds(request);
        redisTemplate.expire(countKey, expireSeconds, TimeUnit.SECONDS);
        redisTemplate.expire(valueKey, expireSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Object getValue(AccumulateRequest request) {
        List<String> keys = keys(request);

        List<String> countKeys = keys.stream().map(key -> String.format(COUNT_KEY, key)).collect(Collectors.toList());
        List<String> valueKeys = keys.stream().map(key -> String.format(VALUE_KEY, key)).collect(Collectors.toList());

        long count = 0;
        List<Object> countList = redisTemplate.opsForValue().multiGet(countKeys);
        if (CollectionUtils.isNotEmpty(countList)) {
            count = countList.stream().mapToLong(item -> item == null ? 0 : NumberUtils.toLong(item)).sum();
        }

        double sum = 0D;
        List<Object> valueList = redisTemplate.opsForValue().multiGet(valueKeys);
        if (CollectionUtils.isNotEmpty(valueList)) {
            sum = valueList.stream().mapToDouble(item -> item == null ? 0D : NumberUtils.toDouble(item)).sum();
        }

        log.debug("Avg.getFactorValue, countKeys: {}, countList: {}, valueKeys: {}, valueList: {}, count: {}, sum: {}", countKeys, countList, valueKeys, valueList, count, sum);
        if (count == 0) {
            return 0;
        }
        BigDecimal avg = new BigDecimal(sum / count);
        return avg.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public String function() {
        return AccumulateFunction.AVG.name();
    }
}

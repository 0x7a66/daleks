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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import top.daleks.risk.common.AccumulateRequest;
import top.daleks.risk.common.AccumulateService;
import top.daleks.risk.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聚合函数
 */
@Slf4j
@Component
public class AccumulateServiceAdaptor implements AccumulateService {

    Map<String, AggFunction> accumulateFunctionMap = new HashMap<>();

    public AccumulateServiceAdaptor(List<AggFunction> aggFunctions) {
        if (CollectionUtils.isNotEmpty(aggFunctions)) {
            for (AggFunction aggFunction : aggFunctions) {
                this.accumulateFunctionMap.put(aggFunction.function(), aggFunction);
            }
        }
    }

    @Override
    public void calculate(AccumulateRequest request) {
        AggFunction aggFunction = this.accumulateFunctionMap.get(request.getAccumulate().getAggFunction());
        if (aggFunction == null) {
            log.error("{} -> {} 聚合函数不存在", request.getRiskId(), request.getAccumulate().getAggFunction());
            return;
        }
        if (StringUtils.isEmpty(request.groupKey())) {
            log.error("{} -> {} 聚合key为空", request.getRiskId(), request.getAccumulate().getAggFunction());
            return;
        }
        aggFunction.calculate(request);
    }

    @Override
    public Object getValue(AccumulateRequest request) {
        long start = System.currentTimeMillis();
        AggFunction aggFunction = this.accumulateFunctionMap.get(request.getAccumulate().getAggFunction());
        if (aggFunction == null) {
            log.error("{} -> {} 聚合函数不存在", request.getRiskId(), request.getAccumulate().getAggFunction());
            return null;
        }
        if (StringUtils.isEmpty(request.groupKey())) {
            log.error("{} -> {} 聚合key为空", request.getRiskId(), request.getAccumulate().getAggFunction());
            return null;
        }
        Object value = aggFunction.getValue(request);
        log.info("{} -> {} 请求参数: {}, 返回结果: {}, 耗时: {}",
                request.getRiskId(),
                request.getAccumulate().getName(),
                JsonUtils.string(request.groupKey()),
                JsonUtils.string(value),
                System.currentTimeMillis() - start);
        return value;
    }

}

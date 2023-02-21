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

package top.daleks.risk.variable;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import top.daleks.risk.common.VariableRequest;
import top.daleks.risk.common.VariableService;
import top.daleks.risk.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class VariableFunctionAdaptor implements VariableService {

    private final Map<String, VariableFunction> variableFunctionMap = new HashMap<>();

    public VariableFunctionAdaptor(List<VariableFunction> variableFunctions) {
        if (CollectionUtils.isNotEmpty(variableFunctions)) {
            for (VariableFunction function : variableFunctions) {
                variableFunctionMap.put(function.function(), function);
            }
        }
    }

    @Override
    public Object getVariable(VariableRequest request) {
        long start = System.currentTimeMillis();
        VariableFunction function = variableFunctionMap.get(request.getFunction());
        if (function == null) {
            log.warn("{} -> function: {} 不存在", request.getRiskId(), request.getFunction());
            return null;
        }
        Object variable = function.getVariable(request.getParameters());

        log.info("{} -> {} 请求参数: {}, 返回结果: {}, 耗时: {}",
                request.getRiskId(),
                request.getFunction(),
                request.getParameters(),
                JsonUtils.string(variable),
                System.currentTimeMillis() - start);

        return variable;
    }
}

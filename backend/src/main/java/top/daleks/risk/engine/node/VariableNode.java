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

package top.daleks.risk.engine.node;

import lombok.extern.slf4j.Slf4j;
import top.daleks.risk.common.DependencyContext;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.common.VariableRequest;
import top.daleks.risk.common.VariableService;
import top.daleks.risk.common.model.Variable;
import top.daleks.risk.engine.AbstractGraphNode;
import top.daleks.risk.engine.executor.VariableExecutor;

import java.util.Map;

@Slf4j
public class VariableNode extends AbstractGraphNode<Variable> {

    private final VariableService variableService;
    private final VariableExecutor executor;

    public VariableNode(DependencyContext dependencyContext, Variable data) {
        super(dependencyContext, data);
        this.variableService = dependencyContext.getVariableService();
        this.executor = new VariableExecutor(getName(), data.getParameters());
    }

    @Override
    public String getName() {
        return getData().getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doWork(RiskContext riskContext) {
        Map<String, Object> parameter = this.executor.execute(getProperties(riskContext));
        if (parameter == null) {
            log.warn("{} -> {} 解析参数为空", riskContext.getRiskId(), getName());
            return;
        }
        VariableRequest request = new VariableRequest();
        request.setRiskId(riskContext.getRiskId());
        request.setFunction(getData().getFunc());
        request.setParameters(parameter);
        Object result = this.variableService.getVariable(request);
        riskContext.addProperty(getData().getName(), result);
    }
}

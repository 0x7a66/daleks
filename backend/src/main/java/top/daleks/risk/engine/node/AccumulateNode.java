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
import org.apache.commons.lang3.StringUtils;
import top.daleks.risk.common.AccumulateRequest;
import top.daleks.risk.common.AccumulateService;
import top.daleks.risk.common.DependencyContext;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.common.enums.Tag;
import top.daleks.risk.common.model.Accumulate;
import top.daleks.risk.engine.AbstractGraphNode;
import top.daleks.risk.engine.executor.AccumulateAggValueExecutor;
import top.daleks.risk.engine.executor.AccumulateCheckExecutor;
import top.daleks.risk.engine.executor.AccumulateGroupValueExecutor;

import java.util.*;

import static top.daleks.risk.common.Constants.RESULT;

@Slf4j
public class AccumulateNode extends AbstractGraphNode<Accumulate> {

    // 前置表达式 groovy executor
    private final AccumulateCheckExecutor accumulateCheckExecutor;
    // 分组key数据 groovy executor
    private final AccumulateGroupValueExecutor accumulateGroupValueExecutor;
    // 聚合key数据 groovy executor
    private final AccumulateAggValueExecutor accumulateAggValueExecutor;
    // 指标计算及获取值服务
    private final AccumulateService accumulateService;
    // 分组key依赖的属性
    private final Set<String> groupKeyDependencies = new TreeSet<>();
    // 聚合key依赖的属性
    private final Set<String> aggKeyDependencies = new TreeSet<>();
    // 绑定的事件
    private final Set<String> bindEvents = new HashSet<>();
    // 是否延迟计算(使用风控结果数据时，需要延迟计算)
    private boolean delay = false;

    public AccumulateNode(DependencyContext dependencyContext, Accumulate data) {
        super(dependencyContext, data);
        this.accumulateService = dependencyContext.getAccumulateService();
        this.accumulateCheckExecutor = new AccumulateCheckExecutor(getName(), data.getScript());
        this.accumulateGroupValueExecutor = new AccumulateGroupValueExecutor(getName(), data.getGroupKey());
        this.accumulateAggValueExecutor = new AccumulateAggValueExecutor(getName(), data.getAggKey());
    }

    @Override
    public String getName() {
        return getData().getName();
    }

    @Override
    protected void doWork(RiskContext riskContext) {

        AccumulateRequest request = build(riskContext);
        // 绑定的事件需要计算指标
        if (hasEvent(riskContext.getEvent())) {
            if (!isDelay()) {
                doCalculate(riskContext, request);
            } else {
                riskContext.addFinishCallback(this::calculate);
            }
        }
        if (StringUtils.isEmpty(request.groupKey())) {
            log.warn("{} -> {} 聚合key为空", riskContext.getRiskId(), getName());
            return;
        }
        Object value = this.accumulateService.getValue(request);
        riskContext.addProperty(getData().getName(), value);
    }

    /**
     * 计算指标
     *
     * @param riskContext 风控请求上下文
     */
    public void calculate(RiskContext riskContext) {
        AccumulateRequest request = build(riskContext);
        doCalculate(riskContext, request);
    }

    /**
     * 计算指标
     *
     * @param riskContext 风控请求上下文
     * @param request     请求参数
     */
    public void doCalculate(RiskContext riskContext, AccumulateRequest request) {
        if (preCheck(riskContext)) {
            if (StringUtils.isEmpty(request.groupKey())) {
                log.warn("{} -> {} 聚合key为空", riskContext.getRiskId(), getName());
                return;
            }
            this.accumulateService.calculate(request);
        }
    }

    /**
     * 指标计算前置条件判断
     *
     * @param riskContext 风控请求上下文
     * @return 是否计算
     */
    public boolean preCheck(RiskContext riskContext) {
        if (riskContext.getRiskAction().hasTag(Tag.TEST.name())) {
            return false;
        }
        Map<String, Object> properties = getProperties(riskContext);
        if (isDelay()) {
            properties.put(RESULT, riskContext.getResult());
        }
        return this.accumulateCheckExecutor.execute(properties);
    }

    public boolean isDelay() {
        return delay;
    }

    public void setDelay(boolean delay) {
        this.delay = delay;
    }

    public void addBindEvent(String event) {
        this.bindEvents.add(event);
    }

    public void addGroupKeyDependencies(Set<String> dependencies) {
        this.groupKeyDependencies.addAll(dependencies);
    }

    public void addAggKeyDependencies(Set<String> dependencies) {
        this.aggKeyDependencies.addAll(dependencies);
    }

    /**
     * 构建指标请求参数
     *
     * @param riskContext 风控请求上下文
     * @return AccumulateRequest
     */
    @SuppressWarnings("rawtypes")
    private AccumulateRequest build(RiskContext riskContext) {
        List parameter = this.accumulateGroupValueExecutor.execute(getProperties(riskContext, this.groupKeyDependencies));
        Object aggValue = this.accumulateAggValueExecutor.execute(getProperties(riskContext, this.aggKeyDependencies));
        AccumulateRequest request = new AccumulateRequest();
        request.setRiskId(riskContext.getRiskId());
        request.setAccumulate(getData());
        request.setGroupKeyValues(parameter);
        request.setAggKeyValue(aggValue);
        request.setTimestamp(riskContext.getRequestTime());
        return request;
    }

    public boolean hasEvent(String event) {
        return this.bindEvents.contains(event);
    }
}

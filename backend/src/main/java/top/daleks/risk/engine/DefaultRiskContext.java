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

package top.daleks.risk.engine;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import top.daleks.risk.access.bean.RiskAction;
import top.daleks.risk.access.bean.RiskLevel;
import top.daleks.risk.access.bean.RiskResult;
import top.daleks.risk.common.DependencyContext;
import top.daleks.risk.common.GraphNode;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.common.RuleResult;
import top.daleks.risk.common.enums.GrayListType;
import top.daleks.risk.common.model.Event;
import top.daleks.risk.common.model.GrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class DefaultRiskContext implements RiskContext {

    // 节点依赖上下文
    private final DependencyContext dependencyContext;
    // 风控请求参数
    private final RiskAction riskAction;
    // 节点执行状态
    private final NodeStates nodeStates;
    // 执行结果判断
    private final RiskDecider riskDecider;
    // 获取的上下文数据
    private final Map<String, Object> contextData = new ConcurrentHashMap<>();
    // 执行结束后的回调函数
    private final List<Consumer<RiskContext>> callbacks = new ArrayList<>();
    // 事件绑定的名单是否命中
    private volatile GrayList grayListHit;
    // 风控结果
    private volatile RiskResult riskResult;
    // 请求耗时
    private long costTime;

    public DefaultRiskContext(RiskAction riskAction, DependencyContext dependencyContext) {
        this.riskAction = riskAction;
        this.dependencyContext = dependencyContext;
        this.nodeStates = new NodeStates();
        this.riskDecider = new RiskDecider(riskAction.getEvent(), dependencyContext);
    }

    @Override
    public String getRiskId() {
        return this.riskAction.getRiskId();
    }

    @Override
    public String getEvent() {
        return this.riskAction.getEvent();
    }

    @Override
    public Long getRequestTime() {
        return this.riskAction.getRequestTime();
    }

    @Override
    public RiskAction getRiskAction() {
        return this.riskAction;
    }

    @Override
    public void addProperty(String key, Object value) {
        if (value == null) {
            return;
        }
        if (this.contextData.get(key) != null) {
            log.warn("属性: {} 值已存在", key);
        }
        this.contextData.put(key, value);
    }

    @Override
    public Object getProperty(String key) {
        return this.contextData.getOrDefault(key, this.riskAction.getValue(key));
    }

    @Override
    public Map<String, Object> getContextData() {
        return this.contextData;
    }

    @Override
    public void hitGrayList(GrayList list) {
        this.grayListHit = list;
    }

    @Override
    public void hitAtomRule(Long id, RiskLevel riskLevel) {
        this.riskDecider.hitAtomRule(id, riskLevel);
    }

    @Override
    public void hitGroupRule(Long id, boolean hit) {
        this.riskDecider.hitGroupRule(id, hit);
    }

    @Override
    public void addFinishCallback(Consumer<RiskContext> callback) {
        this.callbacks.add(callback);
    }

    @Override
    public List<Consumer<RiskContext>> getFinishCallbacks() {
        return this.callbacks;
    }

    @Override
    public synchronized RiskResult waitResult(long millis) {
        if (!checkReturn()) {
            try {
                wait(millis);
            } catch (InterruptedException e) {
                log.info("{} -> 等待失败", getRiskId(), e);
            }
            if (!checkReturn()) {
                log.warn("{} -> 请求超时", getRiskId());
            }
        }
        return getResult();
    }

    @Override
    public synchronized RiskResult getResult() {
        if (this.riskResult != null) {
            return this.riskResult;
        }
        this.costTime = System.currentTimeMillis() - this.riskAction.getRequestTime();
        RiskResult result = new RiskResult(getRiskId());
        result.setLevel(RiskLevel.PASS);
        result.setSuccess(true);

        if (this.grayListHit != null) {
            if (this.grayListHit.getType() == GrayListType.BLACK) {
                result.setLevel(RiskLevel.REJECT);
            }
            result.setGrayListId(this.grayListHit.getGroupId());
        } else if (this.riskDecider.isHit()) {
            result.setLevel(this.riskDecider.getRiskLevel());
            result.setRuleId(this.riskDecider.getHitAtomRule().getId());
            result.setRuleName(this.riskDecider.getHitAtomRule().getName());
            result.setReply(getReply());
            result.setReturnJson(getReturnJson());
        }
        this.riskResult = result;
        return result;
    }

    @Override
    public List<RuleResult> getRuleResults() {
        return this.riskDecider.getRuleResults();
    }

    @Override
    public Long getCostTime() {
        return this.costTime;
    }


    private String getReply() {
        String reply = this.riskDecider.getHitAtomRule().getReply();
        if (StringUtils.isBlank(reply)) {
            reply = this.riskDecider.getHitGroupRule().getReply();
        }
        if (StringUtils.isBlank(reply)) {
            Event event = this.dependencyContext.getEvent(getEvent());
            if (event != null) {
                reply = event.getReply();
            }
        }
        return reply;
    }

    private String getReturnJson() {
        String returnJson = this.riskDecider.getHitAtomRule().getReturnJson();
        if (StringUtils.isBlank(returnJson)) {
            returnJson = this.riskDecider.getHitGroupRule().getReturnJson();
        }
        return returnJson;
    }

    /**
     * 风控请求是否可以返回
     */
    public synchronized boolean checkReturn() {
        boolean b = this.grayListHit != null || this.riskDecider.decide();
        if (b) {
            notifyAll();
        }
        return b;
    }

    @Override
    public boolean isNodeFinished(GraphNode<?> node) {
        return this.nodeStates.get(node.getName()).isFinished();
    }

    @Override
    public boolean isAllDependenciesFinished(Set<GraphNode<?>> children) {
        if (CollectionUtils.isEmpty(children)) {
            return true;
        }
        for (GraphNode<?> child : children) {
            if (!this.nodeStates.get(child.getName()).isFinished()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean setNodeExecuting(GraphNode<?> node) {
        return this.nodeStates.get(node.getName()).setExecuting();
    }

    @Override
    public void setNodeFinished(GraphNode<?> node) {
        this.nodeStates.get(node.getName()).setFinished();
    }

    @Override
    public boolean isFinished() {
        checkReturn();
        return this.riskDecider.isFinished();
    }
}

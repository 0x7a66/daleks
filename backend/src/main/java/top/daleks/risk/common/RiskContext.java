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

package top.daleks.risk.common;

import top.daleks.risk.access.bean.RiskAction;
import top.daleks.risk.access.bean.RiskLevel;
import top.daleks.risk.access.bean.RiskResult;
import top.daleks.risk.common.model.GrayList;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface RiskContext {
    /**
     * 获取风控请求唯一id
     */
    String getRiskId();

    /**
     * 获取风控请求事件
     */
    String getEvent();

    /**
     * 获取风控请求时间
     */
    Long getRequestTime();

    /**
     * 获取风控请求RiskAction
     */
    RiskAction getRiskAction();

    /**
     * 添加风控请求参数
     */
    void addProperty(String key, Object value);

    /**
     * 获取参数值
     */
    Object getProperty(String key);

    /**
     * 获取执行上下文参数
     */
    Map<String, Object> getContextData();

    /**
     * 命中名单
     */
    void hitGrayList(GrayList list);

    /**
     * 保存规则执行结果
     */
    void hitAtomRule(Long id, RiskLevel riskLevel);

    /**
     * 保存规则组执行结果
     */
    void hitGroupRule(Long id, boolean hit);

    /**
     * 添加风控请求执行结束时的回调
     */
    void addFinishCallback(Consumer<RiskContext> callback);

    /**
     * 获取回调
     */
    List<Consumer<RiskContext>> getFinishCallbacks();

    /**
     * 等待风控结果
     *
     * @param millis 超时时间，毫秒
     */
    RiskResult waitResult(long millis);

    /**
     * 获取风控请求结果，执行未出结果时调用，默认返回PASS
     */
    RiskResult getResult();

    /**
     * 获取风控规则执行结果
     */
    List<RuleResult> getRuleResults();

    /**
     * 获取风控处理耗时
     */
    Long getCostTime();

    /**
     * 节点执行是否完成
     *
     * @param node 节点
     */
    boolean isNodeFinished(GraphNode<?> node);

    /**
     * 节点的依赖节点是否执行完成
     *
     * @param children 节点
     * @return 是否完成
     */
    boolean isAllDependenciesFinished(Set<GraphNode<?>> children);

    /**
     * 设置节点执行
     *
     * @param node 节点
     * @return 是否成功
     */
    boolean setNodeExecuting(GraphNode<?> node);

    /**
     * 设置节点完成
     *
     * @param node 节点
     */
    void setNodeFinished(GraphNode<?> node);

    /**
     * 风控请求是否完成
     */
    boolean isFinished();
}

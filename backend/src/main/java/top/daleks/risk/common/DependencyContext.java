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

import top.daleks.risk.common.enums.ConfigType;
import top.daleks.risk.common.model.Event;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public interface DependencyContext {
    /**
     * 获取节点信息
     *
     * @param name 节点名称
     * @return 节点
     */
    GraphNode<?> getNode(String name);

    /**
     * 获取规则指示器
     *
     * @param event 事件
     * @return 规则指示器
     */
    List<RuleIndicator> getRuleIndicatorInEvent(String event);

    /**
     * 获取配置项在事件中的参数映射key
     *
     * @param event      事件
     * @param configType 配置项类型
     * @param configId   id
     * @param name       参数
     * @return
     */
    String getParameterMappingOriginName(String event, ConfigType configType, Long configId, String name);

    /**
     * 获取线程池
     *
     * @return 节点执行线程池
     */
    ExecutorService getNodeExecutorService();

    /**
     * 获取线程池
     *
     * @return 任务执行之前线程池
     */
    ForkJoinPool getRiskBeforeForkJoinPool();

    /**
     * 获取线程池
     *
     * @return 任务执行之后线程池
     */
    ForkJoinPool getRiskAfterForkJoinPool();

    /**
     * 获取风控请求事后处理器
     *
     * @return List<RiskAfterHandler>
     */
    List<RiskAfterHandler> getRiskAfterHandlers();

    /**
     * 累积数据适配器
     *
     * @return AggFunctionAdaptor
     */
    AccumulateService getAccumulateService();

    /**
     * 变量适配器
     *
     * @return VariableFunctionAdaptor
     */
    VariableService getVariableService();

    /**
     * 配置数据
     *
     * @return ConfigDataProvider
     */
    ConfigDataProvider getConfigDataProvider();

    /**
     * 获取event对象
     *
     * @param event 事件code
     * @return event对象
     */
    Event getEvent(String event);
}

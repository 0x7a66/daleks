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

import top.daleks.risk.common.model.BaseEntity;

import java.util.Set;

/**
 * 构建依赖节点
 */
public interface GraphNode<T extends BaseEntity> extends Comparable<GraphNode<?>> {

    /**
     * 获取名称
     *
     * @return 节点名称
     */
    String getName();

    /**
     * 获取数据
     *
     * @return 数据对象
     */
    T getData();

    /**
     * 异步执行当前节点
     *
     * @param riskContext 风控请求上下文
     */
    void runAsync(RiskContext riskContext);

    /**
     * 同步执行当前节点
     *
     * @param riskContext 风控请求上下文
     */
    void runSync(RiskContext riskContext);

    /**
     * 获取依赖上下文
     *
     * @return
     */
    DependencyContext getDependencyContext();

    /**
     * 获取事件对应的 parents
     *
     * @param event 事件
     * @return parents节点
     */
    Set<GraphNode<?>> getParentsInEvent(String event);

    /**
     * 获取事件对应的 children
     *
     * @param event 事件
     * @return children节点
     */
    Set<GraphNode<?>> getChildrenInEvent(String event);

    /**
     * 获取子节点
     *
     * @return 子节点列表
     */
    Set<GraphNode<?>> getChildren();

    /**
     * 添加父节点
     *
     * @param node 执行节点
     */
    void addParent(GraphNode<?> node);

    /**
     * 添加子节点
     *
     * @param child 执行节点
     */
    void addChild(GraphNode<?> child);

    /**
     * 添加event依赖, 同时添加到子节点
     *
     * @param event 事件code
     */
    void addEvent(String event);

    /**
     * 获取关联的事件集合
     *
     * @return 事件集合
     */
    Set<String> getEvents();

    /**
     * 添加依赖
     *
     * @param dependency 依赖
     */
    void addDependency(String dependency);

    /**
     * 获取依赖
     *
     * @return 依赖集合
     */
    Set<String> getDependencies();

    /**
     * 初始化
     */
    void refresh();
}

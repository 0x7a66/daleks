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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import top.daleks.risk.common.DependencyContext;
import top.daleks.risk.common.GraphNode;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.common.enums.ConfigType;
import top.daleks.risk.common.model.BaseEntity;
import top.daleks.risk.engine.node.EndNode;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractGraphNode<T extends BaseEntity> implements GraphNode<T> {

    private final DependencyContext dependencyContext;
    private final T data;
    private final Set<GraphNode<?>> parents = new TreeSet<>();
    private final Set<GraphNode<?>> children = new TreeSet<>();
    private final Set<String> events = new TreeSet<>();
    private final Map<String, Set<GraphNode<?>>> eventParents = new HashMap<>();
    private final Map<String, Set<GraphNode<?>>> eventChildren = new HashMap<>();
    private final Set<String> dependencies = new TreeSet<>();

    public AbstractGraphNode(DependencyContext dependencyContext, T data) {
        this.dependencyContext = dependencyContext;
        this.data = data;
    }

    @Override
    public T getData() {
        return this.data;
    }

    @Override
    public DependencyContext getDependencyContext() {
        return this.dependencyContext;
    }

    @Override
    public Set<GraphNode<?>> getParentsInEvent(String event) {
        Set<GraphNode<?>> nodes = this.eventParents.getOrDefault(event, new HashSet<>());
        if (CollectionUtils.isEmpty(nodes)) {
            nodes.add(getDependencyContext().getNode(EndNode.NAME));
        }
        return nodes;
    }

    @Override
    public Set<GraphNode<?>> getChildrenInEvent(String event) {
        return this.eventChildren.getOrDefault(event, new HashSet<>());
    }

    @Override
    public void addParent(GraphNode<?> node) {
        this.parents.add(node);
    }

    @Override
    public Set<GraphNode<?>> getChildren() {
        return this.children;
    }

    @Override
    public void addChild(GraphNode<?> child) {
        if (child.equals(this)) {
            log.error("child equals parent" + this.toString());
            return;
        }
        this.children.add(child);
    }

    @Override
    public void addEvent(String event) {
        this.events.add(event);
        for (GraphNode<?> child : this.children) {
            child.addEvent(event);
        }
    }

    @Override
    public Set<String> getEvents() {
        return this.events;
    }

    @Override
    public void addDependency(String dependency) {
        this.dependencies.add(dependency);
    }

    @Override
    public Set<String> getDependencies() {
        return this.dependencies;
    }

    @Override
    public void refresh() {
        for (String event : events) {
            for (GraphNode<?> parent : this.parents) {
                if (parent.getEvents().contains(event)) {
                    this.eventParents.putIfAbsent(event, new TreeSet<>());
                    this.eventParents.get(event).add(parent);
                }
            }
            for (GraphNode<?> child : this.children) {
                if (child.getEvents().contains(event)) {
                    this.eventChildren.putIfAbsent(event, new TreeSet<>());
                    this.eventChildren.get(event).add(child);
                }
            }
        }
    }

    @Override
    public void runAsync(RiskContext riskContext) {
        this.dependencyContext.getNodeExecutorService().submit(() -> doRun(riskContext));
    }

    @Override
    public void runSync(RiskContext riskContext) {
        doRun(riskContext);
    }

    protected void doRun(RiskContext riskContext) {
        String riskId = riskContext.getRiskId();
        if (!runPreCheck(riskContext)) {
            return;
        }
        if (riskContext.setNodeExecuting(this)) {
            try {
                doWork(riskContext);
            } catch (Exception e) {
                log.error("{} -> node: {} ????????????", riskId, getName(), e);
            }
            riskContext.setNodeFinished(this);
            runNextNodes(riskContext);
        } else {
            log.debug("{} -> {} ???????????????", riskId, getName());
        }
    }

    /**
     * ???????????????
     *
     * @param riskContext ???????????????
     */
    protected void runNextNodes(RiskContext riskContext) {
        Set<GraphNode<?>> nextNodes = getParentsInEvent(riskContext.getEvent());
        if (CollectionUtils.isEmpty(nextNodes)) {
            return;
        }
        // ?????????????????????????????????????????????????????????????????????????????????
        if (nextNodes.size() == 1) {
            for (GraphNode<?> node : nextNodes) {
                log.debug("{} -> {} ??????????????? {}", riskContext.getRiskId(), getName(), node.getName());
                node.runSync(riskContext);
            }
            return;
        }
        for (GraphNode<?> node : nextNodes) {
            log.debug("{} -> {} ??????????????? {}", riskContext.getRiskId(), getName(), node.getName());
            node.runAsync(riskContext);
        }
    }

    /**
     * 1. ????????????node???????????????????????????
     * 2. ?????????????????????????????????????????????
     * 3. ?????????????????????????????????
     */
    protected boolean runPreCheck(RiskContext riskContext) {

        if (riskContext.isNodeFinished(this)) {
            log.debug("{} -> {} ???????????????, ?????????", riskContext.getRiskId(), getName());
            return false;
        }
        if (!riskContext.isAllDependenciesFinished(getChildrenInEvent(riskContext.getEvent()))) {
            log.debug("{} -> {} ??????????????????, ?????????", riskContext.getRiskId(), getName());
            return false;
        }
        if (riskContext.isFinished()) {
            log.debug("{} -> {} ???????????????, ?????????", riskContext.getRiskId(), getName());
            return false;
        }
        return true;
    }

    /**
     * ????????????
     *
     * @param riskContext ???????????????
     */
    abstract protected void doWork(RiskContext riskContext);

    /**
     * ??????????????????????????????
     *
     * @param riskContext ???????????????
     * @return ???????????????
     */
    protected Map<String, Object> getProperties(RiskContext riskContext) {
        return getProperties(riskContext, getDependencies());
    }

    /**
     * ??????????????????????????????
     *
     * @param riskContext  ???????????????
     * @param dependencies ??????
     * @return ???????????????
     */
    protected Map<String, Object> getProperties(RiskContext riskContext, Set<String> dependencies) {
        Map<String, Object> properties = new HashMap<>();
        if (CollectionUtils.isEmpty(dependencies)) {
            return properties;
        }
        for (String dependency : dependencies) {
            properties.put(dependency, getProperty(riskContext, dependency));
        }
        return properties;
    }

    /**
     * ???????????????
     *
     * @param riskContext ???????????????
     * @param name        ?????????
     * @return ?????????
     */
    protected Object getProperty(RiskContext riskContext, String name) {
        return riskContext.getProperty(getMappingOriginName(riskContext, name));
    }

    /**
     * ??????????????????????????????
     *
     * @param riskContext ???????????????
     * @param name        ?????????
     * @return ??????????????????
     */
    protected String getMappingOriginName(RiskContext riskContext, String name) {
        ConfigType type = ConfigType.classOf(getClass());
        if (getData() == null || type == null) {
            return name;
        }
        String originName = this.dependencyContext.getParameterMappingOriginName(riskContext.getEvent(), type, getData().getId(), name);
        return StringUtils.isBlank(originName) ? name : originName;
    }

    @Override
    public int compareTo(GraphNode<?> o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractGraphNode)) return false;
        AbstractGraphNode<?> that = (AbstractGraphNode<?>) o;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(": from[");
        sb.append(this.children.stream().map(GraphNode::getName).collect(Collectors.joining(",")));
        sb.append("] -> to[");
        sb.append((this.parents.size() == 0 && !getName().equals(EndNode.NAME)) ? EndNode.NAME : this.parents.stream().map(GraphNode::getName).collect(Collectors.joining(",")));
        sb.append("]");
        return sb.toString();
    }
}

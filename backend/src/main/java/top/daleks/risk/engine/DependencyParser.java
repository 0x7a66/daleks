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
import top.daleks.risk.common.Constants;
import top.daleks.risk.common.DependencyContext;
import top.daleks.risk.common.GraphNode;
import top.daleks.risk.common.RuleIndicator;
import top.daleks.risk.common.enums.ConfigType;
import top.daleks.risk.common.model.*;
import top.daleks.risk.engine.node.*;
import top.daleks.risk.utils.GroovyUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static top.daleks.risk.common.Constants.SPLIT;

/**
 * 依赖解析及持有容器
 */
@Slf4j
public class DependencyParser {

    private final static String PARAMETER_MAPPING_FORMAT = "%s_%s_%s_%s";

    private final DependencyContext dependencyContext;
    private final Map<String /* name  */, GraphNode<?>> graphNodeMap = new ConcurrentHashMap<>();
    private final Map<String /* event */, List<RuleIndicator>> eventRuleIndicators = new ConcurrentHashMap<>();
    private final Map<String /* event_code*/, Event> eventMap = new ConcurrentHashMap<>();
    private final Map<String /* event_configType_id_name */, String> parameterMapping = new ConcurrentHashMap<>();

    public DependencyParser(DependencyContext dependencyContext) {
        this.dependencyContext = dependencyContext;
        buildDependency();
        buildParameterMapping();
        this.graphNodeMap.forEach((name, node) -> log.info(node.toString()));
        log.info("DependencyParser 解析结束");
    }

    /**
     * 获取节点信息
     *
     * @param name 节点名称
     * @return 节点
     */
    public GraphNode<?> getNode(String name) {
        return this.graphNodeMap.get(name);
    }

    /**
     * 获取事件
     *
     * @param event 事件code
     * @return Event
     */
    public Event getEvent(String event) {
        return this.eventMap.get(event);
    }

    /**
     * 获取规则指示器
     *
     * @param event 事件
     * @return 规则指示器
     */
    List<RuleIndicator> getRuleIndicatorInEvent(String event) {
        return this.eventRuleIndicators.getOrDefault(event, new ArrayList<>());
    }

    /**
     * 解析配置生成所有数据节点
     */
    public void buildDependency() {

        // 构建end节点
        GraphNode<?> end = new EndNode(this.dependencyContext);

        // 构建变量节点
        List<Variable> variables = this.dependencyContext.getConfigDataProvider().getAllVariable();
        List<VariableNode> variableNodes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(variables)) {
            for (Variable variable : variables) {
                VariableNode node = new VariableNode(this.dependencyContext, variable);
                this.graphNodeMap.put(node.getName(), node);
                variableNodes.add(node);
            }
        }

        // 构建累积因子节点
        List<Accumulate> accumulates = this.dependencyContext.getConfigDataProvider().getAllAccumulate();
        List<AccumulateNode> accumulateNodes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accumulates)) {
            for (Accumulate accumulate : accumulates) {
                AccumulateNode node = new AccumulateNode(this.dependencyContext, accumulate);
                this.graphNodeMap.put(node.getName(), node);
                accumulateNodes.add(node);
            }
        }

        // 变量节点依赖处理
        for (VariableNode node : variableNodes) {
            Set<String> dependencies = GroovyUtils.parse(node.getData().getParameters());
            setParentAndChildren(node, dependencies);
            addDependencies(node, dependencies);
        }

        // 累积因子分组key及聚合key依赖处理
        for (AccumulateNode node : accumulateNodes) {

            Set<String> groupKeyDependencies = GroovyUtils.parse(node.getData().getGroupKey());
            node.addGroupKeyDependencies(groupKeyDependencies);

            Set<String> aggKeyDependencies = GroovyUtils.parse(node.getData().getAggKey());
            node.addAggKeyDependencies(aggKeyDependencies);

            Set<String> dependencies = new TreeSet<>(groupKeyDependencies);
            dependencies.addAll(aggKeyDependencies);

            setParentAndChildren(node, dependencies);
        }

        // 构建规则节点及其依赖处理
        List<AtomRule> atomRules = this.dependencyContext.getConfigDataProvider().getAllAtomRule();
        Map<String, AtomRuleNode> atomRuleNodeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(atomRules)) {
            for (AtomRule atomRule : atomRules) {
                AtomRuleNode node = new AtomRuleNode(this.dependencyContext, atomRule);
                this.graphNodeMap.put(node.getName(), node);
                Set<String> dependencies = GroovyUtils.parse(node.getData().getScript());
                setParentAndChildren(node, dependencies);
                addDependencies(node, dependencies);
                atomRuleNodeMap.put(atomRule.getId().toString(), node);
            }
        }

        // 构建规则组节点及其依赖处理
        List<GroupRule> groupRules = this.dependencyContext.getConfigDataProvider().getAllGroupRule();
        Map<String, GroupRuleNode> groupRuleNodeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(groupRules)) {
            for (GroupRule groupRule : groupRules) {
                GroupRuleNode node = new GroupRuleNode(this.dependencyContext, groupRule);
                this.graphNodeMap.put(node.getName(), node);
                Set<String> dependencies = GroovyUtils.parse(node.getData().getScript());
                setParentAndChildren(node, dependencies);
                addDependencies(node, dependencies);
                groupRuleNodeMap.put(groupRule.getId().toString(), node);
            }
        }

        // 构建起始执行节点
        GraphNode<?> begin = new BeginNode(this.dependencyContext);
        for (Map.Entry<String, GraphNode<?>> entry : this.graphNodeMap.entrySet()) {
            GraphNode<?> node = entry.getValue();
            if (CollectionUtils.isEmpty(node.getChildren())) {
                setParentAndChild(node, begin);
            }
        }

        // event 处理
        // 规则及规则组 event 填充
        List<Event> events = this.dependencyContext.getConfigDataProvider().getAllEvent();
        Map<String, Event> eventIdMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(events)) {
            for (Event event : events) {
                eventMap.put(event.getCode(), event);
                eventIdMap.put(event.getId().toString(), event);
                eventRuleIndicators.putIfAbsent(event.getCode(), new ArrayList<>());

                if (StringUtils.isBlank(event.getGroupRules())) {
                    continue;
                }
                for (String groupId : event.getGroupRules().split(SPLIT)) {
                    GroupRuleNode groupRuleNode = groupRuleNodeMap.get(groupId);
                    if (groupRuleNode == null) {
                        continue;
                    }
                    // 规则组 event 填充
                    groupRuleNode.addEvent(event.getCode());
                    eventRuleIndicators.get(event.getCode()).add(RuleIndicator.create(groupRuleNode.getData()));

                    if (StringUtils.isNotBlank(groupRuleNode.getData().getAtomRules())) {
                        for (String atomRuleId : groupRuleNode.getData().getAtomRules().split(SPLIT)) {
                            AtomRuleNode atomRuleNode = atomRuleNodeMap.get(atomRuleId.trim());
                            if (atomRuleNode != null) {
                                atomRuleNode.addEvent(event.getCode());
                                eventRuleIndicators.get(event.getCode()).add(RuleIndicator.create(groupRuleNode.getData(), atomRuleNode.getData()));
                            }
                        }
                    }
                }
            }
        }

        // 累积因子前置条件依赖处理
        for (AccumulateNode node : accumulateNodes) {
            Set<String> dependencies = GroovyUtils.parse(node.getData().getScript());
            if (dependencies.contains(Constants.RESULT)) {
                node.setDelay(true);
            }
            setParentAndChildren(node, dependencies);
            addDependencies(node, dependencies);
        }

        // 累积因子 event 填充
        for (AccumulateNode node : accumulateNodes) {
            String eventString = node.getData().getEvents();
            if (StringUtils.isNotBlank(eventString)) {
                for (String eventId : eventString.split(SPLIT)) {
                    Event event = eventIdMap.get(eventId);
                    if (event != null) {
                        node.addEvent(event.getCode());
                        node.addBindEvent(event.getCode());
                    }
                }
            }
        }

        this.graphNodeMap.put(begin.getName(), begin);
        this.graphNodeMap.put(end.getName(), end);

        // refresh
        this.graphNodeMap.forEach((name, node) -> node.refresh());
    }

    public void buildParameterMapping() {
        List<ParameterMapping> mappings = this.dependencyContext.getConfigDataProvider().getAllParameterMapping();
        if (CollectionUtils.isNotEmpty(mappings)) {
            for (ParameterMapping mapping : mappings) {
                String key = String.format(PARAMETER_MAPPING_FORMAT, mapping.getEvent(), mapping.getConfig().name(), mapping.getConfigId(), mapping.getMapping());
                this.parameterMapping.put(key, mapping.getOrigin());
            }
        }
    }


    public String getParameterMappingOriginName(String event, ConfigType configType, Long configId, String name) {
        String key = String.format(PARAMETER_MAPPING_FORMAT, event, configType.name(), configId, name);
        return this.parameterMapping.get(key);
    }

    /**
     * 添加依赖属性
     *
     * @param node         节点
     * @param dependencies 依赖
     */
    private void addDependencies(GraphNode<?> node, Set<String> dependencies) {
        if (CollectionUtils.isEmpty(dependencies)) {
            return;
        }
        for (String dependency : dependencies) {
            node.addDependency(dependency);
        }
    }

    /**
     * 更新依赖关系
     *
     * @param parent       父节点
     * @param dependencies 依赖
     */
    private void setParentAndChildren(GraphNode<?> parent, Set<String> dependencies) {
        if (CollectionUtils.isEmpty(dependencies)) {
            return;
        }
        for (String dependency : dependencies) {
            if (this.graphNodeMap.containsKey(dependency)) {
                setParentAndChild(parent, this.graphNodeMap.get(dependency));
            }
        }
    }

    /**
     * 给父节点设置子节点，给子节点设置父节点
     *
     * @param parent 父节点
     * @param child  子节点
     */
    private void setParentAndChild(GraphNode<?> parent, GraphNode<?> child) {

        if (!isLoop(parent, child)) {
            parent.addChild(child);
            child.addParent(parent);
        } else {
            log.error("发现循环引用:{}, {}", parent.getName(), child.getName());
        }
    }

    /**
     * 检查循环
     *
     * @param parent 父节点
     * @param child  子节点
     * @return 是否循环依赖
     */
    private boolean isLoop(GraphNode<?> parent, GraphNode<?> child) {
        Set<String> set = new HashSet<>();
        getAllChild(child, set);
        //包含即循环
        return set.contains(parent.getName());
    }

    /**
     * 递归获取所有的子节点
     *
     * @param child 子节点
     * @param set   子节点集合
     */
    private void getAllChild(GraphNode<?> child, Set<String> set) {
        if (child.getChildren() != null && child.getChildren().size() > 0) {
            for (GraphNode<?> node : child.getChildren()) {
                set.add(node.getName());
                getAllChild(node, set);
            }
        }
    }

}

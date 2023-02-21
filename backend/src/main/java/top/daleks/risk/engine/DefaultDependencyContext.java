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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.daleks.risk.common.*;
import top.daleks.risk.common.enums.ConfigType;
import top.daleks.risk.common.model.Event;
import top.daleks.risk.utils.NamedForkJoinWorkerThreadFactory;
import top.daleks.risk.utils.NamedThreadFactory;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.*;

/**
 * 依赖上下文
 */
@Slf4j
@Component
public class DefaultDependencyContext implements DependencyContext {

    @Autowired
    private ConfigDataProvider configDataProvider;

    @Autowired
    private VariableService variableService;

    @Autowired
    private AccumulateService accumulateService;

    @Autowired
    private List<RiskAfterHandler> riskAfterHandlers;

    @Value("${risk.node.execute.threads:200}")
    private Integer nodeThreads;

    @Value("${risk.before.execute.threads:200}")
    private Integer beforeThreads;

    @Value("${risk.after.execute.threads:200}")
    private Integer afterThreads;

    // 节点执行线程池
    private ExecutorService nodeExecutorService;
    // 前置任务执行线程池
    private ForkJoinPool riskBeforeForkJoinPool;
    // 后置任务执行线程池
    private ForkJoinPool riskAfterForkJoinPool;

    // 依赖解析及配置持有容器
    private volatile DependencyParser dependencyParser;

    @PostConstruct
    public void init() {
        this.nodeExecutorService = new ThreadPoolExecutor(nodeThreads, nodeThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(nodeThreads * 2),
                new NamedThreadFactory("GraphNode"),
                new ThreadPoolExecutor.CallerRunsPolicy());

        this.riskBeforeForkJoinPool = new ForkJoinPool(beforeThreads, new NamedForkJoinWorkerThreadFactory("RiskBeforeHandler"), null, false);
        this.riskAfterForkJoinPool = new ForkJoinPool(afterThreads, new NamedForkJoinWorkerThreadFactory("RiskAfterHandler"), null, false);

        this.build();

        getConfigDataProvider().addChangeListener(this::build);
    }

    public synchronized void build() {
        // help GC to remove groovy script class
        GroovyExecutor.clearCache();
        this.dependencyParser = new DependencyParser(this);
    }

    /**
     * 获取节点信息
     *
     * @param name 节点名称
     * @return 节点
     */
    public GraphNode<?> getNode(String name) {
        return this.dependencyParser.getNode(name);
    }

    @Override
    public List<RuleIndicator> getRuleIndicatorInEvent(String event) {
        return this.dependencyParser.getRuleIndicatorInEvent(event);
    }

    @Override
    public String getParameterMappingOriginName(String event, ConfigType configType, Long configId, String name) {
        return this.dependencyParser.getParameterMappingOriginName(event, configType, configId, name);
    }

    /**
     * 获取线程池
     *
     * @return 节点执行线程池
     */
    public ExecutorService getNodeExecutorService() {
        return this.nodeExecutorService;
    }

    @Override
    public ForkJoinPool getRiskBeforeForkJoinPool() {
        return this.riskBeforeForkJoinPool;
    }

    @Override
    public ForkJoinPool getRiskAfterForkJoinPool() {
        return this.riskAfterForkJoinPool;
    }

    @Override
    public List<RiskAfterHandler> getRiskAfterHandlers() {
        return this.riskAfterHandlers;
    }

    @Override
    public AccumulateService getAccumulateService() {
        return this.accumulateService;
    }

    @Override
    public VariableService getVariableService() {
        return this.variableService;
    }

    @Override
    public ConfigDataProvider getConfigDataProvider() {
        return this.configDataProvider;
    }

    @Override
    public Event getEvent(String event) {
        return this.dependencyParser.getEvent(event);
    }
}

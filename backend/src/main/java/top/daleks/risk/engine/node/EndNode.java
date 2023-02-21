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
import org.apache.commons.collections4.CollectionUtils;
import top.daleks.risk.common.DependencyContext;
import top.daleks.risk.common.GraphNode;
import top.daleks.risk.common.RiskAfterHandler;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.common.model.BaseEntity;
import top.daleks.risk.engine.AbstractGraphNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

@Slf4j
public class EndNode extends AbstractGraphNode<BaseEntity> {

    public static final String NAME = "END";
    private final ForkJoinPool forkJoinPool;
    private final List<RiskAfterHandler> handlers = new ArrayList<>();

    public EndNode(DependencyContext dependencyContext) {
        super(dependencyContext, null);
        if (CollectionUtils.isNotEmpty(dependencyContext.getRiskAfterHandlers())) {
            this.handlers.addAll(dependencyContext.getRiskAfterHandlers());
        }
        this.forkJoinPool = dependencyContext.getRiskAfterForkJoinPool();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Set<GraphNode<?>> getParentsInEvent(String event) {
        return Collections.emptySet();
    }

    @Override
    protected void doWork(RiskContext riskContext) {
        if (CollectionUtils.isNotEmpty(this.handlers)) {
            this.forkJoinPool.execute(() -> this.handlers.parallelStream().forEach(handler -> {
                try {
                    handler.handle(riskContext);
                } catch (Exception e) {
                    log.error("{} -> 后置任务: {} 处理失败", riskContext.getRiskId(), handler.getClass().getSimpleName(), e);
                }
            }));
        }
    }

    @Override
    protected boolean runPreCheck(RiskContext riskContext) {
        // 执行完毕
        if (riskContext.isNodeFinished(this)) {
            return false;
        }
        return riskContext.isFinished();
    }
}

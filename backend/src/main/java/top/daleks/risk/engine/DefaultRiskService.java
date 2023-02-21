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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.daleks.risk.access.RiskService;
import top.daleks.risk.access.bean.RiskAction;
import top.daleks.risk.access.bean.RiskError;
import top.daleks.risk.access.bean.RiskLevel;
import top.daleks.risk.access.bean.RiskResult;
import top.daleks.risk.common.DependencyContext;
import top.daleks.risk.common.RiskBeforeHandler;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.common.enums.Tag;
import top.daleks.risk.engine.handler.LogAfterHandler;
import top.daleks.risk.engine.node.BeginNode;
import top.daleks.risk.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

/**
 * 请求入口
 */
@Slf4j
@Service
public class DefaultRiskService implements RiskService {

    private final DependencyContext dependencyContext;

    private final List<RiskBeforeHandler> riskBeforeHandlers = new ArrayList<>();

    private final LogAfterHandler logAfterHandler;

    @Value("${risk.execute.timeout:1000}")
    private Long defaultTimeout;

    public DefaultRiskService(DependencyContext dependencyContext, List<RiskBeforeHandler> riskBeforeHandlers, LogAfterHandler logAfterHandler) {
        this.dependencyContext = dependencyContext;
        if (CollectionUtils.isNotEmpty(riskBeforeHandlers)) {
            this.riskBeforeHandlers.addAll(riskBeforeHandlers);
        }
        this.logAfterHandler = logAfterHandler;
    }


    @Override
    public RiskResult detect(RiskAction riskAction) {
        String riskId = RandomUtils.uuid();
        if (riskAction == null || StringUtils.isBlank(riskAction.getEvent())) {
            return RiskResult.error(riskId, RiskError.PARAM_ERROR);
        }

        riskAction.getData().put(RiskAction.KEY_RISK_ID, riskId);

        if (riskAction.getTimeout() == null || riskAction.getTimeout() <= 0) {
            riskAction.setTimeout(defaultTimeout);
        }

        return doDetect(riskAction);
    }

    private RiskResult doDetect(RiskAction riskAction) {

        // 检查 DROP 标记
        if (riskAction.hasTag(Tag.DROP.name())) {
            return RiskResult.pass(riskAction.getRiskId());
        }

        RiskContext riskContext = new DefaultRiskContext(riskAction, this.dependencyContext);

        // 检查 IGNORE 标记
        if (riskAction.hasTag(Tag.IGNORE.name())) {
            this.logAfterHandler.handle(riskContext);
            return RiskResult.pass(riskAction.getRiskId());
        }

        try {
            ForkJoinTask<?> task = this.dependencyContext.getRiskBeforeForkJoinPool().submit(
                    () -> this.riskBeforeHandlers.parallelStream().forEach(handler -> handler.handle(riskContext))
            );
            task.join();
        } catch (Exception e) {
            log.error("RiskBeforeHandler 处理失败", e);
        }

        this.dependencyContext.getNode(BeginNode.NAME).runAsync(riskContext);

        // 异步直接返回
        if (riskAction.isAsync()) {
            RiskResult result = RiskResult.success(riskAction.getRiskId());
            result.setLevel(RiskLevel.UNKNOWN);
            return result;
        }
        return riskContext.waitResult(riskAction.getTimeout());
    }
}

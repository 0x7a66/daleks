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

package top.daleks.risk.engine.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import top.daleks.risk.common.RiskAfterHandler;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.common.model.RiskLog;
import top.daleks.risk.utils.JsonUtils;

/**
 * 记录风控请求日志
 */
@Slf4j
@Component
public class LogAfterHandler implements RiskAfterHandler {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void handle(RiskContext riskContext) {
        RiskLog riskLog = new RiskLog();
        riskLog.setRiskId(riskContext.getRiskId());
        riskLog.setCostTime(riskContext.getCostTime());
        riskLog.setAction(riskContext.getRiskAction().getData());
        riskLog.setContext(riskContext.getContextData());
        riskLog.setResult(riskContext.getResult());
        riskLog.setRuleResults(riskContext.getRuleResults());
        log.info("{} -> 执行结束, 保存日志: {}", riskContext.getRiskId(), JsonUtils.string(riskLog));
        mongoTemplate.save(riskLog);
    }
}

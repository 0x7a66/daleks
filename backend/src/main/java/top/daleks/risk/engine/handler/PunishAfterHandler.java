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
import org.springframework.stereotype.Component;
import top.daleks.risk.access.bean.RiskLevel;
import top.daleks.risk.common.RiskAfterHandler;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.common.RuleResult;
import top.daleks.risk.common.enums.ConfigType;
import top.daleks.risk.common.enums.RuleState;

/**
 * 命中规则执行动作
 */
@Slf4j
@Component
public class PunishAfterHandler implements RiskAfterHandler {

    @Override
    public void handle(RiskContext riskContext) {
        boolean groupHit = false;
        for (RuleResult ruleResult : riskContext.getRuleResults()) {
            if (ruleResult.getType().equals(ConfigType.GROUP_RULE)) {
                groupHit = ruleResult.getRuleState().equals(RuleState.ENABLE) && ruleResult.getResult().equals(Boolean.TRUE.toString());
            }
            if (ruleResult.getType().equals(ConfigType.ATOM_RULE)) {
                if (!groupHit) {
                    continue;
                }
                if (ruleResult.getRuleState().equals(RuleState.ENABLE) &&
                        (ruleResult.getResult().equals(RiskLevel.REJECT.name()) || ruleResult.getResult().equals(RiskLevel.REVIEW.name()))
                ) {
                    // TODO 规则命中，执行惩罚
                }
            }
        }
    }
}

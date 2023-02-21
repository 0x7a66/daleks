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

import lombok.Getter;
import lombok.Setter;
import top.daleks.risk.common.enums.ConfigType;
import top.daleks.risk.common.enums.RuleState;

import java.io.Serializable;

/**
 * 规则及规则组命中结果
 */
@Setter
@Getter
public class RuleResult implements Serializable {
    private ConfigType type;
    private Long id;
    private String name;
    private RuleState ruleState;
    /**
     * 规则组：TRUE, FALSE
     * 规则：{@link top.daleks.risk.access.bean.RiskLevel}
     */
    private String result;

    public static RuleResult create(RuleIndicator indicator, String result) {
        RuleResult ruleResult = new RuleResult();
        ruleResult.setType(indicator.getConfigType());
        ruleResult.setId(indicator.getId());
        if (indicator.getConfigType().equals(ConfigType.GROUP_RULE)) {
            ruleResult.setName(indicator.getGroupRule().getName());
        }
        if (indicator.getConfigType().equals(ConfigType.ATOM_RULE)) {
            ruleResult.setName(indicator.getAtomRule().getName());
        }
        ruleResult.setRuleState(indicator.getRuleState());
        ruleResult.setResult(result);
        return ruleResult;
    }
}

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
import top.daleks.risk.common.model.AtomRule;
import top.daleks.risk.common.model.GroupRule;

/**
 * 规则判断指示器
 * 构建[规则组1, 规则1, 规则2, 规则组2, 规则3, 规则组4, 规则4] 数组
 * 用于判断规则优先级命中情况
 */
@Setter
@Getter
public class RuleIndicator {
    private ConfigType configType;
    private Long id;
    private RuleState ruleState;
    private GroupRule groupRule;
    private AtomRule atomRule;

    public static RuleIndicator create(GroupRule groupRule) {
        RuleIndicator indicator = new RuleIndicator();
        indicator.setConfigType(ConfigType.GROUP_RULE);
        indicator.setId(groupRule.getId());
        indicator.setRuleState(groupRule.getState());
        indicator.setGroupRule(groupRule);
        return indicator;
    }

    public static RuleIndicator create(GroupRule groupRule, AtomRule atomRule) {
        RuleIndicator indicator = new RuleIndicator();
        indicator.setConfigType(ConfigType.ATOM_RULE);
        indicator.setId(atomRule.getId());
        indicator.setRuleState(atomRule.getState());
        indicator.setGroupRule(groupRule);
        indicator.setAtomRule(atomRule);
        return indicator;
    }
}

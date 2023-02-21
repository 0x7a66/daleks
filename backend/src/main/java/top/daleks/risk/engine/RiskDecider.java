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

import top.daleks.risk.access.bean.RiskLevel;
import top.daleks.risk.common.DependencyContext;
import top.daleks.risk.common.RuleIndicator;
import top.daleks.risk.common.RuleResult;
import top.daleks.risk.common.enums.ConfigType;
import top.daleks.risk.common.enums.RuleState;
import top.daleks.risk.common.model.AtomRule;
import top.daleks.risk.common.model.GroupRule;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 执行结果判断
 */
public class RiskDecider {

    private final Map<Long, Boolean> groupRuleHits = new ConcurrentHashMap<>();
    private final Map<Long, RiskLevel> atomRuleHits = new ConcurrentHashMap<>();
    private final List<RuleIndicator> ruleIndicators;
    private volatile boolean hit = false;
    private volatile AtomRule hitAtomRule = null;
    private volatile GroupRule hitGroupRule = null;
    private volatile RiskLevel riskLevel = RiskLevel.PASS;
    private int indicator = 0;

    public RiskDecider(String event, DependencyContext dependencyContext) {
        this.ruleIndicators = dependencyContext.getRuleIndicatorInEvent(event);
    }

    public void hitAtomRule(Long id, RiskLevel level) {
        this.atomRuleHits.put(id, level);
    }

    public void hitGroupRule(Long id, Boolean hit) {
        this.groupRuleHits.put(id, hit);
    }

    public boolean isHit() {
        return this.hit;
    }

    public AtomRule getHitAtomRule() {
        return this.hitAtomRule;
    }

    public GroupRule getHitGroupRule() {
        return this.hitGroupRule;
    }


    public RiskLevel getRiskLevel() {
        return this.riskLevel;
    }

    /**
     * 规则命中逻辑判断
     */
    public boolean decide() {
        if (this.hit) {
            return true;
        }

        while (indicator < ruleIndicators.size()) {
            RuleIndicator indicator = ruleIndicators.get(this.indicator);
            if (indicator.getConfigType().equals(ConfigType.GROUP_RULE)) {
                if (indicator.getRuleState().equals(RuleState.ENABLE) && this.groupRuleHits.get(indicator.getId()) == null) {
                    return false;
                }
            }
            if (indicator.getConfigType().equals(ConfigType.ATOM_RULE) && indicator.getGroupRule().getState().equals(RuleState.ENABLE)) {
                RiskLevel riskLevel = this.atomRuleHits.get(indicator.getId());
                if (riskLevel == null) {
                    return false;
                }
                if ((riskLevel == RiskLevel.REJECT || riskLevel == RiskLevel.REVIEW) && indicator.getRuleState().equals(RuleState.ENABLE)) {
                    this.hit = true;
                    this.hitAtomRule = indicator.getAtomRule();
                    this.hitGroupRule = indicator.getGroupRule();
                    this.riskLevel = riskLevel;
                    return true;
                }
            }
            this.indicator++;
        }
        return true;
    }

    /**
     * 风控请求是否处理完成
     * 1. 规则组执行完毕
     * 2. 规则执行完毕
     *
     * @return 是否完成
     */
    public boolean isFinished() {
        return this.ruleIndicators.size() == this.groupRuleHits.size() + this.atomRuleHits.size();
    }

    public List<RuleResult> getRuleResults() {
        return this.ruleIndicators.stream().map(indicator -> {
            String result = "";
            if (indicator.getConfigType().equals(ConfigType.GROUP_RULE)) {
                Boolean hit = this.groupRuleHits.get(indicator.getId());
                result = hit != null ? hit.toString() : "";
            }
            if (indicator.getConfigType().equals(ConfigType.ATOM_RULE)) {
                RiskLevel hit = this.atomRuleHits.get(indicator.getId());
                result = hit != null ? hit.name() : "";
            }
            return RuleResult.create(indicator, result);
        }).collect(Collectors.toList());
    }
}

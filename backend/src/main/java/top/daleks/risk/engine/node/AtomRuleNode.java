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

import top.daleks.risk.access.bean.RiskLevel;
import top.daleks.risk.common.DependencyContext;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.common.model.AtomRule;
import top.daleks.risk.engine.AbstractGraphNode;
import top.daleks.risk.engine.executor.AtomRuleExecutor;

public class AtomRuleNode extends AbstractGraphNode<AtomRule> {

    private final static String NAME_PREFIX = "AtomRule";

    private final AtomRuleExecutor executor;

    public AtomRuleNode(DependencyContext dependencyContext, AtomRule data) {
        super(dependencyContext, data);
        this.executor = new AtomRuleExecutor(getName(), data.getScript());
    }

    @Override
    public String getName() {
        return String.format("%s_%s", NAME_PREFIX, getData().getName());
    }

    @Override
    protected void doWork(RiskContext riskContext) {
        RiskLevel level = this.executor.execute(getProperties(riskContext));
        riskContext.hitAtomRule(getData().getId(), level);
    }
}

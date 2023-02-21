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
import top.daleks.risk.common.DependencyContext;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.common.model.BaseEntity;
import top.daleks.risk.engine.AbstractGraphNode;

@Slf4j
public class BeginNode extends AbstractGraphNode<BaseEntity> {

    public static final String NAME = "BEGIN";

    public BeginNode(DependencyContext dependencyContext) {
        super(dependencyContext, null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void doWork(RiskContext riskContext) {
        log.debug("{} -> 开始执行node", riskContext.getRiskId());
    }

    @Override
    protected boolean runPreCheck(RiskContext riskContext) {
        return true;
    }
}

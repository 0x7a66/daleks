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

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import top.daleks.risk.common.RiskAfterHandler;
import top.daleks.risk.common.RiskContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * node执行结束回调
 */
@Component
public class NodeFinishAfterHandler implements RiskAfterHandler {

    @Override
    public void handle(RiskContext riskContext) {
        List<Consumer<RiskContext>> callbacks = riskContext.getFinishCallbacks();
        if (CollectionUtils.isEmpty(callbacks)) {
            return;
        }
        for (Consumer<RiskContext> callback : callbacks) {
            callback.accept(riskContext);
        }
    }
}

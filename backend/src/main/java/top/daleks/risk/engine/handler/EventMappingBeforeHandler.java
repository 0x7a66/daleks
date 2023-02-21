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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.daleks.risk.common.DependencyContext;
import top.daleks.risk.common.RiskBeforeHandler;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.common.model.Event;
import top.daleks.risk.utils.JsonUtils;

import java.util.Map;

/**
 * 事件参数映射处理
 */
@Slf4j
@Component
public class EventMappingBeforeHandler implements RiskBeforeHandler {

    @Autowired
    DependencyContext dependencyContext;

    @Override
    public void handle(RiskContext riskContext) {
        Event event = dependencyContext.getEvent(riskContext.getEvent());
        if (event == null) {
            return;
        }
        String renameMapping = event.getRenameMapping();
        if (StringUtils.isBlank(renameMapping)) {
            return;
        }
        Map<String, String> mappings = JsonUtils.map(renameMapping);
        if (MapUtils.isNotEmpty(mappings)) {
            mappings.forEach((origin, mapping) -> riskContext.addProperty(mapping, riskContext.getRiskAction().getValue(origin)));
        }
    }
}

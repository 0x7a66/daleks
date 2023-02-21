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

package top.daleks.risk.variable.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.daleks.risk.common.enums.Dimension;
import top.daleks.risk.common.enums.GrayListType;
import top.daleks.risk.common.model.GrayList;
import top.daleks.risk.engine.graylist.GrayListService;
import top.daleks.risk.utils.NumberUtils;
import top.daleks.risk.variable.VariableFunction;

import java.util.Map;

/**
 * 白名单判断
 */
@Component
public class WhiteList implements VariableFunction {

    @Autowired
    GrayListService grayListService;

    @Override
    public String function() {
        return "whiteList";
    }

    @Override
    public Object getVariable(Map<String, Object> parameters) {
        long groupId = NumberUtils.toLong(parameters.get("groupId"));
        if (groupId <= 0) {
            return false;
        }
        Dimension dimension = Dimension.nameOf(parameters.get("dimension").toString());
        if (dimension == null) {
            return false;
        }
        String value = parameters.get("value").toString();
        GrayList hit = grayListService.hit(groupId, GrayListType.WHITE, dimension, value);
        return hit != null;
    }
}

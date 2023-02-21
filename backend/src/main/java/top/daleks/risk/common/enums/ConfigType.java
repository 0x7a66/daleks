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

package top.daleks.risk.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.daleks.risk.common.model.*;

@Getter
@AllArgsConstructor
public enum ConfigType {
    BUSINESS("业务"),
    EVENT("事件"),
    ATOM_RULE("规则"),
    GROUP_RULE("规则组"),
    VARIABLE("变量"),
    ACCUMULATE("累积因子"),
    PUNISH("惩罚"),
    FUNCTION("函数"),
    ;
    private String desc;

    public static ConfigType nameOf(String name) {
        for (ConfigType configType : ConfigType.values()) {
            if (configType.name().equalsIgnoreCase(name)) {
                return configType;
            }
        }
        return null;
    }

    public static ConfigType classOf(Class<?> clazz) {
        if (Business.class.isAssignableFrom(clazz)) {
            return BUSINESS;
        }
        if (Event.class.isAssignableFrom(clazz)) {
            return EVENT;
        }
        if (AtomRule.class.isAssignableFrom(clazz)) {
            return ATOM_RULE;
        }
        if (GroupRule.class.isAssignableFrom(clazz)) {
            return GROUP_RULE;
        }
        if (Variable.class.isAssignableFrom(clazz)) {
            return VARIABLE;
        }
        if (Accumulate.class.isAssignableFrom(clazz)) {
            return ACCUMULATE;
        }
        if (FunctionConfig.class.isAssignableFrom(clazz)) {
            return FUNCTION;
        }
        return null;
    }
}

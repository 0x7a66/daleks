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

package top.daleks.risk.access.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 风险等级
 */
@Getter
@AllArgsConstructor
public enum RiskLevel {

    UNKNOWN("未知"),
    PASS("通过"),
    REVIEW("审核"),
    REJECT("拒绝"),
    ;

    private String desc;

    public static RiskLevel nameOf(String name) {
        for (RiskLevel level : values()) {
            if (level.name().equalsIgnoreCase(name)) {
                return level;
            }
        }
        return null;
    }

}

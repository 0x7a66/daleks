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

@Getter
@AllArgsConstructor
public enum GrayListType {
    WHITE("白名单"),
    BLACK("黑名单"),
    ;
    private String desc;

    public static GrayListType nameOf(String name) {
        for (GrayListType grayListType : GrayListType.values()) {
            if (grayListType.name().equalsIgnoreCase(name)) {
                return grayListType;
            }
        }
        return null;
    }
}
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

package top.daleks.risk.web.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    SUCCESS("0", "成功"),
    UN_LOGIN("-1", "未登陆"),
    SYSTEM_ERROR("1", "系统错误"),
    FORM_ERROR("2", "表单错误"),
    RELY_ON_BUSINESS("3000", "有业务依赖，请先处理相关业务"),
    RELY_ON_EVENT("3001", "有事件依赖，请先处理相关事件"),
    RELY_ON_GROUP_RULE("3002", "有策略组依赖，请先处理相关策略组"),
    RELY_ON_ATOM_RULE("3003", "有策略依赖，请处理除相关策略"),
    RELY_ON_VARIABLE("3004", "有变量依赖，请处理除相关变量"),
    RELY_ON_ACCUMULATE("3005", "有指标依赖，请先处理相关指标"),
    ;

    private String code;
    private String msg;

}

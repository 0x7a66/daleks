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

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 风险判断结果对象
 */
@Setter
@Getter
public class RiskResult implements Serializable {

    private String riskId;      // 风控请求唯一id
    private boolean success;    // 请求处理是否成功
    private RiskLevel level;    // 风险级别
    private Long ruleId;        // 命中的规则id
    private String ruleName;    // 命中的规则名称
    private Long grayListId;    // 命中的名单组id
    private String reply;       // 话术
    private RiskError error;    // 调用出错信息
    private String returnJson;  // 业务参数返回集

    public RiskResult() {
    }

    public RiskResult(String riskId) {
        this.riskId = riskId;
    }

    public static RiskResult error(String riskId, RiskError error) {
        RiskResult result = new RiskResult(riskId);
        result.success = false;
        result.error = error;
        return result;
    }

    public static RiskResult success(String riskId) {
        RiskResult result = new RiskResult(riskId);
        result.success = true;
        return result;
    }

    public static RiskResult pass(String riskId) {
        RiskResult result = success(riskId);
        result.level = RiskLevel.PASS;
        return result;
    }

    public static RiskResult review(String riskId) {
        RiskResult result = success(riskId);
        result.level = RiskLevel.REVIEW;
        return result;
    }

    public static RiskResult reject(String riskId) {
        RiskResult result = success(riskId);
        result.level = RiskLevel.REJECT;
        return result;
    }
}

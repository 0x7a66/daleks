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

package top.daleks.risk.common.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import top.daleks.risk.access.bean.RiskResult;
import top.daleks.risk.common.RuleResult;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Document(value = "risk_log")
public class RiskLog implements Serializable {
    @Id
    private String riskId;
    private Long costTime;
    private Map<String, Object> action;
    private Map<String, Object> context;
    private RiskResult result;
    private List<RuleResult> ruleResults;
}

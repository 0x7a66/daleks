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

package top.daleks.risk.common;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import top.daleks.risk.common.model.Accumulate;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class AccumulateRequest implements Serializable {
    /**
     * 风控请求id
     */
    private String riskId;
    /**
     * 累积因子
     */
    private Accumulate accumulate;
    /**
     * 分组key的值集合
     */
    private List<?> groupKeyValues;
    /**
     * 聚合key值
     */
    private Object aggKeyValue;
    /**
     * 时间戳
     */
    private Long timestamp;

    public String groupKey() {
        return CollectionUtils.isEmpty(groupKeyValues) ? "" : groupKeyValues.stream().map(String::valueOf).collect(Collectors.joining("$"));
    }
}

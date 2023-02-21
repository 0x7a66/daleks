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

package top.daleks.risk.engine.accumulate;

import top.daleks.risk.common.AccumulateRequest;

/**
 * 聚合函数
 */
public interface AggFunction {
    /**
     * 累积因子计算
     */
    void calculate(AccumulateRequest request);

    /**
     * 获取累积因子值
     */
    Object getValue(AccumulateRequest request);

    /**
     * 聚合函数名称
     *
     * @return
     */
    String function();
}

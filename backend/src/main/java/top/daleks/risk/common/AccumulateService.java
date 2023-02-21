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

public interface AccumulateService {
    /**
     * 计算累积因子
     *
     * @param request 请求参数
     */
    void calculate(AccumulateRequest request);

    /**
     * 获取累积因子的值
     *
     * @param request 请求参数
     * @return
     */
    Object getValue(AccumulateRequest request);
}

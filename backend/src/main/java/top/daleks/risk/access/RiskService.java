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

package top.daleks.risk.access;

import top.daleks.risk.access.bean.RiskAction;
import top.daleks.risk.access.bean.RiskResult;

public interface RiskService {
    /**
     * 风控请求入口
     *
     * @param riskAction 事件行为数据
     * @return 风控结果
     */
    RiskResult detect(RiskAction riskAction);
}

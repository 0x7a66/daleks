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

package top.daleks.risk.web.service;

import top.daleks.risk.common.model.AtomRule;
import top.daleks.risk.web.BaseEntityService;

import java.util.List;

public interface AtomRuleOpService extends BaseEntityService<AtomRule> {

    List<AtomRule> queryByAccumulateId(Long id);

    List<AtomRule> queryByVariableId(Long id);

    List<AtomRule> queryByGroupRuleId(Long id);

    List<AtomRule> queryByAccumulateIds(List<Long> ids);

    List<AtomRule> queryByVariableIds(List<Long> ids);

    List<AtomRule> queryByGroupRuleIds(List<Long> ids);
}

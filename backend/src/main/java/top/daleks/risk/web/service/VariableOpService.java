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

import top.daleks.risk.common.model.Variable;
import top.daleks.risk.web.BaseEntityService;
import top.daleks.risk.web.request.QueryValueRequest;

import java.util.List;

public interface VariableOpService extends BaseEntityService<Variable> {

    List<Variable> all();

    boolean existName(String name);

    List<Variable> queryByAccumulateId(Long id);

    List<Variable> queryByGroupRuleId(Long id);

    List<Variable> queryByAtomRuleId(Long id);

    List<Variable> queryByFunctionConfigId(Long id);

    List<Variable> queryDependencyById(Long id);

    Object queryValue(QueryValueRequest request);

    List<Variable> queryByAccumulateIds(List<Long> ids);

    List<Variable> queryDependencyByIds(List<Long> ids);
}

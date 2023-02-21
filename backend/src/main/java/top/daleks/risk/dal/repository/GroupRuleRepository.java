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

package top.daleks.risk.dal.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.daleks.risk.common.enums.RuleState;
import top.daleks.risk.common.model.GroupRule;

import java.util.List;

@Repository
public interface GroupRuleRepository extends BaseRepository<GroupRule> {

    List<GroupRule> findAllByStateNot(RuleState state);

    List<GroupRule> findAllByScriptContains(String script);

    @Query(value = "from GroupRule g where find_in_set(:atomRuleId, g.atomRules) > 0")
    List<GroupRule> findByAtomRuleId(@Param("atomRuleId") Long id);
}

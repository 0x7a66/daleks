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

package top.daleks.risk.web.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.daleks.risk.common.model.Accumulate;
import top.daleks.risk.common.model.AtomRule;
import top.daleks.risk.common.model.GroupRule;
import top.daleks.risk.common.model.Variable;
import top.daleks.risk.dal.repository.AtomRuleRepository;
import top.daleks.risk.utils.GroovyUtils;
import top.daleks.risk.web.AbstractBaseEntityService;
import top.daleks.risk.web.service.*;
import top.daleks.risk.web.support.ErrorMessage;
import top.daleks.risk.web.support.RiskException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static top.daleks.risk.common.Constants.SPLIT;

@Service
public class AtomRuleOpServiceImpl extends AbstractBaseEntityService<AtomRule> implements AtomRuleOpService {

    @Autowired
    AtomRuleRepository atomRuleRepository;

    @Autowired
    AccumulateOpService accumulateOpService;

    @Autowired
    VariableOpService variableOpService;

    @Autowired
    GroupRuleOpService groupRuleOpService;

    @Autowired
    TagService tagService;

    @Override
    public List<AtomRule> queryByAccumulateId(Long id) {
        Accumulate accumulate = accumulateOpService.get(id);
        if (accumulate == null) {
            return Collections.emptyList();
        }
        List<AtomRule> atomRules = atomRuleRepository.findAllByScriptContains(accumulate.getName());
        List<AtomRule> filtered = atomRules.stream().filter(atomRule -> GroovyUtils.parse(atomRule.getScript()).contains(accumulate.getName())).collect(Collectors.toList());
        return tagService.fillTags(filtered);
    }

    @Override
    public List<AtomRule> queryByVariableId(Long id) {
        Variable variable = variableOpService.get(id);
        if (variable == null) {
            return Collections.emptyList();
        }
        List<AtomRule> atomRules = atomRuleRepository.findAllByScriptContains(variable.getName());
        List<AtomRule> filtered = atomRules.stream().filter(atomRule -> GroovyUtils.parse(atomRule.getScript()).contains(variable.getName())).collect(Collectors.toList());
        return tagService.fillTags(filtered);
    }

    @Override
    public List<AtomRule> queryByGroupRuleId(Long id) {
        GroupRule groupRule = groupRuleOpService.get(id);
        if (groupRule == null) {
            return Collections.emptyList();
        }
        String atomRules = groupRule.getAtomRules();
        if (StringUtils.isEmpty(atomRules)) {
            return Collections.emptyList();
        }
        List<Long> ids = Stream.of(atomRules.split(SPLIT)).map(Long::parseLong).collect(Collectors.toList());
        return queryByIds(ids);
    }

    @Override
    public List<AtomRule> queryByAccumulateIds(List<Long> ids) {
        List<AtomRule> result = new ArrayList<>();
        for (Long id : ids) {
            result.addAll(queryByAccumulateId(id));
        }
        return result;
    }

    @Override
    public List<AtomRule> queryByVariableIds(List<Long> ids) {
        List<AtomRule> result = new ArrayList<>();
        for (Long id : ids) {
            result.addAll(queryByVariableId(id));
        }
        return result;
    }

    @Override
    public List<AtomRule> queryByGroupRuleIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<AtomRule> atomRules = new ArrayList<>();
        for (Long id : ids) {
            atomRules.addAll(queryByGroupRuleId(id));
        }
        return atomRules;
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 依赖项检查
        List<GroupRule> groupRules = groupRuleOpService.queryByAtomRuleIds(ids);
        if (CollectionUtils.isNotEmpty(groupRules)) {
            throw new RiskException(ErrorMessage.RELY_ON_GROUP_RULE);
        }
        super.deleteByIds(ids);
    }
}

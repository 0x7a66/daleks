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
import top.daleks.risk.common.AccumulateRequest;
import top.daleks.risk.common.AccumulateService;
import top.daleks.risk.common.enums.AccumulateFunction;
import top.daleks.risk.common.enums.WindowUnit;
import top.daleks.risk.common.model.Accumulate;
import top.daleks.risk.common.model.AtomRule;
import top.daleks.risk.common.model.GroupRule;
import top.daleks.risk.common.model.Variable;
import top.daleks.risk.dal.repository.AccumulateRepository;
import top.daleks.risk.utils.GroovyUtils;
import top.daleks.risk.utils.RandomUtils;
import top.daleks.risk.web.AbstractBaseEntityService;
import top.daleks.risk.web.request.QueryValueRequest;
import top.daleks.risk.web.service.*;
import top.daleks.risk.web.support.ErrorMessage;
import top.daleks.risk.web.support.RiskException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccumulateOpServiceImpl extends AbstractBaseEntityService<Accumulate> implements AccumulateOpService {

    @Autowired
    AccumulateRepository accumulateRepository;

    @Autowired
    VariableOpService variableOpService;

    @Autowired
    AtomRuleOpService atomRuleOpService;

    @Autowired
    GroupRuleOpService groupRuleOpService;

    @Autowired
    TagService tagService;

    @Autowired
    AccumulateService accumulateService;

    @Override
    public List<Accumulate> all() {
        return accumulateRepository.findAll();
    }

    @Override
    public boolean exist(Accumulate entity) {
        return super.exist(entity) || variableOpService.existName(entity.getName());
    }

    @Override
    public boolean existName(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        Accumulate record = new Accumulate();
        record.setName(name);
        return super.exist(record);
    }

    @Override
    public Object aggFunctions() {
        return Arrays.stream(AccumulateFunction.values()).map(Enum::name).collect(Collectors.toList());
    }

    @Override
    public Object timeUnits() {
        return Arrays.stream(WindowUnit.values()).map(Enum::name).collect(Collectors.toList());
    }

    @Override
    public List<Accumulate> queryByVariableId(Long id) {
        Variable variable = variableOpService.get(id);
        if (variable == null) {
            return Collections.emptyList();
        }
        return queryRelationByName(variable.getName());
    }

    @Override
    public List<Accumulate> queryByAtomRuleId(Long id) {
        AtomRule atomRule = atomRuleOpService.get(id);
        if (atomRule == null) {
            return Collections.emptyList();
        }
        Set<String> dependency = GroovyUtils.parse(atomRule.getScript());
        List<Accumulate> accumulates = accumulateRepository.findAllByNameIn(new ArrayList<>(dependency));
        return tagService.fillTags(accumulates);
    }

    @Override
    public List<Accumulate> queryByGroupRuleId(Long id) {
        GroupRule groupRule = groupRuleOpService.get(id);
        if (groupRule == null) {
            return Collections.emptyList();
        }
        Set<String> dependency = GroovyUtils.parse(groupRule.getScript());
        List<Accumulate> accumulates = accumulateRepository.findAllByNameIn(new ArrayList<>(dependency));
        return tagService.fillTags(accumulates);
    }

    @Override
    public List<Accumulate> queryDependencyById(Long id) {
        Accumulate record = get(id);
        if (record == null) {
            return Collections.emptyList();
        }
        return queryRelationByName(record.getName());
    }

    @Override
    public Object queryValue(QueryValueRequest request) {
        Accumulate accumulate = get(request.getId());
        if (accumulate == null) {
            return null;
        }
        List<?> groupKeyValues;
        try {
            Object data = GroovyUtils.execute(accumulate.getGroupKey(), request.getData());
            groupKeyValues = data instanceof List ? (List<?>) data : Collections.singletonList(data);
        } catch (Exception e) {
            return e.getMessage();
        }
        if (CollectionUtils.isEmpty(groupKeyValues)) {
            return null;
        }
        AccumulateRequest accumulateRequest = new AccumulateRequest();
        accumulateRequest.setRiskId(RandomUtils.consoleUuid());
        accumulateRequest.setAccumulate(accumulate);
        accumulateRequest.setGroupKeyValues(groupKeyValues);
        accumulateRequest.setTimestamp(System.currentTimeMillis());
        return accumulateService.getValue(accumulateRequest);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 检查依赖项
        List<AtomRule> atomRules = atomRuleOpService.queryByAccumulateIds(ids);
        if (CollectionUtils.isNotEmpty(atomRules)) {
            throw new RiskException(ErrorMessage.RELY_ON_ATOM_RULE);
        }
        List<GroupRule> groupRules = groupRuleOpService.queryByAccumulateIds(ids);
        if (CollectionUtils.isNotEmpty(groupRules)) {
            throw new RiskException(ErrorMessage.RELY_ON_GROUP_RULE);
        }
        List<Variable> variables = variableOpService.queryByAccumulateIds(ids);
        if (CollectionUtils.isNotEmpty(variables)) {
            throw new RiskException(ErrorMessage.RELY_ON_VARIABLE);
        }
        List<Accumulate> accumulates = queryDependencyByIds(ids);
        if (CollectionUtils.isNotEmpty(accumulates)) {
            throw new RiskException(ErrorMessage.RELY_ON_ACCUMULATE);
        }

        super.deleteByIds(ids);
    }

    @Override
    public List<Accumulate> queryDependencyByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<Accumulate> accumulates = new ArrayList<>();
        for (Long id : ids) {
            accumulates.addAll(queryDependencyById(id));
        }
        return accumulates;
    }

    @Override
    public List<Accumulate> queryByVariableIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<Accumulate> accumulates = new ArrayList<>();
        for (Long id : ids) {
            accumulates.addAll(queryByVariableId(id));
        }
        return accumulates;
    }

    private List<Accumulate> queryRelationByName(String name) {
        List<Accumulate> accumulates = accumulateRepository.findAllByGroupKeyContainsOrAggKeyContainsOrScriptContains(name, name, name);
        List<Accumulate> filtered = accumulates.stream().filter(accumulate -> {
            if (GroovyUtils.parse(accumulate.getGroupKey()).contains(name)) {
                return true;
            }
            if (GroovyUtils.parse(accumulate.getAggKey()).contains(name)) {
                return true;
            }
            return GroovyUtils.parse(accumulate.getScript()).contains(name);
        }).collect(Collectors.toList());
        return tagService.fillTags(filtered);
    }
}

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
import top.daleks.risk.common.VariableRequest;
import top.daleks.risk.common.VariableService;
import top.daleks.risk.common.model.*;
import top.daleks.risk.dal.repository.VariableRepository;
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
public class VariableOpServiceImpl extends AbstractBaseEntityService<Variable> implements VariableOpService {

    @Autowired
    VariableRepository variableRepository;

    @Autowired
    AccumulateOpService accumulateOpService;

    @Autowired
    AtomRuleOpService atomRuleOpService;

    @Autowired
    GroupRuleOpService groupRuleOpService;

    @Autowired
    FunctionConfigOpService functionConfigOpService;

    @Autowired
    TagService tagService;

    @Autowired
    VariableService variableService;

    @Override
    public List<Variable> all() {
        return variableRepository.findAll();
    }

    @Override
    public boolean exist(Variable entity) {
        return super.exist(entity) || accumulateOpService.existName(entity.getName());
    }

    @Override
    public boolean existName(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        Variable record = new Variable();
        record.setName(name);
        return super.exist(record);
    }

    @Override
    public List<Variable> queryByAccumulateId(Long id) {
        Accumulate accumulate = accumulateOpService.get(id);
        if (accumulate == null) {
            return Collections.emptyList();
        }
        List<Variable> variables = variableRepository.findAllByParametersContains(accumulate.getName());
        List<Variable> filtered = variables.stream().filter(variable -> GroovyUtils.parse(variable.getParameters()).contains(accumulate.getName())).collect(Collectors.toList());
        return tagService.fillTags(filtered);
    }

    @Override
    public List<Variable> queryByAtomRuleId(Long id) {
        AtomRule atomRule = atomRuleOpService.get(id);
        if (atomRule == null) {
            return Collections.emptyList();
        }
        Set<String> dependency = GroovyUtils.parse(atomRule.getScript());
        List<Variable> variables = variableRepository.findAllByNameIn(new ArrayList<>(dependency));
        return tagService.fillTags(variables);
    }

    @Override
    public List<Variable> queryByGroupRuleId(Long id) {
        GroupRule groupRule = groupRuleOpService.get(id);
        if (groupRule == null) {
            return Collections.emptyList();
        }
        Set<String> dependency = GroovyUtils.parse(groupRule.getScript());
        List<Variable> variables = variableRepository.findAllByNameIn(new ArrayList<>(dependency));
        return tagService.fillTags(variables);
    }

    @Override
    public List<Variable> queryByFunctionConfigId(Long id) {
        FunctionConfig functionConfig = functionConfigOpService.get(id);
        if (functionConfig == null) {
            return Collections.emptyList();
        }
        List<Variable> variables = variableRepository.findAllByFunc(functionConfig.getFunc());
        return tagService.fillTags(variables);
    }

    @Override
    public List<Variable> queryDependencyById(Long id) {
        Variable variable = get(id);
        if (variable == null) {
            return Collections.emptyList();
        }
        List<Variable> variables = variableRepository.findAllByParametersContains(variable.getName());
        List<Variable> filtered = variables.stream().filter(var -> GroovyUtils.parse(var.getParameters()).contains(variable.getName())).collect(Collectors.toList());
        return tagService.fillTags(filtered);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object queryValue(QueryValueRequest request) {
        Variable variable = get(request.getId());
        if (variable == null) {
            return null;
        }
        Map<String, Object> parameters;
        try {
            parameters = (Map<String, Object>) GroovyUtils.execute(variable.getParameters(), request.getData());
        } catch (Exception e) {
            return e.getMessage();
        }
        VariableRequest variableRequest = new VariableRequest();
        variableRequest.setRiskId(RandomUtils.consoleUuid());
        variableRequest.setFunction(variable.getFunc());
        variableRequest.setParameters(parameters);
        return variableService.getVariable(variableRequest);
    }

    @Override
    public List<Variable> queryByAccumulateIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<Variable> variables = new ArrayList<>();
        for (Long id : ids) {
            variables.addAll(queryByAccumulateId(id));
        }
        return variables;
    }

    @Override
    public List<Variable> queryDependencyByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<Variable> variables = new ArrayList<>();
        for (Long id : ids) {
            variables.addAll(queryDependencyById(id));
        }
        return variables;
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 检查依赖项
        List<AtomRule> atomRules = atomRuleOpService.queryByVariableIds(ids);
        if (CollectionUtils.isNotEmpty(atomRules)) {
            throw new RiskException(ErrorMessage.RELY_ON_ATOM_RULE);
        }
        List<GroupRule> groupRules = groupRuleOpService.queryByVariableIds(ids);
        if (CollectionUtils.isNotEmpty(groupRules)) {
            throw new RiskException(ErrorMessage.RELY_ON_GROUP_RULE);
        }
        List<Accumulate> accumulates = accumulateOpService.queryByVariableIds(ids);
        if (CollectionUtils.isNotEmpty(accumulates)) {
            throw new RiskException(ErrorMessage.RELY_ON_ACCUMULATE);
        }
        List<Variable> variables = queryDependencyByIds(ids);
        if (CollectionUtils.isNotEmpty(variables)) {
            throw new RiskException(ErrorMessage.RELY_ON_VARIABLE);
        }
        super.deleteByIds(ids);
    }
}


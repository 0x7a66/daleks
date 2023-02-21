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
import top.daleks.risk.common.model.*;
import top.daleks.risk.dal.repository.GroupRuleRepository;
import top.daleks.risk.utils.GroovyUtils;
import top.daleks.risk.web.AbstractBaseEntityService;
import top.daleks.risk.web.service.*;
import top.daleks.risk.web.support.ErrorMessage;
import top.daleks.risk.web.support.RiskException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static top.daleks.risk.common.Constants.SPLIT;

@Service
public class GroupRuleOpServiceImpl extends AbstractBaseEntityService<GroupRule> implements GroupRuleOpService {

    @Autowired
    GroupRuleRepository groupRuleRepository;

    @Autowired
    AccumulateOpService accumulateOpService;

    @Autowired
    VariableOpService variableOpService;

    @Autowired
    EventOpService eventOpService;

    @Autowired
    AtomRuleOpService atomRuleOpService;

    @Autowired
    TagService tagService;

    @Override
    public List<GroupRule> queryByAccumulateId(Long id) {
        Accumulate accumulate = accumulateOpService.get(id);
        if (accumulate == null) {
            return Collections.emptyList();
        }
        List<GroupRule> groupRules = groupRuleRepository.findAllByScriptContains(accumulate.getName());
        List<GroupRule> filtered = groupRules.stream().filter(groupRule -> GroovyUtils.parse(groupRule.getScript()).contains(accumulate.getName())).collect(Collectors.toList());
        return tagService.fillTags(filtered);
    }

    @Override
    public List<GroupRule> queryByVariableId(Long id) {
        Variable variable = variableOpService.get(id);
        if (variable == null) {
            return Collections.emptyList();
        }
        List<GroupRule> groupRules = groupRuleRepository.findAllByScriptContains(variable.getName());
        List<GroupRule> filtered = groupRules.stream().filter(groupRule -> GroovyUtils.parse(groupRule.getScript()).contains(variable.getName())).collect(Collectors.toList());
        return tagService.fillTags(filtered);
    }

    @Override
    public List<GroupRule> queryWithAtomRulesByEventId(Long id) {
        Event event = eventOpService.get(id);
        if (event == null) {
            return Collections.emptyList();
        }
        String groupRules = event.getGroupRules();
        if (StringUtils.isEmpty(groupRules)) {
            return Collections.emptyList();
        }
        List<Long> groupIds = Stream.of(groupRules.split(SPLIT)).map(Long::parseLong).collect(Collectors.toList());
        List<GroupRule> groupRuleList = queryByIds(groupIds);

        Set<Long> atomRuleIds = groupRuleList.stream().map(groupRule -> {
            Set<Long> ids = new HashSet<>();
            if (StringUtils.isEmpty(groupRule.getAtomRules())) {
                return ids;
            }
            return Stream.of(groupRule.getAtomRules().split(SPLIT)).map(Long::parseLong).collect(Collectors.toSet());
        }).flatMap(Collection::stream).collect(Collectors.toSet());

        Map<String, AtomRule> atomRuleMap = atomRuleOpService.queryByIds(atomRuleIds).stream().collect(Collectors.toMap(
                atomRule -> atomRule.getId().toString(),
                Function.identity(),
                (o1, o2) -> o1
        ));

        for (GroupRule groupRule : groupRuleList) {
            groupRule.setAtomRuleList(new ArrayList<>());
            for (String atomRuleId : groupRule.getAtomRules().split(SPLIT)) {
                AtomRule atomRule = atomRuleMap.get(atomRuleId);
                if (atomRule != null) {
                    groupRule.getAtomRuleList().add(atomRule);
                }
            }
        }
        return groupRuleList;
    }

    @Override
    public List<GroupRule> queryByAtomRuleId(Long id) {
        List<GroupRule> groupRules = groupRuleRepository.findByAtomRuleId(id);
        return tagService.fillTags(groupRules);
    }

    @Override
    public List<GroupRule> queryByAccumulateIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<GroupRule> groupRules = new ArrayList<>();
        for (Long id : ids) {
            groupRules.addAll(queryByAccumulateId(id));
        }
        return groupRules;
    }

    @Override
    public List<GroupRule> queryByAtomRuleIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<GroupRule> groupRules = new ArrayList<>();
        for (Long id : ids) {
            groupRules.addAll(queryByAtomRuleId(id));
        }
        return groupRules;
    }

    @Override
    public List<GroupRule> queryByVariableIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<GroupRule> groupRules = new ArrayList<>();
        for (Long id : ids) {
            groupRules.addAll(queryByVariableId(id));
        }
        return groupRules;
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        List<Event> events = eventOpService.queryByGroupRuleIds(ids);
        if (CollectionUtils.isNotEmpty(events)) {
            throw new RiskException(ErrorMessage.RELY_ON_EVENT);
        }
        super.deleteByIds(ids);
    }
}


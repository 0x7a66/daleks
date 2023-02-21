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
import top.daleks.risk.common.model.Business;
import top.daleks.risk.common.model.Event;
import top.daleks.risk.common.model.GroupRule;
import top.daleks.risk.dal.repository.EventRepository;
import top.daleks.risk.web.AbstractBaseEntityService;
import top.daleks.risk.web.service.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static top.daleks.risk.common.Constants.SPLIT;

@Service
public class EventOpServiceImpl extends AbstractBaseEntityService<Event> implements EventOpService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    BusinessOpService businessOpService;

    @Autowired
    GroupRuleOpService groupRuleOpService;

    @Autowired
    AtomRuleOpService atomRuleOpService;

    @Autowired
    AccumulateOpService accumulateOpService;

    @Autowired
    TagService tagService;

    @Override
    public List<Event> queryByBusinessId(Long id) {
        Business business = businessOpService.get(id);
        if (business == null) {
            return Collections.emptyList();
        }
        List<Event> events = eventRepository.findByBusinessCode(business.getCode());
        return tagService.fillTags(events);
    }

    @Override
    public List<Event> queryByGroupRuleId(Long id) {
        GroupRule groupRule = groupRuleOpService.get(id);
        if (groupRule == null) {
            return Collections.emptyList();
        }
        List<Event> events = eventRepository.findByGroupRuleId(groupRule.getId());
        return tagService.fillTags(events);
    }

    @Override
    public List<Event> queryByAtomRuleId(Long id) {
        List<GroupRule> groupRules = groupRuleOpService.queryByAtomRuleId(id);
        if (CollectionUtils.isEmpty(groupRules)) {
            return Collections.emptyList();
        }
        Set<Event> result = new HashSet<>();
        for (GroupRule groupRule : groupRules) {
            List<Event> events = queryByGroupRuleId(groupRule.getId());
            result.addAll(events);
        }
        return tagService.fillTags(new ArrayList<>(result));
    }

    @Override
    public List<Event> queryByAccumulateId(Long id) {
        Accumulate accumulate = accumulateOpService.get(id);
        if (accumulate == null) {
            return Collections.emptyList();
        }
        String events = accumulate.getEvents();
        if (StringUtils.isEmpty(events)) {
            return Collections.emptyList();
        }
        List<Long> ids = Stream.of(events.split(SPLIT)).map(Long::parseLong).collect(Collectors.toList());
        return queryByIds(ids);
    }

    @Override
    public List<Event> queryByBusinessIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<Event> events = new ArrayList<>();
        for (Long id : ids) {
            events.addAll(queryByBusinessId(id));
        }
        return events;
    }

    @Override
    public List<Event> queryByGroupRuleIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<Event> events = new ArrayList<>();
        for (Long id : ids) {
            events.addAll(queryByGroupRuleId(id));
        }
        return events;
    }
}

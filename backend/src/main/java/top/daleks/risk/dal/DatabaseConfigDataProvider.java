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

package top.daleks.risk.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.daleks.risk.common.ConfigDataChangeListener;
import top.daleks.risk.common.ConfigDataProvider;
import top.daleks.risk.common.enums.RuleState;
import top.daleks.risk.common.model.*;
import top.daleks.risk.dal.repository.*;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseConfigDataProvider implements ConfigDataProvider {

    private final List<ConfigDataChangeListener> listeners = new ArrayList<>();
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private GroupRuleRepository groupRuleRepository;
    @Autowired
    private AtomRuleRepository atomRuleRepository;
    @Autowired
    private VariableRepository variableRepository;
    @Autowired
    private AccumulateRepository accumulateRepository;
    @Autowired
    private ParameterMappingRepository parameterMappingRepository;

    @Override
    public List<Event> getAllEvent() {
        return eventRepository.findAll();
    }

    @Override
    public List<GroupRule> getAllGroupRule() {
        return groupRuleRepository.findAllByStateNot(RuleState.DISABLE);
    }

    @Override
    public List<AtomRule> getAllAtomRule() {
        return atomRuleRepository.findAllByStateNot(RuleState.DISABLE);
    }

    @Override
    public List<Variable> getAllVariable() {
        return variableRepository.findAll();
    }

    @Override
    public List<Accumulate> getAllAccumulate() {
        return accumulateRepository.findAll();
    }

    @Override
    public List<ParameterMapping> getAllParameterMapping() {
        return parameterMappingRepository.findAll();
    }

    @Override
    public void addChangeListener(ConfigDataChangeListener listener) {
        listeners.add(listener);
    }

    public void onMessage(String message) {
        for (ConfigDataChangeListener listener : listeners) {
            listener.onChange();
        }
    }
}

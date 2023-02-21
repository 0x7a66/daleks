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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.daleks.risk.common.model.Business;
import top.daleks.risk.common.model.Event;
import top.daleks.risk.dal.repository.BusinessRepository;
import top.daleks.risk.web.AbstractBaseEntityService;
import top.daleks.risk.web.service.BusinessOpService;
import top.daleks.risk.web.service.EventOpService;
import top.daleks.risk.web.support.ErrorMessage;
import top.daleks.risk.web.support.RiskException;

import java.util.List;

@Service
public class BusinessOpServiceImpl extends AbstractBaseEntityService<Business> implements BusinessOpService {

    @Autowired
    EventOpService eventOpService;

    @Autowired
    BusinessRepository businessRepository;

    @Override
    public List<Business> all() {
        return businessRepository.findAll();
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        List<Event> events = eventOpService.queryByBusinessIds(ids);
        if (CollectionUtils.isNotEmpty(events)) {
            throw new RiskException(ErrorMessage.RELY_ON_EVENT);
        }

        super.deleteByIds(ids);
    }
}

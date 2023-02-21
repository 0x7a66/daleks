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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.daleks.risk.common.model.Event;
import top.daleks.risk.common.model.GrayGroup;
import top.daleks.risk.web.AbstractBaseEntityService;
import top.daleks.risk.web.service.EventOpService;
import top.daleks.risk.web.service.GrayGroupOpService;
import top.daleks.risk.web.service.GrayListOpService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static top.daleks.risk.common.Constants.SPLIT;

@Service
public class GrayGroupOpServiceImpl extends AbstractBaseEntityService<GrayGroup> implements GrayGroupOpService {

    @Autowired
    GrayListOpService grayListOpService;

    @Autowired
    EventOpService eventOpService;

    @Override
    public void delete(Long id) {
        super.delete(id);
        grayListOpService.deleteByGroupId(id);
    }

    @Override
    public List<GrayGroup> queryByEventId(Long id) {
        Event event = eventOpService.get(id);
        if (event == null) {
            return Collections.emptyList();
        }
        String grayGroups = event.getGrayGroups();
        if (StringUtils.isEmpty(grayGroups)) {
            return Collections.emptyList();
        }
        List<Long> ids = Stream.of(grayGroups.split(SPLIT)).map(Long::parseLong).collect(Collectors.toList());
        return queryByIds(ids);
    }
}

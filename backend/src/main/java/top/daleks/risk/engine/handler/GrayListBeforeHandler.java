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

package top.daleks.risk.engine.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.daleks.risk.common.DependencyContext;
import top.daleks.risk.common.RiskBeforeHandler;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.common.enums.Dimension;
import top.daleks.risk.common.enums.GrayListType;
import top.daleks.risk.common.model.Event;
import top.daleks.risk.common.model.GrayList;
import top.daleks.risk.engine.graylist.GrayListHitRequest;
import top.daleks.risk.engine.graylist.GrayListService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static top.daleks.risk.common.Constants.SPLIT;

/**
 * 灰名单检查
 */
@Slf4j
@Component
public class GrayListBeforeHandler implements RiskBeforeHandler {

    @Autowired
    GrayListService grayListService;

    @Autowired
    DependencyContext dependencyContext;

    @Override
    public void handle(RiskContext riskContext) {
        long start = System.currentTimeMillis();
        Event event = dependencyContext.getEvent(riskContext.getEvent());
        if (event == null || StringUtils.isBlank(event.getGrayGroups())) {
            return;
        }
        Set<Long> groupIds = Stream.of(event.getGrayGroups().split(SPLIT)).map(Long::parseLong).collect(Collectors.toSet());

        List<GrayListHitRequest> requests = new ArrayList<>();

        // 白名单
        requests.add(new GrayListHitRequest(GrayListType.WHITE, Dimension.USER_ID, riskContext.getRiskAction().getUserId()));
        requests.add(new GrayListHitRequest(GrayListType.WHITE, Dimension.MOBILE, riskContext.getRiskAction().getMobile()));
        requests.add(new GrayListHitRequest(GrayListType.WHITE, Dimension.DEVICE_ID, riskContext.getRiskAction().getDeviceId()));
        requests.add(new GrayListHitRequest(GrayListType.WHITE, Dimension.CLIENT_IP, riskContext.getRiskAction().getClientIp()));

        // 黑名单
        requests.add(new GrayListHitRequest(GrayListType.BLACK, Dimension.USER_ID, riskContext.getRiskAction().getUserId()));
        requests.add(new GrayListHitRequest(GrayListType.BLACK, Dimension.MOBILE, riskContext.getRiskAction().getMobile()));
        requests.add(new GrayListHitRequest(GrayListType.BLACK, Dimension.DEVICE_ID, riskContext.getRiskAction().getDeviceId()));
        requests.add(new GrayListHitRequest(GrayListType.BLACK, Dimension.CLIENT_IP, riskContext.getRiskAction().getClientIp()));

        List<GrayList> hits = grayListService.hits(groupIds, requests);

        if (CollectionUtils.isNotEmpty(hits)) {
            riskContext.hitGrayList(hits.get(0));
        }

        log.info("GrayListBeforeHandler 执行结束，耗时：{}ms", System.currentTimeMillis() - start);
    }
}

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
import org.springframework.stereotype.Component;
import top.daleks.risk.common.RiskBeforeHandler;
import top.daleks.risk.common.RiskContext;
import top.daleks.risk.utils.ip2region.IpDetail;
import top.daleks.risk.utils.ip2region.IpSearchService;

import static top.daleks.risk.common.Constants.KEY_CLIENT_IP_DETAIL;

/**
 * ip地址转换
 */
@Slf4j
@Component
public class ClientIpBeforeHandler implements RiskBeforeHandler {

    @Override
    public void handle(RiskContext riskContext) {
        IpDetail ipDetail = IpSearchService.getIpDetail(riskContext.getRiskAction().getClientIp());
        if (ipDetail != null) {
            riskContext.getRiskAction().put(KEY_CLIENT_IP_DETAIL, ipDetail);
        }
    }
}

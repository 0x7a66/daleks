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

package top.daleks.risk.utils.ip2region;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class IpSearchService {

    private static final DbSearcher dbSearcher;

    static {
        try {
            dbSearcher = new DbSearcher();
            Runtime.getRuntime().addShutdownHook(new Thread(dbSearcher::close));
        } catch (Exception e) {
            log.error("ip2region.db失败读取", e);
            throw new RuntimeException("ip2region.db读取失败", e);
        }
    }

    public static IpDetail getIpDetail(String ip) {
        try {
            return (StringUtils.isNotBlank(ip) && Util.isIpAddress(ip)) ? dbSearcher.memorySearch(ip) : null;
        } catch (Exception e) {
            log.error("解析IP信息失败: ip=" + ip, e);
        }
        return null;
    }

}

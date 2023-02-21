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

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class IpDetail {
    private int cityId;
    private String country;
    private String region;
    private String province;
    private String city;
    private String isp;

    public IpDetail() {
    }

    public IpDetail(int cityId, String region) {
        this.cityId = cityId;
        if (StringUtils.isNotBlank(region)) {
            String[] items = region.split("\\|");
            this.country = items[0];
            this.region = items[1];
            this.province = items[2];
            this.city = items[3];
            this.isp = items[4];
        }
    }
}

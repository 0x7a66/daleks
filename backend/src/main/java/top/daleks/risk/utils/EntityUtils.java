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

package top.daleks.risk.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import top.daleks.risk.common.model.BaseEntity;

import java.util.Map;

@Slf4j
public class EntityUtils {
    /**
     * String property convert null to empty
     */
    public static void safeStringProperty(Object object) {
        try {
            Map<String, Object> describe = PropertyUtils.describe(object);
            for (Map.Entry<String, Object> entry : describe.entrySet()) {
                if (entry.getValue() != null) {
                    continue;
                }
                if (String.class.equals(PropertyUtils.getPropertyType(object, entry.getKey()))) {
                    PropertyUtils.setProperty(object, entry.getKey(), "");
                }
            }
        } catch (Exception e) {
            log.warn("safeStringProperty error", e);
        }
    }

    /**
     * 复制非null空属性
     */
    public static <T extends BaseEntity> void copyNotNullProperty(T desc, T origin) {
        try {
            Map<String, Object> describe = PropertyUtils.describe(origin);
            describe.remove("id");
            describe.remove("class");

            for (Map.Entry<String, Object> entry : describe.entrySet()) {
                if (entry.getValue() != null) {
                    PropertyUtils.setProperty(desc, entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            log.warn("copyNotNullProperty error", e);
        }
    }
}

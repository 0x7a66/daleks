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

package top.daleks.risk.access.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 批量请求
 */
public class BatchRequest extends RiskAction {

    private final static String DEFAULT_EVENT = "";

    private final Map<String, RiskAction> actionMap = new HashMap<>();

    public BatchRequest() {
        super(DEFAULT_EVENT);
    }

    public BatchRequest(String event) {
        super(event);
    }

    public static BatchRequest create() {
        return new BatchRequest(DEFAULT_EVENT);
    }

    public BatchRequest addAction(String alias, RiskAction action) {
        if (alias == null || alias.length() <= 0 || action == null) {
            throw new RuntimeException(RiskError.PARAM_IS_NULL.toString());
        }
        actionMap.put(alias, action);
        return this;
    }

    public Map<String, RiskAction> getActions() {
        return actionMap;
    }

}

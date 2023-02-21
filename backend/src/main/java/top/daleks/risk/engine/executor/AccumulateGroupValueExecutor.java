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

package top.daleks.risk.engine.executor;

import top.daleks.risk.engine.GroovyExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("rawtypes")
public class AccumulateGroupValueExecutor extends GroovyExecutor<List> {
    public AccumulateGroupValueExecutor(String name, String script) {
        super(name, script);
    }

    @Override
    protected List emptyValue() {
        return new ArrayList();
    }

    @Override
    public List convert(Object result) {
        if (result == null) {
            return new ArrayList();
        }
        if (result instanceof List) {
            return (List) result;
        }
        if (result instanceof String) {
            return Arrays.asList(result);
        }
        return new ArrayList();
    }
}

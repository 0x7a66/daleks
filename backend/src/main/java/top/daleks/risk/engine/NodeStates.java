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

package top.daleks.risk.engine;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 节点执行状态数据
 */
@Slf4j
public class NodeStates {

    private final Map<String, NodeState> states = new ConcurrentHashMap<>();

    public NodeState get(String name) {
        if (!this.states.containsKey(name)) {
            this.states.putIfAbsent(name, new NodeState());
        }
        return this.states.get(name);
    }

    public static class NodeState {

        private static final Integer INIT = 0;
        private static final Integer EXECUTING = 1;
        private static final Integer FINISHED = 2;

        private final AtomicInteger state;

        public NodeState() {
            this.state = new AtomicInteger(INIT);
        }

        public boolean setExecuting() {
            return this.state.compareAndSet(INIT, EXECUTING);
        }

        public boolean setFinished() {
            return this.state.compareAndSet(EXECUTING, FINISHED);
        }

        public boolean isFinished() {
            return this.state.get() == FINISHED;
        }
    }

}

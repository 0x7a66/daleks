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

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Slf4j
public abstract class GroovyExecutor<T> implements ScriptExecutor<T> {

    private final static GroovyClassLoader GROOVY_LOADER = new GroovyClassLoader();

    private final static int INIT = 0;  // groovy 未解析完成
    private final static int READY = 1; // groovy 解析完成

    private int state = INIT;
    private final String name; // 脚本标识，用于日志排查
    private final String script;
    private Script groovy;

    public GroovyExecutor(String name, String script) {
        this.name = name;
        this.script = script;
        if (StringUtils.isNotBlank(script)) {
            try {
                this.groovy = (Script) GROOVY_LOADER.parseClass(script).newInstance();
                this.state = READY;
            } catch (Exception e) {
                log.error("groovy script 解析失败, {} -> script: {}", this.name, this.script, e);
            }
        }
    }

    @Override
    public T execute(Map<String, Object> context) {
        if (StringUtils.isBlank(this.script)) {
            return emptyValue();
        }
        return convert(doExecute(context));
    }

    protected Object doExecute(Map<String, Object> context) {
        if (this.state != READY) {
            return null;
        }
        try {
            Binding binding = new Binding(context);
            this.groovy.setBinding(binding);
            Object result = this.groovy.run();
            log.debug("groovy执行, name: {}, script: {}, context: {}, result: {}", this.name, this.script, context, result);
            return result;
        } catch (MissingPropertyException e) {
            log.error("groovy执行缺少参数: {}, name: {}, script: {}, context: {}", e.getProperty(), this.name, this.script, context);
        } catch (Exception e) {
            log.error("groovy执行失败, name: {}, script: {}, context: {}", this.name, this.script, context, e);
        }
        return null;
    }

    /**
     * script 为空时的默认值
     *
     * @return T
     */
    protected T emptyValue() {
        return convert(null);
    }

    /**
     * 结果转换
     *
     * @param result script 执行结果
     * @return T
     */
    public abstract T convert(Object result);

    protected boolean ifNullFalse(Object result) {
        if (result == null) {
            return false;
        }
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return false;
    }

    public static void clearCache() {
        GROOVY_LOADER.clearCache();
    }
}

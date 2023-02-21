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

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyClassLoader.ClassCollector;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * groovy 变量解析
 */
@Slf4j
public class GroovyUtils {

    public static String check(String script) {
        String msg = null;
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
        try {
            groovyClassLoader.parseClass(script);
        } catch (Exception e) {
            msg = e.toString();
        } finally {
            groovyClassLoader.clearCache();
        }
        return msg;
    }

    public static Set<String> parse(String script) {
        Set<String> result = new HashSet<>();
        try {
            String msg = check(script);
            if (msg != null) {
                log.warn("解析失败: {}", msg);
                return result;
            }

            CompilationUnit unit = new CompilationUnit(CompilerConfiguration.DEFAULT);

            SourceUnit su = unit.addSource("SourceCode" + System.nanoTime(), script);
            GroovyClassLoader.InnerLoader loader = new GroovyClassLoader.InnerLoader(new GroovyClassLoader());

            ClassCollector collector = new IClassCollector(loader, unit, su);
            unit.setClassgenCallback(collector);

            unit.compile(7);

            unit.getAST().getModules().forEach(t -> result.addAll(t.getStatementBlock().getVariableScope().getReferencedClassVariables().keySet()));

        } catch (Exception e) {
            // ignore
        }
        return result;
    }

    public static Object execute(String script, Map<String, Object> data) throws IllegalAccessException, InstantiationException {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
        try {
            Script groovy = (Script) groovyClassLoader.parseClass(script).newInstance();
            Binding binding = new Binding(data);
            groovy.setBinding(binding);
            return groovy.run();
        } catch (Exception e) {
            throw e;
        } finally {
            groovyClassLoader.clearCache();
        }
    }

    public static void main(String[] args) {
        String text = "def allThirdAccount = userThirdAccountDistinct24Hour + [thirdPayUser];\n" +
                "allThirdAccount.unique();\n" +
                "def function(a, b) {return a + b};\n" +
                "function(1, 2);\n" +
                "def abnormalAccount = allThirdAccount - nomalThreeAccountList.value;\n" +
                "return abnormalAccount.size() >= 3 && payChannelRechargeAmount24Hour >= 3000 && (userExpData.diamondLevel >= 31 || userExpData.userVipLevel >= 31 || userExpData.planetUserLevel >= 31)";

        text = "if(consumeAmount > 100) return true;\n" +
                "for(uid in toUsers) {\n" +
                "\tif(oneToOneAmount(UserId, uid) > 200) return true;\n" +
                "\tif(takeAmount(uid) > 100) return true;\n" +
                "}";

        text = "Double.value(payAmount)<128 && a==b";

        text = "[a:context.a, b:\"b\", c:user?.name]";

        text = "\"The ${numOfWonder} wonders of the world\"";


        text = "[a, user.name, c]";

        text = "-1";

        System.out.println(parse(text));
    }

    private static class IClassCollector extends ClassCollector {
        public IClassCollector(GroovyClassLoader.InnerLoader cl, CompilationUnit unit, SourceUnit su) {
            super(cl, unit, su);
        }
    }

}

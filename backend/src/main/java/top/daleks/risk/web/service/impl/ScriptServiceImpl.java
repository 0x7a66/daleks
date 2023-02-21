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

package top.daleks.risk.web.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.daleks.risk.common.model.Accumulate;
import top.daleks.risk.common.model.FunctionConfig;
import top.daleks.risk.common.model.Variable;
import top.daleks.risk.utils.JsonUtils;
import top.daleks.risk.web.Keyword;
import top.daleks.risk.web.service.AccumulateOpService;
import top.daleks.risk.web.service.FunctionConfigOpService;
import top.daleks.risk.web.service.ScriptService;
import top.daleks.risk.web.service.VariableOpService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ScriptServiceImpl implements ScriptService {
    @Autowired
    AccumulateOpService accumulateOpService;

    @Autowired
    VariableOpService variableOpService;

    @Autowired
    FunctionConfigOpService functionConfigOpService;

    @Override
    public List<Keyword> groovy() {
        List<Keyword> keywords = new ArrayList<>();

        List<Accumulate> accumulates = accumulateOpService.all();
        for (Accumulate accumulate : accumulates) {
            keywords.add(Keyword.create(accumulate.getName(), accumulate.getRemark()));
        }

        Map<String, FunctionConfig> functionConfigMap = functionConfigOpService.allMap();
        List<Variable> variables = variableOpService.all();
        for (Variable variable : variables) {
            Keyword var = Keyword.create(variable.getName(), variable.getRemark());
            if (StringUtils.isNotEmpty(functionConfigMap.get(variable.getFunc()).getResult())) {
                try {
                    List<Keyword> list = JsonUtils.list(functionConfigMap.get(variable.getFunc()).getResult(), Keyword.class);
                    var.addProperties(list);
                } catch (Exception e) {
                    // ignore
                }
            }
            keywords.add(var);
        }


        keywords.add(Keyword.create("PASS", "通过"));
        keywords.add(Keyword.create("REVIEW", "审核"));
        keywords.add(Keyword.create("REJECT", "拒绝"));


        // 系统关键词
        String[] keys = new String[]{
                // groovy 关键词
                "as", "catch", "def", "enum", "for", "import", "new", "super", "throws", "while",
                "assert", "class", "default", "extends", "goto", "in", "null", "switch", "trait", "break",
                "const", "do", "false", "if", "instanceof", "package", "this", "true", "case", "continue", "else",
                "finally", "implements", "interface", "return", "throw", "try",
                // java 关键词
                "abstract", "transient", "int", "synchronized", "boolean", "char", "do",
                "final", "private", "short", "void", "double", "long", "protected", "static", "volatile",
                "byte", "float", "native", "public"
        };
        for (String key : keys) {
            keywords.add(Keyword.create(key));
        }


        keywords.add(Keyword.create("System").addProperty(Keyword.create("currentTimeMillis()")));
        keywords.add(Keyword.create("Integer").addProperty(Keyword.create("parseInt")));
        keywords.add(Keyword.create("Long").addProperty(Keyword.create("parseLong")));
        keywords.add(Keyword.create("Float").addProperty(Keyword.create("parseFloat")));
        keywords.add(Keyword.create("Double").addProperty(Keyword.create("parseDouble")));
        keywords.add(Keyword.create("Boolean").addProperty(Keyword.create("parseBoolean")));
        keywords.add(Keyword.create("String")
                .addProperty(Keyword.create("length"))
                .addProperty(Keyword.create("startsWith"))
                .addProperty(Keyword.create("indexOf"))
                .addProperty(Keyword.create("lastIndexOf"))
                .addProperty(Keyword.create("substring"))
                .addProperty(Keyword.create("contains"))
                .addProperty(Keyword.create("split"))
                .addProperty(Keyword.create("join"))
                .addProperty(Keyword.create("toLowerCase"))
                .addProperty(Keyword.create("toUpperCase"))
                .addProperty(Keyword.create("trim"))
                .addProperty(Keyword.create("valueOf"))
        );
        keywords.add(Keyword.create("Arrays").addProperty(Keyword.create("asList")));

        return keywords;
    }

    @Override
    public List<Keyword> functionConfigKeywords() {
        return Arrays.asList(
                Keyword.create("text", "关键字"),
                Keyword.create("displayText", "备注"),
                Keyword.create("properties", "属性关键字列表")
        );
    }

    @Override
    public List<Keyword> searchKeywords(String entityName) {

        return null;
    }
}

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

package top.daleks.risk.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.daleks.risk.utils.GroovyUtils;
import top.daleks.risk.web.service.ScriptService;
import top.daleks.risk.web.support.Response;

@Slf4j
@RestController
@RequestMapping("/api/script")
public class ScriptController {

    @Autowired
    ScriptService scriptService;

    @RequestMapping("/check")
    public Response check(@RequestBody String script) {
        return Response.success(GroovyUtils.check(script));
    }

    @RequestMapping("/parse")
    public Response parse(@RequestBody String script) {
        return Response.success(GroovyUtils.parse(script));
    }

    @RequestMapping("/keywords/groovy")
    public Response groovy() {
        return Response.success(scriptService.groovy());
    }

    @RequestMapping("/keywords/functionConfig")
    public Response functionConfig() {
        return Response.success(scriptService.functionConfigKeywords());
    }
}

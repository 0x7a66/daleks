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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.daleks.risk.access.RiskService;
import top.daleks.risk.access.bean.RiskAction;
import top.daleks.risk.common.enums.Tag;
import top.daleks.risk.utils.GroovyUtils;
import top.daleks.risk.utils.JsonUtils;
import top.daleks.risk.web.request.EventTestRequest;
import top.daleks.risk.web.request.RiskLogSearchRequest;
import top.daleks.risk.web.request.ScriptTestRequest;
import top.daleks.risk.web.service.RiskLogOpService;
import top.daleks.risk.web.support.ErrorMessage;
import top.daleks.risk.web.support.Response;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    RiskLogOpService riskLogOpService;

    @Autowired
    RiskService riskService;

    @RequestMapping("/riskLog/search")
    public Response riskLogSearch(@Validated @RequestBody RiskLogSearchRequest request) {
        return Response.success(riskLogOpService.search(request));
    }

    @RequestMapping("/test/event")
    public Response testEvent(@Validated @RequestBody EventTestRequest eventTestRequest) {
        Map<String, Object> data = eventTestRequest.getData();
        if (data == null || data.get(RiskAction.KEY_EVENT) == null) {
            return Response.error(ErrorMessage.FORM_ERROR);
        }
        RiskAction riskAction = new RiskAction(data.get(RiskAction.KEY_EVENT).toString());
        riskAction.getData().putAll(data);
        riskAction.setAsync(false);
        if (Boolean.TRUE.equals(eventTestRequest.getTagTest())) {
            riskAction.addTag(Tag.TEST.name());
        }
        riskAction.getData().put(RiskAction.KEY_REQUEST_TIME, System.currentTimeMillis());
        return Response.success(riskService.detect(riskAction));
    }

    @RequestMapping("/test/script")
    public Response testScript(@RequestBody ScriptTestRequest request) {
        String msg = GroovyUtils.check(request.getScript());
        if (msg != null) {
            return Response.success(msg);
        }
        Map<String, Object> data;
        try {
            data = JsonUtils.map(request.getData(), Object.class);
        } catch (Exception e) {
            return Response.success("参数有误");
        }
        try {
            return Response.success(GroovyUtils.execute(request.getScript(), data));
        } catch (Exception e) {
            return Response.success(e.getMessage());
        }

    }
}

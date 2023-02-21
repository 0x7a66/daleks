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
import top.daleks.risk.common.model.Variable;
import top.daleks.risk.web.request.IdRequest;
import top.daleks.risk.web.request.QueryValueRequest;
import top.daleks.risk.web.service.VariableOpService;
import top.daleks.risk.web.support.Response;

@Slf4j
@RestController
@RequestMapping("/api/variable")
public class VariableController extends BaseController<Variable> {

    @Autowired
    VariableOpService variableOpService;

    @RequestMapping("/queryByFunctionConfigId")
    public Response queryByFunctionConfigId(@RequestBody IdRequest request) {
        return Response.success(variableOpService.queryByFunctionConfigId(request.getId()));
    }

    @RequestMapping("/queryByAccumulateId")
    public Response queryByAccumulateId(@RequestBody IdRequest request) {
        return Response.success(variableOpService.queryByAccumulateId(request.getId()));
    }

    @RequestMapping("/queryByGroupRuleId")
    public Response queryByGroupRuleId(@RequestBody IdRequest request) {
        return Response.success(variableOpService.queryByGroupRuleId(request.getId()));
    }

    @RequestMapping("/queryByAtomRuleId")
    public Response queryByAtomRuleId(@RequestBody IdRequest request) {
        return Response.success(variableOpService.queryByAtomRuleId(request.getId()));
    }

    @RequestMapping("/queryDependencyById")
    public Response queryDependencyById(@RequestBody IdRequest request) {
        return Response.success(variableOpService.queryDependencyById(request.getId()));
    }


    @RequestMapping("/queryValue")
    public Response queryValue(@RequestBody QueryValueRequest request) {
        return Response.success(variableOpService.queryValue(request));
    }
}

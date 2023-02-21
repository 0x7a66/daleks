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
import top.daleks.risk.common.model.FunctionConfig;
import top.daleks.risk.web.service.FunctionConfigOpService;
import top.daleks.risk.web.support.Response;

@Slf4j
@RestController
@RequestMapping("/api/function/config")
public class FunctionConfigController extends BaseController<FunctionConfig> {

    @Autowired
    FunctionConfigOpService functionConfigOpService;

    @RequestMapping("/getByFunc")
    Response getByFunc(@RequestBody String func) {
        return Response.success(functionConfigOpService.getByFunc(func));
    }
}

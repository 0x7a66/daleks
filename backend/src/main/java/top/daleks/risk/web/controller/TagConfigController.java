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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.daleks.risk.common.model.TagConfig;
import top.daleks.risk.web.service.TagConfigOpService;
import top.daleks.risk.web.support.Response;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagConfigController extends BaseController<TagConfig> {

    @Autowired
    TagConfigOpService tagConfigOpService;

    @RequestMapping("/all")
    public Response all(@RequestParam("configType") String configType) {
        List<TagConfig> tags = tagConfigOpService.all(configType);
        return Response.success(tags);
    }
}

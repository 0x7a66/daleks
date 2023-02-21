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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.daleks.risk.common.model.GrayList;
import top.daleks.risk.web.service.GrayListOpService;
import top.daleks.risk.web.support.Response;

@Slf4j
@RestController
@RequestMapping("/api/gray/list")
public class GrayListController extends BaseController<GrayList> {

    @Autowired
    GrayListOpService grayListOpService;

    @RequestMapping("/dimensions")
    public Response dimensions() {
        return Response.success(grayListOpService.dimensions());
    }

    @RequestMapping("/sync")
    public Response sync() {
        grayListOpService.sync();
        return Response.success(true);
    }
}

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
import top.daleks.risk.common.model.BaseEntity;
import top.daleks.risk.web.BaseEntityService;
import top.daleks.risk.web.request.IdRequest;
import top.daleks.risk.web.request.SearchRequest;
import top.daleks.risk.web.support.Response;

import java.util.List;

@Slf4j
public class BaseController<T extends BaseEntity> {

    @Autowired
    BaseEntityService<T> baseService;

    @RequestMapping("/search")
    public Response search(@RequestBody SearchRequest request) {
        return Response.success(baseService.search(request));
    }

    @RequestMapping("/get")
    public Response get(@RequestBody IdRequest request) {
        return Response.success(baseService.get(request.getId()));
    }

    @RequestMapping("/add")
    public Response add(@RequestBody T entity) {
        return Response.success(baseService.add(entity));
    }

    @RequestMapping("/update")
    public Response update(@RequestBody T entity) {
        return Response.success(baseService.update(entity));
    }

    @RequestMapping("/delete")
    public Response delete(@RequestBody List<Long> ids) {
        baseService.deleteByIds(ids);
        return Response.success();
    }

    @RequestMapping("/exist")
    public Response exist(@RequestBody T entity) {
        return Response.success(baseService.exist(entity));
    }

    @RequestMapping("/queryByIds")
    public Response queryByIds(@RequestBody List<Long> ids) {
        return Response.success(baseService.queryByIds(ids));
    }
}

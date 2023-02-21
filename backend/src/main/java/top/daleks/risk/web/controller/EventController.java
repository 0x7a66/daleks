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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.daleks.risk.common.model.Event;
import top.daleks.risk.web.request.IdRequest;
import top.daleks.risk.web.service.EventOpService;
import top.daleks.risk.web.support.Response;

import static top.daleks.risk.dal.RedisConfiguration.REDIS_CHANNEL_TOPIC;

@RestController
@RequestMapping("/api/event")
public class EventController extends BaseController<Event> {

    @Autowired
    EventOpService eventOpService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/build")
    public Response build() {
        stringRedisTemplate.convertAndSend(REDIS_CHANNEL_TOPIC.getTopic(), "build");
        return Response.success(true);
    }

    @RequestMapping("/queryByBusinessId")
    public Response queryByBusinessId(@Validated @RequestBody IdRequest request) {
        return Response.success(eventOpService.queryByBusinessId(request.getId()));
    }

    @RequestMapping("/queryByGroupRuleId")
    public Response queryByGroupRuleId(@Validated @RequestBody IdRequest request) {
        return Response.success(eventOpService.queryByGroupRuleId(request.getId()));
    }

    @RequestMapping("/queryByAtomRuleId")
    public Response queryByAtomRuleId(@Validated @RequestBody IdRequest request) {
        return Response.success(eventOpService.queryByAtomRuleId(request.getId()));
    }


    @RequestMapping("/queryByAccumulateId")
    public Response queryByAccumulateId(@Validated @RequestBody IdRequest request) {
        return Response.success(eventOpService.queryByAccumulateId(request.getId()));
    }
}

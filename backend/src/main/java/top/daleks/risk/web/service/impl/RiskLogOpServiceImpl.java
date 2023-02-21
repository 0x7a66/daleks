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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import top.daleks.risk.common.model.RiskLog;
import top.daleks.risk.web.request.RiskLogSearchRequest;
import top.daleks.risk.web.service.RiskLogOpService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class RiskLogOpServiceImpl implements RiskLogOpService {

    private final static int MAX_COUNT = 10000;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Page<RiskLog> search(RiskLogSearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        if (MapUtils.isNotEmpty(searchRequest.getFilters())) {
            Map<String, List<String>> filters = searchRequest.getFilters();
            for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                if (CollectionUtils.isEmpty(value)) {
                    continue;
                }
                criteriaList.add(Criteria.where(key).in(value));
            }
        }
        if (StringUtils.isNotEmpty(searchRequest.getValue())) {
            String[] strings = searchRequest.getValue().split("and");
            for (String string : strings) {
                if (string.contains(":")) {
                    String[] split = string.split(":");
                    if (split.length == 2) {
                        String key = split[0].trim();
                        String value = split[1].trim();
                        if (StringUtils.isNumeric(value)) {
                            criteriaList.add(new Criteria().orOperator(
                                    Criteria.where(key).is(NumberUtils.createNumber(value)),
                                    Criteria.where(key).regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE))
                            ));
                        } else {
                            criteriaList.add(Criteria.where(key).regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE)));
                        }
                    }
                }
                if (string.contains(">")) {
                    String[] split = string.split(">");
                    if (split.length == 2) {
                        String key = split[0].trim();
                        String value = split[1].trim();
                        if (StringUtils.isNumeric(value)) {
                            criteriaList.add(Criteria.where(key).gt(NumberUtils.createNumber(value)));
                        }
                    }
                }
                if (string.contains(">=")) {
                    String[] split = string.split(">=");
                    if (split.length == 2) {
                        String key = split[0].trim();
                        String value = split[1].trim();
                        if (StringUtils.isNumeric(value)) {
                            criteriaList.add(Criteria.where(key).gte(NumberUtils.createNumber(value)));
                        }
                    }
                }
                if (string.contains("<")) {
                    String[] split = string.split("<");
                    if (split.length == 2) {
                        String key = split[0].trim();
                        String value = split[1].trim();
                        if (StringUtils.isNumeric(value)) {
                            criteriaList.add(Criteria.where(key).lt(NumberUtils.createNumber(value)));
                        }
                    }
                }
                if (string.contains("<=")) {
                    String[] split = string.split("<=");
                    if (split.length == 2) {
                        String key = split[0].trim();
                        String value = split[1].trim();
                        if (StringUtils.isNumeric(value)) {
                            criteriaList.add(Criteria.where(key).lte(NumberUtils.createNumber(value)));
                        }
                    }
                }
            }
        }
        if (searchRequest.getStartTime() != null) {
            criteriaList.add(Criteria.where("action.RequestTime").gte(searchRequest.getStartTime()));
        }
        if (searchRequest.getEndTime() != null) {
            criteriaList.add(Criteria.where("action.RequestTime").lt(searchRequest.getEndTime()));
        }
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long count = mongoTemplate.count(query, RiskLog.class);

        query.with(pageable).with(Sort.by(Sort.Direction.DESC, "action.RequestTime"));
        List<RiskLog> list = mongoTemplate.find(query, RiskLog.class);
        return PageableExecutionUtils.getPage(list, pageable, () -> count > MAX_COUNT ? MAX_COUNT : count);
    }
}

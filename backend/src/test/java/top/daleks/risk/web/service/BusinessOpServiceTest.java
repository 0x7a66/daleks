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

package top.daleks.risk.web.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import top.daleks.risk.common.model.Business;
import top.daleks.risk.dal.repository.BusinessRepository;
import top.daleks.risk.utils.JsonUtils;
import top.daleks.risk.web.request.SearchRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BusinessOpServiceTest {

    @Autowired
    BusinessOpService service;

    @Autowired
    BusinessRepository businessRepository;

    @Test
    public void get() {
        Business business = service.get(1L);
        System.out.println(JsonUtils.string(business));
    }

    @Test
    public void search() {
        SearchRequest searchRequest = new SearchRequest();

        searchRequest.setField("id");
        searchRequest.setOrder("ascend");
        searchRequest.setValue("1");

        Map<String, List<String>> filters = new HashMap<>();
        filters.put("name", Arrays.asList("name", "name2", "name3", "name4"));
        filters.put("remark", Arrays.asList("remark"));
        searchRequest.setFilters(filters);
        Page<Business> page = service.search(searchRequest);
        System.out.println(JsonUtils.string(page));
    }
}
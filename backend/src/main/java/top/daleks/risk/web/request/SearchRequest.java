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

package top.daleks.risk.web.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SearchRequest implements Serializable {

    private final static String ASC = "ascend";
    public final static String TAG_KEY = "tags";

    private int page = 0;
    private int size = 10;
    private String field = "id";
    private String order = "desc";
    private Map<String, List<String>> filters;
    private String value;

    public PageRequest buildPageRequest() {
        return PageRequest.of(page, size, Sort.by(getDirection(), field));
    }

    private Sort.Direction getDirection() {
        if (ASC.equalsIgnoreCase(order)) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
    }
}

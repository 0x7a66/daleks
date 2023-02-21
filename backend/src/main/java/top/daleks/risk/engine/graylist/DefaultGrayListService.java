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

package top.daleks.risk.engine.graylist;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.daleks.risk.common.enums.Dimension;
import top.daleks.risk.common.enums.GrayListType;
import top.daleks.risk.common.model.GrayList;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefaultGrayListService implements GrayListService {

    @SuppressWarnings("rawtypes")
    @Autowired
    protected RedisTemplate redisTemplate;

    @Override
    public GrayList hit(Long groupId, GrayListType type, Dimension dimension, String value) {
        return hit(groupId, new GrayListHitRequest(type, dimension, value));
    }

    @Override
    public List<GrayList> hits(Collection<Long> groupIds, GrayListType type, Dimension dimension, String value) {
        return hits(groupIds, new GrayListHitRequest(type, dimension, value));
    }

    @Override
    public GrayList hit(Long groupId, GrayListHitRequest request) {
        List<GrayList> hits = hits(Collections.singleton(groupId), request);
        return CollectionUtils.isNotEmpty(hits) ? hits.get(0) : null;
    }

    @Override
    public List<GrayList> hits(Long groupId, Collection<GrayListHitRequest> requests) {
        return hits(Collections.singleton(groupId), requests);
    }

    @Override
    public List<GrayList> hits(Collection<Long> groupIds, GrayListHitRequest request) {
        return hits(groupIds, Collections.singleton(request));
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public List<GrayList> hits(Collection<Long> groupIds, Collection<GrayListHitRequest> requests) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return null;
        }
        if (CollectionUtils.isEmpty(requests)) {
            return null;
        }

        List<String> keys = new ArrayList<>();
        for (Long groupId : groupIds) {
            for (GrayListHitRequest request : requests) {
                String key = build(groupId, request.getType(), request.getDimension(), request.getValue());
                if (key != null) {
                    keys.add(key);
                }
            }
        }
        List<GrayList> grayLists = redisTemplate.opsForValue().multiGet(keys);
        if (CollectionUtils.isEmpty(grayLists)) {
            return null;
        }
        Date now = Calendar.getInstance().getTime();
        return grayLists.stream().filter(list -> list != null &&
                (list.getExpireTime() == null || list.getExpireTime().after(now))
                && (list.getStartTime() == null || list.getStartTime().before(now))
        ).collect(Collectors.toList());
    }

    private String build(Long groupId, GrayListType type, Dimension dimension, String value) {
        if (groupId == null || type == null || dimension == null || value == null) {
            return null;
        }
        return String.format("%s_%s_%s_%s", groupId, type.name().toLowerCase(), dimension.name().toLowerCase(), value);
    }
}

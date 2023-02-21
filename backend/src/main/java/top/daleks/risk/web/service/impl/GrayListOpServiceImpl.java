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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import top.daleks.risk.common.enums.Dimension;
import top.daleks.risk.common.enums.GrayListType;
import top.daleks.risk.common.model.GrayGroup;
import top.daleks.risk.common.model.GrayList;
import top.daleks.risk.dal.repository.GrayListRepository;
import top.daleks.risk.web.AbstractBaseEntityService;
import top.daleks.risk.web.request.SearchRequest;
import top.daleks.risk.web.service.GrayGroupOpService;
import top.daleks.risk.web.service.GrayListOpService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GrayListOpServiceImpl extends AbstractBaseEntityService<GrayList> implements GrayListOpService {

    private final static String SYNC_KEY = "gray_list_sync_key";
    private final static int SYNC_BATCH_SIZE = 1000;
    private final static int SYNC_DEFAULT_TIMEOUT = 24 * 60 * 60 * 1000; // one day

    @Autowired
    GrayListRepository grayListRepository;

    @Autowired
    GrayGroupOpService grayGroupOpService;

    @Autowired
    RedisLockRegistry redisLockRegistry;

    @SuppressWarnings("rawtypes")
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public Page<GrayList> search(SearchRequest searchRequest) {
        Page<GrayList> page = super.search(searchRequest);
        List<Long> groupIds = page.getContent().stream().map(GrayList::getGroupId).collect(Collectors.toList());
        Map<Long, GrayGroup> grayGroupMap = grayGroupOpService.queryByIds(groupIds).stream().collect(Collectors.toMap(GrayGroup::getId, o -> o, (o1, o2) -> o1));
        return page.map(grayList -> {
            grayList.setGrayGroup(grayGroupMap.get(grayList.getGroupId()));
            return grayList;
        });
    }

    @Override
    public GrayList add(GrayList entity) {
        String[] values = entity.getValue().split("\n");
        GrayList record = new GrayList();
        for (String value : values) {
            try {
                record = new GrayList();
                BeanUtils.copyProperties(record, entity);
                record.setValue(value.trim());
                super.add(record);
            } catch (Exception e) {
                // ignore
            }
        }
        return record;
    }

    @Override
    public void deleteByGroupId(Long groupId) {
        grayListRepository.deleteAllByGroupId(groupId);
    }

    @Override
    public Object dimensions() {
        return Arrays.stream(Dimension.values()).map(Enum::name).collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0/10 * * * *")
    public void schedule() {
        sync();
    }


    @Override
    public void sync() {
        Lock lock = redisLockRegistry.obtain(SYNC_KEY);
        if (lock.tryLock()) {
            try {
                doSync();
            } finally {
                lock.unlock();
            }
        } else {
            log.info("未获取到锁，跳过名单同步");
        }
    }

    @SuppressWarnings({"unchecked"})
    protected void doSync() {
        Date now = new Date();
        int page = 0, total = 0;
        Long start = System.currentTimeMillis();
        Page<GrayList> grayLists;
        do {
            grayLists = grayListRepository.findAll(PageRequest.of(page++, SYNC_BATCH_SIZE));
            for (GrayList grayList : grayLists) {
                long expire = expire(grayList.getExpireTime(), now);
                if (expire > 0) {
                    String key = build(grayList.getGroupId(), grayList.getType(), grayList.getDimension(), grayList.getValue());
                    redisTemplate.opsForValue().set(key, grayList, expire, TimeUnit.MILLISECONDS);
                    total++;
                }
            }
        } while (grayLists.hasNext());
        log.info("已同步{}个名单到redis, 耗时: {}ms", total, System.currentTimeMillis() - start);
    }

    private long expire(Date expireTime, Date now) {
        if (expireTime == null) {
            return SYNC_DEFAULT_TIMEOUT;
        }
        return expireTime.getTime() - now.getTime();
    }

    private String build(Long groupId, GrayListType type, String dimension, String value) {
        if (groupId == null || type == null || dimension == null || value == null) {
            return null;
        }
        return String.format("%s_%s_%s_%s", groupId, type.name().toLowerCase(), dimension.toLowerCase(), value);
    }
}

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.daleks.risk.common.enums.ConfigType;
import top.daleks.risk.common.model.BaseEntity;
import top.daleks.risk.common.model.TagConfig;
import top.daleks.risk.common.model.TagRelation;
import top.daleks.risk.dal.repository.TagConfigRepository;
import top.daleks.risk.dal.repository.TagRelationRepository;
import top.daleks.risk.web.service.TagService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    TagConfigRepository tagConfigRepository;

    @Autowired
    TagRelationRepository tagRelationRepository;

    @Override
    public <T extends BaseEntity> Page<T> fillTags(Page<T> page) {
        if (page.isEmpty()) {
            return page;
        }
        List<Long> entityIds = page.getContent().stream().map(BaseEntity::getId).collect(Collectors.toList());
        Map<Long, List<TagConfig>> tagMap = findTagMapByConfigIds(page.getContent().get(0).getClass(), entityIds);
        return page.map(entity -> {
            entity.setTags(tagMap.getOrDefault(entity.getId(), Collections.emptyList()));
            return entity;
        });
    }

    @Override
    public <T extends BaseEntity> List<T> fillTags(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        List<Long> entityIds = list.stream().map(BaseEntity::getId).collect(Collectors.toList());
        Map<Long, List<TagConfig>> tagMap = findTagMapByConfigIds(list.get(0).getClass(), entityIds);
        for (T entity : list) {
            entity.setTags(tagMap.getOrDefault(entity.getId(), Collections.emptyList()));
        }
        return list;
    }

    @Override
    public <T extends BaseEntity> T fillTags(T record) {
        if (record == null) {
            return null;
        }
        Map<Long, List<TagConfig>> tagMap = findTagMapByConfigIds(record.getClass(), Collections.singletonList(record.getId()));
        record.setTags(tagMap.getOrDefault(record.getId(), Collections.emptyList()));
        return record;
    }

    @Override
    public <T extends BaseEntity> void saveTags(T record) {
        if (record == null || CollectionUtils.isEmpty(record.getTags())) {
            return;
        }
        ConfigType configType = ConfigType.classOf(record.getClass());
        if (configType == null) {
            return;
        }
        List<TagRelation> relations = new ArrayList<>();
        for (TagConfig tag : record.getTags()) {
            TagRelation tagRelation = new TagRelation();
            tagRelation.setTagId(tag.getId());
            tagRelation.setConfigId(record.getId());
            tagRelation.setConfigType(configType);
            relations.add(tagRelation);
        }
        tagRelationRepository.saveAll(relations);
    }

    @Override
    @Transactional
    public <T extends BaseEntity> void updateTags(T origin, T entity) {
        if (origin == null || entity == null) {
            return;
        }
        ConfigType configType = ConfigType.classOf(entity.getClass());
        if (configType == null) {
            return;
        }
        if (CollectionUtils.isEmpty(origin.getTags())) {
            this.saveTags(entity);
            return;
        }
        if (CollectionUtils.isEmpty(entity.getTags())) {
            this.deleteTags(origin);
            return;
        }
        Set<Long> originTags = origin.getTags().stream().map(TagConfig::getId).collect(Collectors.toSet());
        Set<Long> entityTags = entity.getTags().stream().map(TagConfig::getId).collect(Collectors.toSet());

        Collection<Long> toDelete = CollectionUtils.removeAll(originTags, entityTags);
        Collection<Long> toSave = CollectionUtils.removeAll(entityTags, originTags);

        tagRelationRepository.deleteAllByConfigTypeAndTagIdIn(configType, toDelete);

        List<TagRelation> relations = new ArrayList<>();
        for (long tagId : toSave) {
            TagRelation tagRelation = new TagRelation();
            tagRelation.setTagId(tagId);
            tagRelation.setConfigId(origin.getId());
            tagRelation.setConfigType(configType);
            relations.add(tagRelation);
        }
        tagRelationRepository.saveAll(relations);
    }

    @Override
    public <T extends BaseEntity> void deleteTags(T record) {
        if (record == null || CollectionUtils.isEmpty(record.getTags())) {
            return;
        }
        ConfigType configType = ConfigType.classOf(record.getClass());
        if (configType == null) {
            return;
        }
        tagRelationRepository.deleteAllByConfigTypeAndConfigId(configType, record.getId());
    }

    @Override
    public <T extends BaseEntity> List<Long> getConfigIdsByTags(Class<T> clazz, Collection<String> tagIds) {
        ConfigType configType = ConfigType.classOf(clazz);
        if (configType == null) {
            return Collections.emptyList();
        }
        List<Long> ids = tagIds.stream().map(id -> {
            try {
                return Long.parseLong(id);
            } catch (Exception e) {
                return 0L;
            }
        }).collect(Collectors.toList());
        return tagRelationRepository.findAllByConfigTypeAndTagIdIn(configType, ids)
                .stream()
                .map(TagRelation::getConfigId)
                .collect(Collectors.toList());
    }

    private Map<Long, List<TagConfig>> findTagMapByConfigIds(Class<?> clazz, List<Long> configIds) {
        ConfigType configType = ConfigType.classOf(clazz);
        if (configType == null) {
            return Collections.emptyMap();
        }
        List<TagRelation> tagRelations = tagRelationRepository.findAllByConfigTypeAndConfigIdIn(configType, configIds);

        if (CollectionUtils.isEmpty(tagRelations)) {
            return Collections.emptyMap();
        }

        List<Long> tagIds = tagRelations.stream().map(TagRelation::getTagId).collect(Collectors.toList());

        Map<Long, TagConfig> tagConfigMap = tagConfigRepository.findAllById(tagIds)
                .stream()
                .collect(Collectors.toMap(TagConfig::getId, o -> o, (o1, o2) -> o1));

        return tagRelations.stream().collect(Collectors.groupingBy(
                TagRelation::getConfigId,
                Collectors.mapping(tagRelation -> tagConfigMap.get(tagRelation.getTagId()), Collectors.toList())
        ));
    }
}

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
import org.springframework.stereotype.Service;
import top.daleks.risk.common.enums.ConfigType;
import top.daleks.risk.common.model.TagConfig;
import top.daleks.risk.common.model.TagRelation;
import top.daleks.risk.dal.repository.TagConfigRepository;
import top.daleks.risk.dal.repository.TagRelationRepository;
import top.daleks.risk.web.AbstractBaseEntityService;
import top.daleks.risk.web.service.TagConfigOpService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagConfigOpServiceImpl extends AbstractBaseEntityService<TagConfig> implements TagConfigOpService {

    @Autowired
    TagConfigRepository tagConfigRepository;

    @Autowired
    TagRelationRepository tagRelationRepository;

    @Override
    public List<TagConfig> all(String configType) {
        ConfigType type = ConfigType.nameOf(configType);
        if (type == null) {
            return tagConfigRepository.findAll();
        }
        List<TagRelation> tagRelations = tagRelationRepository.findAllByConfigType(type);
        if (CollectionUtils.isEmpty(tagRelations)) {
            return Collections.emptyList();
        }
        List<Long> tagIds = tagRelations.stream().map(TagRelation::getId).collect(Collectors.toList());
        return tagConfigRepository.findAllById(tagIds);
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
        tagRelationRepository.deleteAllByTagId(id);
    }
}

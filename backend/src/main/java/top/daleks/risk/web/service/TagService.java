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

import org.springframework.data.domain.Page;
import top.daleks.risk.common.model.BaseEntity;

import java.util.Collection;
import java.util.List;

public interface TagService {
    <T extends BaseEntity> Page<T> fillTags(Page<T> page);

    <T extends BaseEntity> List<T> fillTags(List<T> list);

    <T extends BaseEntity> T fillTags(T record);

    <T extends BaseEntity> void saveTags(T record);

    <T extends BaseEntity> void updateTags(T origin, T entity);

    <T extends BaseEntity> void deleteTags(T record);

    <T extends BaseEntity> List<Long> getConfigIdsByTags(Class<T> clazz, Collection<String> tagIds);
}

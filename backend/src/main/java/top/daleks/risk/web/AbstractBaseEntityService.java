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

package top.daleks.risk.web;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import top.daleks.risk.common.model.BaseEntity;
import top.daleks.risk.common.model.annotation.Searchable;
import top.daleks.risk.dal.repository.BaseRepository;
import top.daleks.risk.utils.EntityUtils;
import top.daleks.risk.web.request.SearchRequest;
import top.daleks.risk.web.service.TagService;
import top.daleks.risk.web.support.ErrorMessage;
import top.daleks.risk.web.support.RiskException;

import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.util.*;

abstract public class AbstractBaseEntityService<T extends BaseEntity> implements BaseEntityService<T> {

    @Autowired
    BaseRepository<T> baseRepository;

    @Autowired
    TagService tagService;

    @Override
    public Page<T> search(SearchRequest searchRequest) {
        Specification<T> specification = (Specification<T>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> ands = new ArrayList<>();
            if (MapUtils.isNotEmpty(searchRequest.getFilters())) {
                Map<String, List<String>> filters = searchRequest.getFilters();
                for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
                    String key = entry.getKey();
                    List<String> value = entry.getValue();
                    if (CollectionUtils.isEmpty(value)) {
                        continue;
                    }
                    if (SearchRequest.TAG_KEY.equalsIgnoreCase(key)) {
                        List<Long> ids = tagService.getConfigIdsByTags(root.getJavaType(), value);
                        if (CollectionUtils.isNotEmpty(ids)) {
                            ands.add(criteriaBuilder.and(root.get("id").in(ids)));
                        } else {
                            ands.add(criteriaBuilder.and(root.get("id").in(Collections.singleton(0L))));
                        }
                        continue;
                    }
                    ands.add(criteriaBuilder.and(root.get(key).as(String.class).in(value)));
                }
            }
            if (StringUtils.isNotBlank(searchRequest.getValue())) {
                List<Predicate> or = new ArrayList<>();
                for (Field field : FieldUtils.getFieldsListWithAnnotation(root.getJavaType(), Searchable.class)) {
                    or.add(criteriaBuilder.like(root.get(field.getName()).as(String.class), "%" + searchRequest.getValue() + "%"));
                }
                if (CollectionUtils.isNotEmpty(or)) {
                    ands.add(criteriaBuilder.and(criteriaBuilder.or(or.toArray(new Predicate[]{}))));
                }
            }
            criteriaQuery.where(ands.toArray(new Predicate[]{}));
            return criteriaQuery.getRestriction();
        };
        Page<T> page = baseRepository.findAll(specification, searchRequest.buildPageRequest());
        return tagService.fillTags(page);
    }

    @Override
    public List<T> queryByIds(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return tagService.fillTags(baseRepository.findAllById(ids));
    }

    @Override
    public T get(Long id) {
        return tagService.fillTags(baseRepository.findById(id).orElse(null));
    }

    @Override
    public boolean exist(T entity) {
        return baseRepository.exists(Example.of(entity));
    }

    @Override
    public T add(T entity) {
        if (entity == null) {
            throw new RiskException(ErrorMessage.FORM_ERROR);
        }
        EntityUtils.safeStringProperty(entity);
        T record = baseRepository.saveAndFlush(entity);
        tagService.saveTags(record);
        return record;
    }

    @Override
    public T update(T entity) {
        if (entity == null) {
            throw new RiskException(ErrorMessage.FORM_ERROR);
        }
        T origin = get(entity.getId());
        if (origin == null) {
            throw new RiskException(ErrorMessage.FORM_ERROR);
        }

        tagService.updateTags(origin, entity);
        EntityUtils.copyNotNullProperty(origin, entity);
        return baseRepository.saveAndFlush(origin);
    }

    @Override
    public void delete(Long id) {
        T record = get(id);
        if (record == null) {
            return;
        }
        baseRepository.deleteById(id);
        tagService.deleteTags(record);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            for (Long id : ids) {
                delete(id);
            }
        }
    }
}

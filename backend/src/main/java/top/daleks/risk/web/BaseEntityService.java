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

import org.springframework.data.domain.Page;
import top.daleks.risk.common.model.BaseEntity;
import top.daleks.risk.web.request.SearchRequest;

import java.util.Collection;
import java.util.List;

public interface BaseEntityService<T extends BaseEntity> {
    /**
     * 基础搜索
     *
     * @param searchRequest 基础搜索对象
     * @return 分页数据
     */
    Page<T> search(SearchRequest searchRequest);

    /**
     * id 列表查询实体
     *
     * @param ids 主键集合
     * @return 实体列表
     */
    List<T> queryByIds(Collection<Long> ids);

    /**
     * 通过主键获取实体
     *
     * @param id 主键id
     * @return 实体
     */
    T get(Long id);

    /**
     * 是否存在
     *
     * @param entity 实体参数
     * @return 是否存在
     */
    boolean exist(T entity);

    /**
     * 创建实体
     *
     * @param entity 实体
     * @return 保存后的对象
     */
    T add(T entity);

    /**
     * 更新实体
     *
     * @param entity 实体
     * @return 更新后的对象
     */
    T update(T entity);

    /**
     * 通过id删除实体
     *
     * @param id 主键
     */
    void delete(Long id);

    /**
     * 通过id列表删除实体
     *
     * @param ids 主键列表
     */
    void deleteByIds(List<Long> ids);
}

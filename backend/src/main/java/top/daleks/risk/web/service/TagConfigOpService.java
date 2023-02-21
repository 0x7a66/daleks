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

import top.daleks.risk.common.model.TagConfig;
import top.daleks.risk.web.BaseEntityService;

import java.util.List;

public interface TagConfigOpService extends BaseEntityService<TagConfig> {
    /**
     * 根据配置项类型获取对应的所有tags
     *
     * @param configType 配置项类型
     * @return Tag 列表
     */
    List<TagConfig> all(String configType);
}

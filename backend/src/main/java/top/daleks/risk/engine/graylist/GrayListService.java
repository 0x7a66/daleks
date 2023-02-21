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

import top.daleks.risk.common.enums.Dimension;
import top.daleks.risk.common.enums.GrayListType;
import top.daleks.risk.common.model.GrayList;

import java.util.Collection;
import java.util.List;

/**
 * 灰名单服务
 */
public interface GrayListService {

    /**
     * 命中查询
     *
     * @param groupId   名单组id
     * @param type      名单类型
     * @param dimension 纬度
     * @param value     纬度值
     * @return GrayList
     */
    GrayList hit(Long groupId, GrayListType type, Dimension dimension, String value);

    /**
     * 命中查询
     *
     * @param groupId 名单组id
     * @param request 名单请求对象
     * @return GrayList
     */
    GrayList hit(Long groupId, GrayListHitRequest request);

    /**
     * 命中查询
     *
     * @param groupIds  名单组id集合
     * @param type      名单类型
     * @param dimension 纬度
     * @param value     纬度值
     * @return 命中集合
     */
    List<GrayList> hits(Collection<Long> groupIds, GrayListType type, Dimension dimension, String value);

    /**
     * 命中查询
     *
     * @param groupId  名单组id
     * @param requests 名单请求对象集合
     * @return 命中集合
     */
    List<GrayList> hits(Long groupId, Collection<GrayListHitRequest> requests);

    /**
     * 命中查询
     *
     * @param groupIds 名单组id集合
     * @param request  名单请求对象
     * @return 命中集合
     */
    List<GrayList> hits(Collection<Long> groupIds, GrayListHitRequest request);

    /**
     * 命中查询
     *
     * @param groupIds 名单组id集合
     * @param requests 名单请求对象集合
     * @return 命中集合
     */
    List<GrayList> hits(Collection<Long> groupIds, Collection<GrayListHitRequest> requests);
}

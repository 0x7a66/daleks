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

package top.daleks.risk.common.model;

import lombok.Getter;
import lombok.Setter;
import top.daleks.risk.common.enums.GrayListType;
import top.daleks.risk.common.model.annotation.Searchable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "risk_gray_list")
public class GrayList extends BaseEntity implements Serializable {
    /**
     * 名单类型
     * WHITE: 白名单
     * BLACK: 黑名单
     */
    @Searchable(desc = "名单类型")
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private GrayListType type;

    /**
     * 纬度
     */
    @Searchable(desc = "纬度")
    @Column(name = "dimension")
    private String dimension;

    /**
     * 纬度值
     */
    @Searchable(desc = "纬度值")
    @Column(name = "_value")
    private String value;

    /**
     * 生效时间
     */
    @Column(name = "start_time")
    private Date startTime;

    /**
     * 失效时间
     */
    @Column(name = "expire_time")
    private Date expireTime;

    /**
     * 名单组
     */
    @Column(name = "group_id")
    private Long groupId;

    /**
     * 名单组
     */
    @Transient
    private GrayGroup grayGroup;
}

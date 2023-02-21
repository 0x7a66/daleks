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
import top.daleks.risk.common.enums.ConfigType;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "risk_tag_relation")
public class TagRelation extends BaseEntity implements Serializable {

    /**
     * 标签id
     */
    @Column(name = "tag_id")
    private Long tagId;

    /**
     * 配置项id
     */
    @Column(name = "config_id")
    private Long configId;

    /**
     * 配置项类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "config_type")
    private ConfigType configType;
}

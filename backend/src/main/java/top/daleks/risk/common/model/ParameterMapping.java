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
@Table(name = "risk_parameter_mapping")
public class ParameterMapping extends BaseEntity implements Serializable {

    /**
     * 事件
     */
    @Column(name = "event")
    private String event;

    /**
     * 配置项类型
     * VARIABLE=变量, ATOM_RULE=规则, GROUP_RULE=规则组, ACCUMULATE=累积因子, PUNISH=惩罚
     */
    @Column(name = "config")
    @Enumerated(EnumType.STRING)
    private ConfigType config;

    /**
     * 配置项id
     */
    @Column(name = "config_id")
    private String configId;

    /**
     * 原参数名称
     */
    @Column(name = "origin")
    private String origin;

    /**
     * 重写参数名称
     */
    @Column(name = "mapping")
    private String mapping;
}

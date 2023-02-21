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
import top.daleks.risk.common.model.annotation.Searchable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "risk_event")
public class Event extends BaseEntity implements Serializable {
    /**
     * 名称
     */
    @Searchable(desc = "名称")
    @Column(name = "name")
    private String name;
    /**
     * code
     */
    @Searchable(desc = "code")
    @Column(name = "code")
    private String code;
    /**
     * 业务code
     */
    @Searchable(desc = "业务code")
    @Column(name = "business_code")
    private String businessCode;
    /**
     * 关联的规则组
     */
    @Column(name = "group_rules")
    private String groupRules;
    /**
     * 关联的名单组
     */
    @Column(name = "gray_groups")
    private String grayGroups;
    /**
     * 惩罚话术
     */
    @Searchable(desc = "惩罚话术")
    @Column(name = "reply")
    private String reply;
    /**
     * 参数模型
     * json
     */
    @Column(name = "model")
    private String model;
    /**
     * 参数重命名规则
     * json
     */
    @Searchable(desc = "参数重命名规则")
    @Column(name = "rename_mapping")
    private String renameMapping;
    /**
     * 风控结果发送的topic
     */
    @Searchable(desc = "风控结果发送的topic")
    @Column(name = "topic")
    private String topic;
    /**
     * 关联的业务
     */
    @Transient
    private Business business;


}

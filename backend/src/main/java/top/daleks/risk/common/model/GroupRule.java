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
import top.daleks.risk.common.enums.RuleState;
import top.daleks.risk.common.model.annotation.Searchable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "risk_group_rule")
public class GroupRule extends BaseEntity implements Serializable {

    /**
     * 名称
     */
    @Searchable(desc = "名称")
    @Column(name = "name")
    private String name;

    /**
     * 前置条件
     * groovy 表达式
     */
    @Searchable(desc = "前置条件")
    @Column(name = "script")
    private String script;

    /**
     * 关联的规则
     */
    @Column(name = "atom_rules")
    private String atomRules;

    /**
     * 状态
     */
    @Searchable(desc = "状态")
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private RuleState state;

    /**
     * 惩罚话术
     */
    @Searchable(desc = "惩罚话术")
    @Column(name = "reply")
    private String reply;

    /**
     * 返回数据json
     */
    @Searchable(desc = "返回数据json")
    @Column(name = "return_json")
    private String returnJson;

    @Transient
    private List<AtomRule> atomRuleList;

}

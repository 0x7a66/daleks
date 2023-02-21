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
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "risk_variable")
public class Variable extends BaseEntity implements Serializable {

    /**
     * 名称
     */
    @Searchable(desc = "名称")
    @Column(name = "name")
    private String name;

    /**
     * 函数名称
     */
    @Searchable(desc = "函数名称")
    @Column(name = "func")
    private String func;

    /**
     * 函数参数
     * json
     */
    @Searchable(desc = "函数参数")
    @Column(name = "parameters")
    private String parameters;
}

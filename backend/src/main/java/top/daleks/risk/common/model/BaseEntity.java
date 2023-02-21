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
import top.daleks.risk.common.utils.OperatorHolder;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@MappedSuperclass
abstract public class BaseEntity {
    /**
     * 自动递增 id
     */
    @Searchable(desc = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 备注
     */
    @Searchable(desc = "备注")
    @Column(name = "remark")
    private String remark;

    /**
     * 拓展信息
     */
    @Searchable(desc = "拓展信息")
    @Column(name = "ext")
    private String ext;

    /**
     * 创建人
     */
    @Searchable(desc = "创建人")
    @Column(name = "author")
    private String author;

    /**
     * 更新人
     */
    @Searchable(desc = "更新人")
    @Column(name = "modifier")
    private String modifier;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 关联的标签
     */
    @Transient
    private List<TagConfig> tags;

    @PrePersist
    public void prePersist() {
        this.author = OperatorHolder.getOperator();
        this.modifier = OperatorHolder.getOperator();
        this.createTime = new Date();
        this.updateTime = new Date();
        if (this.ext == null) {
            this.ext = "";
        }
        if (this.remark == null) {
            this.remark = "";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.modifier = OperatorHolder.getOperator();
        this.updateTime = new Date();
    }
}

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
import top.daleks.risk.common.enums.WindowType;
import top.daleks.risk.common.enums.WindowUnit;
import top.daleks.risk.common.model.annotation.Searchable;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "risk_accumulate")
public class Accumulate extends BaseEntity implements Serializable {
    /**
     * 名称
     */
    @Searchable(desc = "名称")
    @Column(name = "name")
    private String name;

    /**
     * 分组key
     */
    @Searchable(desc = "分组key")
    @Column(name = "group_key")
    private String groupKey;

    /**
     * 聚合key
     */
    @Searchable(desc = "聚合key")
    @Column(name = "agg_key")
    private String aggKey;

    /**
     * 聚合函数
     */
    @Searchable(desc = "聚合函数")
    @Column(name = "agg_function")
    private String aggFunction;

    /**
     * 聚合窗口
     */
    @Column(name = "time_window")
    private Integer timeWindow;

    /**
     * 窗口时间单位
     */
    @Column(name = "window_unit")
    @Enumerated(EnumType.STRING)
    private WindowUnit windowUnit;

    /**
     * 聚合粒度
     */
    @Column(name = "time_span")
    private Integer timeSpan;

    /**
     * 粒度时间单位
     */
    @Column(name = "span_unit")
    @Enumerated(EnumType.STRING)
    private WindowUnit spanUnit;

    /**
     * 窗口类型
     * DYNAMIC: 滑动
     * FIXED: 固定
     */
    @Searchable(desc = "窗口类型")
    @Column(name = "window_type")
    @Enumerated(EnumType.STRING)
    private WindowType windowType;

    /**
     * 前置条件
     * groovy 表达式
     */
    @Searchable(desc = "前置条件")
    @Column(name = "script")
    private String script;

    /**
     * 关联的事件，逗号分割
     */
    @Column(name = "events")
    private String events;

    @PrePersist
    public void persist() {
        if (this.aggKey == null) {
            this.aggKey = "";
        }
        if (this.script == null) {
            this.script = "";
        }
    }
}

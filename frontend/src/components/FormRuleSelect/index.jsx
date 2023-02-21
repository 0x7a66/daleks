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

import React, {useEffect, useState} from "react";
import RuleState from "@/components/RuleState";
import SelectTable from "@/components/SelectTable";
import {Tag} from "antd";
import * as apiTag from "@/api/tag";

export default (props) => {
  const [tagFilter, setTagFilter] = useState([])

  const {
    onSelect = () => {
    }, selected = [], searchApi
  } = props;

  useEffect(async () => {
    const tags = await apiTag.all()
    const filter = tags.map(t => {
      return {text: t.name, value: t.id + ''}
    })
    setTagFilter(filter)
  }, [])

  return (
    <>
      <SelectTable columns={[
        {
          title: 'ID',
          width: 30,
          dataIndex: 'id',
        },
        {
          title: '名称',
          dataIndex: 'name',
        },
        {
          title: '标签',
          dataIndex: 'tags',
          filters: tagFilter,
          render: (_, {tags = []}) => tags.map(tag => <Tag key={tag.name} color={tag.color}>{tag.name}</Tag>)
        },
        {
          title: '状态',
          width: 100,
          dataIndex: 'state',
          render: (text) => <RuleState state={text}/>
        }
      ]} selected={selected} onSelect={onSelect} searchApi={searchApi}/>
    </>
  )
}

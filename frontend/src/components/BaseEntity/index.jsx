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

import {Descriptions, Tag, Typography} from "antd";
import {dateFormat} from "@/utils/utils";
import DisplayContent from '../DisplayContent'
import React from "react";

export default ({entity}) => {
  return (
    <DisplayContent title={'基本信息'}>
      <Descriptions column={2} size='small'>
        <Descriptions.Item label="ID">
          <Typography.Text copyable={true}>{entity.id}</Typography.Text>
        </Descriptions.Item>
        <Descriptions.Item label="备注">
          <Typography.Text copyable={true} ellipsis={{tooltip: 'more'}}>{entity.remark}</Typography.Text>
        </Descriptions.Item>
        <Descriptions.Item label="创建人">
          <Typography.Text copyable={false}>{entity.author}</Typography.Text>
        </Descriptions.Item>
        <Descriptions.Item label="创建时间">
          <Typography.Text copyable={false}>{dateFormat(entity.createTime)}</Typography.Text>
        </Descriptions.Item>
        <Descriptions.Item label="最后修改人">
          <Typography.Text copyable={false}>{entity.modifier}</Typography.Text>
        </Descriptions.Item>
        <Descriptions.Item label="修改时间">
          <Typography.Text copyable={false}>{dateFormat(entity.updateTime)}</Typography.Text>
        </Descriptions.Item>
      </Descriptions>
      <Descriptions column={2} size='small'>
        {entity.tags && entity.tags.length > 0 ? (
          <Descriptions.Item label="标签">
            {entity.tags.map(tag => <Tag key={tag.name} color={tag.color}>{tag.name}</Tag>)}
          </Descriptions.Item>
        ) : null}
      </Descriptions>
    </DisplayContent>
  )
}

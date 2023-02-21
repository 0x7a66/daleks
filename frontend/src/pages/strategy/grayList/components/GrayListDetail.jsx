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

import {Badge, Descriptions, Spin, Table, Tag, Typography} from "antd";
import React, {useEffect, useState} from "react";
import {Link} from "umi";
import BaseEntity from "@/components/BaseEntity";
import DisplayContent from "@/components/DisplayContent";
import * as api from '@/api/grayList';
import {dateFormat} from "@/utils/utils";
import NotExist from "@/components/Error/NotExist";

export default (props) => {
  const {id} = props;
  const [loading, setLoading] = useState(false);
  const [grayList, setGrayList] = useState({})

  useEffect(async () => {
    setLoading(true)
    const [data] = await Promise.all([
      api.get({id})
    ])
    setGrayList(data)
    setLoading(false)
  }, [id])

  return (
    <Spin spinning={loading}>
      {grayList && grayList.id ? <>
      <BaseEntity entity={grayList} />
      <DisplayContent>
        <Descriptions column={1} size='small'>
          <Descriptions.Item label="名单组">
            <Link to={`/strategy/gray/group/detail?id=${grayList.groupId}`}>{grayList.groupName}</Link>
          </Descriptions.Item>
          <Descriptions.Item label="数据">
            <Typography.Text copyable={true}>{grayList.value}</Typography.Text>
          </Descriptions.Item>
          <Descriptions.Item label="维度">
            <Typography.Text copyable={false}>{grayList.dimension}</Typography.Text>
          </Descriptions.Item>
          <Descriptions.Item label="类型">
            <Badge status={grayList.type === 'WHITE' ? 'success' : 'error'} />
            <Typography.Text copyable={false}>{grayList.type === 'WHITE' ? '白名单' : '黑名单'}</Typography.Text>
          </Descriptions.Item>
          <Descriptions.Item label="生效时间">
            <Typography.Text copyable={false}>{dateFormat(grayList.startTime)}</Typography.Text>
          </Descriptions.Item>
          <Descriptions.Item label="失效时间">
            <Typography.Text copyable={false}>{dateFormat(grayList.expireTime)}</Typography.Text>
          </Descriptions.Item>

        </Descriptions>
      </DisplayContent>
    </> : <NotExist />}


    </Spin>
  )
}

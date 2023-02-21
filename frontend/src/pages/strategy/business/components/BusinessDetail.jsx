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

import {Descriptions, Spin, Table, Typography} from "antd";
import React, {useEffect, useState} from "react";
import {Link} from "umi";
import DisplayContent from "@/components/DisplayContent";
import BaseEntity from "@/components/BaseEntity";
import * as api from '@/api/business';
import * as apiEvent from '@/api/event';
import NotExist from "@/components/Error/NotExist";

export default (props) => {
  const {id} = props;
  const [loading, setLoading] = useState(false);
  const [business, setBusiness] = useState({})
  const [events, setEvents] = useState([])

  useEffect(async () => {
    if(id) {
      setLoading(true)
      const [data, events] = await Promise.all([
        api.get({id}),
        apiEvent.queryByBusinessId({id})
      ])
      setBusiness(data)
      setEvents(events)
      setLoading(false)
    }
  }, [id])

  return (
    <Spin spinning={loading}>
      {business && business.id ? <>
      <BaseEntity entity={business} />
      <DisplayContent>
        <Descriptions column={1} size='small'>
          <Descriptions.Item label="名称">
            <Typography.Text copyable={true}>{business.name}</Typography.Text>
          </Descriptions.Item>
          <Descriptions.Item label="code">
            <Typography.Text copyable={true}>{business.code}</Typography.Text>
          </Descriptions.Item>
        </Descriptions>
      </DisplayContent>

      <DisplayContent title={'关联事件'}>
        <Table size='small' rowKey={'id'} columns={[
          {
            title: 'ID',
            width: 60,
            dataIndex: 'id',
          },
          {
            title: '事件名称',
            dataIndex: 'name',
            render: (text, record) => <Link to={`/strategy/event/detail?id=${record.id}`}>{text}</Link>
          },
          {
            title: '事件code',
            dataIndex: 'code',
          },
        ]} dataSource={events} pagination={false}/>
      </DisplayContent>
    </> : <NotExist />}
    </Spin>
  )
}

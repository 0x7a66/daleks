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

import {Badge, Button, Col, Descriptions, Drawer, message, Modal, Row, Space, Spin, Table, Typography} from "antd";
import React, {useEffect, useState} from "react";
import DisplayContent from "@/components/DisplayContent";
import BaseEntity from "@/components/BaseEntity";
import * as api from '@/api/grayGroup';
import * as apiGrayList from '@/api/grayList';
import {dateFormat} from "@/utils/utils";
import {DeleteOutlined, PlusOutlined} from "@ant-design/icons";
import UseSearchTable from "@/components/SearchTable";
import GrayListDetail from "@/pages/strategy/grayList/components/GrayListDetail";
import NotExist from "@/components/Error/NotExist";
import {history} from "umi";

export default (props) => {
  const {id} = props;
  const [loading, setLoading] = useState(false);
  const [showGrayListDetail, setShowGrayListDetail] = useState(false)
  const [grayListId, setGrayListId] = useState()
  const [grayGroup, setGrayGroup] = useState({})

  const {SearchTable, refresh} = UseSearchTable({
    search: async (params) => {
      const {filters = {}} = params;

      return await apiGrayList.search({...params, filters: {...filters, groupId: [id]}});
    },
    changeHistory: false
  })

  useEffect(async () => {
    setLoading(true)
    const [data] = await Promise.all([
      api.get({id}),
    ])
    setGrayGroup(data)
    setLoading(false)
  }, [id])

  const columns = [
    {
      title: 'ID',
      width: 30,
      dataIndex: 'id',
      sorter: true,
    },
    {
      title: '数据',
      dataIndex: 'type',
      filterMultiple: false,
      filters: [
        {
          text: '黑名单',
          value: 'BLACK',
        },
        {
          text: '白名单',
          value: 'WHITE',
        }],
      render: (text, record) => (
        <>
          <Badge status={record.type === 'WHITE' ? 'success' : 'error'} />
          <span>{record.value}</span>
        </>
      )
    },
    {
      title: '备注',
      dataIndex: 'remark',
    },
    {
      title: '有效期',
      dataIndex: 'expireTime',
      render: (text, record) => dateFormat(text)
    },
    {
      title: '操作',
      dataIndex: 'id',
      width: 60,
      render: (text, record) => <Button type='text' shape={'circle'} icon={<DeleteOutlined/>} onClick={(e) => {
        e.stopPropagation();
        Modal.confirm({
          icon: false,
          title: `删除 ${record.value} ?`,
          onOk: async () => {
            await apiGrayList.del([record.id])
            message.success('删除成功')
            refresh()
          },
        });
      }}/>
    },
  ];

  return (
    <Spin spinning={loading}>
      {grayGroup && grayGroup.id ? <>
      <BaseEntity entity={grayGroup} />
      <DisplayContent>
        <Descriptions column={1} size='small'>
          <Descriptions.Item label="名单组">
            <Typography.Text copyable={true}>{grayGroup.name}</Typography.Text>
          </Descriptions.Item>
          <Descriptions.Item label="分类">
            <Typography.Text copyable={false}>{grayGroup.category}</Typography.Text>
          </Descriptions.Item>
        </Descriptions>
      </DisplayContent>

      <DisplayContent title={'名单列表'}>
        <SearchTable
          columns={columns}
          rowSelectEnable={false}
          onRowClick={record => {
            setGrayListId(record.id)
            setShowGrayListDetail(true)
          }}
          rowClassName={(record) => record.id === grayListId ? 'ant-table-row-selected' : ''}
        />

        <Drawer
          title="名单详情"
          width={800}
          visible={showGrayListDetail}
          destroyOnClose={true}
          onClose={() => setShowGrayListDetail(false)}
        >
          <GrayListDetail id={grayListId} />
        </Drawer>

      </DisplayContent>
    </> : <NotExist />}
    </Spin>
  )
}

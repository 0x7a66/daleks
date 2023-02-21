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

import React, {useRef, useState} from 'react';
import ProCard from '@ant-design/pro-card';
import {Badge, Button, message, Modal, Space, Tooltip} from "antd";
import {styleHeight} from "@/pages/style";
import {CloseOutlined, DeleteOutlined, PlusOutlined, SyncOutlined} from "@ant-design/icons";
import {history, Link} from 'umi';
import GrayListDetail from "./components/GrayListDetail";
import UseSearchTable from '@/components/SearchTable';
import * as api from '@/api/grayList';
import {dateFormat} from "@/utils/utils";

export default () => {
  const ref = useRef()
  const [showRight, setShowRight] = useState(false)
  const [id, setId] = useState(0)
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const {SearchTable, refresh} = UseSearchTable({
    search: async (params) => {
      const data = await api.search(params);
      if (ref.current) {
        ref.current.scrollTo(0, 0);
      }
      return data
    }
  })

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
          <Badge status={record.type === 'WHITE' ? 'success' : 'error'}/>
          <span>{record.value}</span>
        </>
      )
    },
    {
      title: '名单组',
      dataIndex: ['grayGroup', 'name'],
      render: (text, record) => <Link to={`/strategy/gray/group/detail?id=${record.groupId}`}>{text}</Link>
    },
    {
      title: '有效期',
      dataIndex: 'expireTime',
      render: (text, record) => text ? dateFormat(text) : '永久'
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
            await api.del([record.id])
            message.success('删除成功')
            refresh()
          },
        });
      }}/>
    },
  ];


  const onRowClick = (record) => {
    setId(record.id)
    setShowRight(true)
  }

  const onBatchDelete = () => {
    Modal.confirm({
      icon: false,
      title: `删除 ${selectedRowKeys.length} 个名单 ?`,
      onOk: async () => {
        await api.del(selectedRowKeys)
        message.success('删除成功')
        refresh()
      },
    })
  }

  const onSync = () => {
    Modal.confirm({
      icon: false,
      title: `确认同步 ?`,
      onOk: async () => {
        await api.sync()
        message.success('同步成功')
      },
    })
  }

  return (
    <ProCard title={
      <Space size={'middle'}>
        <span>名单管理</span>
        <Button type='primary' onClick={() => {
          history.push('/strategy/gray/list/add')
        }} icon={<PlusOutlined/>}>新增</Button>
        <Button type='primary' onClick={onBatchDelete} disabled={!selectedRowKeys.length > 0}
                icon={<DeleteOutlined/>}>删除</Button>
        <Tooltip title="名单数据同步到redis">
          <Button type='primary' onClick={onSync} icon={<SyncOutlined/>}>同步</Button>
        </Tooltip>
      </Space>
    } headerBordered split={'vertical'}>
      <ProCard style={styleHeight} ref={ref}>
        <SearchTable
          columns={columns}
          onSelectChange={(selectedRowKeys) => {
            setSelectedRowKeys(selectedRowKeys);
          }}
          onRowClick={onRowClick}/>
      </ProCard>
      {showRight ? (
        <ProCard title="详情" size={'small'} headerBordered colSpan="50%" style={styleHeight}
                 extra={<a onClick={() => setShowRight(!showRight)}><CloseOutlined/></a>}>
          <GrayListDetail id={id}/>
        </ProCard>
      ) : null}

    </ProCard>
  );
};

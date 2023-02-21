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

import React, {useEffect, useRef, useState} from 'react';
import ProCard from '@ant-design/pro-card';
import {Button, message, Modal, Space, Tag} from "antd";
import {styleHeight} from "@/pages/style";
import {CloseOutlined, DeleteFilled, DeleteOutlined, EditFilled, PlusOutlined} from "@ant-design/icons";
import {history, Link} from 'umi';
import AtomRuleDetail from "./components/AtomRuleDetail";
import MoreActions from '@/components/MoreActions';
import UseSearchTable from '@/components/SearchTable';
import * as api from '@/api/atomRule';
import * as apiTag from '@/api/tag';

export default () => {
  const ref = useRef()
  const [showRight, setShowRight] = useState(false)
  const [id, setId] = useState(0)
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const [tagFilter, setTagFilter] = useState([])
  const {SearchTable, refresh} = UseSearchTable({
    search: async (params) => {
      const data = await api.search(params);
      if(ref.current) {
        ref.current.scrollTo(0, 0);
      }
      return data
    }
  })

  useEffect(async () => {
    const tags = await apiTag.all()
    const filter = tags.map(t => {return {text: t.name, value: t.id + ''}})
    setTagFilter(filter)
  }, [])

  const columns = [
    {
      title: 'ID',
      width: 40,
      dataIndex: 'id',
      sorter: true,
    },
    {
      title: '名称',
      dataIndex: 'name',
      sorter: true,
      render: (text, record) => <Link to={`/strategy/atom/rule/detail?id=${record.id}`}>{text}</Link>
    },
    {
      title: '标签',
      dataIndex: 'tags',
      filters: tagFilter,
      render: (_, {tags = []}) => tags.map(tag => <Tag key={tag.name} color={tag.color}>{tag.name}</Tag>)
    },
    {
      title: '操作',
      dataIndex: 'id',
      width: 60,
      render: (text, record) => <MoreActions
        menu={[
          {
            key: 'edit',
            text: '编辑',
            icon: <EditFilled />,
            onClick: () => {
              history.push(`/strategy/atom/rule/update?id=${record.id}`);
            }
          },
          {
            key: 'delete',
            text: '删除',
            icon: <DeleteFilled />,
            onClick: () => {
              Modal.confirm({
                icon: false,
                title: `删除 ${record.name} ?`,
                onOk: async () => {
                  await api.del([record.id])
                  message.success('删除成功')
                  refresh()
                },
              });
            }
          },
        ]}/>,
    },
  ];


  const onRowClick = (record) => {
    setId(record.id)
    setShowRight(true)
  }

  const onBatchDelete = () => {
    Modal.confirm({
      icon: false,
      title: `删除 ${selectedRowKeys.length} 个策略 ?`,
      onOk: async () => {
        await api.del(selectedRowKeys)
        message.success('删除成功')
        refresh()
      },
    })
  }

  const onModelUpdate = () => {

  }

  return (
    <ProCard title={
      <Space size={'middle'}>
        <span>策略管理</span>
        <Button type='primary' onClick={() => {history.push('/strategy/atom/rule/add')}} icon={<PlusOutlined/>}>新增</Button>
        <Button type='primary' onClick={onBatchDelete} disabled={!selectedRowKeys.length > 0} icon={<DeleteOutlined/>}>删除</Button>
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
          <AtomRuleDetail id={id} />
        </ProCard>
      ): null}

    </ProCard>
  );
};

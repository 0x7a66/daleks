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
import {CloseOutlined, DeleteFilled, DeleteOutlined, EditFilled, CloudFilled, PlusOutlined} from "@ant-design/icons";
import {history, Link} from 'umi';
import VariableDetail from "./components/VariableDetail";
import MoreActions from '@/components/MoreActions';
import UseSearchTable from '@/components/SearchTable';
import UseQueryValue from './components/QueryValue'
import * as api from '@/api/variable';
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

  const {QueryValue, showQueryValue} = UseQueryValue();

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
      title: '??????',
      dataIndex: 'name',
      sorter: true,
      render: (text, record) => <Link to={`/strategy/variable/detail?id=${record.id}`}>{text}</Link>
    },
    {
      title: '??????',
      dataIndex: 'tags',
      filters: tagFilter,
      render: (_, {tags = []}) => tags.map(tag => <Tag key={tag.name} color={tag.color}>{tag.name}</Tag>)
    },
    {
      title: '??????',
      dataIndex: 'id',
      width: 60,
      render: (text, record) => <MoreActions
        menu={[
          {
            key: 'edit',
            text: '??????',
            icon: <EditFilled />,
            onClick: () => {
              history.push(`/strategy/variable/update?id=${record.id}`);
            }
          },
          {
            key: 'delete',
            text: '??????',
            icon: <DeleteFilled />,
            onClick: () => {
              Modal.confirm({
                icon: false,
                title: `?????? ${record.name} ?`,
                onOk: async () => {
                  await api.del([record.id])
                  message.success('????????????')
                  refresh()
                },
              });
            }
          },
          {
            key: 'query',
            text: '??????',
            icon: <CloudFilled />,
            onClick: () => {
              showQueryValue(record.id)
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
      title: `?????? ${selectedRowKeys.length} ????????? ?`,
      onOk: async () => {
        await api.del(selectedRowKeys)
        message.success('????????????')
        refresh()
      },
    })
  }

  const onModelUpdate = () => {

  }

  return (
    <ProCard title={
      <Space size={'middle'}>
        <span>????????????</span>
        <Button type='primary' onClick={() => {history.push('/strategy/variable/add')}} icon={<PlusOutlined/>}>??????</Button>
        <Button type='primary' onClick={onBatchDelete} disabled={!selectedRowKeys.length > 0} icon={<DeleteOutlined/>}>??????</Button>
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
        <ProCard title="??????" size={'small'} headerBordered colSpan="50%" style={styleHeight}
                 extra={<a onClick={() => setShowRight(!showRight)}><CloseOutlined/></a>}>
          <VariableDetail id={id} />
        </ProCard>
      ): null}
      <QueryValue />
    </ProCard>
  );
};

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
import {Button, message, Modal, Space, Typography} from 'antd';
import {styleHeight} from '@/pages/style';
import {CloseOutlined, DeleteFilled, DeleteOutlined, EditFilled, PlusOutlined} from '@ant-design/icons';
import {history, Link} from 'umi';
import BusinessDetail from '@/pages/strategy/business/components/BusinessDetail';
import MoreActions from '@/components/MoreActions';
import UseSearchTable from '@/components/SearchTable'
import * as api from '@/api/business';

export default () => {
  const ref = useRef();
  const [showRight, setShowRight] = useState(false);
  const [id, setId] = useState({});
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const {SearchTable, refresh} = UseSearchTable({
    search: async (params) => {
      const data = await api.search(params);
      if(ref.current) {
        ref.current.scrollTo(0, 0);
      }
      return data
    }
  })

  const onRowClick = (record) => {
    setId(record.id);
    setShowRight(true);
  };

  const onBatchDelete = () => {
    Modal.confirm({
      icon: false,
      title: `删除 ${selectedRowKeys.length} 个业务 ?`,
      onOk: async () => {
        await api.del(selectedRowKeys)
        message.success('删除成功')
        refresh()
      }
    })
  }

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
      render: (text, record) => <Link to={`/strategy/business/detail?id=${record.id}`}>{text}</Link>,
    },
    {
      title: 'code',
      dataIndex: 'code',
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
              history.push(`/strategy/business/update?id=${record.id}`);
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

  return (
    <ProCard title={
      <Space size={'middle'}>
        <span>业务管理</span>
        <Button type='primary' onClick={() => {history.push('/strategy/business/add')}} icon={<PlusOutlined/>}>新增</Button>
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
          <BusinessDetail id={id} />
        </ProCard>
      ): null}

    </ProCard>
  );
};

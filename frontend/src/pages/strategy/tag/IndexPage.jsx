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
import {Button, message, Modal, Space, Tag, Typography} from 'antd';
import {styleHeight} from '@/pages/style';
import {DeleteFilled, DeleteOutlined, EditFilled, PlusOutlined} from '@ant-design/icons';
import {history} from 'umi';
import UseSearchTable from '@/components/SearchTable'
import * as api from '@/api/tag';
import MoreActions from "@/components/MoreActions";

export default () => {
  const ref = useRef();
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

  const onBatchDelete = () => {
    Modal.confirm({
      icon: false,
      title: `删除 ${selectedRowKeys.length} 个标签及其关联信息 ?`,
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
      title: '标签',
      dataIndex: 'name',
      render: (text, record) => <Tag key={record.name} color={record.color}>{record.name}</Tag>
    },
    // {
    //   title: '操作',
    //   dataIndex: 'id',
    //   width: 60,
    //   render: (text, record) => <Button type='text' shape={'circle'} icon={<DeleteOutlined/>} onClick={(e) => {
    //     e.stopPropagation();
    //     Modal.confirm({
    //       icon: false,
    //       title: `删除 ${record.name} 及其关联信息 ?`,
    //       onOk: async () => {
    //         await api.del([record.id])
    //         message.success('删除成功')
    //         refresh()
    //       },
    //     });
    //   }}/>
    // },

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
              history.push(`/strategy/tag/update?id=${record.id}`);
            }
          },
          {
            key: 'delete',
            text: '删除',
            icon: <DeleteFilled />,
            onClick: () => {
              Modal.confirm({
                icon: false,
                title: `删除 ${record.name} 及其关联信息 ?`,
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
        <span>标签管理</span>
        <Button type='primary' onClick={() => {history.push('/strategy/tag/add')}} icon={<PlusOutlined/>}>新增</Button>
        <Button type='primary' onClick={onBatchDelete} disabled={!selectedRowKeys.length > 0} icon={<DeleteOutlined/>}>删除</Button>
      </Space>
    } headerBordered split={'vertical'}>
      <ProCard style={styleHeight} ref={ref}>
        <SearchTable
          columns={columns}
          onSelectChange={(selectedRowKeys) => {
            setSelectedRowKeys(selectedRowKeys);
          }}
        />
      </ProCard>

    </ProCard>
  );
};

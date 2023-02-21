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
import SelectTable from "@/components/SelectTable";
import {Button, Drawer, Table} from "antd";
import {DeleteOutlined} from "@ant-design/icons";
import * as apiFunctionConfig from '@/api/functionConfig'

export default (props) => {
  const [dataSource, setDataSource] = useState([])
  const [drawerVisit, setDrawerVisit] = useState(false)

  const {value = '', onChange = () => {}, width = 'xl'} = props;

  useEffect(() => {
    if (value) {
      apiFunctionConfig.getByFunc(value).then(data => {
        if(data) {
          setDataSource([data])
        }
      });
    }
  }, [])

  const removeRecord = () => {
    setDataSource([])
    onChange()
  }

  return (
    <>
      <div className={`pro-field pro-field-${width}`}>
        <Button type={'primary'} block onClick={() => setDrawerVisit(true)}>选择</Button>
        {dataSource.length > 0 ? <Table size='small' rowKey={'id'} columns={[
          {
            title: 'ID',
            width: 30,
            dataIndex: 'id',
          },
          {
            title: '函数',
            dataIndex: 'func',
          },
          {
            title: '',
            width: 30,
            dataIndex: 'action',
            render: (text, record) => <Button type='text' shape={'circle'} icon={<DeleteOutlined/>} onClick={() => removeRecord(record)}/>
          }
        ]} dataSource={dataSource} pagination={false}/> : null }
      </div>

      <Drawer
        title="选择函数"
        width={800}
        visible={drawerVisit}
        destroyOnClose={true}
        onClose={() => setDrawerVisit(false)}
      >
        <SelectTable columns={[
          {
            title: 'ID',
            width: 30,
            dataIndex: 'id',
          },
          {
            title: '函数',
            dataIndex: 'func',
          },
          {
            title: '备注',
            dataIndex: 'remark',
          }
        ]} type={'radio'} selected={dataSource.map(item => item.id)} onSelect={(records, selected) => {
          if(selected) {
            setDataSource(records)
            onChange(records[0].func)
          } else {
            setDataSource([])
            onChange()
          }
        }} searchApi={apiFunctionConfig.search}/>
      </Drawer>
    </>
  )
}

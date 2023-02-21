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
import {Button, Drawer, Table, Tag} from "antd";
import {DeleteOutlined, PlusCircleOutlined} from "@ant-design/icons";
import {containEntity} from '@/utils/utils'
import * as apiGrayGroup from '@/api/grayGroup'

export default (props) => {
  const [dataSource, setDataSource] = useState([])
  const [drawerVisit, setDrawerVisit] = useState(false)

  const {value = '', onChange = () => {}, width = 'xl'} = props;

  useEffect(() => {
    if (value) {
      const initValues = value.split(',')
      if (initValues && initValues.length > 0) {
        apiGrayGroup.queryByIds(initValues).then(data => setDataSource(data));
      }
    }
  }, [])

  useEffect(() => {
    onChange(buildValue())
  }, [dataSource])



  const removeRecord = (record) => {
    const newData = dataSource.filter(item => item.id !== record.id);
    setDataSource(newData)
  }

  const buildValue = () => {
    return dataSource.map(item => item.id).join(',')
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
            title: '名单组',
            dataIndex: 'name',
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
        title="选择名单组"
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
            title: '名单组',
            dataIndex: 'name',
          }
        ]} selected={dataSource.map(item => item.id)} onSelect={(records, selected) => {
          if(selected) {
            setDataSource([...dataSource, ...records.filter(x => !containEntity(dataSource, x))])
          } else {
            setDataSource(dataSource.filter(x => !containEntity(records, x)))
          }
        }} searchApi={apiGrayGroup.search}/>
      </Drawer>
    </>
  )
}

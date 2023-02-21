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
import {Button, Drawer, Space, Table, Tag} from "antd";
import {DeleteOutlined, PlusCircleOutlined} from "@ant-design/icons";
import {containEntity} from '@/utils/utils'
import * as apiTag from '@/api/tag'

export default (props) => {
  const {value = [], onChange = () => {}, width = 'xl'} = props;
  const [dataSource, setDataSource] = useState(value)
  const [drawerVisit, setDrawerVisit] = useState(false)

  useEffect(() => {
    onChange(dataSource)
  }, [dataSource])



  const removeRecord = (record) => {
    const newData = dataSource.filter(item => item.id !== record.id);
    setDataSource(newData)
  }

  return (
    <>
      <Space direction={'vertical'} className={`pro-field pro-field-${width}`}>
        <Button type={'primary'} block onClick={() => setDrawerVisit(true)}>选择</Button>
        {dataSource.map(tag => <Tag key={tag.name} color={tag.color} closable onClose={() => removeRecord(tag)}>{tag.name}</Tag>)}
      </Space>

      <Drawer
        title="选择Tag标签"
        width={800}
        visible={drawerVisit}
        destroyOnClose={true}
        onClose={() => setDrawerVisit(false)}
      >
        <SelectTable columns={[
          {
            title: '标签',
            dataIndex: 'name',
            render: (text, record) => <Tag key={record.name} color={record.color}>{record.name}</Tag>
          }
        ]} selected={dataSource.map(item => item.id)} onSelect={(records, selected) => {
          if(selected) {
            setDataSource([...dataSource, ...records.filter(x => !containEntity(dataSource, x))])
          } else {
            setDataSource(dataSource.filter(x => !containEntity(records, x)))
          }
        }} searchApi={apiTag.search}/>
      </Drawer>
    </>
  )
}

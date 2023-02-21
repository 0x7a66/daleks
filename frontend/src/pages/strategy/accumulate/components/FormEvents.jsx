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
import * as apiEvent from '@/api/event'
import * as apiTag from "@/api/tag";

export default (props) => {

  const {value = '', onChange = () => {}, width = 'xl'} = props;

  const [dataSource, setDataSource] = useState([])
  const [drawerVisit, setDrawerVisit] = useState(false)
  const [tagFilter, setTagFilter] = useState([])

  useEffect(async () => {
    const tags = await apiTag.all()
    const filter = tags.map(t => {return {text: t.name, value: t.id}})
    setTagFilter(filter)
  }, [])

  useEffect(() => {
    if (value) {
      const initValues = value.split(',')
      if (initValues && initValues.length > 0) {
        apiEvent.queryByIds(initValues).then(data => setDataSource(data));
      }
    }
  }, [])

  const removeRecord = (record) => {
    const newData = dataSource.filter(item => item.id !== record.id);
    setDataSource(newData)
    onChange(buildValue(newData))
  }

  const buildValue = (datasource) => {
    return datasource.map(item => item.id).join(',')
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
            title: '事件code',
            dataIndex: 'code',
          },
          {
            title: '事件名称',
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
        title="选择事件"
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
            title: '事件名称',
            dataIndex: 'name',
          },
          {
            title: '事件code',
            dataIndex: 'code',
          },
          {
            title: '标签',
            dataIndex: 'tags',
            filters: tagFilter,
            render: (_, {tags = []}) => tags.map(tag => <Tag key={tag.name} color={tag.color}>{tag.name}</Tag>)
          },
          {
            title: '',
            dataIndex: 'none',
          },
        ]} selected={dataSource.map(item => item.id)} onSelect={(records, selected) => {
          let newData = dataSource.filter(x => !containEntity(records, x))
          if(selected) {
            newData = [...dataSource, ...records.filter(x => !containEntity(dataSource, x))]
          }
          setDataSource(newData)
          onChange(buildValue(newData))
        }} searchApi={apiEvent.search}/>
      </Drawer>
    </>
  )
}

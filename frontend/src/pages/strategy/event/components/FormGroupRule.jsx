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
import {SortableContainer, SortableElement, SortableHandle} from 'react-sortable-hoc';
import {Link} from "umi";
import RuleState from "@/components/RuleState";
import FormRuleSelect from "@/components/FormRuleSelect";
import {Button, Drawer, Table, Tag} from "antd";
import MenuOutlined from "@ant-design/icons/lib/icons/MenuOutlined";
import {DeleteOutlined, PlusCircleOutlined} from "@ant-design/icons";
import {containEntity} from '@/utils/utils'
import * as apiGroupRule from '@/api/groupRule'
import arrayMove from 'array-move'

export default (props) => {
  const [dataSource, setDataSource] = useState([])
  const [drawerVisit, setDrawerVisit] = useState(false)

  const {value = '', onChange = () => {}, width = 'xl'} = props;

  useEffect(() => {
    if (value) {
      const initValues = value.split(',')
      if (initValues && initValues.length > 0) {
        apiGroupRule.queryByIds(initValues).then(data => setDataSource(data));
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

  const DragHandle = SortableHandle(() => <MenuOutlined style={{cursor: 'grab', color: '#ccc'}}/>);
  const DragElement = SortableElement(props => <tr {...props} />);
  const DragContainer = SortableContainer(props => <tbody {...props} />);

  const onSortEnd = ({oldIndex, newIndex}) => {
    if (oldIndex !== newIndex) {
      setDataSource(arrayMove(dataSource, oldIndex, newIndex));
    }
  };

  const DraggableBodyRow = ({className, style, ...restProps}) => {
    const index = dataSource.findIndex(x => x.id === restProps['data-row-key']);
    return <DragElement index={index} {...restProps} />;
  };
  return (
    <>
      <div className={`pro-field pro-field-${width}`}>
        <Button type={'primary'} block onClick={() => setDrawerVisit(true)}>选择</Button>
        {dataSource.length > 0 ? <Table size='small' rowKey={'id'} columns={[
          {
            title: '',
            width: 30,
            dataIndex: 'sort',
            render: () => <DragHandle/>
          },
          {
            title: 'ID',
            width: 30,
            dataIndex: 'id',
          },
          {
            title: '策略组',
            dataIndex: 'name',
            render: (text, record) => <Link to={`/strategy/atom/rule/detail?id=${record.id}`}>{text}</Link>
          },
          {
            title: '状态',
            width: 40,
            dataIndex: 'state',
            render: (text) => <RuleState state={text}/>
          },
          {
            title: '',
            width: 30,
            dataIndex: 'action',
            className: 'drag-hidden',
            render: (text, record) => <Button type='text' shape={'circle'} icon={<DeleteOutlined/>} onClick={() => removeRecord(record)}/>
          }
        ]} dataSource={dataSource} components={{
          body: {
            wrapper: (props) => <DragContainer useDragHandle disableAutoscroll helperClass="row-dragging" onSortEnd={onSortEnd} {...props}/>,
            row: DraggableBodyRow,
          },
        }} pagination={false}/> : null }
      </div>

      <Drawer
        title="选择策略组"
        width={800}
        visible={drawerVisit}
        destroyOnClose={true}
        onClose={() => setDrawerVisit(false)}
      >
        <FormRuleSelect selected={dataSource.map(item => item.id)} onSelect={(records, selected) => {
          if(selected) {
            setDataSource([...dataSource, ...records.filter(x => !containEntity(dataSource, x))])
          } else {
            setDataSource(dataSource.filter(x => !containEntity(records, x)))
          }
        }} searchApi={apiGroupRule.search}/>
      </Drawer>
    </>
  )
}

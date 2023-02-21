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
import {Badge, DatePicker, Drawer, Radio, Space, Table} from 'antd';
import {styleHeight} from '@/pages/style';
import DisplayContent from '@/components/DisplayContent'
import * as api from '@/api/analysis';
import {dateFormat} from "@/utils/utils";
import ReactJson from 'react-json-view'
import Search from "antd/lib/input/Search";
import moment from "moment";

const {RangePicker} = DatePicker;

const RiskLevel = ({text}) => {
  let status = 'success';
  let value = '通过'
  if (text === 'REVIEW') {
    status = 'warning';
    value = '审核';
  }
  if (text === 'REJECT') {
    status = 'error';
    value = '拒绝';
  }
  return (
    <>
      <Badge status={status}/>
      <span>{value}</span>
    </>
  )
}

export default () => {
  const ref = useRef();

  const [riskLog, setRiskLog] = useState({})
  const [showRiskLog, setShowRiskLog] = useState(false)
  const [loading, setLoading] = useState(false);
  const [datasource, setDatasource] = useState([]);
  const [filters, setFilters] = useState({});
  const [sorter, setSorter] = useState({});
  const [value, setValue] = useState('');
  const [total, setTotal] = useState(0);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 20,
  });
  const [dates, setDates] = useState([
    moment(),
    moment(),
  ])

  const search = async () => {
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      field: sorter.field,
      order: sorter.order,
      filters,
      value,
      startTime: dates[0].set({hour: 0, minute: 0, second: 0, millisecond: 0}).valueOf(),
      endTime: dates[1].set({hour: 0, minute: 0, second: 0, millisecond: 0}).add(1, 'days').valueOf(),
    }
    setLoading(true);
    const data = await api.riskLogSearch(params);
    setDatasource(data.content || []);
    setTotal(data.totalElements || 0);
    setLoading(false);
    if (ref.current) {
      ref.current.scrollTo(0, 0);
    }
  }

  useEffect(async () => {
    search();
  }, [pagination, filters, sorter, value, dates]);

  const columns = [
    {
      title: '风险',
      width: 100,
      dataIndex: 'result.level',
      filterMultiple: false,
      filters: [
        {text: '通过', value: 'PASS',},
        {text: '审核', value: 'REVIEW',},
        {text: '拒绝', value: 'REJECT',}
      ],
      render: (text, record) => <RiskLevel text={record.result.level}/>
    },
    {
      title: '耗时ms',
      dataIndex: ['costTime'],
      width: 100,
    },
    {
      title: '事件',
      dataIndex: ['action', 'Event'],
    },
    {
      title: '用户',
      dataIndex: ['action', 'UserId'],
    },
    {
      title: '原因',
      dataIndex: ['result', 'ruleName'],
    },
    {
      title: '创建时间',
      dataIndex: ['action', 'RequestTime'],
      render: (text) => dateFormat(text)
    },
  ];

  return (
    <ProCard title={
      <Space size={'middle'}>
        <span>风控历史</span>
        <RangePicker allowClear={false} value={dates} onChange={dates => setDates(dates)}/>
        <Radio.Group
          value={''}
          onChange={event => {
            setDates([moment().add(-1, event.target.value).add(1, 'days'), moment()])
          }}
        >
          <Radio.Button value='days'>日</Radio.Button>
          <Radio.Button value='weeks'>周</Radio.Button>
          <Radio.Button value='months'>月</Radio.Button>
        </Radio.Group>
      </Space>
    } headerBordered split={'vertical'}>
      <ProCard style={styleHeight} ref={ref}>
        <Search
          style={{marginBottom: 8, width: '100%'}}
          placeholder='请输入搜索内容'
          allowClear
          onSearch={(val) => {
            if (value === val) {
              search();
            } else {
              setValue(val);
            }
          }}
        />
        <Table
          className={'table-row-click'}
          size='small'
          loading={loading}
          rowKey={'riskId'}
          onRow={record => {
            return {
              onClick: () => {
                setRiskLog(record);
                setShowRiskLog(true);
              },
            };
          }}
          rowClassName={(record) => record.riskId === riskLog.riskId ? 'ant-table-row-selected' : ''}
          pagination={{
            ...pagination,
            total,
            pageSizeOptions: [10, 20, 50, 100, 200],
            showSizeChanger: true,
            showLessItems: true,
            showTotal: (total) => `总共 ${total} 条`,
          }}
          scroll={{x: 'max-content'}}
          onChange={(pagination, filters, sorter) => {
            setPagination(pagination);
            setFilters(filters);
            setSorter(sorter);
          }}
          columns={columns}
          dataSource={datasource}
        />
      </ProCard>
      <Drawer
        title="数据详情"
        width={800}
        visible={showRiskLog}
        destroyOnClose={true}
        onClose={() => setShowRiskLog(false)}
      >
        <DisplayContent title={'请求参数'}>
          <ReactJson name={false} displayDataTypes={false} indentWidth={2} src={riskLog.action}/>
        </DisplayContent>
        <DisplayContent title={'上下文'}>
          <ReactJson name={false} displayDataTypes={false} indentWidth={2} src={riskLog.context}/>
        </DisplayContent>
        <DisplayContent title={'风控结果'}>
          <ReactJson name={false} displayDataTypes={false} indentWidth={2} src={riskLog.result}/>
        </DisplayContent>
        <DisplayContent title={'规则命中详情'}>
          <ReactJson name={false} displayDataTypes={false} indentWidth={2} src={riskLog.ruleResults}/>
        </DisplayContent>
      </Drawer>
    </ProCard>
  );
};

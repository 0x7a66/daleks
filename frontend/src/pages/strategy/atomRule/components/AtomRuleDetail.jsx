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

import {Descriptions, Spin, Table, Tag, Typography} from "antd";
import React, {useEffect, useState} from "react";
import {Link} from "umi";
import BaseEntity from "@/components/BaseEntity";
import DisplayContent from "@/components/DisplayContent";
import GroovyCode from "@/components/GroovyCode";
import RuleState from "@/components/RuleState";
import * as api from '@/api/atomRule';
import * as apiEvent from '@/api/event';
import * as apiGroupRule from '@/api/groupRule';
import * as apiAccumulate from '@/api/accumulate';
import * as apiVariable from '@/api/variable';
import ReactJson from "react-json-view";
import NotExist from "@/components/Error/NotExist";

export default (props) => {
  const {id} = props;
  const [loading, setLoading] = useState(false);
  const [atomRule, setAtomRule] = useState({})
  const [events, setEvents] = useState([])
  const [groupRules, setGroupRules] = useState([])
  const [accumulates, setAccumulates] = useState([])
  const [variables, setVariables] = useState([])

  useEffect(async () => {
    setLoading(true)
    const [data, events, groupRules, accumulates, variables] = await Promise.all([
      api.get({id}),
      apiEvent.queryByAtomRuleId({id}),
      apiGroupRule.queryByAtomRuleId({id}),
      apiAccumulate.queryByAtomRuleId({id}),
      apiVariable.queryByAtomRuleId({id}),
    ])
    setAtomRule(data)
    setEvents(events)
    setGroupRules(groupRules)
    setAccumulates(accumulates)
    setVariables(variables)
    setLoading(false)
  }, [id])

  return (
    <Spin spinning={loading}>
      {atomRule && atomRule.id ? <>
      <BaseEntity entity={atomRule} />
      <DisplayContent>
        <Descriptions column={1} size='small'>
          <Descriptions.Item label="名称">
            <Typography.Text copyable={true}>{atomRule.name}</Typography.Text>
          </Descriptions.Item>
          <Descriptions.Item label="话术">
            <Typography.Text copyable={true}>{atomRule.reply}</Typography.Text>
          </Descriptions.Item>
        </Descriptions>
      </DisplayContent>

      <DisplayContent title={'前置条件'}>
        <GroovyCode code={atomRule.script} />
      </DisplayContent>

      { accumulates.length > 0 ?
        <DisplayContent title={'关联指标'}>
          <Table size='small' rowKey={'id'} columns={[
            {
              title: 'ID',
              width: 60,
              dataIndex: 'id',
            },
            {
              title: '名称',
              dataIndex: 'name',
              render: (text, record) => <Link to={`/strategy/accumulate/detail?id=${record.id}`}>{text}</Link>
            },
            {
              title: '备注',
              dataIndex: 'remark',
            },
          ]} dataSource={accumulates} pagination={false}/>
        </DisplayContent> : null }

      { variables.length > 0 ?
        <DisplayContent title={'关联变量'}>
          <Table size='small' rowKey={'id'} columns={[
            {
              title: 'ID',
              width: 60,
              dataIndex: 'id',
            },
            {
              title: '名称',
              dataIndex: 'name',
              render: (text, record) => <Link to={`/strategy/variable/detail?id=${record.id}`}>{text}</Link>
            },
            {
              title: '备注',
              dataIndex: 'remark',
            },
          ]} dataSource={variables} pagination={false}/>
        </DisplayContent> : null }

      <DisplayContent title={'关联策略组'}>
        <Table size='small' rowKey={'id'} columns={[
          {
            title: 'ID',
            width: 60,
            dataIndex: 'id',
          },
          {
            title: '名称',
            dataIndex: 'name',
            render: (text, record) => <Link to={`/strategy/group/rule/detail?id=${record.id}`}>{text}</Link>
          },
          {
            title: '状态',
            width: 100,
            dataIndex: 'state',
            render: (text) => <RuleState state={text} />
          }
        ]} dataSource={groupRules} pagination={false}/>
      </DisplayContent>

      <DisplayContent title={'关联事件'}>
        <Table size='small' rowKey={'id'} columns={[
          {
            title: 'ID',
            width: 60,
            dataIndex: 'id',
          },
          {
            title: '事件名称',
            dataIndex: 'name',
            render: (text, record) => <Link to={`/strategy/event/detail?id=${record.id}`}>{text}</Link>
          },
          {
            title: '事件code',
            dataIndex: 'code',
          },
        ]} dataSource={events}  pagination={false}/>
      </DisplayContent>

      <DisplayContent title={'返回Json'}>
        <ReactJson name={false} displayDataTypes={false} indentWidth={2} src={atomRule.returnJson ? JSON.parse(atomRule.returnJson) : {}} />
      </DisplayContent>
      </> : <NotExist />}
    </Spin>
  )
}

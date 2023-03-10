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
import NotExist from "@/components/Error/NotExist";
import * as api from '@/api/accumulate';
import * as apiAtomRule from '@/api/atomRule';
import * as apiGroupRule from '@/api/groupRule';
import * as apiVariable from '@/api/variable';
import * as apiEvent from '@/api/event';

export default (props) => {
  const {id} = props;
  const [loading, setLoading] = useState(false);
  const [accumulate, setAccumulate] = useState({})
  const [accumulates, setAccumulates] = useState([])
  const [atomRules, setAtomRules] = useState([])
  const [groupRules, setGroupRules] = useState([])
  const [variables, setVariables] = useState([])
  const [events, setEvents] = useState([])

  useEffect(async () => {
    setLoading(true)
    const [data, accumulates, atomRules, groupRules, variables, events] = await Promise.all([
      api.get({id}),
      api.queryDependencyById({id}),
      apiAtomRule.queryByAccumulateId({id}),
      apiGroupRule.queryByAccumulateId({id}),
      apiVariable.queryByAccumulateId({id}),
      apiEvent.queryByAccumulateId({id}),
    ])
    setAccumulate(data)
    setAccumulates(accumulates)
    setAtomRules(atomRules)
    setGroupRules(groupRules)
    setVariables(variables)
    setEvents(events)
    setLoading(false)
  }, [id])

  return (
    <Spin spinning={loading}>
      {accumulate && accumulate.id ? <>
        <BaseEntity entity={accumulate} />
        <DisplayContent>
          <Descriptions column={1} size='small'>
            <Descriptions.Item label="??????">
              <Typography.Text copyable={true}>{accumulate.name}</Typography.Text>
            </Descriptions.Item>
            <Descriptions.Item label="??????">
              <Typography.Text copyable={false}>{accumulate.groupKey}</Typography.Text>
            </Descriptions.Item>
            <Descriptions.Item label="??????">
              <Typography.Text copyable={false}>{`${accumulate.aggFunction}(${accumulate.aggKey})`}</Typography.Text>
            </Descriptions.Item>
            <Descriptions.Item label="????????????">
              <Typography.Text copyable={false}>{accumulate.windowType}</Typography.Text>
            </Descriptions.Item>
            <Descriptions.Item label="????????????">
              <Typography.Text copyable={false}>{`${accumulate.timeWindow} ${accumulate.windowUnit}`}</Typography.Text>
            </Descriptions.Item>
            <Descriptions.Item label="????????????">
              <Typography.Text copyable={false}>{`${accumulate.timeSpan} ${accumulate.spanUnit}`}</Typography.Text>
            </Descriptions.Item>

          </Descriptions>
        </DisplayContent>

        <DisplayContent title={'????????????'}>
          <GroovyCode code={accumulate.script} />
        </DisplayContent>


        <DisplayContent title={'????????????'}>
          <Table size='small' rowKey={'id'} columns={[
            {
              title: 'ID',
              width: 60,
              dataIndex: 'id',
            },
            {
              title: '??????code',
              dataIndex: 'code',
            },
            {
              title: '????????????',
              dataIndex: 'name',
              render: (text, record) => <Link to={`/strategy/event/detail?id=${record.id}`}>{text}</Link>
            }
          ]} dataSource={events}  pagination={false}/>
        </DisplayContent>

        {atomRules.length > 0 ?
          <DisplayContent title={'????????????'}>
            <Table size='small' rowKey={'id'} columns={[
              {
                title: 'ID',
                width: 60,
                dataIndex: 'id',
              },
              {
                title: '??????',
                dataIndex: 'name',
                render: (text, record) => <Link to={`/strategy/atom/rule/detail?id=${record.id}`}>{text}</Link>
              },
              {
                title: '??????',
                width: 100,
                dataIndex: 'state',
                render: (text) => <RuleState state={text} />
              }
            ]} dataSource={atomRules} pagination={false}/>
          </DisplayContent> : null }

        {groupRules.length > 0 ?
          <DisplayContent title={'???????????????'}>
            <Table size='small' rowKey={'id'} columns={[
              {
                title: 'ID',
                width: 60,
                dataIndex: 'id',
              },
              {
                title: '??????',
                dataIndex: 'name',
                render: (text, record) => <Link to={`/strategy/group/rule/detail?id=${record.id}`}>{text}</Link>
              },
              {
                title: '??????',
                width: 100,
                dataIndex: 'state',
                render: (text) => <RuleState state={text} />
              }
            ]} dataSource={groupRules} pagination={false}/>
          </DisplayContent>: null }



        {accumulates.length > 0 ?
          <DisplayContent title={'?????????????????????'}>
            <Table size='small' rowKey={'id'} columns={[
              {
                title: 'ID',
                width: 60,
                dataIndex: 'id',
              },
              {
                title: '????????????',
                dataIndex: 'name',
                render: (text, record) => <Link to={`/strategy/accumulate/detail?id=${record.id}`}>{text}</Link>
              },
              {
                title: '??????',
                dataIndex: 'remark',
              },
            ]} dataSource={accumulates}  pagination={false}/>
          </DisplayContent> : null }

        {variables.length > 0 ?
          <DisplayContent title={'?????????????????????'}>
            <Table size='small' rowKey={'id'} columns={[
              {
                title: 'ID',
                width: 60,
                dataIndex: 'id',
              },
              {
                title: '????????????',
                dataIndex: 'name',
                render: (text, record) => <Link to={`/strategy/variable/detail?id=${record.id}`}>{text}</Link>
              },
              {
                title: '??????',
                dataIndex: 'remark',
              },
            ]} dataSource={variables}  pagination={false}/>
          </DisplayContent> : null }
        </> : <NotExist />}
    </Spin>
  )
}

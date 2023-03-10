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

import {Descriptions, Spin, Table, Typography} from "antd";
import React, {useEffect, useState} from "react";
import {Link} from "umi";
import DisplayContent from "@/components/DisplayContent";
import BaseEntity from "@/components/BaseEntity";
import RuleState from "@/components/RuleState";
import * as api from '@/api/event';
import * as apiGrayGroup from '@/api/grayGroup';
import * as apiGroupRule from '@/api/groupRule';
import NotExist from "@/components/Error/NotExist";

export default (props) => {
  const {id} = props;
  const [loading, setLoading] = useState(false);
  const [event, setEvent] = useState({})
  const [grayGroups, setGrayGroups] = useState([])
  const [groupRules, setGroupRules] = useState([])
  const [renameMapping, setRenameMapping] = useState([])

  useEffect(async () => {
    setLoading(true)
    const [data, grayGroups, groupRules] = await Promise.all([
      api.get({id}),
      apiGrayGroup.queryByEventId({id}),
      apiGroupRule.queryWithAtomRulesByEventId({id}),
    ])
    setEvent(data)
    setGrayGroups(grayGroups)
    setGroupRules(groupRules)
    setLoading(false)

    if (data.renameMapping) {
      const renameMapping = JSON.parse(data.renameMapping)
      setRenameMapping(Object.keys(renameMapping).map(k => {
        return {
          'origin': k,
          'mapping': renameMapping[k]
        }
      }))
    }
  }, [id])


  const renderAtomRule = (atomRuleList) => {
    return <Table size='small' rowKey={'id'} columns={[
      {
        title: '??????',
        width: 60,
        dataIndex: 'id',
      },
      {
        title: '',
        dataIndex: 'name',
        render: (text, record) => <Link to={`/strategy/atom/rule/detail?id=${record.id}`}>{text}</Link>
      },
      {
        title: '',
        width: 100,
        dataIndex: 'state',
        render: (text) => <RuleState state={text}/>
      }
    ]} dataSource={atomRuleList} pagination={false}/>
  }

  return (
    <Spin spinning={loading}>
      {event && event.id ? <>
        <BaseEntity entity={event}/>
        <DisplayContent>
          <Descriptions column={1} size='small'>
            <Descriptions.Item label="??????">
              <Typography.Text copyable={true}>{event.name}</Typography.Text>
            </Descriptions.Item>
            <Descriptions.Item label="code">
              <Typography.Text copyable={true}>{event.code}</Typography.Text>
            </Descriptions.Item>
            <Descriptions.Item label="??????">
              <Typography.Text copyable={true}>{event.reply}</Typography.Text>
            </Descriptions.Item>
          </Descriptions>
        </DisplayContent>

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
              render: (text) => <RuleState state={text}/>
            }
          ]} expandable={{
            defaultExpandAllRows: true,
            expandedRowRender: (groupRule, index, indent, expanded) => {
              return renderAtomRule(groupRule.atomRuleList)
            },
            rowExpandable: groupRule => groupRule.atomRuleList.length > 0,
          }} dataSource={groupRules} pagination={false}/>
        </DisplayContent>

        {grayGroups.length > 0 ?
          <DisplayContent title={'???????????????'}>
            <Table size='small' rowKey={'id'} columns={[
              {
                title: 'ID',
                width: 60,
                dataIndex: 'id',
              },
              {
                title: '?????????',
                dataIndex: 'name',
                render: (text, record) => <Link to={`/strategy/gray/group/detail?id=${record.id}`}>{text}</Link>
              }
            ]} dataSource={grayGroups} pagination={false}/>
          </DisplayContent> : null}

        {renameMapping.length > 0 ?
          <DisplayContent title={'????????????'}>
            <Table size='small' rowKey={'origin'} columns={[
              {
                title: '????????????',
                width: '30%',
                dataIndex: 'origin',
              },
              {
                title: '????????????',
                dataIndex: 'mapping',
              }
            ]} dataSource={renameMapping} pagination={false}/>
          </DisplayContent> : null}
      </> : <NotExist/>}
    </Spin>
  )
}

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
import JsonView from "@/components/JsonView";
import * as api from '@/api/functionConfig';
import * as apiVariable from '@/api/variable';
import ReactJson from "react-json-view";
import NotExist from "@/components/Error/NotExist";

export default (props) => {
  const {id} = props;
  const [loading, setLoading] = useState(false);
  const [functionConfig, setFunctionConfig] = useState({})
  const [variables, setVariables] = useState([])

  useEffect(async () => {
    setLoading(true)
    const [data, variables] = await Promise.all([
      api.get({id}),
      apiVariable.queryByFunctionConfigId({id}),
    ])
    setFunctionConfig(data)
    setVariables(variables)
    setLoading(false)
  }, [id])

  return (
    <Spin spinning={loading}>
      {functionConfig && functionConfig.id ? <>
      <BaseEntity entity={functionConfig} />
      <DisplayContent>
        <Descriptions column={1} size='small'>
          <Descriptions.Item label="函数名称">
            <Typography.Text copyable={true}>{functionConfig.func}</Typography.Text>
          </Descriptions.Item>
        </Descriptions>
      </DisplayContent>

      <DisplayContent title={'函数参数'}>
        <GroovyCode code={functionConfig.parameters} />
      </DisplayContent>

      <DisplayContent title={'返回结果'}>
        <JsonView code={functionConfig.result} />
      </DisplayContent>

      <DisplayContent title={'关联变量'}>
        <Table size='small' rowKey={'id'} columns={[
          {
            title: 'ID',
            width: 60,
            dataIndex: 'id',
          },
          {
            title: '变量名称',
            dataIndex: 'name',
            render: (text, record) => <Link to={`/strategy/variable/detail?id=${record.id}`}>{text}</Link>
          }
        ]} dataSource={variables}  pagination={false}/>
      </DisplayContent>
    </> : <NotExist />}
    </Spin>
  )
}

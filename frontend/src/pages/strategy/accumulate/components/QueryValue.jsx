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

import React, {useState} from "react";
import {Button, Descriptions, Drawer, Typography} from "antd";
import * as apiScript from '@/api/script';
import * as api from '@/api/accumulate';
import DisplayContent from "@/components/DisplayContent";
import JsonView from "@/components/JsonView";
import ProForm, {ProFormText} from "@ant-design/pro-form";

export default () => {
  const [drawerVisit, setDrawerVisit] = useState(false)
  const [accumulate, setAccumulate] = useState({})
  const [keys, setKeys] = useState([])
  const QueryValue = () => {
    const [result, setResult] = useState('')
    const queryValue = async (parameters) => {
      setResult('')
      const result = await api.queryValue({
        id: accumulate.id,
        data: parameters,
      })
      setResult(result)
    }
    return (
      <Drawer
        title="查询指标值"
        width={800}
        visible={drawerVisit}
        destroyOnClose={true}
        onClose={() => setDrawerVisit(false)}
      >
        <DisplayContent>
          <Descriptions column={1} size='small'>
            <Descriptions.Item label="指标名">
              <Typography.Text copyable={true}>{accumulate.name}</Typography.Text>
            </Descriptions.Item>
            <Descriptions.Item label="分组">
              <Typography.Text copyable={true} ellipsis={{tooltip: 'more'}}>{accumulate.groupKey}</Typography.Text>
            </Descriptions.Item>
            <Descriptions.Item label="备注">
              <Typography.Text copyable={false} ellipsis={{tooltip: 'more'}}>{accumulate.remark}</Typography.Text>
            </Descriptions.Item>
          </Descriptions>
        </DisplayContent>

        <DisplayContent title={'请输入参数'}>
          <ProForm
            onFinish={queryValue}
            submitter={{
              render: (props) => {
                return <Button type="primary" key="submit" onClick={() => props.form.submit()}>查询</Button>
              },
            }}
          >
            {keys.map(key => <ProFormText
              key={key}
              width="xl"
              name={key}
              label={key}
              placeholder="请输入"
              rules={[
                {required: true, message: '请输入'},
              ]}
            />)}

          </ProForm>
        </DisplayContent>

        <DisplayContent title={'查询结果'}>
          <JsonView code={result}/>
        </DisplayContent>
      </Drawer>
    )
  }

  const showQueryValue = async (id) => {
    const accumulate = await api.get({id})
    setAccumulate(accumulate)
    if (accumulate && accumulate.groupKey) {
      const keys = await apiScript.parse(accumulate.groupKey)
      setKeys(keys)
    }
    setDrawerVisit(true)
  }

  return {
    QueryValue,
    showQueryValue,
  }
}

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

import React, {useState} from 'react';
import ProForm, {ProFormDigit, ProFormSelect, ProFormText, ProFormTextArea} from "@ant-design/pro-form";
import FormTag from '@/components/FormTag'
import * as api from '@/api/accumulate';
import {Button} from "antd";
import GroovyEditor from "@/components/GroovyEditor";
import FormEvents from "./FormEvents";
import {groovyValidate} from '@/utils/utils'

export default ({
                  id, onSubmit = () => {
  }
                }) => {
  const [origin, setOrigin] = useState({})
  const nameValidate = async (rule, value) => {
    const {name = ''} = origin;
    if (value && value !== name) {
      const data = await api.exist({name: value})
      if (data) {
        return Promise.reject('名称已存在')
      }
    }
    return Promise.resolve()
  }

  return (
    <ProForm
      params={{id}}
      request={async (params) => {
        if (id) {
          const entity = await api.get(params)
          setOrigin(entity)
          return entity
        }
        return {}
      }}
      onValuesChange={(values) => console.log(values)}
      onFinish={onSubmit}
      submitter={{
        render: (props) => {
          return <Button type="primary" key="submit" onClick={() => props.form.submit()}>提交</Button>;
        },
      }}
    >

      <ProFormText
        width="xl"
        name="name"
        label="名称"
        placeholder="请输入名称"
        validateTrigger={'onBlur'}
        rules={[
          {required: true, message: '请输入名称'},
          {validator: nameValidate},
        ]}
      />

      <ProForm.Item
        name="events"
        label="绑定事件"
        rules={[
          {required: true, message: '请选择事件'},
        ]}>
        <FormEvents/>
      </ProForm.Item>

      <ProForm.Group>
        <ProFormText
          width="xl"
          name="groupKey"
          label="分组key"
          placeholder="请输入分组key"
          validateTrigger={'onBlur'}
          rules={[
            {required: true, message: '请输入分组key'},
            {validator: groovyValidate},
          ]}
          tooltip={'支持 Groovy List 结构'}
        />
      </ProForm.Group>

      <ProForm.Group>

        <ProFormSelect
          width="sm"
          name="aggFunction"
          label="聚合函数"
          params={{}}
          showSearch
          request={async ({keyWords}) => {
            const functions = await api.aggFunctions({name: keyWords})
            return functions.map(func => {
              return {
                label: func,
                value: func
              }
            })
          }}
          placeholder="请选择聚合函数"
          validateTrigger={'onBlur'}
          rules={[
            {required: true, message: '请选择聚合函数'},
          ]}
        />

        <ProFormText
          width="sm"
          name="aggKey"
          label="聚合key"
          placeholder="请输入聚合key"
          rules={[
            {validator: groovyValidate},
          ]}
        />
      </ProForm.Group>

      <ProFormSelect
        width="xl"
        name="windowType"
        label="窗口类型"
        valueEnum={{
          FIXED: '固定窗口',
          SLIDING: '滑动窗口',
        }}
        placeholder="请选择窗口类型"
        rules={[
          {required: true, message: '请选择窗口类型'},
        ]}
      />

      <ProForm.Group>
        <ProFormDigit
          label="时间窗口"
          name="timeWindow"
          width="sm"
          min={1}
          rules={[
            {required: true, message: '请输入时间窗口'},
          ]}
        />

        <ProFormSelect
          width="sm"
          name="windowUnit"
          label="窗口单位"
          params={{}}
          showSearch
          request={async ({keyWords}) => {
            const units = await api.timeUnits({name: keyWords})
            return units.map(unit => {
              return {
                label: unit,
                value: unit
              }
            })
          }}
          placeholder="请选择窗口单位"
          rules={[
            {required: true, message: '请选择窗口单位'},
          ]}
        />
      </ProForm.Group>
      <ProForm.Group>
        <ProFormDigit
          label="时间粒度"
          name="timeSpan"
          width="sm"
          min={1}
          rules={[
            {required: true, message: '请输入时间粒度'},
          ]}
        />

        <ProFormSelect
          width="sm"
          name="spanUnit"
          label="粒度单位"
          params={{}}
          showSearch
          request={async ({keyWords}) => {
            const units = await api.timeUnits({name: keyWords})
            return units.map(unit => {
              return {
                label: unit,
                value: unit
              }
            })
          }}
          placeholder="请选择粒度单位"
          rules={[
            {required: true, message: '请选择粒度单位'},
          ]}
        />
      </ProForm.Group>

      <ProForm.Item
        name="script"
        label="前置条件"
        tooltip={'groovy语法'}
        validateTrigger={'onBlur'}
        rules={[
          {validator: groovyValidate},
        ]}>
        <GroovyEditor/>
      </ProForm.Item>

      <ProForm.Group title={'其他信息'}/>

      <ProForm.Item
        name="tags"
        label="标签">
        <FormTag/>
      </ProForm.Item>

      <ProFormTextArea
        width="xl"
        name="remark"
        label="备注"
        placeholder="请输入备注"
      />

    </ProForm>
  );
}

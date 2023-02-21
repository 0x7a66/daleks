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
import ProForm, {ProFormRadio, ProFormText, ProFormTextArea} from "@ant-design/pro-form";
import FormTag from '@/components/FormTag'
import GroovyEditor from '@/components/GroovyEditor'
import * as api from '@/api/groupRule';
import FormAtomRule from "./FormAtomRule";
import {Button} from "antd";
import {groovyValidate} from "@/utils/utils";

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
          return entity;
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

      <ProFormRadio.Group
        name="state"
        label="状态"
        options={[
          {
            label: '有效',
            value: 'ENABLE',
          },
          {
            label: '无效',
            value: 'DISABLE',
          },
          {
            label: '测试',
            value: 'TEST',
          },
        ]}
        rules={[
          {required: true, message: '请选择状态'},
        ]}
      />

      <ProForm.Item
        name="script"
        label="前置条件"
        tooltip={'groovy语法，返回true/false'}
        validateTrigger={'onBlur'}
        rules={[
          {validator: groovyValidate},
        ]}>
        <GroovyEditor/>
      </ProForm.Item>

      <ProForm.Item
        name="atomRules"
        label="策略"
        tooltip={'拖拽排序，靠前则优先判断返回'}>
        <FormAtomRule/>
      </ProForm.Item>

      <ProFormTextArea
        width="xl"
        name="reply"
        label="话术"
        placeholder="请输入话术"
        tooltip={'命中规则时返回的话术，优先级：策略>策略组>事件'}
      />

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

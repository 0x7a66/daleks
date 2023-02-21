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
import ProForm, {ProFormText} from "@ant-design/pro-form";
import * as api from '@/api/tag';
import {Button} from "antd";
import FormColor from './FormColor';

export default ({id, onSubmit = () => {}}) => {
  const [origin, setOrigin] = useState({})

  const validate = async (rule, value) => {
    const {name = ''} = origin;
    if (value && value !== name) {
      const data = await api.exist({name: value})
      if (data) {
        return Promise.reject('标签已经存在')
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
          {validator: validate},
        ]}
      />
      <ProForm.Item
        width="xl"
        name="color"
        label="颜色"
        placeholder="请选择颜色"
        rules={[
          {required: true, message: '请选择颜色'},
        ]}
      >
        <FormColor />
      </ProForm.Item>

    </ProForm>
  );
}

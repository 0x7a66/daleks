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
import ProForm, {ProFormText, ProFormTextArea} from "@ant-design/pro-form";
import FormTag from '@/components/FormTag'
import GroovyEditor from '@/components/GroovyEditor'
import * as api from '@/api/functionConfig';
import {Button} from "antd";

export default ({
                  id, onSubmit = () => {
  }
                }) => {
  const [origin, setOrigin] = useState({})

  const nameValidate = async (rule, value) => {
    const {func = ''} = origin;
    if (value && value !== func) {
      const data = await api.exist({func: value})
      if (data) {
        return Promise.reject('函数名称已存在')
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
        name="func"
        label="函数名称"
        placeholder="请输入函数名称"
        validateTrigger={'onBlur'}
        rules={[
          {required: true, message: '请输入名称'},
          {validator: nameValidate},
        ]}
      />

      <ProForm.Item
        name="parameters"
        label="函数参数"
        tooltip={'groovy语法，Map结构'}>
        <GroovyEditor/>
      </ProForm.Item>

      <ProForm.Item
        name="result"
        label="返回结果">
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

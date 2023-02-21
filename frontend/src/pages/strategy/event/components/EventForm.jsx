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
import ProForm, {ProFormSelect, ProFormText, ProFormTextArea} from "@ant-design/pro-form";
import FormTag from '@/components/FormTag'
import * as api from '@/api/event';
import * as apiBusiness from '@/api/business';
import FormGroupRule from "./FormGroupRule";
import FormGrayGroup from "./FormGrayGroup";
import {Button} from "antd";

export default ({
                  id, onSubmit = () => {
  }
                }) => {

  const [origin, setOrigin] = useState({})

  const codeValidate = async (rule, value) => {
    const {code = ''} = origin;
    if (value && value !== code) {
      const data = await api.exist({code: value})
      if (data) {
        return Promise.reject('已存在')
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
        name="code"
        label="code"
        placeholder="请输入code"
        validateTrigger={'onBlur'}
        rules={[
          {required: true, message: '请输入code'},
          {validator: codeValidate},
        ]}
      />

      <ProFormText
        width="xl"
        name="name"
        label="名称"
        placeholder="请输入名称"
        rules={[
          {required: true, message: '请输入名称'},
        ]}
      />

      <ProFormSelect
        width="xl"
        name="businessCode"
        label="业务"
        params={{}}
        showSearch
        request={async ({keyWords}) => {
          const businesses = await apiBusiness.all({name: keyWords})
          return businesses.map(i => {
            return {
              label: i.name,
              value: i.code
            }
          })
        }}
        placeholder="请选择业务"
        rules={[
          {required: true, message: '请选择业务'},
        ]}
      />

      <ProFormTextArea
        width="xl"
        name="reply"
        label="话术"
        placeholder="请输入话术"
        tooltip={'命中规则时返回的话术，优先级：策略>策略组>事件'}
      />

      <ProForm.Item
        name="groupRules"
        label="策略组"
        tooltip={'拖拽排序，靠前则优先判断返回'}>
        <FormGroupRule/>
      </ProForm.Item>


      <ProForm.Item
        name="grayGroups"
        label="名单组">
        <FormGrayGroup/>
      </ProForm.Item>

      <ProFormTextArea
        width="xl"
        name="renameMapping"
        label="参数映射"
        rows='10'
        placeholder="请输入参数映射(json)"
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

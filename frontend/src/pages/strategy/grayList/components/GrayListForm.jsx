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

import React from 'react';
import ProForm, {ProFormDateTimePicker, ProFormSelect, ProFormTextArea} from "@ant-design/pro-form";
import GroovyEditor from '@/components/GroovyEditor'
import {Button} from "antd";
import * as api from "@/api/grayList";
import FormGrayGroup from "./FormGrayGroup";

export default ({
                  onSubmit = () => {
                  }, grayGroupId = ''
                }) => {

  return (
    <ProForm
      params={{}}
      request={async () => {
        return {groupId: grayGroupId}
      }}
      onValuesChange={(values) => console.log(values)}
      onFinish={onSubmit}
      submitter={{
        render: (props) => {
          return <Button type="primary" key="submit" onClick={() => props.form.submit()}>提交</Button>;
        },
      }}
    >

      <ProForm.Item
        name="groupId"
        label="名单组"
        rules={[
          {required: true, message: '请选择名单组'},
        ]}>
        <FormGrayGroup/>
      </ProForm.Item>

      <ProFormSelect
        width="xl"
        name="type"
        label="类型"
        valueEnum={{
          BLACK: '黑名单',
          WHITE: '白名单',
        }}
        placeholder="请选择类型"
        rules={[
          {required: true, message: '请选择类型'},
        ]}
      />
      <ProFormSelect
        width="xl"
        name="dimension"
        label="维度"
        params={{}}
        showSearch
        request={async ({keyWords}) => {
          const dimensions = await api.dimensions({name: keyWords})
          return dimensions.map(i => {
            return {
              label: i,
              value: i
            }
          })
        }}
        placeholder="请选择维度"
        rules={[
          {required: true, message: '请选择维度'},
        ]}
      />

      <ProForm.Item
        name="value"
        label="数据"
        tooltip={'支持批量，一行一个'}
        rules={[
          {required: true, message: '请输入数据'},
        ]}>
        <GroovyEditor height={200}/>
      </ProForm.Item>

      <ProFormDateTimePicker
        width="xl"
        name="startTime"
        label="生效时间"
        tooltip={'默认生效'}
      />

      <ProFormDateTimePicker
        width="xl"
        name="expireTime"
        label="过期时间"
        tooltip={'默认用不过期'}
      />

      <ProForm.Group title={'其他信息'}/>

      <ProFormTextArea
        width="xl"
        name="remark"
        label="备注"
        placeholder="请输入备注"
      />

    </ProForm>
  );
}

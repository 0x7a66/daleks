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
import ProCard from '@ant-design/pro-card';
import {message, Space} from "antd";
import {styleHeight} from "@/pages/style";
import {ArrowLeftOutlined} from "@ant-design/icons";
import {history} from 'umi';
import EventForm from "./components/EventForm";
import DocTitle from "@/components/DocTitle";
import * as api from '@/api/event';

export default () => {

  const onSubmit = async (record) => {
    const result = await api.add(record)
    if (result) {
      message.success('创建成功')
      history.goBack()
    }
  }

  return (
    <ProCard headerBordered title={
      <Space size={'middle'}>
        <a onClick={() => {
          history.goBack()
        }}>
          <ArrowLeftOutlined/>
        </a>
        <span>创建事件</span>
      </Space>
    } split={'vertical'}>
      <DocTitle title='创建事件'/>
      <ProCard style={styleHeight}>
        <EventForm onSubmit={onSubmit}/>
      </ProCard>
    </ProCard>
  );
}

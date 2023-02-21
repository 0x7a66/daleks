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
import {Button, Space} from "antd";
import {styleHeight} from "@/pages/style";
import VariableDetail from "./components/VariableDetail";
import {history} from "umi";
import {ArrowLeftOutlined, EditOutlined, CloudOutlined} from "@ant-design/icons";
import DocTitle from '@/components/DocTitle'
import UseQueryValue from "./components/QueryValue";

export default () => {
  const {location: {query: {id = 0}}} = history;
  const {QueryValue, showQueryValue} = UseQueryValue();
  return (
    <>
      <ProCard headerBordered title={
          <Space size={'middle'}>
            <a onClick={() => { history.goBack() }}>
              <ArrowLeftOutlined/>
            </a>
            <span>变量详情</span>
            <Button type='primary' onClick={() => {history.push(`/strategy/variable/update?id=${id}`)}} icon={<EditOutlined/>}>编辑</Button>
            <Button type='primary' onClick={() => {showQueryValue(id)}} icon={<CloudOutlined/>}>查值</Button>
          </Space>
      } split={'vertical'}>
        <DocTitle title='变量详情' />
        <ProCard style={styleHeight}>
          <VariableDetail id={id}/>
        </ProCard>
        <QueryValue />
      </ProCard>
    </>
  );
}

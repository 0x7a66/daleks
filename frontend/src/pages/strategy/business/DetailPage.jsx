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
import BusinessDetail from "@/pages/strategy/business/components/BusinessDetail";
import {history} from "umi";
import {ArrowLeftOutlined, EditOutlined} from "@ant-design/icons";
import DocTitle from '@/components/DocTitle'

export default () => {
  const {location: {query: {id = 0}}} = history;
  return (
    <ProCard headerBordered title={
        <Space size={'middle'}>
          <a onClick={() => { history.goBack() }}>
            <ArrowLeftOutlined/>
          </a>
          <span>业务详情</span>
          <Button type='primary' onClick={() => {history.push(`/strategy/business/update?id=${id}`)}} icon={<EditOutlined/>}>编辑</Button>
        </Space>
    } split={'vertical'}>
      <DocTitle title='业务详情' />
      <ProCard style={styleHeight}>
        <BusinessDetail id={id}/>
      </ProCard>

    </ProCard>
  );
}

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
import ProCard from '@ant-design/pro-card';
import {Button, Checkbox, Input} from 'antd';
import {styleHeight} from '@/pages/style';
import * as api from '@/api/analysis';
import ReactJson from 'react-json-view'

const {TextArea} = Input;

export default () => {
  const [value, setValue] = useState('{\n' +
    '  "UserId": "0x0005",\n' +
    '  "Event": "LOGIN"\n' +
    '}')
  const [tagTest, setTagTest] = useState(true)
  const [result, setResult] = useState({})

  const onChange = async () => {
    setTagTest(!tagTest)
  }

  const handleTest = async () => {
    if (value) {
      try {
        const result = await api.testEvent({tagTest: tagTest, data: JSON.parse(value)})
        setResult(result)
      } catch (e) {
        setResult(e)
      }
    }
  }

  return (
    <ProCard title={'模拟测试'} headerBordered split={'vertical'}>
      <ProCard style={styleHeight}>
        <Checkbox checked={tagTest} onChange={onChange}>添加Tag: TEST</Checkbox>
        <Button style={{float: 'right'}} type="primary" key="submit" onClick={handleTest}>测试</Button>
        <TextArea placeholder={'请输入请求json'} rows={20} value={value} onChange={(e) => setValue(e.target.value)}/>
      </ProCard>
      <ProCard title={'测试结果'} style={styleHeight}>
        {Object.keys(result).length > 0 ?
          <ReactJson name={false} displayDataTypes={false} indentWidth={2} src={result}/>
          : null}
      </ProCard>
    </ProCard>
  );
};

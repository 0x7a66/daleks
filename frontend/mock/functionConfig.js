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

import mockjs from 'mockjs';

const timestamp = () => {
  return new Date().getTime();
}
const json = () => {
  return JSON.stringify({
    "currency": "STAR",
    "tradeType": "P_T_000018",
    "payPlatform": "CLUB",
    "procedure": "DIRECT",
    "clientAppId": "30",
    "oCurrency": "STAR_DIAMOND",
    "payApp": "UNIVERSE_WECHAT_CLUB",
    "createTime": "1622192845836",
    "clientIp": "112.48.62.247",
    "Event": "NEW_EXCHANGE_CHECK",
  }, null, 2)
}

const result = () => {
  return JSON.stringify([
    {
      "text": "name1",
      "displayText": "备注1",
      "properties": [
        {
          "text": "property1",
          "displayText": "备注property1",
          "properties": [
            {
              "text": "p1",
              "displayText": "备注property1p1"
            },
            {
              "text": "p2",
              "displayText": "备注property2p2"
            },
          ]
        },
        {
          "text": "property2",
          "displayText": "备注property2"
        },
        {
          "text": "property2",
          "displayText": "备注property3"
        },
        {
          "text": "property3",
          "displayText": "备注property3"
        }
      ]
    },
    {
      "text": "name2",
      "displayText": "备注2",
      "properties": [
        {
          "text": "property1",
          "displayText": "p2备注property1"
        },
        {
          "text": "property2",
          "displayText": "p2备注property2"
        },
        {
          "text": "property2",
          "displayText": "p2备注property3"
        },
        {
          "text": "property3",
          "displayText": "p2备注property3"
        }
      ]
    }
  ], null, 2);
}

const list = (req, res) => {
  res.json({
    code: '0',
    msg: 'success',
    success: true,
    ...mockjs.mock({
      'result|10': [{
        'id|+1': 1,
        'func': '@word(3, 10)',
        'parameters': json,
        'result': result,
        'remark': '@cword(3, 10)',
        'ext': '@cword(3, 10)',
        'author': '@cname',
        'modifier': '@cname',
        'createTime': timestamp,
        'updateTime': timestamp,
        'tags|0-2': [{
          'id|+1': 1,
          'name': '@cword(3, 5)',
          'color': '@hex',
        }]
      }]
    })
  })
}
export default {
  'POST /api/function/config/search': (req, res) => {
    const max = 120;
    const current = req.body.page || 0;
    let pageSize = req.body.size || 10;
    if (pageSize > max) pageSize = max;
    const contentKey = `content|${pageSize}`;
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: mockjs.mock({
        [contentKey]: [{
          'id|+1': (current * pageSize) + 1,
          'func': '@word(3, 10)',
          'parameters': json,
          'result': result,
          'remark': '@cword(3, 10)',
          'ext': '@cword(3, 10)',
          'author': '@cname',
          'modifier': '@cname',
          'createTime': timestamp,
          'updateTime': timestamp,
          'tags|0-2': [{
            'id|+1': 1,
            'name': '@cword(3, 5)',
            'color': '@hex',
          }],
        }],
        'totalElements': max
      })
    })
  },
  'POST /api/function/config/get': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: mockjs.mock({
        'id': req.body.id || 1,
        'func': '@word(3, 10)',
        'parameters': json,
        'result': result,
        'remark': '@cword(3, 10)',
        'ext': '@cword(3, 10)',
        'author': '@cname',
        'modifier': '@cname',
        'createTime': timestamp,
        'updateTime': timestamp,
        'tags|0-2': [{
          'id|+1': 1,
          'name': '@cword(3, 5)',
          'color': '@hex',
        }]
      })
    })
  },
  'POST /api/function/config/getByFunc': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: mockjs.mock({
        'id': req.body.id || 1,
        'func': '@word(3, 10)',
        'parameters': json,
        'result': result,
        'remark': '@cword(3, 10)',
        'ext': '@cword(3, 10)',
        'author': '@cname',
        'modifier': '@cname',
        'createTime': timestamp,
        'updateTime': timestamp,
        'tags|0-2': [{
          'id|+1': 1,
          'name': '@cword(3, 5)',
          'color': '@hex',
        }]
      })
    })
  },
  'POST /api/function/config/all': list,
  'POST /api/function/config/queryByIds': list,

  'POST /api/function/config/add': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: true,
    })
  },
  'POST /api/function/config/update': {
    code: '0',
    msg: 'success',
    success: true,
    result: true,
  },
  'POST /api/function/config/delete': {
    code: '0',
    msg: 'success',
    success: true,
    result: true,
  },
  'POST /api/function/config/exist': {
    code: '0',
    msg: 'success',
    success: true,
    result: false,
  },
};

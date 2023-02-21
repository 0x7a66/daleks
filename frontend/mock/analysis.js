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
  })
}

const script = () => {
  return 'UserId == 123456 && abc > 1'
}

export default {
  'POST /api/analysis/riskLog/search': (req, res) => {
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
          'riskId': '@string("lower", 32)',
          'costTime': '@natural(10, 100)',
          'action': {
            "Event": "RECHARGE_CHECK",
            "UserId": "210890862388260001",
            "DeviceId": "device1939472809487298372371",
            "ClientIp": "@ip",
            "Mobile": "13033111385",
            "AppId": "10",
            "RequestTime": 1622192845836,
            "SourceFrom": "rechargeService",
            "Async": "false",
            "Tags": "",
          },
          'context': {
            "amount": "100",
            "currency": "RMB",
            "tradeType": "P_T_000018",
            "payPlatform": "CLUB",
            "procedure": "DIRECT",
            "oCurrency": "STAR_DIAMOND",
          },
          'result': {
            'level': '@pick(["PASS", "REVIEW", "REJECT"])',
            'ruleId|1-100': 1,
            'ruleName': '@cword(5, 12)',
          },
          'ruleResults':
            [
              {
                "type": "GROUP_RULE",
                "id": 1,
                "name": "登录检查策略组",
                "ruleState": "ENABLE",
                "result": "true"
              },
              {
                "type": "ATOM_RULE",
                "id": 1,
                "name": "登录次数策略",
                "ruleState": "ENABLE",
                "result": "PASS"
              },
              {
                "type": "ATOM_RULE",
                "id": 2,
                "name": "登录地域限制策略",
                "ruleState": "ENABLE",
                "result": "PASS"
              }
            ]
        }],
        'totalElements': max
      })
    })
  },
  'POST /api/analysis/test/event': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: mockjs.mock({
        'level': '@pick(["PASS", "REVIEW", "REJECT"])',
        'ruleId|1-100': 1,
        'ruleName': '@cword(5, 12)',
      })
    })
  },
};

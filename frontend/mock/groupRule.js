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

const list = (req, res) => {
  res.json({
    code: '0',
    msg: 'success',
    success: true,
    ...mockjs.mock({
      'result|6': [{
        'id|+1': 1,
        'name': '@cword(3, 10)',
        'script': script,
        'state': '@pick(["ENABLE", "DISABLE", "TEST"])',
        'reply': '@cword(3, 15)',
        'atomRules': '1,2,3,4,5,6',
        'returnJson': json,
        'remark': '@cword(3, 10)',
        'ext': '@cword(3, 10)',
        'author': '@cname',
        'modifier': '@cname',
        'createTime': timestamp,
        'updateTime': timestamp,
      }]
    })
  })
}

const script = () => {
  return `if(
  payPlatform != "TOP"
  && !app.contains('ALIPAY_WINDOW')
  && payChannel != "PING_AN_BANK"
  && userAuthRemoteData.status==1
  && userAuthRemoteData.age>=60
  && !isExposeUnderAge.value
  && (userAuthRemoteData.certificateType==0 || userAuthRemoteData.certificateType==1 || userAuthRemoteData.certificateType==6)
  && userFaceAuthData.faceAuthenSwitch == true
  && (riskTraceId == null || riskTraceId == '')
  && (faceResult.passTime == null || (System.currentTimeMillis() - faceResult.passTime.getTime()) > 120000L)
) {
  if(payPlatform == 'ANDROID' || payPlatform == 'IOS') {
    if((clientAppId == '10' && versionCompareBixin > -1) || (clientAppId=='20' && versionCompareYuer > -1) || (clientAppId=='30' && versionCompareUniverse > -1)) {
      return "REVIEW"
    }
  } else {
    return "REVIEW"
  }
}`
}

export default {
  'POST /api/group/rule/search': (req, res) => {
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
          'name': '@cword(3, 10)',
          'script': script,
          'state': '@pick(["ENABLE", "DISABLE", "TEST"])',
          'reply': '@cword(3, 15)',
          'atomRules': '1,2,3,4,5,6',
          'returnJson': json,
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
  'POST /api/group/rule/get': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: mockjs.mock({
        'id': req.body.id || 1,
        'name': '@cword(3, 10)',
        'script': script,
        'state': '@pick(["ENABLE", "DISABLE", "TEST"])',
        'reply': '@cword(3, 15)',
        'atomRules': '1,2,3,4,5,6',
        'returnJson': json,
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
  'POST /api/group/rule/queryByIds': list,
  'POST /api/group/rule/queryByAtomRuleId': list,
  'POST /api/group/rule/queryByVariableId': list,
  'POST /api/group/rule/queryByAccumulateId': list,
  'POST /api/group/rule/queryWithAtomRulesByEventId': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      ...mockjs.mock({
        'result|6': [{
          'id|+1': 1,
          'name': '@cword(3, 10)',
          'state': '@pick(["ENABLE", "DISABLE", "TEST"])',
          'remark': '@cword(3, 10)',
          'ext': '@cword(3, 10)',
          'author': '@cname',
          'modifier': '@cname',
          'createTime': timestamp,
          'updateTime': timestamp,
          'atomRuleList|7': [{
            'id|+1': 1,
            'name': '@cword(3, 10)',
            'state': '@pick(["ENABLE", "DISABLE", "TEST"])',
            'remark': '@cword(3, 10)',
            'ext': '@cword(3, 10)',
            'author': '@cname',
            'modifier': '@cname',
            'createTime': timestamp,
            'updateTime': timestamp,
          }]
        }]
      })
    })
  },
  'POST /api/group/rule/add': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: true,
    })
  },
  'POST /api/group/rule/update': {
    code: '0',
    msg: 'success',
    success: true,
    result: true,
  },
  'POST /api/group/rule/delete': {
    code: '0',
    msg: 'success',
    success: true,
    result: true,
  },
  'POST /api/group/rule/exist': {
    code: '0',
    msg: 'success',
    success: true,
    result: false,
  },
};

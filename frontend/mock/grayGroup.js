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
export default {
  'POST /api/gray/group/search': (req, res) => {
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
          'category': '@string("upper", 5, 8)',
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
  'POST /api/gray/group/get': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: mockjs.mock({
        'id': req.body.id || 1,
        'name': '@cword(3, 10)',
        'category': '@string("upper", 5, 8)',
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
  'POST /api/gray/group/queryByIds': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      ...mockjs.mock({
        'result|6': [{
          'id|+1': 1,
          'name': '@cword(3, 10)',
          'category': '@string("upper", 5, 8)',
          'remark': '@cword(3, 10)',
          'ext': '@cword(3, 10)',
          'author': '@cname',
          'modifier': '@cname',
          'createTime': timestamp,
          'updateTime': timestamp,
        }]
      })
    })
  },
  'POST /api/gray/group/queryByEventId': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      ...mockjs.mock({
        'result|3': [{
          'id|+1': 1,
          'name': '@cword(3, 10)',
          'remark': '@cword(3, 10)',
          'ext': '@cword(3, 10)',
          'author': '@cname',
          'modifier': '@cname',
          'createTime': timestamp,
          'updateTime': timestamp
        }]
      })
    })
  },
  'POST /api/gray/group/add': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: true,
    })
  },
  'POST /api/gray/group/update': {
    code: '0',
    msg: 'success',
    success: true,
    result: true,
  },
  'POST /api/gray/group/delete': {
    code: '0',
    msg: 'success',
    success: true,
    result: true,
  },
  'POST /api/gray/group/exist': {
    code: '0',
    msg: 'success',
    success: true,
    result: false,
  },
};

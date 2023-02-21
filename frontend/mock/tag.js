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

export default {
  'POST /api/tag/search': (req, res) => {
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
          'color': '@hex',
        }],
        'totalElements': max
      })
    })
  },
  'POST /api/tag/get': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: mockjs.mock({
        'id|1-10': 1,
        'name': '@cword(3, 10)',
        'color': '@hex',
      }),
    })
  },
  'POST /api/tag/all': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      ...mockjs.mock({
        'result|10': [{
          'id|+1': 1,
          'name': '@cword(3, 10)',
          'color': '@hex',
        }],
      })
    })
  },
  'POST /api/tag/add': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: true,
    })
  },
  'POST /api/tag/update': {
    code: '0',
    msg: 'success',
    success: true,
    result: true,
  },
  'POST /api/tag/delete': {
    code: '0',
    msg: 'success',
    success: true,
    result: true,
  },
  'POST /api/tag/exist': {
    code: '0',
    msg: 'success',
    success: true,
    result: false,
  },
};

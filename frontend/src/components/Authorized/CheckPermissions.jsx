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
import {CURRENT} from './renderAuthorize'; // eslint-disable-next-line import/no-cycle
import PromiseRender from './PromiseRender';

/**
 * 通用权限检查方法 Common check permissions method
 *
 * @param { 权限判定 | Permission judgment } authority
 * @param { 你的权限 | Your permission description } currentAuthority
 * @param { 通过的组件 | Passing components } target
 * @param { 未通过的组件 | no pass components } Exception
 */
const checkPermissions = (authority, currentAuthority, target, Exception) => {
  // 没有判定权限.默认查看所有
  // Retirement authority, return target;
  if (!authority) {
    return target;
  } // 数组处理

  if (Array.isArray(authority)) {
    if (Array.isArray(currentAuthority)) {
      if (currentAuthority.some((item) => authority.includes(item))) {
        return target;
      }
    } else if (authority.includes(currentAuthority)) {
      return target;
    }

    return Exception;
  } // string 处理

  if (typeof authority === 'string') {
    if (Array.isArray(currentAuthority)) {
      if (currentAuthority.some((item) => authority === item)) {
        return target;
      }
    } else if (authority === currentAuthority) {
      return target;
    }

    return Exception;
  } // Promise 处理

  if (authority instanceof Promise) {
    return <PromiseRender ok={target} error={Exception} promise={authority}/>;
  } // Function 处理

  if (typeof authority === 'function') {
    const bool = authority(currentAuthority); // 函数执行后返回值是 Promise

    if (bool instanceof Promise) {
      return <PromiseRender ok={target} error={Exception} promise={bool}/>;
    }

    if (bool) {
      return target;
    }

    return Exception;
  }

  throw new Error('unsupported parameters');
};

export {checkPermissions};

function check(authority, target, Exception) {
  return checkPermissions(authority, CURRENT, target, Exception);
}

export default check;

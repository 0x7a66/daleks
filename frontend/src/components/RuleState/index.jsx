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

import {Tag} from "antd";
import React from "react";

export default ({state}) => {
  if (state === 'TEST') {
    return (
      <Tag color='#108ee9'>测试</Tag>
    )
  }
  if (state === 'ENABLE') {
    return (
      <Tag color='#87d068'>有效</Tag>
    )
  }
  if (state === 'DISABLE') {
    return (
      <Tag color='#f50'>无效</Tag>
    )
  }
  return null
}
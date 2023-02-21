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

import moment from 'moment';
import * as api from "@/api/script";

export const dateFormat = (timestamp, format = 'YYYY-MM-DD HH:mm:ss') => {
  if(!timestamp) {
    return '';
  }
  return moment(timestamp).format(format)
}
export const containEntity = (arrays = [], item) => {
  const has = arrays.filter(i => i.id === item.id);
  return has && has.length > 0;
}
export const contain = (arrays = [], item) => {
  const has = arrays.filter(i => i === item);
  return has && has.length > 0;
}

export const groovyValidate = async (rule, value) => {
  if(value) {
    const data = await api.check(value)
    if (data) {
      return Promise.reject(data)
    }
  }
  return Promise.resolve()
}

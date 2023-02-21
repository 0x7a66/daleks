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

import request from '@/utils/request'

export const search = payload => request.post('/api/accumulate/search', {data: payload})
export const get = payload => request.post('/api/accumulate/get', {data: payload})
export const add = payload => request.post('/api/accumulate/add', {data: payload})
export const update = payload => request.post('/api/accumulate/update', {data: payload})
export const del = payload => request.post('/api/accumulate/delete', {data: payload})
export const exist = payload => request.post('/api/accumulate/exist', {data: payload})
export const queryByIds = payload => request.post('/api/accumulate/queryByIds', {data: payload})
export const aggFunctions = () => request.post('/api/accumulate/aggFunctions')
export const timeUnits = () => request.post('/api/accumulate/timeUnits')
export const queryByVariableId = payload => request.post('/api/accumulate/queryByVariableId', {data: payload})
export const queryByAtomRuleId = payload => request.post('/api/accumulate/queryByAtomRuleId', {data: payload})
export const queryByGroupRuleId = payload => request.post('/api/accumulate/queryByGroupRuleId', {data: payload})
export const queryDependencyById = payload => request.post('/api/accumulate/queryDependencyById', {data: payload})
export const queryValue = payload => request.post('/api/accumulate/queryValue', {data: payload})

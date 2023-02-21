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

export const search = payload => request.post('/api/event/search', {data: payload})
export const get = payload => request.post('/api/event/get', {data: payload})
export const add = payload => request.post('/api/event/add', {data: payload})
export const update = payload => request.post('/api/event/update', {data: payload})
export const del = payload => request.post('/api/event/delete', {data: payload})
export const exist = payload => request.post('/api/event/exist', {data: payload})
export const queryByIds = payload => request.post('/api/event/queryByIds', {data: payload})
export const queryByBusinessId = payload => request.post('/api/event/queryByBusinessId', {data: payload})
export const queryByAtomRuleId = payload => request.post('/api/event/queryByAtomRuleId', {data: payload})
export const queryByGroupRuleId = payload => request.post('/api/event/queryByGroupRuleId', {data: payload})
export const queryByAccumulateId = payload => request.post('/api/event/queryByAccumulateId', {data: payload})
export const build = payload => request.post('/api/event/build', {data: payload})

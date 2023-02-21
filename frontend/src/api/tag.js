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

import request from "@/utils/request";

export const all = (configType = '') => request.post('/api/tag/all', {params: {configType}})
export const search = (payload) => request.post('/api/tag/search', {data: payload})
export const get = payload => request.post('/api/tag/get', {data: payload})
export const add = payload => request.post('/api/tag/add', {data: payload})
export const update = payload => request.post('/api/tag/update', {data: payload})
export const del = payload => request.post('/api/tag/delete', {data: payload})
export const exist = payload => request.post('/api/tag/exist', {data: payload})

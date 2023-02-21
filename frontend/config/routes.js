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

export default [
  {
    path: '/',
    component: '../layouts/BlankLayout',
    routes: [
      {
        path: '/',
        redirect: '/strategy/event',
      },
      {
        path: '/',
        component: '../layouts/BasicLayout',
        routes: [
          {
            path: '/strategy',
            name: '策略管理',
            icon: 'dashboard',
            routes: [
              {
                path: '/',
                redirect: '/strategy/event',
              },
              {
                name: '业务管理',
                path: '/strategy/business',
                component: './strategy/business/IndexPage',
              },
              {
                path: '/strategy/business/add',
                component: './strategy/business/AddPage',
              },
              {
                path: '/strategy/business/update',
                component: './strategy/business/UpdatePage',
              },
              {
                path: '/strategy/business/detail',
                component: './strategy/business/DetailPage',
              },
              {
                name: '事件管理',
                path: '/strategy/event',
                component: './strategy/event/IndexPage',
              },
              {
                path: '/strategy/event/detail',
                component: './strategy/event/DetailPage',
              },
              {
                path: '/strategy/event/add',
                component: './strategy/event/AddPage',
              },
              {
                path: '/strategy/event/update',
                component: './strategy/event/UpdatePage',
              },
              {
                name: '策略组管理',
                path: '/strategy/group/rule',
                component: './strategy/groupRule/IndexPage',
              },
              {
                path: '/strategy/group/rule/detail',
                component: './strategy/groupRule/DetailPage',
              },
              {
                path: '/strategy/group/rule/add',
                component: './strategy/groupRule/AddPage',
              },

              {
                path: '/strategy/group/rule/update',
                component: './strategy/groupRule/UpdatePage',
              },
              {
                name: '策略管理',
                path: '/strategy/atom/rule',
                component: './strategy/atomRule/IndexPage',
              },
              {
                path: '/strategy/atom/rule/detail',
                component: './strategy/atomRule/DetailPage',
              },
              {
                path: '/strategy/atom/rule/add',
                component: './strategy/atomRule/AddPage',
              },
              {
                path: '/strategy/atom/rule/update',
                component: './strategy/atomRule/UpdatePage',
              },
              {
                name: '变量管理',
                path: '/strategy/variable',
                component: './strategy/variable/IndexPage',
              },
              {
                path: '/strategy/variable/detail',
                component: './strategy/variable/DetailPage',
              },

              {
                path: '/strategy/variable/add',
                component: './strategy/variable/AddPage',
              },
              {
                path: '/strategy/variable/update',
                component: './strategy/variable/UpdatePage',
              },
              {
                name: '函数管理',
                path: '/strategy/function/config',
                component: './strategy/functionConfig/IndexPage',
              },
              {
                path: '/strategy/function/config/detail',
                component: './strategy/functionConfig/DetailPage',
              },

              {
                path: '/strategy/function/config/add',
                component: './strategy/functionConfig/AddPage',
              },
              {
                path: '/strategy/function/config/update',
                component: './strategy/functionConfig/UpdatePage',
              },
              {
                name: '指标管理',
                path: '/strategy/accumulate',
                component: './strategy/accumulate/IndexPage',
              },
              {
                path: '/strategy/accumulate/detail',
                component: './strategy/accumulate/DetailPage',
              },
              {
                path: '/strategy/accumulate/add',
                component: './strategy/accumulate/AddPage',
              },
              {
                path: '/strategy/accumulate/update',
                component: './strategy/accumulate/UpdatePage',
              },
              {
                name: '名单组管理',
                path: '/strategy/gray/group',
                component: './strategy/grayGroup/IndexPage',
              },
              {
                path: '/strategy/gray/group/detail',
                component: './strategy/grayGroup/DetailPage',
              },
              {
                path: '/strategy/gray/group/add',
                component: './strategy/grayGroup/AddPage',
              },
              {
                path: '/strategy/gray/group/update',
                component: './strategy/grayGroup/UpdatePage',
              },
              {
                name: '名单管理',
                path: '/strategy/gray/list',
                component: './strategy/grayList/IndexPage',
              },
              {
                path: '/strategy/gray/list/detail',
                component: './strategy/grayList/DetailPage',
              },
              {
                path: '/strategy/gray/list/add',
                component: './strategy/grayList/AddPage',
              },
              {
                path: '/strategy/gray/list/update',
                component: './strategy/grayList/UpdatePage',
              },
              {
                name: '标签管理',
                path: '/strategy/tag',
                component: './strategy/tag/IndexPage',
              },
              {
                path: '/strategy/tag/add',
                component: './strategy/tag/AddPage',
              },
              {
                path: '/strategy/tag/update',
                component: './strategy/tag/UpdatePage',
              },
            ],
          },
          {
            path: '/analysis',
            name: '分析工具',
            icon: 'dashboard',
            routes: [
              {
                path: '/',
                redirect: '/analysis/log',
              },
              {
                name: '风控历史',
                path: '/analysis/log',
                component: './analysis/riskLog/IndexPage',
              },
              {
                name: '仿真模拟',
                path: '/analysis/test/mock',
                component: './analysis/test/IndexPage',
              },
            ],
          },
        ],
      },
    ],
  },
];

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

use risk;

INSERT INTO `risk_accumulate` (`name`, `group_key`, `agg_key`, `agg_function`, `time_window`, `window_unit`,
                               `time_span`, `span_unit`, `window_type`, `script`, `events`, `remark`)
VALUES ('loginTimes1Min', 'UserId', '', 'COUNT', 1, 'MINUTES', 1, 'MINUTES', 'FIXED', '', '12', '1分钟登录次数');


INSERT INTO `risk_atom_rule` (`name`, `script`, `state`, `reply`)
VALUES ('登录次数策略', 'loginTimes1Min > 5 && !whiteListHit', 'ENABLE', '登录次数过多，请稍后重试'),
       ('登录地域限制策略',
        'if(ClientIpDetail != null && ClientIpDetail.country != null && ClientIpDetail.country.contains(\'中国\')) {\n  return \"REJECT\"\n}',
        'ENABLE', '暂不支持外国登录');


INSERT INTO `risk_business` (`name`, `code`, `remark`)
VALUES ('支付', 'PAYMENT', ''),
       ('风控', 'RISK', '风控业务'),
       ('测试', 'RECHARGE_POST_SUCCESS', ''),
       ('用户', 'USER', '');


INSERT INTO `risk_event` (`name`, `code`, `business_code`, `group_rules`, `gray_groups`)
VALUES ('充值', 'RECHARGE', 'PAYMENT', '2', ''),
       ('交易', 'TRADE', 'PAYMENT', '', ''),
       ('提现', 'WITHDRAW', 'PAYMENT', '', ''),
       ('登录', 'LOGIN', 'USER', '1', '2,1');


INSERT INTO `risk_function_config` (`func`, `parameters`, `result`, `remark`)
VALUES ('whiteList', '[groupId:groupId, dimension:dimension, value:value]', '', '是否命中白名单'),
       ('blackList', '[groupId:groupId, dimension:dimension, value:value]', '', '是否命中黑名单');


INSERT INTO `risk_gray_group` (`name`, `category`, `remark`)
VALUES ('全局名单', 'system', ''),
       ('登录名单组', 'user', ''),
       ('限额充值名单组', 'user', ''),
       ('限额交易名单组', 'user', '');


INSERT INTO `risk_gray_list` (`type`, `dimension`, `_value`, `start_time`, `expire_time`, `group_id`, `remark`)
VALUES ('WHITE', 'USER_ID', '0x0001', '2023-01-01 00:00:00', NULL, 1, '测试'),
       ('BLACK', 'USER_ID', '0x0002', '2023-01-01 00:00:00', '2023-02-01 00:00:00', 1, ''),
       ('BLACK', 'USER_ID', '0x0003', '2023-01-01 00:00:00', NULL, 1, '');


INSERT INTO `risk_group_rule` (`name`, `script`, `state`, `reply`, `atom_rules`, `return_json`)
VALUES ('登录检查策略组', '', 'ENABLE', '请稍后重试', '1,2', ''),
       ('充值检查策略组', '', 'ENABLE', '', '', ''),
       ('交易检查策略组', '', 'ENABLE', '', '', ''),
       ('提现检查策略组', '', 'ENABLE', '', '', '');


INSERT INTO `risk_variable` (`name`, `func`, `parameters`, `remark`)
VALUES ('whiteListHit', 'whiteList', '[groupId:1, dimension:\"USER_ID\", value:UserId]', '是否命中全局白名单'),
       ('blackListHit', 'blackList', '[groupId:1, dimension:\"USER_ID\", value:UserId]', '是否命中全局黑名单');






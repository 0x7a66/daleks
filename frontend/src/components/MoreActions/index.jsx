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

import {Button, Dropdown} from "antd";
import React from "react";
import {MoreOutlined} from "@ant-design/icons";
import Menu from "antd/es/menu";


export default (props) => {
  const {menu = []} = props;
  const handleMenuClick = ({key, domEvent}) => {
    domEvent.stopPropagation();
    menu.map(m => {
      if (m.key === key) {
        m.onClick();
      }
    })
  };
  return (
    <Dropdown overlay={
      <Menu onClick={handleMenuClick}>
        {menu.map(m =>
          <Menu.Item key={m.key}>
            &nbsp;{m.icon} {m.text} &nbsp;&nbsp;
          </Menu.Item>
        )}
      </Menu>
    } trigger={['click']}>
      <Button type='text' shape={'circle'} icon={<MoreOutlined/>} onClick={e => e.stopPropagation()}/>
    </Dropdown>
  )
}

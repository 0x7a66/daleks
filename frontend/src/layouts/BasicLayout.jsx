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

/**
 * Ant Design Pro v4 use `@ant-design/pro-layout` to handle Layout.
 *
 * @see You can view component api by: https://github.com/ant-design/ant-design-pro-layout
 */
import ProLayout from '@ant-design/pro-layout';
import {UserOutlined} from "@ant-design/icons";
import React, {useEffect, useMemo, useRef} from 'react';
import {history, Link} from 'umi';
import {Avatar, Button, Result} from 'antd';
import Authorized from '@/utils/Authorized';
import {getMatchMenu} from '@umijs/route-utils';
import logo from '../assets/logo.svg';
import './index.less'
import defaultSettings from '../../config/defaultSettings';

const noMatch = (
  <Result
    status={403}
    title="403"
    subTitle="Sorry, you are not authorized to access this page."
    extra={
      <Button type="primary">
        <Link to="/user/login">Go Login</Link>
      </Button>
    }
  />
);

/** Use Authorized check all menu item */
const menuDataRender = (menuList) =>
  menuList.map((item) => {
    const localItem = {
      ...item,
      children: item.children ? menuDataRender(item.children) : undefined,
    };
    return Authorized.check(item.authority, localItem, null);
  });

const BasicLayout = (props) => {
  const {
    children,
    staticContext,
    location = {
      pathname: '/',
    },
    route,
  } = props;
  const menuDataRef = useRef([]);
  useEffect(() => {

  }, []);
  /** Init variables */

  // get children authority

  const authorized = useMemo(
    () =>
      getMatchMenu(location.pathname || '/', menuDataRef.current).pop() || {
        authority: undefined,
      },
    [location.pathname],
  );

  return (
    <>
      <ProLayout
        logo={logo}
        {...defaultSettings}
        fixSiderbar
        fixedHeader
        location={location}
        route={route}
        onMenuHeaderClick={() => history.push('/')}
        menuItemRender={(menuItemProps, defaultDom) => {
          if (
            menuItemProps.isUrl ||
            !menuItemProps.path ||
            location.pathname === menuItemProps.path
          ) {
            return defaultDom;
          }

          return <Link to={menuItemProps.path}>{defaultDom}</Link>;
        }}
        // footerRender={() => <Footer/>}
        // menuDataRender={menuDataRender}
        // menuFooterRender={() => <Footer/>}
        breadcrumbRender={false}
        rightContentRender={() => (
          <div>
            <Avatar shape="circle" size="small" icon={<UserOutlined/>}/>
          </div>
        )}
      >
        <Authorized authority={authorized.authority} noMatch={noMatch}>
          <div style={{minHeight: 'calc(100vh - 172px)'}}>
            {children}
          </div>
        </Authorized>
      </ProLayout>
    </>
  );
};

export default BasicLayout;

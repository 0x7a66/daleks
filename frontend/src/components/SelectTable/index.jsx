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

import React, {useEffect, useState} from 'react';
import {Table} from 'antd';
import Search from 'antd/lib/input/Search';

export default ({
                  searchApi, columns, selected = [], onSelect = () => {
  }, type = 'checkbox', pageSizeOptions = [10, 20, 50, 100, 200]
                }) => {

  const [selectedRowKeys, setSelectedRowKeys] = useState(selected);
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [filters, setFilters] = useState({});
  const [sorter, setSorter] = useState({});
  const [value, setValue] = useState('');
  const [total, setTotal] = useState(0);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 20,
  });

  const handleTableChange = (pagination, filters, sorter) => {
    setPagination(pagination);
    setFilters(filters);
    setSorter(sorter);
  };

  useEffect(async () => {
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      field: sorter.field,
      order: sorter.order,
      filters,
      value,
    };
    setLoading(true)
    searchApi(params).then((data) => {
      setData(data.content || []);
      setTotal(data.totalElements || 0);
      setLoading(false)
    });
  }, [pagination, filters, sorter, value]);

  const doSelect = (records, selected) => {
    onSelect(records, selected)
    if (selected) {
      const contains = records.map(i => i.id).filter(x => !selectedRowKeys.includes(x))
      if (type === 'checkbox') {
        setSelectedRowKeys([...selectedRowKeys, ...contains])
      } else {
        setSelectedRowKeys([...contains])
      }
    } else {
      setSelectedRowKeys(selectedRowKeys.filter(x => !records.map(i => i.id).includes(x)))
    }
  }

  return (
    <>
      <Search
        style={{marginBottom: 8, width: 500}}
        placeholder='请输入搜索内容'
        allowClear
        onSearch={(value, e) => {
          e.preventDefault();
          setValue(value);
        }}
        defaultValue={value}
      />
      <Table
        className={'table-row-click'}
        size='small'
        rowKey={'id'}
        loading={loading}
        onRow={record => {
          return {
            onClick: () => {
              const index = selectedRowKeys.indexOf(record.id)
              if (index > -1) {
                doSelect([record], false)
              } else {
                doSelect([record], true)
              }
            },
          };
        }}
        rowSelection={{
          type: type,
          columnWidth: 40,
          selectedRowKeys: selectedRowKeys,
          onSelect: (record, selected, selectedRows, nativeEvent) => {
            doSelect([record], selected);
          },
          onSelectAll: (selected, selectedRows, changeRows) => {
            doSelect(changeRows, selected)
          }
        }}
        pagination={{
          ...pagination,
          total,
          pageSizeOptions,
          showLessItems: true,
          showTotal: (total) => `总共 ${total} 条`,
        }}
        scroll={{x: 'max-content'}}
        onChange={handleTableChange}
        columns={columns}
        dataSource={data}/>
    </>
  );

};

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
import {history} from 'umi';
import Search from 'antd/lib/input/Search';

const UseSearchTable = ({search, changeHistory = true}) => {

  const {location: {query}} = history;
  const pageQuery = changeHistory ? JSON.parse(query.q || '{}') : {};

  const [loading, setLoading] = useState(false);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [data, setData] = useState([]);
  const [filters, setFilters] = useState(pageQuery.filters || {});
  const [sorter, setSorter] = useState(pageQuery.sorter || {});
  const [value, setValue] = useState(pageQuery.value || '');
  const [total, setTotal] = useState(0);
  const [counter, setCounter] = useState(0);
  const [pagination, setPagination] = useState(pageQuery.pagination || {
    current: 1,
    pageSize: 20,
  });

  const handleTableChange = (pagination, filters, sorter) => {
    setPagination(pagination);
    setFilters(filters);
    setSorter(sorter);
  };

  const handleSearch = async () => {
    if (changeHistory) {
      history.replace(`?q=${JSON.stringify({
        pagination: {current: pagination.current, pageSize: pagination.pageSize},
        filters,
        sorter: {field: sorter.field, order: sorter.order},
        value,
      })}`);
    }

    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      field: sorter.field,
      order: sorter.order,
      filters,
      value
    }
    setLoading(true)
    search(params).then((data) => {
      setData(data.content || []);
      setTotal(data.totalElements || 0);
      if (selectedRowKeys.length > 0) {
        setSelectedRowKeys([]);
      }
      setLoading(false)
    });
  }

  useEffect(async () => {
    handleSearch();
  }, [pagination, filters, sorter, value, counter]);


  const SearchTable = (props) => {
    const {
      columns = [],
      onSelectChange = () => {
      },
      onRowClick = () => {
      },
      pageSizeOptions = [10, 20, 50, 100, 200],
      rowSelectEnable = true,
      rowKey = 'id',
      ...rest
    } = props;

    let _columns = columns.map(item => {
      for (let _key in filters) {
        if (item.dataIndex === _key) {
          item['defaultFilteredValue'] = filters[_key];
        }
      }
      if (item.dataIndex === sorter['field']) {
        item['defaultSortOrder'] = sorter['order'];
      }
      return item;
    });

    useEffect(() => {
      onSelectChange(selectedRowKeys);
    }, [selectedRowKeys]);

    return (
      <>
        <Search
          // style={{marginBottom: 8, width: searchWidth}}
          placeholder='请输入搜索内容'
          allowClear
          onSearch={(val) => {
            if (value === val) {
              handleSearch();
            } else {
              setValue(val);
            }

          }}
          defaultValue={value}
        />
        <Table
          className={'table-row-click'}
          size='small'
          loading={loading}
          rowKey={rowKey}
          onRow={record => {
            return {
              onClick: () => {
                setSelectedRowKeys([record.id]);
                onRowClick(record);
              },
            };
          }}
          rowSelection={rowSelectEnable ? {
            columnWidth: 40,
            selectedRowKeys: selectedRowKeys,
            onChange: (selectedRowKeys) => {
              setSelectedRowKeys(selectedRowKeys);
            },
          } : false}
          pagination={{
            ...pagination,
            total,
            pageSizeOptions,
            showSizeChanger: true,
            showLessItems: true,
            showTotal: (total) => `总共 ${total} 条`,
          }}
          scroll={{x: 'max-content'}}
          onChange={handleTableChange}
          columns={_columns}
          dataSource={data}
          {...rest}
        />
      </>
    );
  }

  const refresh = () => {
    setCounter(counter + 1)
  }

  return {
    SearchTable,
    refresh
  }
};

export default UseSearchTable;

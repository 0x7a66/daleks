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

export default {
  'POST /api/script/check': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: ''
    })
  },
  'POST /api/script/keywords/groovy': (req, res) => {
    res.json({
      code: '0',
      msg: 'success',
      success: true,
      result: [
        {
          "text": "name1",
          "displayText": "备注1",
          "properties": [
            {
              "text": "property1",
              "displayText": "备注property1",
              "properties": [
                {
                  "text": "p1",
                  "displayText": "备注property1p1"
                },
                {
                  "text": "p2",
                  "displayText": "备注property2p2"
                },
              ]
            },
            {
              "text": "property2",
              "displayText": "备注property2"
            },
            {
              "text": "property2",
              "displayText": "备注property3"
            },
            {
              "text": "property3",
              "displayText": "备注property3"
            }
          ]
        },
        {
          "text": "name2",
          "displayText": "备注2",
          "properties": [
            {
              "text": "property1",
              "displayText": "p2备注property1"
            },
            {
              "text": "property2",
              "displayText": "p2备注property2"
            },
            {
              "text": "property2",
              "displayText": "p2备注property3"
            },
            {
              "text": "property3",
              "displayText": "p2备注property3"
            }
          ]
        }
      ]
    })
  },
};

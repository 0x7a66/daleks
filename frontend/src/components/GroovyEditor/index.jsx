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

import React, {useEffect, useRef, useState} from "react";
import ReactDOM from 'react-dom'
import {UnControlled as CodeMirror} from 'react-codemirror2'
import * as api from '@/api/script'
import {Pos} from 'codemirror';
import 'codemirror/lib/codemirror.css';
import 'codemirror/lib/codemirror';
import 'codemirror/mode/groovy/groovy';
import 'codemirror/addon/edit/closebrackets';
import 'codemirror/addon/selection/active-line';
import 'codemirror/addon/hint/show-hint';
import 'codemirror/addon/hint/show-hint.css';
import 'codemirror/theme/idea.css';

let keywords = [];
let properties = {};

export default ({
                  value = '', onChange = () => {
  }, onBlur = () => {
  }
                }) => {
  const ref = useRef()
  const [initValue, setInitValue] = useState('')

  useEffect(async () => {
    setInitValue(value)
    const data = await api.keywords()

    if (data) {
      keywords = data;
      properties = buildProperties(data);
    }
  }, [])

  const buildProperties = (keywords = []) => {
    let properties = {};
    keywords.map(item => {
      if (item.properties && item.properties.length > 0) {
        properties[item.text] = item.properties
        properties = {...properties, ...buildProperties(item.properties)}
      }
    })
    return properties
  }

  const getToken = (e, cur) => {
    return e.getTokenAt(cur);
  }

  const getBeforeToken = (e, cur, token) => {
    return e.getTokenAt(Pos(cur.line, token.start))
  }

  const contains = (item, prefix) => {
    if (typeof item === 'string') {
      return item.startsWith(prefix)
    }
    return item.text.startsWith(prefix)
  }

  const filterKeywords = (prefix) => {
    return keywords.filter(item => contains(item, prefix))
  }

  const filterProperty = (property, prefix) => {
    const p = properties[property];
    if (properties[property] && properties[property].length > 0) {
      if (prefix) {
        return p.filter(item => contains(item, prefix))
      }
      return p;
    }
  }

  const render = (element, self, data) => {
    ReactDOM.render(
      <div style={{fontSize: '13px'}}>
        {data.text}
        <span style={{
          display: 'inline-block',
          fontSize: '12px',
          color: 'gray',
          marginLeft: '10px',
          maxWidth: '400px'
        }}>
        {data.displayText}
      </span>
      </div>, element
    )
  }

  const handleHint = (editor) => {
    const cur = editor.getCursor()
    const token = getToken(editor, cur)
    const beforeToken = getBeforeToken(editor, cur, token)
    if (token.string === '.') {
      showHint(editor, filterProperty(beforeToken.string), Pos(cur.line, token.start + 1), cur)
    } else {
      if (beforeToken.string === '.') {
        const propertyToken = getBeforeToken(editor, cur, beforeToken)
        if (propertyToken) {
          showHint(editor, filterProperty(propertyToken.string, token.string), Pos(cur.line, token.start), cur)
        }
      } else {
        showHint(editor, filterKeywords(token.string), Pos(cur.line, token.start), cur)
      }
    }

  }

  const showHint = (editor, list, from, to) => {
    if (list && list.length > 0) {
      const hintList = list.map(item => {
        if (typeof item === 'string') {
          return item;
        }
        return {
          ...item,
          render: render,
        }
      })
      const options = {
        hint: function () {
          return {from, to, list: hintList}
        }
      };
      editor.showHint(options);
    } else {
      editor.showHint();
    }
  }


  return (
    <div className={`pro-field pro-field-xl code-mirror-resize`}>
      <CodeMirror
        ref={ref}
        keywords={keywords}
        value={initValue}
        onChange={(editor, data, value) => onChange(value)}
        onBlur={() => onBlur()}
        options={{
          mode: 'groovy',
          theme: 'idea',
          tabSize: 2,
          lineNumbers: true,
          lineWrapping: true,
          autoCloseBrackets: true,
          styleActiveLine: true,
          hintOptions: {
            completeSingle: false
          }
        }}
        onInputRead={handleHint}
        onKeyUp={(editor, e) => {
          // backspace key
          if (e.keyCode === 8) {
            handleHint(editor)
          }
        }}
      />
    </div>
  )
}

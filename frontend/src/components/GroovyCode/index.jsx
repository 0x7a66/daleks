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

import {Prism as SyntaxHighlighter} from "react-syntax-highlighter";
import {prism as codeStyle} from "react-syntax-highlighter/dist/cjs/styles/prism";
import React from "react";
import style from './style.less'

export default ({code}) => {
  return (
    <div className={style.code}>
      <SyntaxHighlighter language="groovy" style={codeStyle} showLineNumbers={true} wrapLines={true}>
        {code ? code : ''}
      </SyntaxHighlighter>
    </div>
  )
}

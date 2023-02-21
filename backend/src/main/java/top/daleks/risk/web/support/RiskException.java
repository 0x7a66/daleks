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

package top.daleks.risk.web.support;

import java.io.Serializable;

/**
 * 系统异常
 */

public class RiskException extends RuntimeException implements Serializable {
    private String code;

    public String getCode() {
        return code;
    }

    public RiskException() {
        super();
    }

    public RiskException(ErrorMessage errorMessage) {
        this(errorMessage.getCode(), errorMessage.getMsg());
    }

    public RiskException(String code, String message) {
        super(message);
        this.code = code;
    }

    public RiskException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public RiskException(Throwable cause) {
        super(cause);
    }
}

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

package top.daleks.risk.access.bean;

import java.io.Serializable;

/**
 * 风控错误
 */
public class RiskError implements Serializable {

    /**
     * 系统错误:SYS-
     */
    public final static RiskError SYSTEM_ERROR = new RiskError("SYS-000000", "系统错误");
    /**
     * 服务错误:SRV-
     */
    public final static RiskError SERVICE_ERROR = new RiskError("SRV-000000", "请求处理失败");
    public final static RiskError PARAM_ERROR = new RiskError("SRV-001000", "客户端参数错误");
    public final static RiskError PARAM_IS_NULL = new RiskError("SRV-001001", "缺少必传参数");
    public final static RiskError EVENT_NOT_EXIST = new RiskError("SRV-001002", "指定的Event不存在");
    public final static RiskError REQUEST_TIMEOUT = new RiskError("SRV-002001", "请求处理超时");
    public final static RiskError ENGINE_ERROR = new RiskError("SRV-002002", "引擎识别失败");

    private String code;
    private String message;

    public RiskError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return code + ":" + message;
    }
}

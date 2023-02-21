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

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 接口返回值包装类型
 */
@Setter
@Getter
public class Response implements Serializable {

    private static final String SUCCESS_CODE = "0";
    private static final String SUCCESS_MSG = "success";

    private Boolean success;
    private String code;
    private String msg;
    private Object result;

    public Response(Boolean success, String code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    public Response(Boolean success, String code, String msg, Object result) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public static Response success() {
        return success(null);
    }

    public static Response success(Object data) {
        return new Response(true, SUCCESS_CODE, SUCCESS_MSG, data);
    }

    public static Response error(RiskException e) {
        return error(e.getCode(), e.getMessage());
    }

    public static Response error(ErrorMessage errorMessage) {
        return error(errorMessage, null);
    }

    public static Response error(ErrorMessage errorMessage, Object data) {
        return error(errorMessage.getCode(), errorMessage.getMsg(), data);
    }

    public static Response error(String code, String msg) {
        return error(code, msg, null);
    }

    public static Response error(String code, String msg, Object data) {
        return new Response(false, code, msg, data);
    }

}

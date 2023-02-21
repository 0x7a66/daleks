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

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理
 */
@Slf4j
@ControllerAdvice
public class ExceptionResolver {

    /**
     * 自定义异常
     */
    @ResponseBody
    @ExceptionHandler(value = RiskException.class)
    public Response customExceptionResolver(RiskException e) {
        log.debug("自定义异常出错 code: {}, msg: {}", e.getCode(), e.getMessage());
        return Response.error(e);
    }

    /**
     * 表单校验异常, 用于 bean validation 异常
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Response validationExceptionResolver(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();

        log.debug("MethodArgumentNotValidException: {}", error);

        return Response.error(ErrorMessage.FORM_ERROR.getCode(),
                error.getObjectName() + " " + error.getField() + " " + error.getDefaultMessage(), null);
    }

    /**
     * 表单校验异常, 用于 form 参数绑定异常
     */
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public Response validationExceptionResolver(BindException e) {
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();

        log.debug("BindException: [{}]", error);

        return Response.error(ErrorMessage.FORM_ERROR.getCode(),
                error.getObjectName() + " " + error.getField() + " " + error.getDefaultMessage(), null);

    }

    /**
     * 系统异常
     */
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public Response exceptionResolver(RuntimeException e) {
        log.warn("系统异常", e);
        return Response.error(ErrorMessage.SYSTEM_ERROR.getCode(), e.getMessage());
    }
}

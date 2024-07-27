/**
 * @Author: zhangzheng
 */
package com.company.common.exception;

import com.company.common.api.vo.Result;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<String> errorMessages = new ArrayList<>();
        for (ObjectError error : allErrors) {
            errorMessages.add(error.getDefaultMessage());
        }
        return Result.error(String.join(",", errorMessages));
    }
}

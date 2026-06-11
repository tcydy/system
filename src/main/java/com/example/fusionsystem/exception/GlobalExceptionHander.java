package com.example.fusionsystem.exception;


import com.example.fusionsystem.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHander {
    /*如果抛出的是serviceexception,则调用该方法
    * @param se 业务异常
    *@return Result*/
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result handle(ServiceException se){
      return Result.error(se.getCode(),se.getMessage());
    }

}

package com.mychat_sys.controller;

import com.mychat_sys.utils.myJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@ResponseBody
@Slf4j
public class myExceptionHandler {

    @ExceptionHandler
    public myJSONResult allExceptionHandler(HttpServletRequest request,
                                      Exception exception) throws Exception{
        String contextPath = request.getContextPath();
        //将异常对象写入日志内
        log.error(exception.getMessage(), exception);
        //返回异常信息, 直接返回给客户端
        return myJSONResult.errorMsg("系统异常");
    }
}

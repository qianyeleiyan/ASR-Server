package org.codesdream.asr.controller;

import org.codesdream.asr.component.api.QuickJSONRespond;
import org.codesdream.asr.component.json.respond.ErrorInfoJSONRespond;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class ASRErrorController implements ErrorController {

    @Resource
    private QuickJSONRespond quickJSONRespond;

    @RequestMapping("/error")
    @ResponseBody
    public String handleError(HttpServletRequest request){
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");

        // 检查返回的状态
        if (statusCode == HttpStatus.NOT_FOUND.value()) return quickJSONRespond.getRespond404(null);
        ErrorInfoJSONRespond errorInfoJSONRespond  = new ErrorInfoJSONRespond();

        // 检查是否含有引发异常
        if (exception.getCause() == null) {
            errorInfoJSONRespond.setException(exception.getClass().getName());
            errorInfoJSONRespond.setExceptionMessage(exception.getMessage());
        } else {
            errorInfoJSONRespond.setException(exception.getCause().getClass().getName());
            errorInfoJSONRespond.setExceptionMessage(exception.getCause().getMessage());
        }
        errorInfoJSONRespond.setDate(new Date());

        return quickJSONRespond.getJSONStandardRespond(
                statusCode,
                "Internal Server Error",
                null,
                errorInfoJSONRespond);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

package org.codesdream.asr.component.auth;

import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.component.api.QuickJSONRespond;
import org.codesdream.asr.component.json.respond.ErrorInfoJSONRespond;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 认证失败处理
 */
@Slf4j
@Component
public class ASRAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Resource
    private QuickJSONRespond quickJSONRespond;

    /**
     *
     * @param request HTTP请求
     * @param response HTTP返回
     * @param exception 异常类型
     * @throws IOException I/O异常
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException
    {
        log.info("ASEAuthenticationFailureHandler Login Fail!");

        // 填写异常信息存储对象
        ErrorInfoJSONRespond errorInfoJSONRespond = new ErrorInfoJSONRespond();
        errorInfoJSONRespond.setDate(new Date());
        errorInfoJSONRespond.setExceptionMessage(exception.getMessage());
        errorInfoJSONRespond.setException(exception.getClass().getSimpleName());

        // 认证失败返回406
        response.getWriter().write(quickJSONRespond.getJSONStandardRespond(
                406,
                "Not Acceptable",
                "Authentication Failure",
                errorInfoJSONRespond));
        response.setStatus(406);
    }
}

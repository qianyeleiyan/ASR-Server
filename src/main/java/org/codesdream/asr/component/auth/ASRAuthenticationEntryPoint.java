package org.codesdream.asr.component.auth;

import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.component.api.QuickJSONRespond;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 匿名用户访问无权限资源（未得到认证的用户视为匿名用户）
 */
@Slf4j
@Component
public class ASRAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Resource
    private QuickJSONRespond quickJSONRespond;

    /**
     * 处理函数
     * @param request HTTP请求
     * @param response HTTP返回
     * @param authException 认证异常
     * @throws IOException I/O异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        // 对匿名用户返回401
        response.getWriter().print(quickJSONRespond.getRespond401(null));
        response.setStatus(401);
    }
}

package org.codesdream.asr.component.auth;

import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.component.api.QuickJSONRespond;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 已认证用户访问无权限资源状态处理
 */
@Slf4j
@Component
public class ASRAccessDeniedHandler implements AccessDeniedHandler {

    @Resource
    private QuickJSONRespond quickJSONRespond;

    /**
     * 处理函数
     * @param request HTTP请求
     * @param response HTTP返回
     * @param accessDeniedException 无权限访问异常
     * @throws IOException I/O异常
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {

        log.info("ASEAccessDeniedHandler Found!");

        // 对无权限操作返回403
        response.getWriter().print(quickJSONRespond.getRespond403(null));
        response.setStatus(403);

    }
}

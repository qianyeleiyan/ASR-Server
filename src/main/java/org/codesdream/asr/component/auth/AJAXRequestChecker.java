package org.codesdream.asr.component.auth;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 检查请求是否为Ajax的方式发起
 */
@Component
public class AJAXRequestChecker {

    /**
     * 检查请求是否为Ajax的方式发起
     * @param request HTTP请求
     * @return 布尔值
     */
    public boolean checkAjaxPOSTRequest(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader("X-Requested-With")).isPresent();
    }
}

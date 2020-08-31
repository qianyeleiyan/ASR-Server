package org.codesdream.asr.component.auth;

import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.component.datamanager.JSONParameter;
import org.codesdream.asr.component.json.request.UserLoginChecker;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 账号认证过滤器
 */
@Slf4j
public class ASRUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Resource
    private JSONParameter jsonParameter;

    @Resource
    private AJAXRequestChecker ajaxRequestChecker;

    @Resource
    private TimestampExpiredChecker timestampExpiredChecker;

    /**
     * 验证函数
     * @param request HTTP请求
     * @param response HTTP返回
     * @return 认证柄
     * @throws AuthenticationException 认证异常
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String timestamp =  request.getHeader("timestamp");

        // 检查时间戳是否合理(60秒内)
        if(timestamp == null || !timestampExpiredChecker.checkTimestampBeforeMaxTime(timestamp, 60)){
            throw new AuthenticationServiceException("Timestamp Expired.");
        }

        // 判断是否为AJAX请求格式的数据
        if(!ajaxRequestChecker.checkAjaxPOSTRequest(request)) {
            throw new AuthenticationServiceException("Authentication method not supported: NOT Ajax Method.");
        }

        Optional<UserLoginChecker> checkerOptional = jsonParameter.getJavaObjectByRequest(request, UserLoginChecker.class);
        if(!checkerOptional.isPresent()) throw new BadCredentialsException("Invalid AJAX JSON Request");

        UserLoginChecker checker = checkerOptional.get();

        if(checker.getOpenid() == null
                || checker.getPassword() == null
                || checker.getClientCode() == null)
            throw new AuthenticationServiceException("Request Data IS Incomplete");

        // 获得相应的用户名密码
        String openid = checker.getOpenid();
        // 得到加密密码
        String password = checker.getPassword();
        String clientCode = checker.getClientCode();

        if (openid == null) openid = "";
        if (password == null) password = "";

        // 去除首尾两端的空白字符
        openid = openid.trim();
        password = password.trim();


        JSONTokenUsernamePasswordAuthenticationToken authRequest =
                new JSONTokenUsernamePasswordAuthenticationToken(openid, password, clientCode);

        authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}

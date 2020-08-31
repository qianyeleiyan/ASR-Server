package org.codesdream.asr.component.auth;

import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.model.auth.JSONToken;
import org.codesdream.asr.service.AuthService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;


/**
 * API请求验证服务
 */
@Slf4j
public class ASRJSONTokenAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JSONRandomCodeGenerator randomCodeGenerator;

    @Resource
    private AuthService authService;

    @Resource
    private JSONSignedGenerator signedGenerator;

    @Resource
    private UserDetailsService userDetailsService;

    /**
     *
     * @param request HTTP请求
     * @param response HTTP返回
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException I/O异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 用户名
        String openid = request.getHeader( "openid");
        // 客户端签名
        String signed = request.getHeader("signed");
        // 时间戳
        String timestamp = request.getHeader("timestamp");

        // 服务端API测试豁免签名
        if(signed != null && signed.equals("6d4923fca4dcb51f67b85e54a23a8d763d9e02af")){
            //执行授权
            doAuthentication("oFy6M5Rao0qQkVosVv1Qeh01Gw-Y", request);
        }
        // 正常认证
        else if (signed != null && openid != null && timestamp != null) {
            // 获得具体时间
            Date date = new Date(Long.parseLong(timestamp));

            Date now = new Date();

            // 限制时间戳有效区间为60s
            long dtTime = 60*1000;
            Date maxDate = new Date(now.getTime() + dtTime);

            // 检查时间戳是否合理
            if(maxDate.after(date)) {
                // 从服务器中查找token
                Optional<JSONToken> optionalJSONToken = authService.findTokenByUserName(openid);
                if (optionalJSONToken.isPresent()) {
                    JSONToken token = optionalJSONToken.get();

                    // 检查token是否过期
                    if (!authService.checkTokenIfExpired(token)) {
                        // 生成特征随机代码
                        String randomCode = randomCodeGenerator.generateRandomCode(openid,
                                date, "965cda1983f569b03abfb80dc9af8dd8");

                        log.info(String.format("Determined Signed: %s",
                                signedGenerator.generateSigned(openid, randomCode, token.getToken())));
                        log.info(String.format("Get Signed: %s", signed));

                        // 检查签名是否正确
                        if (signed.equals(signedGenerator.generateSigned(openid, randomCode, token.getToken()))) {
                            // 执行授权操作
                            doAuthentication(openid, request);
                        }
                    }
                }
            }
        }

        filterChain.doFilter(request, response);

    }

    // 执行授权
    private void doAuthentication(String username, HttpServletRequest request){
        // 查询用户的相关信息
        UserDetails user = userDetailsService.loadUserByUsername(username);

        // 生成用户权限列表
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // 生成授权柄 (储存上下文信息)
        JSONTokenAuthenticationToken authentication =
                new JSONTokenAuthenticationToken(user, null, authorities);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 执行授权
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}

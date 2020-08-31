package org.codesdream.asr.component.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;

// 普通用户名密码验证, 用户获得Token
@Slf4j
@Component
public class ASRSecurityAuthenticationProvider implements AuthenticationProvider {
    @Resource
    UserDetailsService userDetailsService;

    @Resource
    ASRPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JSONTokenUsernamePasswordAuthenticationToken authenticationToken =
                (JSONTokenUsernamePasswordAuthenticationToken) authentication;

        // 获得JSON中的学号
        String username = (String) authenticationToken.getPrincipal();
        // 获得JSON中的加密密码
        String encrypted_password =  passwordEncoder.encode((String) authenticationToken.getCredentials());
        // 获得客户端代码
        String clientCode = authenticationToken.getClientCode();
        // 判断用户是否存在并获取用户信息
        UserDetails userInfo = userDetailsService.loadUserByUsername(username);

        if (userInfo == null) {
            throw new UsernameNotFoundException("User Not Exist");
        }

        // 判断密码是否正确
        if(!userInfo.getPassword().equals(encrypted_password)){
            throw new BadCredentialsException("Password IS INCORRECT");
        }

        // 判断账号是否停用/删除
        if (!userInfo.isEnabled()) {
            throw new DisabledException("User IS Disabled");
        }
        else if(!userInfo.isAccountNonLocked()) {
            throw new LockedException("User IS Locked");
        }
        else if(!userInfo.isAccountNonExpired()) {
            throw new AccountExpiredException("User IS Expired");
        }

        // 生成权限列表
        Collection<? extends GrantedAuthority> authorities = userInfo.getAuthorities();

        return new JSONTokenAuthenticationToken(userInfo, clientCode, authorities);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(JSONTokenUsernamePasswordAuthenticationToken.class);
    }
}

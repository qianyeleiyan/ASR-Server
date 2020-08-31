package org.codesdream.asr.component.auth;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 权限管理器
 */
@Component
public class ASRAccessDecisionManager implements AccessDecisionManager {

    /**
     * 确定用户的权限
     * @param authentication 认证柄
     * @param object 传入对象
     * @param configAttributes 角色信息对象
     * @throws AccessDeniedException 访问禁止异常
     * @throws InsufficientAuthenticationException 会话认证异常
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        if(null == configAttributes || configAttributes.size() <= 0) {
            return;
        }

        for (ConfigAttribute c : configAttributes) {
            String needRole = c.getAttribute();
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                if (needRole.trim().equals(ga.getAuthority())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("Access Denied");
    }

    /**
     * 是否支持该角色信息对象
     * @param attribute 角色信息对象
     * @return 布尔值
     */
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    /**
     * 是否支持该认证柄
     * @param clazz 认证柄对象信息
     * @return 布尔值
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}

package org.codesdream.asr.component.auth;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 关联Token与其他用户的相关数据的认证柄
 */
public class JSONTokenAuthenticationToken extends AbstractAuthenticationToken {

    // 客户端签名
    private String signed = null;
    // 用户名
    private Object principal = null;
    // 客户端代码
    private String clientCode = null;


    public JSONTokenAuthenticationToken(UserDetails principal,
                                        String clientCode,
                                        Collection<? extends GrantedAuthority> authorities)
    {
        super(authorities);
        this.principal = principal;
        this.clientCode = clientCode;
        this.signed = null;
        setAuthenticated(true);
    }

    public JSONTokenAuthenticationToken(String principal, String clientCode, String signed) {
        super(null);
        this.principal = principal;
        this.clientCode = clientCode;
        this.signed = signed;
        setAuthenticated(false);
    }

    @Override
    public String getCredentials() {
        return signed;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }
}

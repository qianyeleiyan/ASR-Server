package org.codesdream.asr.configure;

import org.codesdream.asr.component.auth.*;
import org.codesdream.asr.service.ASRUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.Resource;

/**
 * Spring Security 配置类
 * 用于Spring Security相关参数的配置
 */
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    ASRUserDetailsService aseUserDetailService;

    @Resource
    ASRPasswordEncoder ASRPasswordEncoder;

    @Resource
    ASRSecurityAuthenticationProvider ASRSecurityAuthenticationProvider;

    @Resource
    ASRAuthenticationSuccessHandler successHandler;

    @Resource
    ASRAuthenticationFailureHandler failureHandler;

    @Resource
    ASRAuthenticationEntryPoint authenticationEntryPoint;

    @Resource
    ASRAccessDeniedHandler accessDeniedHandler;

    /**
     * HTTP服务器安全配置
     * @param http HTTP服务器安全对象
     * @throws Exception 异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .logout().permitAll();

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        // 替换掉原有的UsernamePasswordAuthenticationFilter
        http.addFilterAt(epUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jsonTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

    /**
     * 认证器设置
     * @param auth 认证对象
     * @throws Exception 异常
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ASRSecurityAuthenticationProvider)
                .userDetailsService(aseUserDetailService)
                .passwordEncoder(ASRPasswordEncoder);
    }

    /**
     * 安全配置
     * @param web web对象
     * @throws Exception 异常
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(
                        "/assets/**",
                        "/forget/**",
                        "/not_found/**",
                        "/error/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/configuration/security",
                        "/wx/**");
    }

    /**
     * 注册自定义的UsernamePasswordAuthenticationFilter
     * @return UsernamePasswordAuthenticationFilter
     * @throws Exception 异常
     */
    @Bean
    ASRJSONTokenAuthenticationFilter jsonTokenAuthenticationFilter() throws Exception {
        return new ASRJSONTokenAuthenticationFilter();
    }

    /**
     * 注册自定义的UsernamePasswordAuthenticationFilter
     * @return UsernamePasswordAuthenticationFilter
     * @throws Exception 异常
     */
    @Bean
    ASRUsernamePasswordAuthenticationFilter epUsernamePasswordAuthenticationFilter() throws Exception {
        ASRUsernamePasswordAuthenticationFilter filter = new ASRUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy(sessionRegistry()));
        filter.setAllowSessionCreation(true);
        filter.setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher("/user/login", "POST"));

        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    /**
     * 注册Session会话储存器
     * @return SessionRegistry
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * 登记sessionAuthenticationStrategy
     * @param sessionRegistry sessionRegistry
     * @return SessionAuthenticationStrategy
     */
    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(SessionRegistry sessionRegistry){
        return new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry){{
            setMaximumSessions(1);
        }};
    }

}

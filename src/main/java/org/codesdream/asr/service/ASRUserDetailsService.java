package org.codesdream.asr.service;

import org.codesdream.asr.exception.notfound.UserNotFoundException;
import org.codesdream.asr.model.user.User;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 用户对象获取服务实例
 * （Spring Security依赖）
 */
@Service
public class ASRUserDetailsService implements UserDetailsService {

    @Resource
    IUserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) {
        try {
            Optional<User> userOptional = userService.findUserByOpenid(s);
            if(!userOptional.isPresent()) throw new UserNotFoundException(s);
            User user = userOptional.get();
            user.setAuthorities(new ArrayList<>());
            return user;
        } catch (UserNotFoundException e){
            throw  new AuthenticationServiceException("User Not Exist");
        }

    }
}

package org.codesdream.asr.service;

import javafx.util.Pair;
import org.codesdream.asr.component.auth.AuthTokenGenerator;
import org.codesdream.asr.component.auth.TimestampExpiredChecker;
import org.codesdream.asr.model.auth.JSONToken;
import org.codesdream.asr.model.auth.PreValidationCode;
import org.codesdream.asr.model.user.User;
import org.codesdream.asr.repository.auth.JSONTokenRepository;
import org.codesdream.asr.repository.auth.PreValidationCodeRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * 身份验证服务类
 */
@Service
public class AuthService implements IAuthService {

    @Resource
    private JSONTokenRepository jsonTokenRepository;

    @Resource
    private IUserService userService;

    @Resource
    private AuthTokenGenerator authTokenGenerator;

    @Resource
    private PreValidationCodeRepository preValidationCodeRepository;

    @Resource
    private TimestampExpiredChecker timestampExpiredChecker;

    /**
     * 通过用户名查找Token有效
     * @param username 用户名
     * @return Token对象
     */
    @Override
    public Optional<JSONToken> findTokenByUserName(String username) {
        return jsonTokenRepository.findByUsername(username);
    }

    /**
     * 检查Token是否过期
     * @param token Token对象
     * @return 布尔值
     */
    @Override
    public boolean checkTokenIfExpired(JSONToken token) {
        return token.getExpiredDate().compareTo(new Date()) <= 0;
    }

    /**
     * 获得新的有效Token
     * @param openid 用户名
     * @param sessionKey 会话秘钥
     * @return Token值
     */
    @Override
    public Optional<String> userNewTokenGetter(String openid, String sessionKey) {
        Pair<Boolean, User> userPair = userService.checkIfUserExists(openid);
        if(userPair.getKey()){
            Optional<JSONToken> jsonTokenOptional = jsonTokenRepository.findByUsername(openid);
            JSONToken token = jsonTokenOptional.orElseGet(JSONToken::new);

            // 过期时间设置为60分钟后
            long currentTime = System.currentTimeMillis();
            currentTime +=60*60*1000;
            token.setExpiredDate(new Date(currentTime));
            token.setToken(authTokenGenerator.generateAuthToken(openid));


            // 设置用户名
            token.setUsername(openid);
            // 设置客户端代号
            token.setSessionKey(sessionKey);

            // 在数据库中更新新的token
            token = jsonTokenRepository.save(token);
            return Optional.ofNullable(token.getToken());
        }
        else return Optional.empty();
    }

    /**
     * 预验证码生成
     * @return 预验证码
     */
    @Override
    public String preValidationCodeGetter() {
        PreValidationCode preValidationCode = new
                PreValidationCode();
        preValidationCode.setValue(UUID.randomUUID().toString());
        preValidationCode = preValidationCodeRepository.save(preValidationCode);
        return preValidationCode.getValue();
    }

    /**
     * 预验证码检查
     * @param pvc 预验证码
     * @return 布尔值
     */
    @Override
    public boolean preValidationCodeChecker(String pvc) {
        Optional<PreValidationCode> preValidationCode =
                preValidationCodeRepository.findByValue(pvc);
        if(preValidationCode.filter(validationCode -> timestampExpiredChecker.checkDateBeforeMaxTime(validationCode.getDate(), 60)).isPresent()){
            preValidationCodeRepository.delete(preValidationCode.get());
            return true;
        }
        else return false;
    }
}

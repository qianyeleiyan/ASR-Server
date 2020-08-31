package org.codesdream.asr.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.codesdream.asr.component.datamanager.JSONParameter;
import org.codesdream.asr.component.datamanager.SHA256Encoder;
import org.codesdream.asr.component.json.model.JsonableToken;
import org.codesdream.asr.component.json.model.JsonableUser;
import org.codesdream.asr.configure.WxMaConfiguration;
import org.codesdream.asr.exception.innerservererror.InnerDataTransmissionException;
import org.codesdream.asr.model.user.User;
import org.codesdream.asr.service.IAuthService;
import org.codesdream.asr.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/wx/user/{appid}")
public class LoginController {

    @Resource
    private JSONParameter jsonParameter;

    @Resource
    private IUserService userService;

    @Resource
    private IAuthService authService;

    @Resource
    private SHA256Encoder encoder;

    @GetMapping("/login")
    public String login(@PathVariable String appid, @RequestParam(name="code") String code) {
        if (StringUtils.isBlank(code)) {
            return "empty jscode";
        }

        final WxMaService wxService = WxMaConfiguration.getMaService(appid);

        try {
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
            log.info(session.getSessionKey());
            log.info(session.getOpenid());

            Optional<User> userOptional = userService.findUserByOpenid(session.getOpenid());

            if(!userOptional.isPresent()){
                User user = new User();
                user.setUsername(session.getOpenid());
                user.setPassword(encoder.encode(session.getOpenid()));
                user = userService.save(user);
                userService.createPools(user);
            }

            Optional<String> token = authService.userNewTokenGetter(session.getOpenid(), session.getSessionKey());

            if(!token.isPresent()) throw new InnerDataTransmissionException();

            JsonableToken tokenObject = new  JsonableToken(session.getOpenid(), token.get());

            return jsonParameter.getJSONString(tokenObject);
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            return e.toString();
        }
    }




    /**
     * <pre>
     * 获取用户绑定手机号信息
     * </pre>
     */
    @GetMapping("/phone")
    public String phone(@PathVariable String appid, String sessionKey, String signature,
                        String rawData, String encryptedData, String iv) {
        final WxMaService wxService = WxMaConfiguration.getMaService(appid);

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return "user check failed";
        }

        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);

        return jsonParameter.getJSONString(phoneNoInfo);
    }

}

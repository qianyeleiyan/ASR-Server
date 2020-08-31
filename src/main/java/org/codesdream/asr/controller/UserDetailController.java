package org.codesdream.asr.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import org.codesdream.asr.component.datamanager.JSONParameter;
import org.codesdream.asr.configure.WxMaConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserDetailController {

    @Resource
    private JSONParameter jsonParameter;

    /**
     * <pre>
     * 获取用户信息接口
     * </pre>
     */
    @GetMapping("info")
    public String info(@RequestParam(name="appid") String appid,
                       @RequestParam(name="signature") String signature, String rawData, String encryptedData, String iv) {
        final WxMaService wxService = WxMaConfiguration.getMaService(appid);

//        // 用户信息校验
//        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
//            return "user check failed";
//        }
//
//        // 解密用户信息
//        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
//
//        return jsonParameter.getJSONString(userInfo);
        return null;
    }
}

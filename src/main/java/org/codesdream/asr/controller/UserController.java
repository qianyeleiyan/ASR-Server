package org.codesdream.asr.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.codesdream.asr.component.json.model.JsonableStar;
import org.codesdream.asr.component.json.model.JsonableSubmit;
import org.codesdream.asr.component.json.model.JsonableUser;
import org.codesdream.asr.exception.badrequest.AlreadyExistException;
import org.codesdream.asr.exception.badrequest.IllegalException;
import org.codesdream.asr.exception.notfound.NotFoundException;
import org.codesdream.asr.model.user.User;
import org.codesdream.asr.model.user.UserDetail;
import org.codesdream.asr.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("user")
@Api("用户验证类接口")
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("用户注册接口")
    public JsonableUser createUser(@RequestBody JsonableUser jsonableUser){
        if (jsonableUser.getOpenid() == null) throw new IllegalAccessError("Null Value Openid");
        if (userService.findUserByOpenid(jsonableUser.getOpenid()).isPresent())
            throw new AlreadyExistException(jsonableUser.getOpenid());

        User user = userService.getDefaultUser();
        user = userService.save(jsonableUser.parseObject(user));
        userService.createPools(user);
        return new JsonableUser(user);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("检查用户是否存在接口")
    public JsonableUser getUser(@RequestParam(value = "openid") String openid){
        Optional<User> user = userService.findUserByOpenid(openid);
        if(!user.isPresent())
            throw new NotFoundException(openid);

        JsonableUser jsonableUser = new JsonableUser();
        jsonableUser.setId(user.get().getId());
        jsonableUser.setOpenid(user.get().getUsername());
        return jsonableUser;
    }

    @PostMapping("submit")
    @ResponseStatus(HttpStatus.CREATED)
    public boolean getSubmit(@RequestBody JsonableSubmit submit, Authentication authentication){
        if(submit.getText() == null) throw new  IllegalException(submit.toString());

        User user = (User) authentication.getPrincipal();
        Optional<User> userOptional = userService.findUserByOpenid(user.getUsername());

        assert userOptional.isPresent();

        UserDetail detail = user.getUserDetail();

        if(submit.getType() == 0){
            detail.getComplain().add(submit.getText());
        }
        else{
            detail.getAdvice().add(submit.getText());
        }

        userService.update(userOptional.get());

        return true;
    }

    @GetMapping("star")
    @ResponseStatus(HttpStatus.OK)
    public JsonableStar getUserStar(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return new JsonableStar(user);
    }

}

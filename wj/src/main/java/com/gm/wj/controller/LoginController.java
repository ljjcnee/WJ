package com.gm.wj.controller;

import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

import javax.validation.Valid;

/**
 * Login and register controller.
 *
 * @author Evan
 * @date 2019/4
 */
@RestController
public class LoginController {

    @Autowired
    UserService userService;

    @PostMapping("/api/login")
    public Result login(@RequestBody User requestUser) {
        String username = requestUser.getUsername();
        username = HtmlUtils.htmlEscape(username);

        Subject subject = SecurityUtils.getSubject();
//        subject.getSession().setTimeout(10000);
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, requestUser.getPassword());
        usernamePasswordToken.setRememberMe(true);
        try {
            subject.login(usernamePasswordToken);
            User user = userService.findByUsername(username);
            if (!user.isEnabled()) {
                return ResultFactory.buildFailResult("该用户已被禁用");
            }
            return ResultFactory.buildSuccessResult(username);
        } catch (IncorrectCredentialsException e) {
            return ResultFactory.buildFailResult("密码错误");
        } catch (UnknownAccountException e) {
            return ResultFactory.buildFailResult("账号不存在");
        }
    }

    @PostMapping("api/register")
    @ResponseBody
    public Result register(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        // 1. 前置安全拦截：防止引发空指针异常导致前端弹空框
        if (username == null || username.trim().equals("") || password == null || password.trim().equals("")) {
            return ResultFactory.buildFailResult("用户名和密码不能为空");
        }

        username = HtmlUtils.htmlEscape(username);
        user.setUsername(username);

        // 2. 检查该用户名是否已经存在数据库中
        boolean exist = userService.isExist(username);
        if (exist) {
            return ResultFactory.buildFailResult("该用户名已被注册，请换一个重试");
        }

        // 3. 👑 核心修复：删除了重复的加盐加密代码，直接把纯净的 user 对象丢给 UserService 处理！
        // 你的 UserService 内部会自动为它生成盐值、MD5加密并存入数据库
        userService.register(user);

        return ResultFactory.buildSuccessResult("注册成功");
    }

    @GetMapping("/api/logout")
    public Result logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return ResultFactory.buildSuccessResult("成功登出");
    }

    @GetMapping("/api/authentication")
    public String authentication() {
        return "身份认证成功";
    }
}

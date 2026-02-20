package com.gm.wj.controller;

import com.gm.wj.entity.*;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * User controller.
 *
 * @author Evan
 * @date 2019/11
 */

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    AdminUserRoleService adminUserRoleService;

    @GetMapping("/api/admin/user")
    public Result listUsers() {
        return ResultFactory.buildSuccessResult(userService.list());
    }

    @PutMapping("/api/admin/user/status")
    public Result updateUserStatus(@RequestBody @Valid User requestUser) {
        userService.updateUserStatus(requestUser);
        return ResultFactory.buildSuccessResult("用户状态更新成功");
    }

    @PutMapping("/api/admin/user/password")
    public Result resetPassword(@RequestBody @Valid User requestUser) {
        userService.resetPassword(requestUser);
        return ResultFactory.buildSuccessResult("重置密码成功");
    }

    @PutMapping("/api/admin/user")
    public Result editUser(@RequestBody @Valid User requestUser) {
        userService.editUser(requestUser);
        return ResultFactory.buildSuccessResult("修改用户信息成功");
    }

    // 👑 彻底修复：完整补上缺失的删除接口，并加上“防撞击”的 try-catch 保护壳
    @DeleteMapping("/api/admin/user/{id}")
    public Result deleteUser(@PathVariable("id") int id) {
        try {
            userService.deleteById(id);
            return ResultFactory.buildSuccessResult("删除用户成功");
        } catch (Exception e) {
            // 拦截可能发生的数据库外键冲突（比如该用户借了书还没还，或者有绑定的角色），
            // 把错误优雅地返回给前端，防止报 500 错误被拦截器踢回登录页！
            return ResultFactory.buildFailResult("删除失败：该用户可能有未归还的图书或存在其他关联数据");
        }
    }
}
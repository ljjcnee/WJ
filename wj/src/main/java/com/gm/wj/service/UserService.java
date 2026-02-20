package com.gm.wj.service;

import com.gm.wj.dao.UserDAO;
import com.gm.wj.entity.AdminRole;
import com.gm.wj.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;
    @Autowired
    AdminRoleService adminRoleService;
    @Autowired
    AdminUserRoleService adminUserRoleService;

    public List<User> list() {
        List<User> users =  userDAO.findAll();
        List<AdminRole> roles;
        for (User user : users) {
            // 👑 核心修复：原来是 user.getName()，现在必须改成 user.getUsername()！
            // 因为 admin 等老用户没有真实姓名，传 null 会直接导致后端崩溃
            roles = adminRoleService.listRolesByUser(user.getUsername());
            user.setRoles(roles);

            // 安全优化：在发给前端展示之前，把密码和盐值清空，防止敏感信息在浏览器被抓包
            user.setPassword("");
            user.setSalt("");
        }
        return users;
    }

    public boolean isExist(String username) {
        User user = getByName(username);
        return null != user;
    }

    // 1. 供借阅模块调用
    public User getByName(String username) {
        return userDAO.findByUsername(username);
    }

    // 2. 供登录模块调用 (兼容旧代码)
    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    // 3. 供仪表盘统计调用
    public long count() {
        return userDAO.count();
    }

    public User get(String username, String password){
        return userDAO.getByUsernameAndPassword(username, password);
    }

    public void add(User user) {
        user.setEnabled(true);
        userDAO.save(user);
    }

    public int register(User user) {
        String username = user.getUsername();
        String name = user.getName();
        String phone = user.getPhone();
        String email = user.getEmail();
        String password = user.getPassword();

        username = HtmlUtils.htmlEscape(username);
        user.setUsername(username);
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        phone = HtmlUtils.htmlEscape(phone);
        user.setPhone(phone);
        email = HtmlUtils.htmlEscape(email);
        user.setEmail(email);
        user.setEnabled(true);

        if (username.equals("") || password.equals("")) {
            return 0;
        }

        boolean exist = isExist(username);

        if (exist) {
            return 2;
        }

        String salt = new java.security.SecureRandom().toString();
        int times = 2;
        String encodedPassword = new org.apache.shiro.crypto.hash.SimpleHash("md5", password, salt, times).toString();

        user.setSalt(salt);
        user.setPassword(encodedPassword);

        userDAO.save(user);
        return 1;
    }

    public void updateUserStatus(User user) {
        User userInDB = userDAO.findByUsername(user.getUsername());
        userInDB.setEnabled(user.isEnabled());
        userDAO.save(userInDB);
    }

    public User resetPassword(User user) {
        User userInDB = userDAO.findByUsername(user.getUsername());
        String salt = new java.security.SecureRandom().toString();
        int times = 2;
        userInDB.setSalt(salt);
        String encodedPassword = new org.apache.shiro.crypto.hash.SimpleHash("md5", "123", salt, times).toString();
        userInDB.setPassword(encodedPassword);
        return userDAO.save(userInDB);
    }

    public void editUser(User user) {
        User userInDB = userDAO.findByUsername(user.getUsername());
        userInDB.setName(user.getName());
        userInDB.setPhone(user.getPhone());
        userInDB.setEmail(user.getEmail());
        userDAO.save(userInDB);
        adminUserRoleService.saveRoleChanges(userInDB.getId(), user.getRoles());
    }

    public void deleteById(int id) {
        userDAO.deleteById(id);
    }
}
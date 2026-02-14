package com.gm.wj.dao;

import com.gm.wj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User,Integer> {
    // 补全根据用户名查询
    User findByUsername(String username);

    User getByUsernameAndPassword(String username,String password);
}
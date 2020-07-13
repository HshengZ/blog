package com.hshengz.blog.dao;

import com.hshengz.blog.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
    //校验用户名和密码是否存在
    User findByUsernameAndPassword(String username,String password);
}

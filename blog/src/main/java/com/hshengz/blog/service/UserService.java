package com.hshengz.blog.service;

import com.hshengz.blog.dao.UserRepository;
import com.hshengz.blog.pojo.User;
import com.hshengz.blog.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    //校验用户名和密码是否存在
    public User checkUser(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, MD5Utils.code(password));
    }
}
